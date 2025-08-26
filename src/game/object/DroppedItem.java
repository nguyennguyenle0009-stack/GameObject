package game.object;

import java.awt.Rectangle;

import game.entity.item.Item;

/**
 * Vật phẩm nằm trên đất sau khi rơi ra.
 */
public class DroppedItem extends SuperObject {
    private final Item item;
    private final long spawnTime = System.currentTimeMillis();

    public DroppedItem(Item it) {
        this.item = it;
        setName(it.getName());
        setImage(it.getIcon());
        setCollisionArea(new Rectangle(0, 0, 32, 32));
    }

    public Item getItem() { return item; }
    public long getSpawnTime() { return spawnTime; }
}
