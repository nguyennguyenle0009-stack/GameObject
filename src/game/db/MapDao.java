package game.db;

import java.sql.*;

/**
 * DAO for map metadata.
 */
public class MapDao {

    /** Ensure a map record exists; insert if missing. */
    public void ensureExists(String mapId, String name, String info) throws ClassNotFoundException, SQLException {
        try (Connection conn = DBAccount.getConnectDB()) {
            try (PreparedStatement check = conn.prepareStatement(
                    "SELECT 1 FROM dbo.Maps WHERE MapId = ?")) {
                check.setString(1, mapId);
                try (ResultSet rs = check.executeQuery()) {
                    if (rs.next()) {
                        return; // already exists
                    }
                }
            }
            try (PreparedStatement insert = conn.prepareStatement(
                    "INSERT INTO dbo.Maps(MapId, Name, Info) VALUES(?,?,?)")) {
                insert.setString(1, mapId);
                insert.setString(2, name);
                insert.setString(3, info);
                insert.executeUpdate();
            }
        }
    }
}
