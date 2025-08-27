package game.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import game.entity.Player;
import game.entity.item.Item;
import game.entity.item.EquipmentItem;
import game.entity.inventory.Inventory;
import game.enums.Attr;
import game.enums.EquipSlot;

/**
 * DAO handling persistence of player state across multiple normalized tables.
 */
public class PlayerDAO {

    /** Save the given player's state into the database. */
    public static void save(Player p) {
        try (Connection conn = DBAccount.getConnectDB()) {
            conn.setAutoCommit(false);
            UUID playerId = ensurePlayer(conn, p);
            saveBaseStats(conn, playerId, p);
            saveRuntime(conn, playerId, p);
            saveInventory(conn, playerId, p.getBag());
            saveEquipment(conn, playerId, p);
            conn.commit();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    /** Load player state for the name already set in the Player object. */
    public static boolean load(Player p) {
        try (Connection conn = DBAccount.getConnectDB()) {
            UUID playerId = fetchPlayerId(conn, p.getName());
            if (playerId == null) return false;
            loadBaseStats(conn, playerId, p);
            loadRuntime(conn, playerId, p);
            loadInventory(conn, playerId, p);
            loadEquipment(conn, playerId, p);
            return true;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- internal helpers -------------------------------------------------

    private static UUID ensurePlayer(Connection conn, Player p) throws SQLException {
        UUID id = fetchPlayerId(conn, p.getName());
        if (id != null) {
            try (PreparedStatement up = conn.prepareStatement("UPDATE Players SET Realm=? WHERE PlayerId=?")) {
                up.setString(1, p.getRealm().name());
                up.setObject(2, id);
                up.executeUpdate();
            }
            return id;
        }
        try (PreparedStatement ins = conn.prepareStatement(
                "INSERT INTO Players (Name, Realm) OUTPUT inserted.PlayerId VALUES (?,?)")) {
            ins.setString(1, p.getName());
            ins.setString(2, p.getRealm().name());
            try (ResultSet rs = ins.executeQuery()) {
                rs.next();
                return (UUID) rs.getObject(1);
            }
        }
    }

    private static UUID fetchPlayerId(Connection conn, String name) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT PlayerId FROM Players WHERE Name=?")) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return (UUID) rs.getObject(1);
                }
                return null;
            }
        }
    }

