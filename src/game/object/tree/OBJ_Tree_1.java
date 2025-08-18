package game.object.tree;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Objects;

import javax.imageio.ImageIO;

import game.object.SuperObject;

public class OBJ_Tree_1 extends SuperObject {
    public OBJ_Tree_1() {
        setName("Tree_1");
        try {
            BufferedImage img = ImageIO.read(
            		Objects.requireNonNull(getClass()
            				.getResourceAsStream("/data/tile/object/Tree_1.png")));
            setImage(img);
            setCollision(true);
            setDrawOffsetY(24); //hình ảnh cao tính từ gốc
            setCollisionArea(new Rectangle( 14, 52, 20, 20));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}