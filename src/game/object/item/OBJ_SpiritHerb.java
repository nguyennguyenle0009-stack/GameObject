package game.object.item;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Objects;
import javax.imageio.ImageIO;

import game.object.SuperObject;

public class OBJ_SpiritHerb extends SuperObject {
    public OBJ_SpiritHerb() {
        setName("SpiritHerb");
        try {
            BufferedImage img = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/data/tile/aa.png")));
            setImage(img);
            setCollision(false);
            setCollisionArea(new Rectangle(0, 0, 48, 48));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