    private static void saveBaseStats(Connection conn, UUID pid, Player p) throws SQLException {
        String sql = "MERGE PlayerBaseStats AS t USING (SELECT ? AS PlayerId) AS s ON t.PlayerId=s.PlayerId "
                + "WHEN MATCHED THEN UPDATE SET Atk=?, Def=?, HealthMax=?, PepMax=?, Sould=?, Spirit=?, SpiritMax=?, Strength=? "
                + "WHEN NOT MATCHED THEN INSERT (PlayerId,Atk,Def,HealthMax,PepMax,Sould,Spirit,SpiritMax,Strength) "
                + "VALUES (?,?,?,?,?,?,?,?,?);";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, pid);
            ps.setInt(2, p.getBaseAtts().getBase(Attr.ATTACK));
            ps.setInt(3, p.getBaseAtts().getBase(Attr.DEF));
            ps.setInt(4, p.getBaseAtts().getMax(Attr.HEALTH));
            ps.setInt(5, p.getBaseAtts().getMax(Attr.PEP));
            ps.setInt(6, p.getBaseAtts().getBase(Attr.SOULD));
            ps.setInt(7, p.getBaseAtts().getBase(Attr.SPIRIT));
            ps.setInt(8, p.getBaseAtts().getMax(Attr.SPIRIT));
            ps.setInt(9, p.getBaseAtts().getBase(Attr.STRENGTH));
            ps.setObject(10, pid);
            ps.setInt(11, p.getBaseAtts().getBase(Attr.ATTACK));
            ps.setInt(12, p.getBaseAtts().getBase(Attr.DEF));
            ps.setInt(13, p.getBaseAtts().getMax(Attr.HEALTH));
            ps.setInt(14, p.getBaseAtts().getMax(Attr.PEP));
            ps.setInt(15, p.getBaseAtts().getBase(Attr.SOULD));
            ps.setInt(16, p.getBaseAtts().getBase(Attr.SPIRIT));
            ps.setInt(17, p.getBaseAtts().getMax(Attr.SPIRIT));
            ps.setInt(18, p.getBaseAtts().getBase(Attr.STRENGTH));
            ps.executeUpdate();
        }
    }

    private static void saveRuntime(Connection conn, UUID pid, Player p) throws SQLException {
        String sql = "MERGE PlayerRuntime AS t USING (SELECT ? AS PlayerId) AS s ON t.PlayerId=s.PlayerId "
                + "WHEN MATCHED THEN UPDATE SET CurrentHP=?, CurrentPep=?, Money=? "
                + "WHEN NOT MATCHED THEN INSERT (PlayerId,CurrentHP,CurrentPep,Money) VALUES (?,?,?,?);";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, pid);
            ps.setInt(2, p.atts().get(Attr.HEALTH));
            ps.setInt(3, p.atts().get(Attr.PEP));
            ps.setLong(4, 0);
            ps.setObject(5, pid);
            ps.setInt(6, p.atts().get(Attr.HEALTH));
            ps.setInt(7, p.atts().get(Attr.PEP));
            ps.setLong(8, 0);
            ps.executeUpdate();
        }
    }

    private static void saveInventory(Connection conn, UUID pid, Inventory inv) throws SQLException {
        try (PreparedStatement del = conn.prepareStatement("DELETE FROM PlayerInventory WHERE PlayerId=?")) {
            del.setObject(1, pid);
            del.executeUpdate();
        }
        try (PreparedStatement ins = conn.prepareStatement(
                "INSERT INTO PlayerInventory (PlayerId,ItemId,Quantity) VALUES (?,?,?)")) {
            for (Item it : inv.all()) {
                if (it instanceof EquipmentItem eq) {
                    ins.setObject(1, pid);
                    ins.setString(2, eq.getId());
                    ins.setInt(3, it.getQuantity());
                    ins.addBatch();
                }
            }
            ins.executeBatch();
        }
    }

    private static void saveEquipment(Connection conn, UUID pid, Player p) throws SQLException {
        try (PreparedStatement del = conn.prepareStatement("DELETE FROM PlayerEquipment WHERE PlayerId=?")) {
            del.setObject(1, pid);
            del.executeUpdate();
        }
        try (PreparedStatement ins = conn.prepareStatement(
                "INSERT INTO PlayerEquipment (PlayerId,Slot,ItemId) VALUES (?,?,?)")) {
            for (var e : p.getEquipmentMap().entrySet()) {
                ins.setObject(1, pid);
                ins.setString(2, e.getKey().name());
                EquipmentItem eq = e.getValue();
                ins.setString(3, eq != null ? eq.getId() : null);
                ins.addBatch();
            }
            ins.executeBatch();
        }
    }

    private static void loadBaseStats(Connection conn, UUID pid, Player p) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT Atk,Def,HealthMax,PepMax,Sould,Spirit,SpiritMax,Strength FROM PlayerBaseStats WHERE PlayerId=?")) {
            ps.setObject(1, pid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    p.getBaseAtts().setBase(Attr.ATTACK, rs.getInt("Atk"));
                    p.getBaseAtts().setBase(Attr.DEF, rs.getInt("Def"));
                    p.getBaseAtts().setMax(Attr.HEALTH, rs.getInt("HealthMax"));
                    p.getBaseAtts().set(Attr.HEALTH, rs.getInt("HealthMax"));
                    p.getBaseAtts().setMax(Attr.PEP, rs.getInt("PepMax"));
                    p.getBaseAtts().set(Attr.PEP, rs.getInt("PepMax"));
                    p.getBaseAtts().setBase(Attr.SOULD, rs.getInt("Sould"));
                    p.getBaseAtts().setBase(Attr.SPIRIT, rs.getInt("Spirit"));
                    p.getBaseAtts().setMax(Attr.SPIRIT, rs.getInt("SpiritMax"));
                    p.getBaseAtts().setBase(Attr.STRENGTH, rs.getInt("Strength"));
                }
            }
        }
    }

    private static void loadRuntime(Connection conn, UUID pid, Player p) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT CurrentHP, CurrentPep FROM PlayerRuntime WHERE PlayerId=?")) {
            ps.setObject(1, pid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    p.atts().set(Attr.HEALTH, rs.getInt("CurrentHP"));
                    p.atts().set(Attr.PEP, rs.getInt("CurrentPep"));
                }
            }
        }
    }

    private static void loadInventory(Connection conn, UUID pid, Player p) throws SQLException, ClassNotFoundException {
        p.getBag().clear();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT ItemId, Quantity FROM PlayerInventory WHERE PlayerId=?")) {
            ps.setObject(1, pid);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String itemId = rs.getString("ItemId");
                    int qty = rs.getInt("Quantity");
                    Item it = ItemDAO.loadItem(itemId, qty);
                    if (it != null) p.getBag().add(it);
                }
            }
        }
    }

    private static void loadEquipment(Connection conn, UUID pid, Player p) throws SQLException, ClassNotFoundException {
        p.getEquipmentMap().clear();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT Slot, ItemId FROM PlayerEquipment WHERE PlayerId=?")) {
            ps.setObject(1, pid);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    EquipSlot slot = EquipSlot.valueOf(rs.getString("Slot"));
                    String itemId = rs.getString("ItemId");
                    if (itemId != null) {
                        EquipmentItem eq = ItemDAO.loadEquipment(itemId);
                        if (eq != null) {
                            p.getEquipmentMap().put(slot, eq);
                            if (eq.getType() == game.enums.EquipType.RING) {
                                p.getBag().increaseCapacity(10);
                            }
                        }
                    }
                }
            }
        }
        p.refreshStats();
    }
}
