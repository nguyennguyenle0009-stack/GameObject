package client.cache;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import client.net.ServerApi;
import game.entity.Player;

/**
 * Simple in-memory cache for a player's attributes and inventory.
 * Loads data at startup, flushes to the database every 10 minutes
 * and again when the game exits.
 */
public class PlayerCache implements AutoCloseable {
    private final Player player;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });

    public PlayerCache(Player player) {
        this.player = player;
    }

    /** Load player data into memory. Returns true if an existing record was found. */
    public boolean load() {
        return ServerApi.getInstance().loadPlayer(player);
    }

    /** Start periodic flushes to the database every 10 minutes. */
    public void start() {
        scheduler.scheduleAtFixedRate(player::saveState, 10, 10, TimeUnit.MINUTES);
    }

    /** Flush cached state and stop the scheduler. */
    @Override
    public void close() {
        scheduler.shutdownNow();
        player.saveState();
    }
}
