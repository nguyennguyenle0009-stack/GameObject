package game.db;

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

    /**
     * Insert a consumable or material item definition if it does not already exist.
     *
     * @param item item to persist
     * @return {@code true} if the item exists or was inserted; {@code false} if unsupported
     */
    public static boolean insert(Item item) {
        if (item == null || item.getId() == null) return false;
        try (Connection conn = DBAccount.getConnectDB()) {
            PreparedStatement check = conn.prepareStatement(
                "SELECT 1 FROM " + ITEMS + " WHERE ItemId = ?");
            check.setString(1, item.getId());
            if (check.executeQuery().next()) return true; // already present

            PreparedStatement insItem = conn.prepareStatement(
                "INSERT INTO " + ITEMS + " (ItemId, Name, Type) VALUES (?,?,?)");
            if (item instanceof HealthPotion hp) {
                insItem.setString(1, hp.getId());
                insItem.setString(2, hp.getName());
                insItem.setString(3, "HEALTH_POTION");
                insItem.executeUpdate();
                PreparedStatement insMod = conn.prepareStatement(
                    "INSERT INTO " + ITEM_MODS + " (ItemId, Stat, Flat) VALUES (?,?,?)");
                insMod.setString(1, hp.getId());
                insMod.setString(2, Attr.HEALTH.name());
                insMod.setInt(3, hp.getHealthAmount());
                insMod.executeUpdate();
                return true;
            } else if (item instanceof SpiritPotion sp) {
                insItem.setString(1, sp.getId());
                insItem.setString(2, sp.getName());
                insItem.setString(3, "SPIRIT_POTION");
                insItem.executeUpdate();
                PreparedStatement insMod = conn.prepareStatement(
                    "INSERT INTO " + ITEM_MODS + " (ItemId, Stat, Flat) VALUES (?,?,?)");
                insMod.setString(1, sp.getId());
                insMod.setString(2, Attr.SPIRIT.name());
                insMod.setInt(3, sp.getSpiritAmount());
                insMod.executeUpdate();
                return true;
            } else if (item instanceof MaterialItem m) {
                insItem.setString(1, m.getId());
                insItem.setString(2, m.getName());
                insItem.setString(3, "MATERIAL");
                insItem.executeUpdate();
                return true;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
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

