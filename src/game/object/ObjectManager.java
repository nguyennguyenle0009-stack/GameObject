package game.object;

import game.entity.Entity;
import game.entity.animal.cat.Cat_yellow;
import game.main.GamePanel;
import game.object.house.OBJ_House_1;
import game.object.tree.OBJ_Tree_1;
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
        house.setWorldX(6 * gp.getTileSize()); //Vị trí trên trục X
        house.setWorldY(3 * gp.getTileSize()); //Vị trí trên trục Y
        house.setScaleObjectWidth(4);	//Tỉ lệ chiều cao * 48
        house.setScaleObjectHeight(4);	//Tỉ lệ chiều rộng * 48
        house.setIndex(1);
        
        SuperObject tree1 = new OBJ_Tree_1();
        tree1.setWorldX(6 * gp.getTileSize()); //Vị trí trên trục X
        tree1.setWorldY(1 * gp.getTileSize()); //Vị trí trên trục Y
        tree1.setScaleObjectWidth(1);	//Tỉ lệ chiều cao * 48
        tree1.setScaleObjectHeight(2);	//Tỉ lệ chiều rộng * 48
        tree1.setIndex(2);

        gp.getObjects()[0] = tree1;
//        gp.getObjects()[1] = tree;
        gp.getObjects()[2] = house;
    }
    
    public void setEntity(){
        Entity cat1 = new Cat_yellow(gp);
        cat1.setWorldX(7 * 48);
        cat1.setWorldY(7 * 48);
        
        Entity cat2 = new Cat_yellow(gp);
        cat2.setWorldX(6 * 48);
        cat2.setWorldY(7 * 48);
        gp.getNpc()[0] = cat1;
        gp.getNpc()[1] = cat2;
    }
}

