package game.db;

import game.entity.skill.CultivationTechnique;
import game.enums.SkillGrade;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** DAO for {@code PlayerTechniques} table. */
public class PlayerSkillDao {

    /** Load all cultivation techniques for a player. */
    public List<CultivationTechnique> load(String playerId) throws ClassNotFoundException, SQLException {
        List<CultivationTechnique> list = new ArrayList<>();
        try (Connection conn = DBAccount.getConnectDB();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT Name, Grade, Level, SpiritPerSecond FROM dbo.PlayerTechniques WHERE PlayerId = ?")) {
            ps.setString(1, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("Name");
                    String gradeStr = rs.getString("Grade");
                    int level = rs.getInt("Level");
                    int sps = rs.getInt("SpiritPerSecond");
                    SkillGrade grade;
                    try {
                        grade = SkillGrade.valueOf(gradeStr);
                    } catch (IllegalArgumentException e) {
                        grade = SkillGrade.HA;
                    }
                    list.add(new CultivationTechnique(name, grade, level, sps));
                }
            }
        }
        return list;
    }

    /** Replace player's techniques with given list. */
    public void replaceAll(String playerId, List<CultivationTechnique> techniques) throws ClassNotFoundException, SQLException {
        try (Connection conn = DBAccount.getConnectDB()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement del = conn.prepareStatement(
                        "DELETE FROM dbo.PlayerTechniques WHERE PlayerId = ?")) {
                    del.setString(1, playerId);
                    del.executeUpdate();
                }
                if (!techniques.isEmpty()) {
                    try (PreparedStatement ins = conn.prepareStatement(
                            "INSERT INTO dbo.PlayerTechniques(PlayerId, Name, Grade, Level, SpiritPerSecond) VALUES(?,?,?,?,?)")) {
                        for (CultivationTechnique t : techniques) {
                            ins.setString(1, playerId);
                            ins.setString(2, t.getName());
                            ins.setString(3, t.getGrade().name());
                            ins.setInt(4, t.getLevel());
                            ins.setInt(5, t.getSpiritPerSecond());
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