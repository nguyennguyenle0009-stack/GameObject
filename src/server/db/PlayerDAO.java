package server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import game.entity.Player;
import game.entity.item.EquipmentItem;
import game.entity.item.Item;
import game.entity.inventory.Inventory;
import game.enums.Attr;
import game.enums.EquipSlot;
import game.entity.attributes.Attributes;

/**
 * DAO helper for persisting {@link Player} state into normalized tables.
 */
public class PlayerDAO {
    private static final String PLAYERS = "Players";
    private static final String BASE = "PlayerBaseStats";
    private static final String RUNTIME = "PlayerRuntime";
    private static final String INV = "PlayerInventory";
    private static final String EQUIP = "PlayerEquipment";

    private PlayerDAO() {}

    /**
     * Load player state from database into {@code p}.
     *
     * @return true if data existed; false otherwise
     */
    public static boolean load(Player p) {
        try (Connection conn = DBAccount.getConnectDB()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT PlayerId, Realm FROM " + PLAYERS + " WHERE Name=?");
            ps.setString(1, p.getName());
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return false;
            String playerId = rs.getString("PlayerId");
            // Realm could be stored but Player currently keeps default; ignore for brevity

            loadBaseStats(conn, playerId, p.getBaseAttributes());
            loadRuntime(conn, playerId, p.getBaseAttributes());
            loadInventory(conn, playerId, p.getBag());
            loadEquipment(conn, playerId, p);
            p.refreshStats();
            return true;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void loadBaseStats(Connection conn, String pid, Attributes atts) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
            "SELECT Atk, Def, HealthMax, PepMax, Sould, Spirit, SpiritMax, Strength FROM " + BASE + " WHERE PlayerId=?");
        ps.setString(1, pid);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            atts.setBase(Attr.ATTACK, rs.getInt("Atk"));
            atts.set(Attr.ATTACK, rs.getInt("Atk"));
            atts.setBase(Attr.DEF, rs.getInt("Def"));
            atts.set(Attr.DEF, rs.getInt("Def"));
            atts.setBase(Attr.SOULD, rs.getInt("Sould"));
            atts.set(Attr.SOULD, rs.getInt("Sould"));
            atts.setBase(Attr.STRENGTH, rs.getInt("Strength"));
            atts.set(Attr.STRENGTH, rs.getInt("Strength"));
            atts.setBase(Attr.SPIRIT, rs.getInt("Spirit"));
            atts.set(Attr.SPIRIT, rs.getInt("Spirit"));
            atts.setMax(Attr.HEALTH, rs.getInt("HealthMax"));
            atts.setMax(Attr.PEP, rs.getInt("PepMax"));
            atts.setMax(Attr.SPIRIT, rs.getInt("SpiritMax"));
        }
    }

    private static void loadRuntime(Connection conn, String pid, Attributes atts) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
            "SELECT CurrentHP, CurrentPep FROM " + RUNTIME + " WHERE PlayerId=?");
        ps.setString(1, pid);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            atts.set(Attr.HEALTH, rs.getInt("CurrentHP"));
            atts.set(Attr.PEP, rs.getInt("CurrentPep"));
        }
    }

    private static void loadInventory(Connection conn, String pid, Inventory bag) throws SQLException, ClassNotFoundException {
        bag.clear();
        PreparedStatement ps = conn.prepareStatement(
            "SELECT ItemId, Quantity FROM " + INV + " WHERE PlayerId=?");
        ps.setString(1, pid);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            String itemId = rs.getString("ItemId");
            int qty = rs.getInt("Quantity");
            Item item = ItemDAO.load(itemId);
            if (item != null) {
                item.setQuantity(qty);
                bag.add(item);
            }
        }
    }

    private static void loadEquipment(Connection conn, String pid, Player p) throws SQLException, ClassNotFoundException {
        for (EquipSlot slot : EquipSlot.values()) {
            p.getEquipmentMap().remove(slot);
        }
        PreparedStatement ps = conn.prepareStatement(
            "SELECT Slot, ItemId FROM " + EQUIP + " WHERE PlayerId=?");
        ps.setString(1, pid);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            String slotName = rs.getString("Slot");
            String itemId = rs.getString("ItemId");
            try {
                EquipSlot slot = EquipSlot.valueOf(slotName);
                if (itemId != null && !itemId.isBlank()) {
                    Item it = ItemDAO.load(itemId);
                    if (it instanceof EquipmentItem eq) {
                        p.getEquipmentMap().put(slot, eq);
                        if (eq.getType().name().equals("RING")) {
                            p.getBag().increaseCapacity(10);
                        }
                    }
                }
            } catch (IllegalArgumentException ignore) { }
        }
    }

    /** Persist current state of {@code p} into the database. */
    public static void save(Player p) {
        try (Connection conn = DBAccount.getConnectDB()) {
            conn.setAutoCommit(false);
            String playerId = upsertPlayer(conn, p);
            saveBaseStats(conn, playerId, p.getBaseAttributes());
            saveRuntime(conn, playerId, p.getBaseAttributes());
            saveInventory(conn, playerId, p.getBag());
            saveEquipment(conn, playerId, p);
            conn.commit();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Quickly persist only runtime stats (HP/PEP) without touching inventory
     * or equipment. Useful when health changes frequently during gameplay.
     */
    public static void saveRuntime(Player p) {
        try (Connection conn = DBAccount.getConnectDB()) {
            conn.setAutoCommit(false);
            String playerId = upsertPlayer(conn, p);
            saveRuntime(conn, playerId, p.getBaseAttributes());
            conn.commit();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static String upsertPlayer(Connection conn, Player p) throws SQLException {
        String playerId = null;
        PreparedStatement sel = conn.prepareStatement("SELECT PlayerId FROM " + PLAYERS + " WHERE Name=?");
        sel.setString(1, p.getName());
        ResultSet rs = sel.executeQuery();
        if (rs.next()) {
            playerId = rs.getString(1);
            PreparedStatement upd = conn.prepareStatement("UPDATE " + PLAYERS + " SET Realm=? WHERE PlayerId=?");
            upd.setString(1, p.getRealm().name());
            upd.setString(2, playerId);
            upd.executeUpdate();
        } else {
            PreparedStatement ins = conn.prepareStatement(
                "INSERT INTO " + PLAYERS + " (Name, Realm) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
            ins.setString(1, p.getName());
            ins.setString(2, p.getRealm().name());
            ins.executeUpdate();
            // Retrieve generated id
            rs = sel.executeQuery();
            if (rs.next()) {
                playerId = rs.getString(1);
            }
        }
        return playerId;
    }

    private static void saveBaseStats(Connection conn, String pid, Attributes atts) throws SQLException {
        PreparedStatement del = conn.prepareStatement("DELETE FROM " + BASE + " WHERE PlayerId=?");
        del.setString(1, pid);
        del.executeUpdate();
        PreparedStatement ins = conn.prepareStatement(
            "INSERT INTO " + BASE + " (PlayerId, Atk, Def, HealthMax, PepMax, Sould, Spirit, SpiritMax, Strength) VALUES (?,?,?,?,?,?,?,?,?)");
        ins.setString(1, pid);
        ins.setInt(2, atts.getBase(Attr.ATTACK));
        ins.setInt(3, atts.getBase(Attr.DEF));
        ins.setInt(4, atts.getMax(Attr.HEALTH));
        ins.setInt(5, atts.getMax(Attr.PEP));
        ins.setInt(6, atts.getBase(Attr.SOULD));
        ins.setInt(7, atts.get(Attr.SPIRIT));
        ins.setInt(8, atts.getMax(Attr.SPIRIT));
        ins.setInt(9, atts.getBase(Attr.STRENGTH));
        ins.executeUpdate();
    }

    private static void saveRuntime(Connection conn, String pid, Attributes atts) throws SQLException {
        PreparedStatement del = conn.prepareStatement("DELETE FROM " + RUNTIME + " WHERE PlayerId=?");
        del.setString(1, pid);
        del.executeUpdate();
        PreparedStatement ins = conn.prepareStatement(
            "INSERT INTO " + RUNTIME + " (PlayerId, CurrentHP, CurrentPep, Money) VALUES (?,?,?,?)");
        ins.setString(1, pid);
        ins.setInt(2, atts.get(Attr.HEALTH));
        ins.setInt(3, atts.get(Attr.PEP));
        ins.setLong(4, 0); // money not tracked in Player yet
        ins.executeUpdate();
    }

    private static void saveInventory(Connection conn, String pid, Inventory bag) throws SQLException {
        PreparedStatement del = conn.prepareStatement("DELETE FROM " + INV + " WHERE PlayerId=?");
        del.setString(1, pid);
        del.executeUpdate();
        PreparedStatement ins = conn.prepareStatement(
            "INSERT INTO " + INV + " (PlayerId, ItemId, Quantity) VALUES (?,?,?)");
        for (Item it : bag.all()) {
            boolean exists = ItemDAO.exists(conn, it.getId());
            if (!exists) {
                try {
                    if (!ItemDAO.insert(conn, it)) {
                        continue; // skip unsupported items
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    continue;
                }
            }
            ins.setString(1, pid);
            ins.setString(2, it.getId());
            ins.setInt(3, it.getQuantity());
            ins.addBatch();
        }
        ins.executeBatch();
    }

    private static void saveEquipment(Connection conn, String pid, Player p) throws SQLException {
        PreparedStatement del = conn.prepareStatement("DELETE FROM " + EQUIP + " WHERE PlayerId=?");
        del.setString(1, pid);
        del.executeUpdate();
        PreparedStatement ins = conn.prepareStatement(
            "INSERT INTO " + EQUIP + " (PlayerId, Slot, ItemId) VALUES (?,?,?)");
        for (EquipSlot slot : EquipSlot.values()) {
            EquipmentItem eq = p.getEquipment(slot);
            ins.setString(1, pid);
            ins.setString(2, slot.name());
            if (eq != null) {
                ins.setString(3, eq.getId());
            } else {
                ins.setNull(3, java.sql.Types.NVARCHAR);
            }
            ins.addBatch();
        }
        ins.executeBatch();
    }
}

