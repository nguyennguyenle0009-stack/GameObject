package game.object;

import java.awt.image.BufferedImage;
import java.util.Objects;
import javax.imageio.ImageIO;

/** Portal object that teleports the player to another map. */
public class OBJ_Portal extends SuperObject {
    public OBJ_Portal() {
        setName("Portal");
        try {
            BufferedImage img = ImageIO.read(Objects.requireNonNull(
                getClass().getResourceAsStream("/data/tile/aaa11.png")));
            setImage(img);
            setCollision(false);
            setScaleObjectWidth(1);
            setScaleObjectHeight(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
