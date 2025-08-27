package game.db;

import java.util.EnumMap;

import game.entity.item.EquipmentItem;
import game.entity.item.Item;
import game.entity.item.book.CultivationBook;
import game.entity.item.elixir.CultivationPill;
import game.entity.item.elixir.HealthPotion;
import game.entity.item.elixir.SpiritPotion;
import game.entity.skill.CultivationTechnique;
import game.enums.Attr;
import game.enums.EquipSlot;
import game.enums.EquipType;
import game.enums.SkillGrade;

/**
 * DAO responsible for constructing items based on SQL definitions.
 * Currently uses in-memory fallbacks until proper queries are implemented.
 */
public class ItemDAO {

    /** Create an item instance by name. */
    public Item createItemByName(String name, int qty) {
        // TODO: Replace switch with SQL-backed lookup.
        return switch (name) {
            case "Đan dược hồi máu" -> new HealthPotion(50, qty);
            case "Đan dược tinh thần" -> new SpiritPotion(200, qty);
            case "Đan hạ phẩm" -> new CultivationPill("Đan hạ phẩm", 1, qty);
            case "Đan trung phẩm" -> new CultivationPill("Đan trung phẩm", 2, qty);
            case "Đan thượng phẩm" -> new CultivationPill("Đan thượng phẩm", 3, qty);
            case "Đan cực phẩm" -> new CultivationPill("Đan cực phẩm", 4, qty);
            case "Sách Công pháp hạ phẩm" -> new CultivationBook(new CultivationTechnique("Công pháp hạ phẩm", SkillGrade.HA, 1, 1));
            case "Sách Công pháp trung phẩm" -> new CultivationBook(new CultivationTechnique("Công pháp trung phẩm", SkillGrade.TRUNG, 1, 2));
            case "Sách Công pháp thượng phẩm" -> new CultivationBook(new CultivationTechnique("Công pháp thượng phẩm", SkillGrade.THUONG, 1, 3));
            case "Sách Công pháp cực phẩm" -> new CultivationBook(new CultivationTechnique("Công pháp cực phẩm", SkillGrade.CUC, 1, 5));
            case "Áo giáp" -> equipmentWithBonus(name, "+3 DEF", "/data/item/equipment/armor.png", EquipType.ARMOR, Attr.DEF, 3);
            case "Mũ sắt" -> equipmentWithBonus(name, "+3 DEF", "/data/item/equipment/helmet.png", EquipType.HELMET, Attr.DEF, 3);
            case "Quần vải" -> equipmentWithBonus(name, "+3 DEF", "/data/item/equipment/pants.png", EquipType.PANTS, Attr.DEF, 3);
            case "Giày da" -> equipmentWithBonus(name, "+3 DEF", "/data/item/equipment/shoes.png", EquipType.SHOES, Attr.DEF, 3);
            case "Kiếm gỗ" -> equipmentWithBonus(name, "+10 ATTACK", "/data/item/equipment/sword.png", EquipType.WEAPON, Attr.ATTACK, 10);
            case "Kiếm sắt" -> equipmentWithBonus(name, "+10 ATTACK", "/data/item/equipment/sword.png", EquipType.WEAPON, Attr.ATTACK, 10);
            case "Dây chuyền" -> equipmentWithBonus(name, "+10 SOULD", "/data/item/equipment/ring.png", EquipType.NECKLACE, Attr.SOULD, 10);
            case "Nhẫn đá" -> equipmentWithBonus(name, "+10 ô kho", "/data/item/equipment/ring.png", EquipType.RING, Attr.DEF, 0);
            case "Nhẫn bạc" -> equipmentWithBonus(name, "+10 ô kho", "/data/item/equipment/ring.png", EquipType.RING, Attr.DEF, 0);
            case "Bùa hộ mệnh" -> equipmentWithBonus(name, "Chưa có tác dụng", "/data/item/equipment/d_1.png", EquipType.AMULET, Attr.DEF, 0);
            default -> null;
        };
    }

    /** Create equipment from slot metadata (used during loading). */
    public EquipmentItem createEquipmentFromSlot(String id, String name, String desc, EquipSlot slot) {
        EquipType type = switch (slot) {
            case ARMOR -> EquipType.ARMOR;
            case HELMET -> EquipType.HELMET;
            case PANTS -> EquipType.PANTS;
            case SHOES -> EquipType.SHOES;
            case NECKLACE -> EquipType.NECKLACE;
            case AMULET -> EquipType.AMULET;
            case RING1, RING2 -> EquipType.RING;
            case WEAPON1, WEAPON2 -> EquipType.WEAPON;
        };
        String path = iconPathFromSlot(slot);
        return new EquipmentItem(id, name, desc, path, type);
    }

    private EquipmentItem equipmentWithBonus(String name, String desc, String icon, EquipType type, Attr attr, int val) {
        EnumMap<Attr, Integer> map = new EnumMap<>(Attr.class);
        if (val != 0) {
            map.put(attr, val);
        }
        return new EquipmentItem(name, desc, icon, type, map);
    }

    private String iconPathFromSlot(EquipSlot slot) {
        return switch (slot) {
            case ARMOR -> "/data/item/equipment/armor.png";
            case HELMET -> "/data/item/equipment/helmet.png";
            case PANTS -> "/data/item/equipment/pants.png";
            case SHOES -> "/data/item/equipment/shoes.png";
            case NECKLACE, RING1, RING2 -> "/data/item/equipment/ring.png";
            case AMULET -> "/data/item/equipment/d_1.png";
            case WEAPON1, WEAPON2 -> "/data/item/equipment/sword.png";
        };
    }
}

