package game.object;

import java.awt.Rectangle;

import game.entity.item.Item;
import game.main.GamePanel;
import game.util.UtilityTool;

/** SuperObject representing an item dropped on the ground. */
public class DroppedItem extends SuperObject {
    private final Item item;

    public DroppedItem(GamePanel gp, Item item) {
        this.item = item;
        setImage(UtilityTool.scaleImage(item.getIcon(), gp.getTileSize(), gp.getTileSize()));
        setCollision(false);
        setScaleObjectWidth(1);
        setScaleObjectHeight(1);
        setCollisionArea(new Rectangle(0, 0, gp.getTileSize(), gp.getTileSize()));
    }

    public Item getItem() {
        return item;
    }
}
