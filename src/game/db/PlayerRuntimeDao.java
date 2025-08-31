package game.db;

import java.sql.*;

/** DAO for {@code PlayerRuntime}. */
public class PlayerRuntimeDao {

    /** DTO for runtime stats. */
    public static class RuntimeStats {
        public int currentHP;
        public int currentPep;
        public long money;
        /** Identifier of the current map the player is in. */
        public String mapId;
        /** Player position within the map. */
        public int posX;
        public int posY;
    }

    public RuntimeStats load(String playerId) throws ClassNotFoundException, SQLException {
        try (Connection conn = DBAccount.getConnectDB();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT CurrentHP, CurrentPep, Money, MapId, PosX, PosY FROM dbo.PlayerRuntime WHERE PlayerId = ?")) {
            ps.setString(1, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    RuntimeStats rt = new RuntimeStats();
                    rt.currentHP = rs.getInt("CurrentHP");
                    rt.currentPep = rs.getInt("CurrentPep");
                    rt.money = rs.getLong("Money");
                    rt.mapId = rs.getString("MapId");
                    rt.posX = rs.getInt("PosX");
                    rt.posY = rs.getInt("PosY");
                    return rt;
                }
            }
        }
        return null;
    }

    public void upsert(String playerId, int currentHP, int currentPep, long money,
                        String mapId, int posX, int posY) throws ClassNotFoundException, SQLException {
        try (Connection conn = DBAccount.getConnectDB();
             PreparedStatement ps = conn.prepareStatement(
                     "MERGE dbo.PlayerRuntime AS target " +
                     "USING (SELECT ? AS PlayerId, ? AS CurrentHP, ? AS CurrentPep, ? AS Money, ? AS MapId, ? AS PosX, ? AS PosY) AS src " +
                     "ON target.PlayerId = src.PlayerId " +
                     "WHEN MATCHED THEN UPDATE SET CurrentHP=src.CurrentHP, CurrentPep=src.CurrentPep, Money=src.Money, MapId=src.MapId, PosX=src.PosX, PosY=src.PosY " +
                     "WHEN NOT MATCHED THEN INSERT (PlayerId, CurrentHP, CurrentPep, Money, MapId, PosX, PosY) VALUES (src.PlayerId, src.CurrentHP, src.CurrentPep, src.Money, src.MapId, src.PosX, src.PosY);")) {
            ps.setString(1, playerId);
            ps.setInt(2, currentHP);
            ps.setInt(3, currentPep);
            ps.setLong(4, money);
            ps.setString(5, mapId);
            ps.setInt(6, posX);
            ps.setInt(7, posY);
            ps.executeUpdate();
        }
    }
}
