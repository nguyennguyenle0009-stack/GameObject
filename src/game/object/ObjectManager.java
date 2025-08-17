package game.object;

import game.main.GamePanel;
import game.object.house.OBJ_House_1;
import game.object.tree.OBJ_Tree_da;

public class ObjectManager {
    private final GamePanel gp; 
    public ObjectManager(GamePanel gp) { this.gp = gp; }

    public void setObject() {
        SuperObject tree = new OBJ_Tree_da();
        tree.setWorldX(5 * gp.getTileSize()); //Vị trí trên trục X
        tree.setWorldY(5 * gp.getTileSize()); //Vị trí trên trục Y
        tree.setScaleObjectWidth(4);	//Tỉ lệ chiều cao * 48
        tree.setScaleObjectHeight(4);	//Tỉ lệ chiều rộng * 48
        tree.setIndex(0);
        
        SuperObject house = new OBJ_House_1();
        house.setWorldX(3 * gp.getTileSize()); //Vị trí trên trục X
        house.setWorldY(4 * gp.getTileSize()); //Vị trí trên trục Y
        house.setScaleObjectWidth(4);	//Tỉ lệ chiều cao * 48
        house.setScaleObjectHeight(4);	//Tỉ lệ chiều rộng * 48
        house.setIndex(1);

        gp.getObjects()[0] = tree;
        gp.getObjects()[1] = house;
    }
}

