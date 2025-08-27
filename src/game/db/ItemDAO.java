package game.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumMap;
import java.util.Map;

import game.entity.item.EquipmentItem;
import game.entity.item.Item;
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
                EquipType eType = parseEquipType(type);
                if (eType == null) {
                    return null; // unsupported for now
                }
                EnumMap<Attr, Integer> bonuses = loadBonuses(conn, itemId);
                String icon = defaultIcon(eType);
                return new EquipmentItem(itemId, name, "", icon, eType, bonuses);
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

