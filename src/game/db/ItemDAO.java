package game.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumMap;

import game.entity.item.EquipmentItem;
import game.entity.item.Item;
import game.enums.Attr;
import game.enums.EquipType;

/**
 * Data access for item definitions and their stat modifiers.
 */
public class ItemDAO {

    /** Load an item by id. Supports equipment items with stat bonuses. */
    public static Item loadItem(String itemId, int quantity) throws SQLException, ClassNotFoundException {
        EquipmentItem eq = loadEquipment(itemId);
        if (eq != null) {
            eq.setQuantity(quantity);
            return eq;
        }
        return null; // unsupported item type
    }

    /** Load equipment definition including stat modifiers. */
    public static EquipmentItem loadEquipment(String itemId) throws SQLException, ClassNotFoundException {
        try (Connection conn = DBAccount.getConnectDB()) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT Name, Type FROM Items WHERE ItemId = ?")) {
                ps.setString(1, itemId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return null;
                    String name = rs.getString("Name");
                    String typeStr = rs.getString("Type");
                    EquipType type;
                    try {
                        type = EquipType.valueOf(typeStr.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                    EnumMap<Attr, Integer> bonuses = new EnumMap<>(Attr.class);
                    try (PreparedStatement ps2 = conn.prepareStatement(
                            "SELECT Stat, Flat FROM ItemStatMods WHERE ItemId = ?")) {
                        ps2.setString(1, itemId);
                        try (ResultSet rs2 = ps2.executeQuery()) {
                            while (rs2.next()) {
                                try {
                                    Attr a = Attr.valueOf(rs2.getString("Stat"));
                                    int flat = rs2.getInt("Flat");
                                    bonuses.merge(a, flat, Integer::sum);
                                } catch (IllegalArgumentException ignore) {
                                }
                            }
                        }
                    }
                    String icon = switch (type) {
                        case ARMOR -> "/data/item/equipment/armor.png";
                        case HELMET -> "/data/item/equipment/helmet.png";
                        case PANTS -> "/data/item/equipment/pants.png";
                        case SHOES -> "/data/item/equipment/shoes.png";
                        case NECKLACE -> "/data/item/equipment/ring.png";
                        case AMULET -> "/data/item/equipment/d_1.png";
                        case RING -> "/data/item/equipment/ring.png";
        case WEAPON -> "/data/item/equipment/sword.png";
                    };
                    return new EquipmentItem(itemId, name, "", icon, type, bonuses);
                }
            }
        }
    }
}
