package game.object;

import java.awt.Rectangle;

import game.entity.item.Item;
import game.main.GamePanel;
import game.util.UtilityTool;

/** Object representing an item dropped on the ground. */
public class DroppedItem extends SuperObject {
    private final Item item;
    private final long dropTime;
    private static final long LIFETIME_MS = 120000; // 2 minutes

    public DroppedItem(Item item, int worldX, int worldY, GamePanel gp) {
        this.item = item;
        setWorldX(worldX);
        setWorldY(worldY);
        setImage(UtilityTool.scaleImage(item.getIcon(), gp.getTileSize(), gp.getTileSize()));
        setCollisionArea(new Rectangle(0, 0, gp.getTileSize(), gp.getTileSize()));
        setCollisionDefaultX(0);
        setCollisionDefaultY(0);
        dropTime = System.currentTimeMillis();
    }

    public Item getItem() { return item; }

    /** Check if the item has existed longer than its lifetime. */
    public boolean isExpired() {
        return System.currentTimeMillis() - dropTime > LIFETIME_MS;
    }

    /** Checks if a world coordinate lies within this item. */
    public boolean contains(int worldX, int worldY) {
        Rectangle r = new Rectangle(getWorldX(), getWorldY(),
                getCollisionArea().width, getCollisionArea().height);
        return r.contains(worldX, worldY);
    }
}
