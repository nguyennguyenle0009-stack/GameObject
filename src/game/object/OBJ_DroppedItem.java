package game.object;

import java.awt.Rectangle;
import game.entity.item.Item;
import game.main.GamePanel;
import game.util.UtilityTool;

/**
 * Object đại diện cho item rơi trên mặt đất.
 * Item biến mất sau một khoảng thời gian nếu không được nhặt.
 */
public class OBJ_DroppedItem extends SuperObject {
    private static final long LIFETIME = 120_000; // 2 phút

    private final Item item;
    private final long spawnTime;

    public OBJ_DroppedItem(Item item, int worldX, int worldY, GamePanel gp) {
        this.item = item;
        setWorldX(worldX);
        setWorldY(worldY);
        setCollisionArea(new Rectangle(0, 0, gp.getTileSize(), gp.getTileSize()));
        setImage(UtilityTool.scaleImage(item.getIcon(), gp.getTileSize(), gp.getTileSize()));
        setScaleObjectWidth(1);
        setScaleObjectHeight(1);
        this.spawnTime = System.currentTimeMillis();
    }

    public Item getItem() { return item; }

    /** Kiểm tra item đã hết thời gian tồn tại chưa. */
    public boolean isExpired() {
        return System.currentTimeMillis() - spawnTime > LIFETIME;
    }
}
