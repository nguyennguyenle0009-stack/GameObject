package game.db;

import game.entity.attributes.Attributes;
import game.enums.Attr;

import java.sql.*;

/** DAO for {@code PlayerBaseStats}. */
public class PlayerBaseStatsDao {

    /** DTO for base stats. */
    public static class BaseStats {
        public int atk;
        public int def;
        public int healthMax;
        public int pepMax;
        public int sould;
        public int spirit;
        public int spiritMax;
        public int strength;
    }

    /** Load base stats for given player. */
    public BaseStats load(String playerId) throws ClassNotFoundException, SQLException {
        try (Connection conn = DBAccount.getConnectDB();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT Atk, Def, HealthMax, PepMax, Sould, Spirit, SpiritMax, Strength " +
                     "FROM dbo.PlayerBaseStats WHERE PlayerId = ?")) {
            ps.setString(1, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BaseStats bs = new BaseStats();
                    bs.atk = rs.getInt("Atk");
                    bs.def = rs.getInt("Def");
                    bs.healthMax = rs.getInt("HealthMax");
                    bs.pepMax = rs.getInt("PepMax");
                    bs.sould = rs.getInt("Sould");
                    bs.spirit = rs.getInt("Spirit");
                    bs.spiritMax = rs.getInt("SpiritMax");
                    bs.strength = rs.getInt("Strength");
                    return bs;
                }
            }
        }
        return null;
    }

    /** Upsert base stats for player. */
    public void upsert(String playerId, Attributes atts) throws ClassNotFoundException, SQLException {
        try (Connection conn = DBAccount.getConnectDB();
             PreparedStatement ps = conn.prepareStatement(
                     "MERGE dbo.PlayerBaseStats AS target " +
                     "USING (SELECT ? AS PlayerId, ? AS Atk, ? AS Def, ? AS HealthMax, ? AS PepMax, ? AS Sould, ? AS Spirit, ? AS SpiritMax, ? AS Strength) AS src " +
                     "ON target.PlayerId = src.PlayerId " +
                     "WHEN MATCHED THEN UPDATE SET Atk=src.Atk, Def=src.Def, HealthMax=src.HealthMax, PepMax=src.PepMax, Sould=src.Sould, Spirit=src.Spirit, SpiritMax=src.SpiritMax, Strength=src.Strength " +
                     "WHEN NOT MATCHED THEN INSERT (PlayerId, Atk, Def, HealthMax, PepMax, Sould, Spirit, SpiritMax, Strength) VALUES (src.PlayerId, src.Atk, src.Def, src.HealthMax, src.PepMax, src.Sould, src.Spirit, src.SpiritMax, src.Strength);")) {
            ps.setString(1, playerId);
            ps.setInt(2, atts.get(Attr.ATTACK));
            ps.setInt(3, atts.get(Attr.DEF));
            ps.setInt(4, atts.getMax(Attr.HEALTH));
            ps.setInt(5, atts.getMax(Attr.PEP));
            ps.setInt(6, atts.get(Attr.SOULD));
            ps.setInt(7, atts.get(Attr.SPIRIT));
            ps.setInt(8, atts.getMax(Attr.SPIRIT));
            ps.setInt(9, atts.get(Attr.STRENGTH));
            ps.executeUpdate();
        }
    }
}

