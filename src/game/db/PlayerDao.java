package game.db;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * DAO for the {@code Players} table.
 */
public class PlayerDao {

    /** Simple DTO representing a row in {@code Players}. */
    public static class PlayerRecord {
        public String playerId;
        public String realm;
        public int realmStage;
        public String physique;
        public LocalDateTime createdAt;
    }

    /**
     * Load a player by name.
     *
     * @param name player name
     * @return record or {@code null} if not found
     */
    public PlayerRecord load(String name) throws ClassNotFoundException, SQLException {
        try (Connection conn = DBAccount.getConnectDB();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT PlayerId, Realm, RealmStage, Physique, CreatedAt FROM dbo.Players WHERE Name = ?")) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PlayerRecord rec = new PlayerRecord();
                    rec.playerId = rs.getString("PlayerId");
                    rec.realm = rs.getString("Realm");
                    rec.realmStage = rs.getInt("RealmStage");
                    rec.physique = rs.getString("Physique");
                    Timestamp ts = rs.getTimestamp("CreatedAt");
                    rec.createdAt = ts != null ? ts.toLocalDateTime() : null;
                    return rec;
                }
            }
        }
        return null;
    }

    /**
     * Insert or update a player and return the {@code PlayerId}.
     */
    public String upsert(String name, String realm, int realmStage, String physique) throws ClassNotFoundException, SQLException {
        try (Connection conn = DBAccount.getConnectDB();
             PreparedStatement ps = conn.prepareStatement(
                     "MERGE dbo.Players AS target " +
                     "USING (SELECT ? AS Name, ? AS Realm, ? AS RealmStage, ? AS Physique) AS src " +
                     "ON target.Name = src.Name " +
                     "WHEN MATCHED THEN UPDATE SET Realm = src.Realm, RealmStage = src.RealmStage, Physique = src.Physique " +
                     "WHEN NOT MATCHED THEN INSERT (Name, Realm, RealmStage, Physique) VALUES (src.Name, src.Realm, src.RealmStage, src.Physique) " +
                     "OUTPUT inserted.PlayerId;")) {
            ps.setString(1, name);
            ps.setString(2, realm);
            ps.setInt(3, realmStage);
            ps.setString(4, physique);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        }
        return null;
    }
}

