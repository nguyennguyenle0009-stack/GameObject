package game.ui;

import java.awt.Color;
import java.awt.Graphics2D;

import game.entity.Player;
import game.enums.Attr;
import game.main.GamePanel;

/**
 * Heads-up display that renders the player's vital bars.
 */
public class GameHUD {

    private final GamePanel gp;

    // COLOR
    private static final Color HP_FILL = new Color(210, 50, 50);
    private static final Color MP_FILL = new Color(60, 120, 230);
    private static final Color EXP_FILL = new Color(250, 150, 40);
    private static final Color BAR_BACK = new Color(30, 30, 30, 180);
    private static final Color BAR_BORDER = new Color(0, 0, 0, 180);

    private static final int MAX_HEALTH = 100;
    private static final int MAX_PEP = 100;
    private static final int MAX_SPIRIT = 100;

    public GameHUD(GamePanel gp) {
        this.gp = gp;
    }

    public void draw(Graphics2D g2) {
        Player p = gp.getPlayer();
        int barWidth = gp.getTileSize() * 4;
        int barHeight = gp.getTileSize() / 3;
        int x = gp.getTileSize();
        int y = gp.getTileSize() / 2;

        drawBar(g2, x, y, barWidth, barHeight,
                p.atts().get(Attr.HEALTH), MAX_HEALTH, HP_FILL);
        y += barHeight + 6;
        drawBar(g2, x, y, barWidth, barHeight,
                p.atts().get(Attr.PEP), MAX_PEP, MP_FILL);
        y += barHeight + 6;
        drawBar(g2, x, y, barWidth, barHeight,
                p.atts().get(Attr.SPIRIT), MAX_SPIRIT, EXP_FILL);
    }

    private void drawBar(Graphics2D g2, int x, int y, int w, int h,
                         int value, int max, Color fill) {
        g2.setColor(BAR_BACK);
        g2.fillRect(x, y, w, h);
        int filled = (int) (w * Math.max(0, Math.min(value, max)) / (double) max);
        g2.setColor(fill);
        g2.fillRect(x, y, filled, h);
        g2.setColor(BAR_BORDER);
        g2.drawRect(x, y, w, h);
    }
}

