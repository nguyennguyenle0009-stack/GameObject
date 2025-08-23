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

    public GreenSlime(GamePanel gp) {
        super(gp);
        setSpeed(1);
        setDirection("down");
        setScaleEntityX(gp.getTileSize());
        setScaleEntityY(gp.getTileSize());
        setCollisionArea(new Rectangle(8, 16, 32, 32));
        atts().set(Attr.HEALTH, 10);
    }

    @Override
    public void update() {
        setAction();
        checkCollision();
        moveIfCollisionNotDetected();
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
    }

    @Override
    public void draw(Graphics2D g2) {
        // not used
    }
}
