package client.net;

import game.entity.Player;
import game.entity.item.Item;

/**
 * Abstraction for client to interact with server-side persistence.
 * Actual network implementation can replace the local one in the future.
 */
public interface ServerApi {
    void savePlayer(Player p);
    void savePlayerRuntime(Player p);
    boolean loadPlayer(Player p);
    void insertItem(Item item);

    static ServerApi getInstance() {
        return LocalServerApi.INSTANCE;
    }
}
