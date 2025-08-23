package game.entity.monster;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Random;

import game.enums.Attr;

import game.entity.GameActor;
import game.main.GamePanel;
import game.util.CameraHelper;

/**
 * Simple wandering monster. Damages the player on contact.
 */
public class GreenSlime extends GameActor {
    private final Random random = new Random();
    private final Rectangle attackArea;
    private int attackCooldown = 0;
    private static final int ATTACK_INTERVAL = 60;

    public GreenSlime(GamePanel gp) {
        super(gp);
        setSpeed(1);
        setDirection("down");
        setScaleEntityX(gp.getTileSize());
        setScaleEntityY(gp.getTileSize());
        setCollisionArea(new Rectangle(8, 16, 32, 32));
        atts().set(Attr.HEALTH, 10);
        attackArea = new Rectangle(-8, -8, gp.getTileSize() + 16, gp.getTileSize() + 16);
    }

    @Override
    public void update() {
        setAction();
        checkCollision();
        moveIfCollisionNotDetected();
        attackPlayer();
        checkAndChangeSpriteAnimation();
    }

    private void setAction() {
        setActionLockCounter(getActionLockCounter() + 1);
        if (getActionLockCounter() >= 120) {
            int i = random.nextInt(100) + 1;
            if (i <= 25) setDirection("up");
            else if (i <= 50) setDirection("down");
            else if (i <= 75) setDirection("left");
            else setDirection("right");
            setActionLockCounter(0);
        }
    }

    @Override
    public void draw(Graphics2D g2, GamePanel gp) {
        Point screenPos = CameraHelper.worldToScreen(getWorldX(), getWorldY(), gp);
        g2.setColor(Color.GREEN);
        g2.fillOval(screenPos.x, screenPos.y, getScaleEntityX(), getScaleEntityY());
        if (gp.keyH.isDrawRect()) {
            g2.setColor(Color.RED);
            g2.drawRect(screenPos.x + attackArea.x, screenPos.y + attackArea.y,
                    attackArea.width, attackArea.height);
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        // not used
    }

    private void attackPlayer() {
        if (attackCooldown > 0) {
            attackCooldown--;
            return;
        }

        Rectangle attackZone = new Rectangle(
                getWorldX() + attackArea.x,
                getWorldY() + attackArea.y,
                attackArea.width,
                attackArea.height);

        Rectangle playerArea = new Rectangle(
                gp.getPlayer().getWorldX() + gp.getPlayer().getCollisionArea().x,
                gp.getPlayer().getWorldY() + gp.getPlayer().getCollisionArea().y,
                gp.getPlayer().getCollisionArea().width,
                gp.getPlayer().getCollisionArea().height);

        if (attackZone.intersects(playerArea)) {
            gp.getPlayer().takeDamage(1);
            attackCooldown = ATTACK_INTERVAL;
        }
    }
}
