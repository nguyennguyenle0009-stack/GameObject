package game.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumMap;

import game.entity.item.EquipmentItem;
import game.entity.item.Item;
import game.enums.Attr;
import game.enums.EquipSlot;
import game.enums.EquipType;

/**
 * Data access object for loading item definitions from the database.
 * Only a minimal subset is implemented for equipment items.
 */
public class ItemDAO {

    /**
     * Load an item by its identifier.
     *
     * @param itemId database identifier
     * @param qty    desired quantity
     * @return materialised {@link Item} or {@code null} if not found
     */
    public Item loadItem(String itemId, int qty) {
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
                    } catch (IllegalArgumentException ex) {
                        return null; // unsupported type
                    }
                    EquipmentItem eq = new EquipmentItem(itemId, name, "", null, type);
                    loadBonuses(conn, itemId, eq);
                    return eq;
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadBonuses(Connection conn, String itemId, EquipmentItem eq) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT Stat, Flat FROM ItemStatMods WHERE ItemId = ?")) {
            ps.setString(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String stat = rs.getString("Stat");
                    int flat = rs.getInt("Flat");
                    try {
                        Attr a = Attr.valueOf(stat);
                        eq.addBonus(a, flat);
                    } catch (IllegalArgumentException ignore) { }
                }
            }
        }
    }
}
