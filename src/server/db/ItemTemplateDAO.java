package server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumMap;

import game.entity.item.ItemTemplate;
import game.enums.Attr;
import game.enums.EquipType;

/**
 * DAO for loading equipment templates from the database.
 */
public class ItemTemplateDAO {
    private static final String ITEMS = "Items";
    private static final String ITEM_MODS = "ItemStatMods";

    private ItemTemplateDAO() {}

    /**
     * Load a template by its identifier.
     */
    public static ItemTemplate load(String templateId) throws SQLException, ClassNotFoundException {
        try (Connection conn = DBAccount.getConnectDB()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT Name, Type FROM " + ITEMS + " WHERE ItemId=?");
            ps.setString(1, templateId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                String name = rs.getString("Name");
                EquipType type = parseEquipType(rs.getString("Type"));
                EnumMap<Attr, Integer> bonuses = loadBonuses(conn, templateId);
                return new ItemTemplate(templateId, name, type, bonuses);
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
                    // ignore unknown stat
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
}
