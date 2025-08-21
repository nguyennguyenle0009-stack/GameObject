package game.object.item;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Objects;

import javax.imageio.ImageIO;

import game.object.SuperObject;

/**
 * Item that grants qi to the player when picked up.
 */
public class OBJ_QiPill extends SuperObject {
    private final int qiAmount;

    public OBJ_QiPill(int qiAmount) {
        this.qiAmount = qiAmount;
        setName("QiPill");
        try {
            BufferedImage img = ImageIO.read(Objects.requireNonNull(
                    getClass().getResourceAsStream("/data/tile/aa2.png")));
            setImage(img);
            setCollision(true);
            setCollisionArea(new Rectangle(8, 8, 32, 32));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getQiAmount() {
        return qiAmount;
    }
}

