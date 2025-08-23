package game.object;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.Objects;

import javax.imageio.ImageIO;

import game.main.GamePanel;
import game.util.UtilityTool;

/**
 * Simple dropped item that represents a health potion.
 * Player can pick it up when colliding with it.
 */
public class DroppedHealthPotion extends SuperObject {
    /**
     * Creates a health potion object at tile size of the game panel.
     */
    public DroppedHealthPotion(GamePanel gp) {
        setName("HealthPotion");
        try {
            setImage(UtilityTool.scaleImage(
                ImageIO.read(Objects.requireNonNull(
                    getClass().getResourceAsStream("/data/item/elixir/HealthPotion.png"))),
                gp.getTileSize(), gp.getTileSize()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setCollisionArea(new Rectangle(0, 0, gp.getTileSize(), gp.getTileSize()));
        setCollision(true);
        setScaleObjectWidth(1);
        setScaleObjectHeight(1);
    }
}
