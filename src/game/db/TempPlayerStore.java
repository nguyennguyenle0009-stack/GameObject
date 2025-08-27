package game.db;

import game.entity.Player;

/**
 * Simple in-memory cache for player data that periodically persists to the
 * database. Data is written to this cache first and flushed to the actual
 * database on a schedule or when the game shuts down.
 */
public class TempPlayerStore {

    private Player cachedPlayer;
    private final PlayerDAO dao = new PlayerDAO();

    /** Save the latest player snapshot into temporary memory. */
    public synchronized void update(Player p) {
        this.cachedPlayer = p;
    }

    /** Flush cached data to the database if available. */
    public synchronized void flush() {
        if (cachedPlayer != null) {
            dao.save(cachedPlayer);
        }
    }
}
