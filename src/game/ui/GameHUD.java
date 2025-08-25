package game.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import game.entity.Player;
import game.enums.Attr;
import game.main.GamePanel;

public class GameHUD {
	
	private final GamePanel gp;
	
    // COLOR
    private static final Color REALM_COLOR = new Color(10, 170, 80);
    private static final Color HP_FILL = new Color(210, 50, 50);
    private static final Color MP_FILL = new Color(60, 120, 230);
    private static final Color EXP_FILL = new Color(250, 150, 40);
    private static final Color BAR_BACK = new Color(30, 30, 30, 180);
    private static final Color BAR_BORDER = new Color(0, 0, 0, 180);

    private final Rectangle cancelRect;

    public GameHUD(GamePanel gp) {
        this.gp = gp;
        // Nút hủy tu luyện nằm góc trái dưới màn hình
        int w = 80;
        int h = 30;
        this.cancelRect = new Rectangle(5, gp.getScreenHeight() - h - 5, w, h);
    }

    public void draw(Graphics2D g2) {
        Player p = gp.getPlayer();
        int barWidth = gp.getTileSize() * 4;
        int barHeight = gp.getTileSize() / 3;
        int margin = 5;

        // draw realm box
        String realmText = p.getRealmName();
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

        int infoY = y + barHeight + 20;
        if (p.getPillSpiritBonus() > 0) {
            long sec = p.getPillTimeLeft() / 1000;
            String text = p.getActivePillName() + " " + (sec / 60) + ":" + String.format("%02d", sec % 60);
            g2.setColor(Color.WHITE);
            g2.drawString(text, x, infoY);
            infoY += 20;
        }
        if (p.isCultivating()) {
            long sec = p.getCultivationTimeLeft() / 1000;
            String text = "Tu luyện: " + (sec / 60) + ":" + String.format("%02d", sec % 60);
            g2.setColor(Color.WHITE);
            g2.drawString(text, x, infoY);
        } else {
            long cd = p.getCultivationCooldownRemaining();
            if (cd > 0) {
                long sec = cd / 1000;
                String text = "Hồi chiêu: " + (sec / 60) + ":" + String.format("%02d", sec % 60);
                g2.setColor(Color.WHITE);
                g2.drawString(text, x, infoY);
            }
        }

        // Nếu đang tu luyện, vẽ nút hủy
        if (p.isCultivating()) {
            HUDUtils.drawSubWindow(g2, cancelRect.x, cancelRect.y, cancelRect.width, cancelRect.height,
                    BAR_BACK, BAR_BORDER);
            g2.setColor(Color.WHITE);
            g2.drawString("Huỷ", cancelRect.x + 20, cancelRect.y + cancelRect.height - 10);
        }
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

    /** Xử lý click chuột trên HUD. */
    public boolean handleMousePress(int mx, int my, int button) {
        if (button == MouseEvent.BUTTON1 && gp.getPlayer().isCultivating() && cancelRect.contains(mx, my)) {
            gp.getPlayer().cancelCultivation();
            return true;
        }
        return false;
    }
}