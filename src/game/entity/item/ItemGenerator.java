package game.entity.item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import game.db.DBAccount;
import game.db.ItemTemplateDAO;
import game.enums.Attr;
import game.enums.EquipType;

/**
 * Utility to create randomized equipment instances from templates.
 */
public class ItemGenerator {

    private ItemGenerator() {}

    /**
     * Generate a new equipment item from the given template id. The created
     * item is inserted into the {@code Items} and {@code ItemStatMods} tables
     * with a unique identifier.
     */
    public static EquipmentItem createFromTemplate(String templateId)
            throws SQLException, ClassNotFoundException {
        ItemTemplate t = ItemTemplateDAO.load(templateId);
        if (t == null) return null;
        EnumMap<Attr, Integer> rolled = rollStats(t.getBonuses());
        String newId = t.getId() + "#" + UUID.randomUUID();
        persist(newId, t.getName(), t.getType(), rolled);
        String icon = defaultIcon(t.getType());
        return new EquipmentItem(newId, t.getName(), "", icon, t.getType(), rolled);
    }

    private static EnumMap<Attr, Integer> rollStats(Map<Attr, Integer> base) {
        EnumMap<Attr, Integer> rolled = new EnumMap<>(Attr.class);
        ThreadLocalRandom r = ThreadLocalRandom.current();
        for (Map.Entry<Attr, Integer> e : base.entrySet()) {
            int b = e.getValue();
            int min = (int)(b * 0.8);
            int max = (int)(b * 1.2);
            int val = r.nextInt(max - min + 1) + min;
            rolled.put(e.getKey(), val);
        }
        return rolled;
    }

    private static void persist(String id, String name, EquipType type,
            Map<Attr, Integer> bonuses) throws SQLException, ClassNotFoundException {
        try (Connection conn = DBAccount.getConnectDB()) {
            PreparedStatement insItem = conn.prepareStatement(
                "INSERT INTO Items (ItemId, Name, Type) VALUES (?,?,?)");
            insItem.setString(1, id);
            insItem.setString(2, name);
            insItem.setString(3, type.name());
            insItem.executeUpdate();
            PreparedStatement insMod = conn.prepareStatement(
                "INSERT INTO ItemStatMods (ItemId, Stat, Flat) VALUES (?,?,?)");
            for (Map.Entry<Attr, Integer> e : bonuses.entrySet()) {
                insMod.setString(1, id);
                insMod.setString(2, e.getKey().name());
                insMod.setInt(3, e.getValue());
                insMod.addBatch();
            }
            insMod.executeBatch();
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
