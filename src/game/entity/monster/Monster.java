package game.entity.monster;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Random;

import game.entity.GameActor;
import game.enums.Attr;
import game.main.GamePanel;
import game.object.DroppedHealthPotion;
import game.util.CameraHelper;

/**
 * Base class for all monsters. Handles health, damage and item drops.
 */
public abstract class Monster extends GameActor {
    /** Reference to main game panel used for world data */
    protected final GamePanel gp;
    /** Maximum health of the monster */
    private final int maxHealth;
    /** Flag indicating the monster is dead */
    private boolean dead = false;
    /** Show the health bar when true */
    private boolean showHealthBar = false;
    /** Counter to hide the health bar after a delay */
    private int healthBarCounter = 0;
    /** Duration for which the health bar is shown (frames) */
    private static final int HEALTH_BAR_TIME = 120; // 2 seconds at 60 FPS
    /** Random generator for item drop chances */
    protected final Random random = new Random();

    public Monster(GamePanel gp, int maxHealth) {
        super(gp);
        this.gp = gp;
        this.maxHealth = maxHealth;
        atts().set(Attr.HEALTH, maxHealth);
    }

    /**
     * Applies damage to the monster and handles death.
     * @param amount amount of health to subtract
     */
    public void takeDamage(int amount) {
        atts().add(Attr.HEALTH, -amount);
        showHealthBar = true;
        healthBarCounter = HEALTH_BAR_TIME;
        if (atts().get(Attr.HEALTH) <= 0) {
            dead = true;
            dropItem();
        }
    }

    /**
     * @return true if monster has died
     */
    public boolean isDead() { return dead; }

    /**
     * Updates health bar timer each frame.
     */
    protected void updateHealthBar() {
        if (showHealthBar) {
            healthBarCounter--;
            if (healthBarCounter <= 0) {
                showHealthBar = false;
            }
        }
    }

    /**
     * Draws the health bar above the monster when visible.
     */
    protected void drawHealthBar(Graphics2D g2) {
        if (!showHealthBar) return;
        Point screenPos = CameraHelper.worldToScreen(getWorldX(), getWorldY(), gp);
        int barWidth = getScaleEntityX();
        int barHeight = 4;
        double hpPercent = atts().get(Attr.HEALTH) / (double) maxHealth;
        int healthWidth = (int) (barWidth * hpPercent);
        g2.setColor(Color.RED);
        g2.fillRect(screenPos.x, screenPos.y - 10, barWidth, barHeight);
        g2.setColor(Color.GREEN);
        g2.fillRect(screenPos.x, screenPos.y - 10, healthWidth, barHeight);
    }

    /**
     * Called when the monster dies to possibly drop an item.
     */
    protected void dropItem() {
        if (random.nextInt(100) < 30) { // 30% drop chance
            DroppedHealthPotion potion = new DroppedHealthPotion(gp);
            potion.setWorldX(getWorldX());
            potion.setWorldY(getWorldY());
            gp.getObjects().add(potion);
        }
    }
}
