package game.ui;

import java.awt.Color;
import java.awt.Graphics2D;

import game.entity.Player;
import game.enums.Attr;
import game.enums.Realm;
import game.main.GamePanel;

public class GameHUD {
	
	private final GamePanel gp;
	
    // COLOR
    private static final Color PURPLE_BG = new Color(64, 40, 90, 200);
    private static final Color PURPLE_BORDER = new Color(140, 100, 180);
    private static final Color NAME_COLOR = Color.BLACK;
    private static final Color REALM_COLOR = new Color(10, 170, 80);
    private static final Color HP_FILL = new Color(210, 50, 50);
    private static final Color MP_FILL = new Color(60, 120, 230);
    private static final Color EXP_FILL = new Color(250, 150, 40);
    private static final Color BAR_BACK = new Color(30, 30, 30, 180);
    private static final Color BAR_BORDER = new Color(0, 0, 0, 180);
    // các giá trị tối đa sẽ lấy trực tiếp từ Attributes nên không cần hằng số

    public GameHUD(GamePanel gp) {
        this.gp = gp;
    }

    public void draw(Graphics2D g2) {
        Player p = gp.getPlayer();
        int barWidth = gp.getTileSize() * 4;
        int barHeight = gp.getTileSize() / 3;
        int margin = 5;

        // draw realm box
        String realmText = p.getRealm().getDisplayName();
        if (p.getRealm() != Realm.PHAM_NHAN) {
            realmText += " tầng " + p.getRealmLevel();
        }
        int boxWidth = g2.getFontMetrics().stringWidth(realmText) + 20;
        int boxHeight = barHeight;
        HUDUtils.drawSubWindow(g2, margin, margin, boxWidth, boxHeight, BAR_BACK, BAR_BORDER);
        g2.setColor(REALM_COLOR);
        g2.drawString(realmText, margin + 10, margin + boxHeight - 6);

        int x = margin + boxWidth + 10;
        int y = margin;

        drawBar(g2, x, y, barWidth, barHeight,
                p.atts().get(Attr.HEALTH), p.atts().getMax(Attr.HEALTH), HP_FILL);
        y += barHeight + 6;
        drawBar(g2, x, y, barWidth, barHeight,
                p.atts().get(Attr.PEP), p.atts().getMax(Attr.PEP), MP_FILL);
        y += barHeight + 6;
        drawBar(g2, x, y, barWidth, barHeight,
                p.atts().get(Attr.SPIRIT), p.atts().getMax(Attr.SPIRIT), EXP_FILL);
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