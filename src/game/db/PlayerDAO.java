package game.db;

import java.sql.*;
import java.util.*;

import game.entity.Player;
import game.entity.item.Item;
import game.entity.item.EquipmentItem;
import game.enums.Attr;
import game.enums.EquipSlot;

/** DAO for persisting {@link Player} using normalized tables. */
public class PlayerDAO {

    /** Load player data; return true if player exists. */
    public static boolean load(Player p) {
        try (Connection conn = DBAccount.getConnectDB()) {
            String sql = "SELECT p.PlayerId, b.Atk, b.Def, b.HealthMax, b.PepMax, b.Sould, b.Spirit, b.SpiritMax, b.Strength, r.CurrentHP, r.CurrentPep " +
                    "FROM Players p JOIN PlayerBaseStats b ON p.PlayerId=b.PlayerId JOIN PlayerRuntime r ON p.PlayerId=r.PlayerId WHERE p.Name=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, p.getName());
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return false;
                    String pid = rs.getString("PlayerId");
                    var base = p.getBaseAttributes();
                    base.setMax(Attr.HEALTH, rs.getInt("HealthMax"));
                    base.set(Attr.HEALTH, rs.getInt("CurrentHP"));
                    base.setMax(Attr.PEP, rs.getInt("PepMax"));
                    base.set(Attr.PEP, rs.getInt("CurrentPep"));
                    base.set(Attr.ATTACK, rs.getInt("Atk"));
                    base.set(Attr.DEF, rs.getInt("Def"));
                    base.set(Attr.SOULD, rs.getInt("Sould"));
                    base.set(Attr.SPIRIT, rs.getInt("Spirit"));
                    base.setMax(Attr.SPIRIT, rs.getInt("SpiritMax"));
                    base.set(Attr.STRENGTH, rs.getInt("Strength"));

                    // load inventory
                    p.getBag().clear();
                    ItemDAO itemDao = new ItemDAO();
                    try (PreparedStatement psInv = conn.prepareStatement("SELECT ItemId, Quantity FROM PlayerInventory WHERE PlayerId=?")) {
                        psInv.setString(1, pid);
                        try (ResultSet rsInv = psInv.executeQuery()) {
                            while (rsInv.next()) {
                                String itemId = rsInv.getString("ItemId");
                                int qty = rsInv.getInt("Quantity");
                                Item it = itemDao.loadById(itemId, qty);
                                if (it != null) p.getBag().add(it);
                            }
                        }
                    }

                    // load equipment
                    p.clearEquipment();
                    try (PreparedStatement psEq = conn.prepareStatement("SELECT Slot, ItemId FROM PlayerEquipment WHERE PlayerId=?")) {
                        psEq.setString(1, pid);
                        try (ResultSet rsEq = psEq.executeQuery()) {
                            while (rsEq.next()) {
                                String slot = rsEq.getString("Slot");
                                String itemId = rsEq.getString("ItemId");
                                if (itemId != null) {
                                    Item it = itemDao.loadById(itemId, 1);
                                    if (it instanceof EquipmentItem eq) {
                                        p.setEquipmentSlot(EquipSlot.valueOf(slot), eq);
                                    }
                                }
                            }
                        }
                    }
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Save player data to database. */
    public static void save(Player p) {
        try (Connection conn = DBAccount.getConnectDB()) {
            conn.setAutoCommit(false);
            String playerId = ensurePlayer(conn, p);
            saveBaseStats(conn, playerId, p);
            saveRuntime(conn, playerId, p);
            saveInventory(conn, playerId, p);
            saveEquipment(conn, playerId, p);
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String ensurePlayer(Connection conn, Player p) throws SQLException {
        String pid = null;
        try (PreparedStatement ps = conn.prepareStatement("SELECT PlayerId FROM Players WHERE Name=?")) {
            ps.setString(1, p.getName());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) pid = rs.getString(1);
            }
        }
        if (pid == null) {
            pid = java.util.UUID.randomUUID().toString();
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO Players (PlayerId, Name, Realm) VALUES (?,?,?)")) {
                ps.setString(1, pid);
                ps.setString(2, p.getName());
                ps.setString(3, p.getRealmName());
                ps.executeUpdate();
            }
        }
        return pid;
    }

    private static void saveBaseStats(Connection conn, String pid, Player p) throws SQLException {
        var base = p.getBaseAttributes();
        String sql = "MERGE PlayerBaseStats AS t USING (SELECT ? AS PlayerId) AS s ON t.PlayerId=s.PlayerId " +
                "WHEN MATCHED THEN UPDATE SET Atk=?, Def=?, HealthMax=?, PepMax=?, Sould=?, Spirit=?, SpiritMax=?, Strength=? " +
                "WHEN NOT MATCHED THEN INSERT (PlayerId, Atk, Def, HealthMax, PepMax, Sould, Spirit, SpiritMax, Strength) VALUES (?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pid);
            ps.setInt(2, base.getBase(Attr.ATTACK));
            ps.setInt(3, base.getBase(Attr.DEF));
            ps.setInt(4, base.getMax(Attr.HEALTH));
            ps.setInt(5, base.getMax(Attr.PEP));
            ps.setInt(6, base.getBase(Attr.SOULD));
            ps.setInt(7, base.getBase(Attr.SPIRIT));
            ps.setInt(8, base.getMax(Attr.SPIRIT));
            ps.setInt(9, base.getBase(Attr.STRENGTH));
            ps.setString(10, pid);
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
    }

    private static void saveRuntime(Connection conn, String pid, Player p) throws SQLException {
        String sql = "MERGE PlayerRuntime AS t USING (SELECT ? AS PlayerId) AS s ON t.PlayerId=s.PlayerId " +
                "WHEN MATCHED THEN UPDATE SET CurrentHP=?, CurrentPep=?, Money=? " +
                "WHEN NOT MATCHED THEN INSERT (PlayerId, CurrentHP, CurrentPep, Money) VALUES (?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pid);
            ps.setInt(2, p.atts().get(Attr.HEALTH));
            ps.setInt(3, p.atts().get(Attr.PEP));
            ps.setLong(4, 0);
            ps.setString(5, pid);
            ps.setInt(6, p.atts().get(Attr.HEALTH));
            ps.setInt(7, p.atts().get(Attr.PEP));
            ps.setLong(8, 0);
            ps.executeUpdate();
        }
    }

