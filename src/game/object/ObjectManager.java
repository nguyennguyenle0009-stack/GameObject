package game.object;

import game.main.GamePanel;
import game.object.tree.OBJ_Tree_da;

public class ObjectManager {
	private final GamePanel gp;
	public ObjectManager(GamePanel gp) {
		this.gp = gp;
	}
	public void setObject() {
		gp.getObjects()[0] = new OBJ_Tree_da();
		gp.getObjects()[0].setWorldX(200);
		gp.getObjects()[0].setWorldY(200);
		gp.getObjects()[0].setIndex(0);
	}
}
