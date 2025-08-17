package game.object;

import game.main.GamePanel;
import game.object.tree.OBJ_Tree_da;

public class ObjectManager {
	private final GamePanel gp;
	public ObjectManager(GamePanel gp) {
		this.gp = gp;
	}
	public void setObject() {
		gp.getObjects()[0] = new OBJ_Tree_da(gp);
		gp.getObjects()[0].setWorldX(5 * gp.getTileSize());
		gp.getObjects()[0].setWorldY(5 * gp.getTileSize());
		gp.getObjects()[0].setIndex(0);
	}
}
