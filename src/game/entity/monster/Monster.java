package game.entity.monster;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Random;

import game.entity.GameActor;
import game.enums.Attr;
import game.main.GamePanel;
import game.util.CameraHelper;

/**
 * Base class for all monsters. Handles health, damage and drawing the health bar.
 */
public abstract class Monster extends GameActor {
    /** Reference to game panel for access to world data. */
    protected final GamePanel gp;
    /** Random helper for subclasses (drop rate, movement ...). */
    protected final Random random = new Random();
    /** Maximum health of the monster. */
    protected final int maxHealth;
    /** Counter to display health bar (in frames). */
    protected int healthBarCounter = 0;
    /** Duration the health bar stays visible after taking damage. */
    protected static final int HEALTH_BAR_DISPLAY_TIME = 120; // 2 seconds at 60 FPS
    /** Flag to indicate monster is still alive. */
    private boolean alive = true;

    public Monster(GamePanel gp, int maxHealth) {
        super(gp);
        this.gp = gp;
        this.maxHealth = maxHealth;
        // initialize health attribute
        atts().set(Attr.HEALTH, maxHealth);
    }

    /**
     * Apply damage to the monster.
     * Shows the health bar and triggers item drop when dead.
     */
    public void takeDamage(int amount) {
        atts().add(Attr.HEALTH, -amount);
        healthBarCounter = HEALTH_BAR_DISPLAY_TIME;
        if (atts().get(Attr.HEALTH) <= 0) {
            alive = false;
            dropItem();
        }
    }

    /**
     * Item drop logic implemented by each monster type.
     */
    protected abstract void dropItem();

    /**
     * Update common monster logic.
     * Subclasses should call super.update() within their update method.
     */
    @Override
    public void update() {
        if (healthBarCounter > 0) {
            healthBarCounter--;
        }
    }

    /** Draw health bar above the monster. */
    protected void drawHealthBar(Graphics2D g2, GamePanel gp) {
        if (healthBarCounter > 0) {
            int barWidth = gp.getTileSize();
            int barHeight = 4;
            int currentHealth = atts().get(Attr.HEALTH);
            float percent = currentHealth / (float) maxHealth;
            int healthWidth = (int) (barWidth * percent);
            Point screenPos = CameraHelper.worldToScreen(getWorldX(), getWorldY(), gp);
            int x = screenPos.x;
            int y = screenPos.y - 6; // draw slightly above the head
            g2.setColor(Color.RED);
            g2.fillRect(x, y, barWidth, barHeight);
            g2.setColor(Color.GREEN);
            g2.fillRect(x, y, healthWidth, barHeight);
            g2.setColor(Color.BLACK);
            g2.drawRect(x, y, barWidth, barHeight);
        }
    }

    /** Check whether monster is alive. */
    public boolean isAlive() { return alive; }
}