    private static void saveInventory(Connection conn, String pid, Player p) throws SQLException {
        try (PreparedStatement del = conn.prepareStatement("DELETE FROM PlayerInventory WHERE PlayerId=?")) {
            del.setString(1, pid);
            del.executeUpdate();
        }
        try (PreparedStatement ins = conn.prepareStatement("INSERT INTO PlayerInventory (PlayerId, ItemId, Quantity) VALUES (?,?,?)")) {
            for (Item it : p.getBag().all()) {
                String itemId = resolveItemId(conn, it);
                if (itemId != null) {
                    ins.setString(1, pid);
                    ins.setString(2, itemId);
                    ins.setInt(3, it.getQuantity());
                    ins.addBatch();
                }
            }
            ins.executeBatch();
        }
    }

    private static void saveEquipment(Connection conn, String pid, Player p) throws SQLException {
        try (PreparedStatement del = conn.prepareStatement("DELETE FROM PlayerEquipment WHERE PlayerId=?")) {
            del.setString(1, pid);
            del.executeUpdate();
        }
        try (PreparedStatement ins = conn.prepareStatement("INSERT INTO PlayerEquipment (PlayerId, Slot, ItemId) VALUES (?,?,?)")) {
            for (Map.Entry<EquipSlot, EquipmentItem> e : p.getEquipmentMap().entrySet()) {
                if (e.getValue() == null) continue;
                String itemId = resolveItemId(conn, e.getValue());
                if (itemId != null) {
                    ins.setString(1, pid);
                    ins.setString(2, e.getKey().name());
                    ins.setString(3, itemId);
                    ins.addBatch();
                }
            }
            ins.executeBatch();
        }
    }

    private static String resolveItemId(Connection conn, Item it) throws SQLException {
        if (it instanceof EquipmentItem eq) {
            // ensure item exists in Items table
            String itemId = eq.getId();
            try (PreparedStatement ps = conn.prepareStatement(
                    "MERGE Items AS t USING (SELECT ? AS ItemId, ? AS Name, ? AS Type) AS s ON t.ItemId=s.ItemId " +
                    "WHEN NOT MATCHED THEN INSERT (ItemId, Name, Type) VALUES (s.ItemId, s.Name, s.Type);")) {
                ps.setString(1, itemId);
                ps.setString(2, eq.getName());
                ps.setString(3, eq.getType().name().toLowerCase());
                ps.executeUpdate();
            }
            return itemId;
        } else {
            try (PreparedStatement ps = conn.prepareStatement("SELECT ItemId FROM Items WHERE Name=?")) {
                ps.setString(1, it.getName());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getString(1);
                }
            }
        }
        return null;
    }
}
