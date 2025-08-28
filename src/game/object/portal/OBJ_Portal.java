package game.object.portal;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Objects;

import javax.imageio.ImageIO;

import game.object.SuperObject;

/**
 * A simple portal that teleports the player to another map.
 */
public class OBJ_Portal extends SuperObject {
    private final String targetMap;
    private final int targetX;
    private final int targetY;

    public OBJ_Portal(String targetMap, int targetX, int targetY) {
        this.targetMap = targetMap;
        this.targetX = targetX;
        this.targetY = targetY;
        setName("Portal");
        try {
            BufferedImage img = ImageIO.read(
                    Objects.requireNonNull(getClass().getResourceAsStream("/data/tile/aaa10.png")));
            setImage(img);
            setCollision(true);
            setCollisionArea(new Rectangle(0, 0, 48, 48));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getTargetMap() { return targetMap; }
    public int getTargetX() { return targetX; }
    public int getTargetY() { return targetY; }
}
