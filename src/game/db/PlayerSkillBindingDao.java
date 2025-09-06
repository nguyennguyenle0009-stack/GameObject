package game.db;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;

/** DAO for player's technique key bindings. */
public class PlayerSkillBindingDao {

    /** Load key bindings for a player. Returns map of key string -> technique name. */
    public Map<String, String> load(String playerId) throws ClassNotFoundException, SQLException {
        Map<String, String> map = new HashMap<>();
        try (Connection conn = DBAccount.getConnectDB();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT [Key], TechniqueName FROM dbo.PlayerTechniqueBindings WHERE PlayerId = ?")) {
            ps.setString(1, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getString("Key"), rs.getString("TechniqueName"));
                }
            }
        }
        return map;
    }

    /** Replace all bindings for player with given map. */
    public void replaceAll(String playerId, Map<String, String> bindings) throws ClassNotFoundException, SQLException {
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
                            "INSERT INTO dbo.PlayerTechniqueBindings(PlayerId, [Key], TechniqueName) VALUES(?,?,?)")) {
                        for (Map.Entry<String, String> e : bindings.entrySet()) {
                            ins.setString(1, playerId);
                            ins.setString(2, e.getKey());
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

    /** Kiểm tra phím đã được gán cho người chơi chưa. */
    public boolean isKeyBound(String playerId, String key) throws ClassNotFoundException, SQLException {
        try (Connection conn = DBAccount.getConnectDB();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT 1 FROM dbo.PlayerTechniqueBindings WHERE PlayerId = ? AND [Key] = ?")) {
            ps.setString(1, playerId);
            ps.setString(2, key);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** Xuất danh sách các phím đã dùng ra file text. */
    public void exportUsedKeys(String playerId, Path path) throws ClassNotFoundException, SQLException, java.io.IOException {
        List<String> keys = new ArrayList<>();
        try (Connection conn = DBAccount.getConnectDB();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT [Key] FROM dbo.PlayerTechniqueBindings WHERE PlayerId = ?")) {
            ps.setString(1, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) keys.add(rs.getString("Key"));
            }
        }
        Files.write(path, keys);
    }
}
