package game.object.house;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Objects;

import javax.imageio.ImageIO;

import game.object.SuperObject;

public class OBJ_House_1 extends SuperObject {
	    public OBJ_House_1() {
	        setName("House_1");

	        try {
	            BufferedImage img = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/data/tile/house.png")));
	            setImage(img);
	            setCollision(true);

	            setCollisionArea(new Rectangle( 0, 144, 144, 48));
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	
}
