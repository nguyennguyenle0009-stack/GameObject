package game.db;

import java.sql.*;

/** DAO for {@code PlayerRuntime}. */
public class PlayerRuntimeDao {

    /** DTO for runtime stats. */
    public static class RuntimeStats {
        public int currentHP;
        public int currentPep;
        public long money;
    }

    public RuntimeStats load(String playerId) throws ClassNotFoundException, SQLException {
        try (Connection conn = DBAccount.getConnectDB();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT CurrentHP, CurrentPep, Money FROM dbo.PlayerRuntime WHERE PlayerId = ?")) {
            ps.setString(1, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    RuntimeStats rt = new RuntimeStats();
                    rt.currentHP = rs.getInt("CurrentHP");
                    rt.currentPep = rs.getInt("CurrentPep");
                    rt.money = rs.getLong("Money");
                    return rt;
                }
            }
        }
        return null;
    }

    public void upsert(String playerId, int currentHP, int currentPep, long money) throws ClassNotFoundException, SQLException {
        try (Connection conn = DBAccount.getConnectDB();
             PreparedStatement ps = conn.prepareStatement(
                     "MERGE dbo.PlayerRuntime AS target " +
                     "USING (SELECT ? AS PlayerId, ? AS CurrentHP, ? AS CurrentPep, ? AS Money) AS src " +
                     "ON target.PlayerId = src.PlayerId " +
                     "WHEN MATCHED THEN UPDATE SET CurrentHP=src.CurrentHP, CurrentPep=src.CurrentPep, Money=src.Money " +
                     "WHEN NOT MATCHED THEN INSERT (PlayerId, CurrentHP, CurrentPep, Money) VALUES (src.PlayerId, src.CurrentHP, src.CurrentPep, src.Money);")) {
            ps.setString(1, playerId);
            ps.setInt(2, currentHP);
            ps.setInt(3, currentPep);
            ps.setLong(4, money);
            ps.executeUpdate();
        }
    }
}
