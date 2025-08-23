package game.ui;

import java.awt.Color;
import java.awt.Graphics2D;

import game.enums.Attr;
import game.main.GamePanel;

/**
 * Heads-up display showing player stats like health, energy and experience.
 */
public class GameHUD {
    private final GamePanel gp;

    // COLORS
    private static final Color HP_FILL = new Color(210, 50, 50);
    private static final Color MP_FILL = new Color(60, 120, 230);
    private static final Color EXP_FILL = new Color(250, 150, 40);
    private static final Color BAR_BACK = new Color(30, 30, 30, 180);
    private static final Color BAR_BORDER = new Color(0, 0, 0, 180);

    private static final int BAR_WIDTH = 200;
    private static final int BAR_HEIGHT = 20;
    private static final int BAR_GAP = 10;

    public GameHUD(GamePanel gp) {
        this.gp = gp;
    }

    public void draw(Graphics2D g2) {
        int x = 20;
        int y = 20;
        drawBar(g2, x, y, gp.getPlayer().atts().get(Attr.HEALTH), 100, HP_FILL);
        y += BAR_HEIGHT + BAR_GAP;
        drawBar(g2, x, y, gp.getPlayer().atts().get(Attr.PEP), 100, MP_FILL);
        y += BAR_HEIGHT + BAR_GAP;
        drawBar(g2, x, y, gp.getPlayer().atts().get(Attr.SPIRIT), 100, EXP_FILL);
    }

    private void drawBar(Graphics2D g2, int x, int y, int value, int max, Color fill) {
        g2.setColor(BAR_BACK);
        g2.fillRect(x, y, BAR_WIDTH, BAR_HEIGHT);
        int width = (int) (Math.max(0, Math.min(value, max)) / (double) max * BAR_WIDTH);
        g2.setColor(fill);
        g2.fillRect(x, y, width, BAR_HEIGHT);
        g2.setColor(BAR_BORDER);
        g2.drawRect(x, y, BAR_WIDTH, BAR_HEIGHT);
    }
}
