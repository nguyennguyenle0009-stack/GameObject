package server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumMap;
import java.util.Map;

import game.entity.item.EquipmentItem;
import game.entity.item.Item;
import game.entity.item.MaterialItem;
import game.entity.item.elixir.HealthPotion;
import game.entity.item.elixir.SpiritPotion;
import game.enums.Attr;
import game.enums.EquipType;

/**
 * Data access helper for items and their stat modifiers.
 */
public class ItemDAO {

    private static final String ITEMS = "Items";
    private static final String ITEM_MODS = "ItemStatMods";

    private ItemDAO() {}

    /**
     * Check if an item already exists in the {@code Items} table.
     */
    public static boolean exists(String itemId) throws SQLException, ClassNotFoundException {
        try (Connection conn = DBAccount.getConnectDB()) {
            return exists(conn, itemId);
        }
    }

    public static boolean exists(Connection conn, String itemId) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM " + ITEMS + " WHERE ItemId=?");
        ps.setString(1, itemId);
        try (ResultSet rs = ps.executeQuery()) {
            return rs.next();
        }
    }

    /**
     * Insert a new consumable or material item into {@code Items} and
     * {@code ItemStatMods}. Unsupported item types return {@code false}.
     */
    public static boolean insert(Item item) throws SQLException, ClassNotFoundException {
        try (Connection conn = DBAccount.getConnectDB()) {
            return insert(conn, item);
        }
    }

    /** Insert using an existing connection. */
    public static boolean insert(Connection conn, Item item) throws SQLException {
        if (item == null) return false;
        if (exists(conn, item.getId())) return true; // already persisted

        String type;
        EnumMap<Attr, Integer> mods = new EnumMap<>(Attr.class);
        if (item instanceof HealthPotion hp) {
            type = "HEALTH_POTION";
            mods.put(Attr.HEALTH, hp.getHealthAmount());
        } else if (item instanceof SpiritPotion sp) {
            type = "SPIRIT_POTION";
            mods.put(Attr.SPIRIT, sp.getSpiritAmount());
        } else if (item instanceof MaterialItem) {
            type = "MATERIAL";
        } else {
            return false; // unsupported type
        }

        PreparedStatement ins = conn.prepareStatement(
            "INSERT INTO " + ITEMS + " (ItemId, Name, Type) VALUES (?,?,?)");
        ins.setString(1, item.getId());
        ins.setString(2, item.getName());
        ins.setString(3, type);
        ins.executeUpdate();

        for (Map.Entry<Attr, Integer> e : mods.entrySet()) {
            PreparedStatement mod = conn.prepareStatement(
                "INSERT INTO " + ITEM_MODS + " (ItemId, Stat, Flat, PercentBonus) VALUES (?,?,?,0)");
            mod.setString(1, item.getId());
            mod.setString(2, e.getKey().name());
            mod.setInt(3, e.getValue());
            mod.executeUpdate();
        }
        return true;
    }

    /**
     * Load an item by id. Supports equipment types; other item types are
     * returned as null.
     *
     * @param itemId identifier in the {@code Items} table
     * @return constructed {@link Item} or {@code null} if not found/unsupported
     */
    public static Item load(String itemId) throws SQLException, ClassNotFoundException {
        try (Connection conn = DBAccount.getConnectDB()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT Name, Type FROM " + ITEMS + " WHERE ItemId = ?");
            ps.setString(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                String name = rs.getString("Name");
                String type = rs.getString("Type");
                  EnumMap<Attr, Integer> bonuses = loadBonuses(conn, itemId);
                  EquipType eType = parseEquipType(type);
                  if (eType != null) {
                      String icon = defaultIcon(eType);
                      return new EquipmentItem(itemId, name, "", icon, eType, bonuses);
                  }
                  String t = type == null ? "" : type.trim().toUpperCase();
                  switch (t) {
                      case "HEALTH_POTION" -> {
                          int heal = bonuses.getOrDefault(Attr.HEALTH, 0);
                          return new HealthPotion(itemId, heal, 1);
                      }
                      case "SPIRIT_POTION" -> {
                          int amount = bonuses.getOrDefault(Attr.SPIRIT, 0);
                          return new SpiritPotion(itemId, amount, 1);
                      }
                      case "MATERIAL" -> {
                          return new MaterialItem(itemId, name, "", null, 1, 99);
                      }
                      default -> {
                          return null; // unsupported type
                      }
                  }
              }
          }
      }

    private static EnumMap<Attr, Integer> loadBonuses(Connection conn, String itemId) throws SQLException {
        EnumMap<Attr, Integer> map = new EnumMap<>(Attr.class);
        PreparedStatement ps = conn.prepareStatement(
            "SELECT Stat, Flat FROM " + ITEM_MODS + " WHERE ItemId = ?");
        ps.setString(1, itemId);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String stat = rs.getString("Stat");
                try {
                    Attr attr = Attr.valueOf(stat);
                    int val = rs.getInt("Flat");
                    map.put(attr, val);
                } catch (IllegalArgumentException ex) {
                    // unknown stat, ignore
                }
            }
        }
        return map;
    }

    private static EquipType parseEquipType(String type) {
        if (type == null) return null;
        try {
            return EquipType.valueOf(type.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static String defaultIcon(EquipType type) {
        return switch (type) {
            case ARMOR -> "/data/item/equipment/armor.png";
            case HELMET -> "/data/item/equipment/helmet.png";
            case PANTS -> "/data/item/equipment/pants.png";
            case SHOES -> "/data/item/equipment/shoes.png";
            case NECKLACE, RING -> "/data/item/equipment/ring.png";
            case WEAPON -> "/data/item/equipment/sword.png";
            case AMULET -> "/data/item/equipment/d_1.png";
        };
    }
}

