package game.db;

import java.sql.*;

/** DAO for map information and permissions. */
public class MapDao {

    /** Insert or update a map definition. */
    public void upsertMap(String mapId, String name, String info) throws ClassNotFoundException, SQLException {
        try (Connection conn = DBAccount.getConnectDB();
             PreparedStatement ps = conn.prepareStatement(
                     "MERGE dbo.Maps AS t " +
                     "USING (SELECT ? AS MapId, ? AS Name, ? AS Info) AS s " +
                     "ON t.MapId = s.MapId " +
                     "WHEN MATCHED THEN UPDATE SET Name = s.Name, Info = s.Info " +
                     "WHEN NOT MATCHED THEN INSERT (MapId, Name, Info) VALUES (s.MapId, s.Name, s.Info);")) {
            ps.setString(1, mapId);
            ps.setString(2, name);
            ps.setString(3, info);
            ps.executeUpdate();
        }
    }

    /** Grant or update permission for a player on a map. */
    public void grantPermission(String mapId, String playerId, boolean canEnter, boolean canManage)
            throws ClassNotFoundException, SQLException {
        try (Connection conn = DBAccount.getConnectDB();
             PreparedStatement ps = conn.prepareStatement(
                     "MERGE dbo.MapPermissions AS t " +
                     "USING (SELECT ? AS MapId, ? AS PlayerId, ? AS CanEnter, ? AS CanManage) AS s " +
                     "ON t.MapId = s.MapId AND t.PlayerId = s.PlayerId " +
                     "WHEN MATCHED THEN UPDATE SET CanEnter = s.CanEnter, CanManage = s.CanManage " +
                     "WHEN NOT MATCHED THEN INSERT (MapId, PlayerId, CanEnter, CanManage) VALUES (s.MapId, s.PlayerId, s.CanEnter, s.CanManage);")) {
            ps.setString(1, mapId);
            ps.setString(2, playerId);
            ps.setBoolean(3, canEnter);
            ps.setBoolean(4, canManage);
            ps.executeUpdate();
        }
    }
}
