package game.entity.monster;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Random;

import game.entity.GameActor;
import game.enums.Attr;
import game.main.GamePanel;
import game.util.CameraHelper;

/**
 * Simple wandering monster. Damages the player on contact.
 */
public class GreenSlime extends GameActor {
    private final Random random = new Random();
    private final GamePanel gp;

    private final Rectangle attackArea;
    private boolean attacking = false;
    private int attackCounter = 0;
    private int attackCooldown = 0;
    private static final int ATTACK_COOLDOWN = 60;
    private static final int ATTACK_DURATION = 20;
    private final int attackDamage = 1;

    public GreenSlime(GamePanel gp) {
        super(gp);
        this.gp = gp;
        setSpeed(1);
        setDirection("down");
        setScaleEntityX(gp.getTileSize());
        setScaleEntityY(gp.getTileSize());
        setCollisionArea(new Rectangle(8, 16, 32, 32));
        atts().set(Attr.HEALTH, 10);
        attackArea = new Rectangle(0, 0, gp.getTileSize(), gp.getTileSize());
    }

    @Override
    public void update() {
        setAction();
        checkCollision();
        moveIfCollisionNotDetected();
        checkAndChangeSpriteAnimation();
        handleAttack();
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

    private void handleAttack() {
        if (attacking) {
            attackCounter++;
            if (attackCounter == 1) {
                physicalAttack();
            }
            if (attackCounter > ATTACK_DURATION) {
                attacking = false;
                attackCounter = 0;
                attackCooldown = ATTACK_COOLDOWN;
            }
        } else {
            if (attackCooldown > 0) attackCooldown--;
            Rectangle attackRect = getAttackRectangle();
            Rectangle playerRect = new Rectangle(
                    gp.getPlayer().getWorldX() + gp.getPlayer().getCollisionArea().x,
                    gp.getPlayer().getWorldY() + gp.getPlayer().getCollisionArea().y,
                    gp.getPlayer().getCollisionArea().width,
                    gp.getPlayer().getCollisionArea().height
            );
            if (attackCooldown == 0 && attackRect.intersects(playerRect)) {
                attacking = true;
            }
        }
    }

    private Rectangle getAttackRectangle() {
        int attackX = getWorldX();
        int attackY = getWorldY();
        switch (getDirection()) {
            case "up":
                attackY -= attackArea.height;
                break;
            case "down":
                attackY += getScaleEntityY();
                break;
            case "left":
                attackX -= attackArea.width;
                break;
            case "right":
                attackX += getScaleEntityX();
                break;
        }
        return new Rectangle(attackX, attackY, attackArea.width, attackArea.height);
    }

    private void physicalAttack() {
        Rectangle attackRect = getAttackRectangle();
        Rectangle playerRect = new Rectangle(
                gp.getPlayer().getWorldX() + gp.getPlayer().getCollisionArea().x,
                gp.getPlayer().getWorldY() + gp.getPlayer().getCollisionArea().y,
                gp.getPlayer().getCollisionArea().width,
                gp.getPlayer().getCollisionArea().height
        );
        if (attackRect.intersects(playerRect)) {
            gp.getPlayer().atts().add(Attr.HEALTH, -attackDamage);
            gp.getUi().triggerDamageEffect();
        }
    }

    @Override
    public void draw(Graphics2D g2, GamePanel gp) {
        Point screenPos = CameraHelper.worldToScreen(getWorldX(), getWorldY(), gp);
        g2.setColor(Color.GREEN);
        g2.fillOval(screenPos.x, screenPos.y, getScaleEntityX(), getScaleEntityY());
        if (attacking) {
            Rectangle attackRect = getAttackRectangle();
            Point attackScreen = CameraHelper.worldToScreen(attackRect.x, attackRect.y, gp);
            g2.setColor(Color.RED);
            g2.drawRect(attackScreen.x, attackScreen.y, attackRect.width, attackRect.height);
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        // not used
    }
}
