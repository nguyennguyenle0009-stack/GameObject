package game.object.tree;

import java.util.Objects;

import javax.imageio.ImageIO;

import game.object.SuperObject;

public class OBJ_Tree_da extends SuperObject {
	public OBJ_Tree_da() {
		setName("Tree_da");
		
		try {
			setImage(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/data/tile/Tree_da.png"))));
			setCollision(true);
		} catch (Exception e) {

		}
	}
}
