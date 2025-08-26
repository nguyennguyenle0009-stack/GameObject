package game.entity.item;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.Point;

import game.entity.Entity;
import game.main.GamePanel;
import game.util.CameraHelper;
import game.util.UtilityTool;

/**
 * Item nằm trên mặt đất có thể nhặt được.
 */
public class GroundItem extends Entity {
    private final Item item;
    private final BufferedImage image;
    private final long spawnTime;
    private static final long LIFE_TIME = 120_000; // 2 phút

    public GroundItem(GamePanel gp, Item item, int x, int y) {
        super(gp);
        this.item = item;
        this.image = UtilityTool.scaleImage(item.getIcon(), gp.getTileSize(), gp.getTileSize());
        setWorldX(x);
        setWorldY(y);
        setCollisionArea(new Rectangle(0,0,gp.getTileSize(), gp.getTileSize()));
        spawnTime = System.currentTimeMillis();
    }

    @Override
    public void update() {
        if (System.currentTimeMillis() - spawnTime > LIFE_TIME) {
            gp.getGroundItems().remove(this);
        }
    }

    @Override
    public void draw(Graphics2D g2) {}

    @Override
    public void draw(Graphics2D g2, GamePanel gp) {
        Point screen = CameraHelper.worldToScreen(getWorldX(), getWorldY(), gp);
        g2.drawImage(image, screen.x, screen.y, null);
    }

    public Item getItem() { return item; }
}
