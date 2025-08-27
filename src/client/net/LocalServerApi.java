package client.net;

import game.entity.Player;
import game.entity.item.Item;
import server.db.ItemDAO;
import server.db.PlayerDAO;

/**
 * Simple in-process implementation of {@link ServerApi} that directly
 * invokes DAO methods. This allows the project to run without a real
 * network layer while keeping client code decoupled from database access.
 */
class LocalServerApi implements ServerApi {
    static final LocalServerApi INSTANCE = new LocalServerApi();
    private LocalServerApi() {}

    @Override
    public void savePlayer(Player p) {
        PlayerDAO.save(p);
    }

    @Override
    public void savePlayerRuntime(Player p) {
        PlayerDAO.saveRuntime(p);
    }

    @Override
    public boolean loadPlayer(Player p) {
        return PlayerDAO.load(p);
    }

    @Override
    public void insertItem(Item item) {
        try {
            ItemDAO.insert(item);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
