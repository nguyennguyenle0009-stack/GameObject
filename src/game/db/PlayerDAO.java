package game.db;

import game.entity.Player;

/**
 * Data-access object responsible for persisting and loading {@link Player}
 * state using the normalized table layout.
 * <p>
 * Only a minimal skeleton is provided; the actual SQL queries should populate
 * and read the tables defined in {@code sql/game_db_core_schema.sql}.
 */
public class PlayerDAO {

    /** Persist the player state to the database. */
    public void save(Player p) {
        // TODO: implement INSERT/UPDATE statements for Players and related tables
    }

    /**
     * Load the player state from the database.
     *
     * @return {@code true} if data existed and was loaded
     */
    public boolean load(Player p) {
        // TODO: query tables and populate player fields
        return false;
    }
}

