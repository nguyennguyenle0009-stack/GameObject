package game.entity.monster;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import game.entity.item.Item;
import game.entity.item.LootItem;
import game.enums.Attr;
import game.main.GamePanel;
import game.util.CameraHelper;

/** Simple wandering slime. */
public class GreenSlime extends Monster {
    public GreenSlime(GamePanel gp) {
        super(gp);
        setSpeed(1);
        setDirection("down");
        setScaleEntityX(gp.getTileSize());
        setScaleEntityY(gp.getTileSize());
        setCollisionArea(new Rectangle(8, 16, 32, 32));
        atts().set(Attr.HEALTH, 10);
        maxHealth = 10;
        attackDamage = 1;
        attackArea = new Rectangle(0, 0, gp.getTileSize(), gp.getTileSize());
    }

    @Override
    public void draw(Graphics2D g2, GamePanel gp) {
        Point screenPos = CameraHelper.worldToScreen(getWorldX(), getWorldY(), gp);
        g2.setColor(Color.GREEN);
        g2.fillOval(screenPos.x, screenPos.y, getScaleEntityX(), getScaleEntityY());
    }

    @Override
    protected Item[] createDropItems() {
        return new Item[] {
            new LootItem("Chất nhầy", "", "/data/item/elixir/HealthPotion.png", 1, 99)
        };
    }
}
