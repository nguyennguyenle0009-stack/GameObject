package game.db;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/** DAO for player's technique key bindings. */
public class PlayerSkillBindingDao {

    /** Load key bindings for a player. Returns map of keyCode -> technique name. */
    public Map<Integer, String> load(String playerId) throws ClassNotFoundException, SQLException {
        Map<Integer, String> map = new HashMap<>();
        try (Connection conn = DBAccount.getConnectDB();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT KeyCode, TechniqueName FROM dbo.PlayerTechniqueBindings WHERE PlayerId = ?")) {
            ps.setString(1, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getInt("KeyCode"), rs.getString("TechniqueName"));
                }
            }
        }
        return map;
    }

    /** Replace all bindings for player with given map. */
    public void replaceAll(String playerId, Map<Integer, String> bindings) throws ClassNotFoundException, SQLException {
        try (Connection conn = DBAccount.getConnectDB()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement del = conn.prepareStatement(
                        "DELETE FROM dbo.PlayerTechniqueBindings WHERE PlayerId = ?")) {
                    del.setString(1, playerId);
                    del.executeUpdate();
                }
                if (!bindings.isEmpty()) {
                    try (PreparedStatement ins = conn.prepareStatement(
                            "INSERT INTO dbo.PlayerTechniqueBindings(PlayerId, KeyCode, TechniqueName) VALUES(?,?,?)")) {
                        for (Map.Entry<Integer, String> e : bindings.entrySet()) {
                            ins.setString(1, playerId);
                            ins.setInt(2, e.getKey());
                            ins.setString(3, e.getValue());
                            ins.addBatch();
                        }
                        ins.executeBatch();
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}
