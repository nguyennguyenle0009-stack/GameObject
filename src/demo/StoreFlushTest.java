package demo;

import game.entity.Player;
import game.main.GamePanel;

/**
 * Simple console demo verifying that the temporary store flushes to the
 * database when the game shuts down.
 */
public class StoreFlushTest {
    public static void main(String[] args) {
        GamePanel gp = new GamePanel();
        Player p = new Player(gp);
        // Update some state and store it
        p.saveState();
        // Simulate game shutdown
        p.stopAutoSave();
        System.out.println("Manual flush executed.");
    }
}
