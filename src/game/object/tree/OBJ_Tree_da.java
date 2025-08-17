package game.object.tree;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Objects;

import javax.imageio.ImageIO;

import game.object.SuperObject;

public class OBJ_Tree_da extends SuperObject {
    public OBJ_Tree_da() {
        setName("Tree_da");

        try {
            BufferedImage img = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/data/tile/Tree_da.png")));
            setImage(img);
            setCollision(true);
            setDrawOffsetY(10); //hình ảnh cao tính từ gốc
            setCollisionArea(new Rectangle( 72, 150, 48, 32));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

