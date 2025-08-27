package game.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

import game.entity.Player;
import game.entity.item.Item;
import game.entity.item.EquipmentItem;
import game.enums.Attr;
import game.enums.EquipSlot;

/**
 * DAO responsible for persisting and loading {@link Player} state using the
 * normalized tables defined in {@code game_db_core_schema.sql}.
 * <p>
 * The implementation is intentionally lightweight and focuses on base stats,
 * inventory and equipment.
 */
public class PlayerDAO {

    /** Load player data from database into the provided {@link Player}. */
    public boolean load(Player p) {
        try (Connection conn = DBAccount.getConnectDB()) {
            String playerId = null;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT PlayerId FROM Players WHERE Name = ?")) {
                ps.setString(1, p.getName());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) playerId = rs.getString(1);
                }
            }
            if (playerId == null) return false;

            // Base stats
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT Atk, Def, HealthMax, PepMax, Sould, Spirit, SpiritMax, Strength FROM PlayerBaseStats WHERE PlayerId = ?")) {
                ps.setString(1, playerId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var base = p.getBaseAtts();
                        base.set(Attr.ATTACK, rs.getInt("Atk"));
                        base.set(Attr.DEF, rs.getInt("Def"));
                        base.setMax(Attr.HEALTH, rs.getInt("HealthMax"));
                        base.setMax(Attr.PEP, rs.getInt("PepMax"));
                        base.set(Attr.SOULD, rs.getInt("Sould"));
                        base.set(Attr.SPIRIT, rs.getInt("Spirit"));
                        base.setMax(Attr.SPIRIT, rs.getInt("SpiritMax"));
                        base.set(Attr.STRENGTH, rs.getInt("Strength"));
                    }
                }
            }

            // Inventory
            ItemDAO itemDao = new ItemDAO();
            p.getBag().clear();
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT ItemId, Quantity FROM PlayerInventory WHERE PlayerId = ?")) {
                ps.setString(1, playerId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String itemId = rs.getString("ItemId");
                        int qty = rs.getInt("Quantity");
                        Item item = itemDao.loadItem(itemId, qty);
                        if (item != null) {
                            item.setQuantity(qty);
                            p.getBag().add(item);
                        }
                    }
                }
            }

            // Equipment
            p.getEquipmentMap().clear();
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT Slot, ItemId FROM PlayerEquipment WHERE PlayerId = ?")) {
                ps.setString(1, playerId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String slotName = rs.getString("Slot");
                        String itemId = rs.getString("ItemId");
                        if (itemId != null) {
                            Item item = itemDao.loadItem(itemId, 1);
                            if (item instanceof EquipmentItem eq) {
                                EquipSlot slot = EquipSlot.valueOf(slotName);
                                p.setEquipment(slot, eq);
                            }
                        }
                    }
                }
            }

            p.refreshStats();
            return true;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Save current player state into the database. */
    public void save(Player p) {
        try (Connection conn = DBAccount.getConnectDB()) {
            conn.setAutoCommit(false);
            String playerId = null;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT PlayerId FROM Players WHERE Name = ?")) {
                ps.setString(1, p.getName());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) playerId = rs.getString(1);
                }
            }
            if (playerId == null) {
                playerId = UUID.randomUUID().toString();
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO Players (PlayerId, Name) VALUES (?, ?)")) {
                    ps.setString(1, playerId);
                    ps.setString(2, p.getName());
                    ps.executeUpdate();
                }
            }

            var base = p.getBaseAtts();
            try (PreparedStatement ps = conn.prepareStatement(
                    "MERGE PlayerBaseStats AS t USING (SELECT ? AS PlayerId) AS s ON t.PlayerId=s.PlayerId " +
                    "WHEN MATCHED THEN UPDATE SET Atk=?, Def=?, HealthMax=?, PepMax=?, Sould=?, Spirit=?, SpiritMax=?, Strength=? " +
                    "WHEN NOT MATCHED THEN INSERT (PlayerId, Atk, Def, HealthMax, PepMax, Sould, Spirit, SpiritMax, Strength) VALUES (?,?,?,?,?,?,?,?,?);")) {
                ps.setString(1, playerId);
                ps.setInt(2, base.getBase(Attr.ATTACK));
                ps.setInt(3, base.getBase(Attr.DEF));
                ps.setInt(4, base.getMax(Attr.HEALTH));
                ps.setInt(5, base.getMax(Attr.PEP));
                ps.setInt(6, base.getBase(Attr.SOULD));
                ps.setInt(7, base.getBase(Attr.SPIRIT));
                ps.setInt(8, base.getMax(Attr.SPIRIT));
                ps.setInt(9, base.getBase(Attr.STRENGTH));
                ps.setString(10, playerId);
                ps.setInt(11, base.getBase(Attr.ATTACK));
                ps.setInt(12, base.getBase(Attr.DEF));
                ps.setInt(13, base.getMax(Attr.HEALTH));
                ps.setInt(14, base.getMax(Attr.PEP));
                ps.setInt(15, base.getBase(Attr.SOULD));
                ps.setInt(16, base.getBase(Attr.SPIRIT));
                ps.setInt(17, base.getMax(Attr.SPIRIT));
                ps.setInt(18, base.getBase(Attr.STRENGTH));
                ps.executeUpdate();
            }

            // Inventory
            try (PreparedStatement del = conn.prepareStatement(
                    "DELETE FROM PlayerInventory WHERE PlayerId=?")) {
                del.setString(1, playerId);
                del.executeUpdate();
            }
            try (PreparedStatement ins = conn.prepareStatement(
                    "INSERT INTO PlayerInventory (PlayerId, ItemId, Quantity) VALUES (?,?,?)")) {
                for (Item it : p.getBag().all()) {
                    if (it instanceof EquipmentItem eq) {
                        ins.setString(1, playerId);
                        ins.setString(2, eq.getId());
                        ins.setInt(3, it.getQuantity());
                        ins.addBatch();
                    }
                }
                ins.executeBatch();
            }

            // Equipment
            try (PreparedStatement del = conn.prepareStatement(
                    "DELETE FROM PlayerEquipment WHERE PlayerId=?")) {
                del.setString(1, playerId);
                del.executeUpdate();
            }
            try (PreparedStatement ins = conn.prepareStatement(
                    "INSERT INTO PlayerEquipment (PlayerId, Slot, ItemId) VALUES (?,?,?)")) {
                for (Map.Entry<EquipSlot, EquipmentItem> e : p.getEquipmentMap().entrySet()) {
                    ins.setString(1, playerId);
                    ins.setString(2, e.getKey().name());
                    EquipmentItem eq = e.getValue();
                    ins.setString(3, eq == null ? null : eq.getId());
                    ins.addBatch();
                }
                ins.executeBatch();
            }

            conn.commit();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
