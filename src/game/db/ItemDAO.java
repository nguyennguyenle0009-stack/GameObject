package game.db;

import java.sql.*;
import java.util.*;

import game.entity.item.EquipmentItem;
import game.entity.item.Item;
import game.enums.Attr;
import game.enums.EquipType;

/** Data access for items and their stat modifiers. */
public class ItemDAO {

    /** Load item definition by name. */
    public Item loadByName(String name, int quantity) throws SQLException {
        try (Connection conn = DBAccount.getConnectDB()) {
            String sql = "SELECT ItemId FROM Items WHERE Name = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, name);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return loadById(conn, rs.getString("ItemId"), quantity);
                    }
                }
            }
            return createNonEquipment(name, quantity);
        } catch (ClassNotFoundException e) {
            throw new SQLException(e);
        }
    }

    /** Load item definition by item id. */
    public Item loadById(String itemId, int quantity) throws SQLException {
        try (Connection conn = DBAccount.getConnectDB()) {
            return loadById(conn, itemId, quantity);
        } catch (ClassNotFoundException e) {
            throw new SQLException(e);
        }
    }

    private Item loadById(Connection conn, String itemId, int quantity) throws SQLException {
        String sql = "SELECT ItemId, Name, Type FROM Items WHERE ItemId = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                String name = rs.getString("Name");
                String type = rs.getString("Type");
                if (isEquipment(type)) {
                    EquipType eqType = mapEquipType(type);
                    Map<Attr,Integer> bonuses = fetchBonuses(conn, itemId);
                    String icon = iconFor(eqType);
                    return new EquipmentItem(itemId, name, "", icon, eqType, bonuses);
                } else {
                    return createNonEquipment(name, quantity);
                }
            }
        }
    }

    private Map<Attr,Integer> fetchBonuses(Connection conn, String itemId) throws SQLException {
        Map<Attr,Integer> bonuses = new EnumMap<>(Attr.class);
        String sql = "SELECT Stat, Flat FROM ItemStatMods WHERE ItemId = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    try {
                        Attr a = Attr.valueOf(rs.getString("Stat"));
                        bonuses.put(a, rs.getInt("Flat"));
                    } catch (IllegalArgumentException e) {
                        // ignore unknown stat
                    }
                }
            }
        }
        return bonuses;
    }

    private boolean isEquipment(String type) {
        return switch (type.toLowerCase()) {
            case "weapon","armor","helmet","pants","shoes","amulet","ring","necklace" -> true;
            default -> false;
        };
    }

    private EquipType mapEquipType(String type) {
        return switch (type.toLowerCase()) {
            case "helmet" -> EquipType.HELMET;
            case "armor" -> EquipType.ARMOR;
            case "pants" -> EquipType.PANTS;
            case "shoes" -> EquipType.SHOES;
            case "amulet" -> EquipType.AMULET;
            case "ring" -> EquipType.RING;
            case "necklace" -> EquipType.NECKLACE;
            default -> EquipType.WEAPON;
        };
    }

    private String iconFor(EquipType t) {
        return switch (t) {
            case ARMOR -> "/data/item/equipment/armor.png";
            case HELMET -> "/data/item/equipment/helmet.png";
            case PANTS -> "/data/item/equipment/pants.png";
            case SHOES -> "/data/item/equipment/shoes.png";
            case NECKLACE, RING -> "/data/item/equipment/ring.png";
            case AMULET -> "/data/item/equipment/d_1.png";
            default -> "/data/item/equipment/sword.png";
        };
    }

    private Item createNonEquipment(String name, int qty) {
        // Fallback for consumables/books not stored in database yet
        return switch (name) {
            case "Đan dược hồi máu" -> new game.entity.item.elixir.HealthPotion(50, qty);
            case "Đan dược tinh thần" -> new game.entity.item.elixir.SpiritPotion(200, qty);
            case "Đan hạ phẩm" -> new game.entity.item.elixir.CultivationPill("Đan hạ phẩm", 1, qty);
            case "Đan trung phẩm" -> new game.entity.item.elixir.CultivationPill("Đan trung phẩm", 2, qty);
            case "Đan thượng phẩm" -> new game.entity.item.elixir.CultivationPill("Đan thượng phẩm", 3, qty);
            case "Đan cực phẩm" -> new game.entity.item.elixir.CultivationPill("Đan cực phẩm", 4, qty);
            case "Sách Công pháp hạ phẩm" -> new game.entity.item.book.CultivationBook(new game.entity.skill.CultivationTechnique("Công pháp hạ phẩm", game.enums.SkillGrade.HA, 1, 1));
            case "Sách Công pháp trung phẩm" -> new game.entity.item.book.CultivationBook(new game.entity.skill.CultivationTechnique("Công pháp trung phẩm", game.enums.SkillGrade.TRUNG, 1, 2));
            case "Sách Công pháp thượng phẩm" -> new game.entity.item.book.CultivationBook(new game.entity.skill.CultivationTechnique("Công pháp thượng phẩm", game.enums.SkillGrade.THUONG, 1, 3));
            case "Sách Công pháp cực phẩm" -> new game.entity.item.book.CultivationBook(new game.entity.skill.CultivationTechnique("Công pháp cực phẩm", game.enums.SkillGrade.CUC, 1, 5));
            default -> null;
        };
    }
}
