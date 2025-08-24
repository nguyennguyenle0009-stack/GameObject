package game.object;

import game.entity.Entity;
import game.entity.animal.cat.Cat_yellow;
import game.entity.monster.GreenSlime;
import game.entity.monster.MonsterZone;
import game.entity.monster.Orc;
import game.entity.monster.SkeletonLord;
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

        gp.getObjects().add(tree1);
//        gp.getObjects()[1] = tree;
        gp.getObjects().add(house);
    }
    
    public void setEntity(){
        Entity cat1 = new Cat_yellow(gp);
        cat1.setWorldX(7 * 48);
        cat1.setWorldY(7 * 48);
        
        Entity cat2 = new Cat_yellow(gp);
        cat2.setWorldX(6 * 48);
        cat2.setWorldY(7 * 48);
        gp.getNpcs().add(cat1);
        gp.getNpcs().add(cat2);
    }
    
    /**
     * Khởi tạo các quái vật trong bản đồ.
     */
    public void setMonsters(){
        // Slime cơ bản
        Entity slime = new GreenSlime(gp);
        slime.setWorldX(10 * gp.getTileSize());
        slime.setWorldY(10 * gp.getTileSize());
        gp.getMonsters().add(slime);

        // Skeleton Lord sử dụng sprite mới
        Entity skeleton = new SkeletonLord(gp);
        skeleton.setWorldX(14 * gp.getTileSize());
        skeleton.setWorldY(12 * gp.getTileSize());
        gp.getMonsters().add(skeleton);
    }
    
    /**
     * Khởi tạo các khu vực sinh quái.
     */
    public void setMonsterZones() {
        MonsterZone zone1 = new MonsterZone(gp,
                10, 2, 20, 10,
                () -> new Orc(gp),
                2,
                1,
                true, // bật cờ quái vật xuất hiện trong khu vực spawn
                false); // bật cờ quái vật tấn công lẫn nhau
        MonsterZone zone2 = new MonsterZone(gp,
                2, 20, 10, 30,
                () -> new Orc(gp),
                3,
                2,
                false,
                true);
        gp.getMonsterZones().add(zone1);
        gp.getMonsterZones().add(zone2);
    }
}