package game.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.List;

import game.entity.skill.CultivationTechnique;
import game.main.GamePanel;

/**
 * Bảng hiển thị danh sách công pháp đã học.
 */
public class SkillUi {
    private final GamePanel gp;
    private boolean visible = false;
    private int scrollOffset = 0;
    private Rectangle[] entries = new Rectangle[0];
    private Rectangle[] useBtns = new Rectangle[0];
    private Rectangle[] assignBtns = new Rectangle[0];
    private int firstIndex = 0; // index của skill đầu tiên đang hiển thị

    public SkillUi(GamePanel gp) {
        this.gp = gp;
    }

    public void draw(Graphics2D g2) {
        if (!visible) return;
        int w = gp.getTileSize() * 8;
        int h = gp.getTileSize() * 6;
        int x = gp.getScreenWidth() - w - gp.getTileSize(); // dịch sang bên phải
        int y = gp.getTileSize();
        HUDUtils.drawSubWindow(g2, x, y, w, h, new Color(0,0,0,200), Color.WHITE);

        List<CultivationTechnique> skills = gp.getPlayer().getTechniques();
        int rowH = 40;
        int visibleRows = (h - 40) / rowH;
        int total = skills.size();
        int maxOffset = Math.max(0, total - visibleRows);
        if (scrollOffset > maxOffset) scrollOffset = maxOffset;
        firstIndex = scrollOffset;
        int end = Math.min(total, scrollOffset + visibleRows);
        int count = end - scrollOffset;
        entries = new Rectangle[count];
        useBtns = new Rectangle[count];
        assignBtns = new Rectangle[count];

        long cd = gp.getPlayer().getCultivationCooldownRemaining();
        for (int i = 0; i < count; i++) {
            int skillIdx = scrollOffset + i;
            int yy = y + 40 + i * rowH;
            CultivationTechnique tech = skills.get(skillIdx);
            String name = tech.getName();
            if (cd > 0) {
                name += " (" + formatTime(cd) + ")";
            }
            g2.setColor(Color.WHITE);
            g2.drawString(name, x + 30, yy);

            int btnW = 50, btnH = 25;
            int useX = x + w - btnW * 2 - 30;
            int btnY = yy - 20;
            useBtns[i] = new Rectangle(useX, btnY, btnW, btnH);
            HUDUtils.drawSubWindow(g2, useX, btnY, btnW, btnH, new Color(40,40,40,200), Color.WHITE);
            g2.drawString("Dùng", useX + 8, btnY + 17);

            int assignX = x + w - btnW - 20;
            assignBtns[i] = new Rectangle(assignX, btnY, btnW, btnH);
            HUDUtils.drawSubWindow(g2, assignX, btnY, btnW, btnH, new Color(40,40,40,200), Color.WHITE);
            g2.drawString("Gán", assignX + 10, btnY + 17);

            entries[i] = new Rectangle(x + 20, yy - 25, w - 40, 30);
        }

        // Thanh cuộn
        if (total > visibleRows) {
            int trackH = h - 40;
            int barH = Math.max(20, trackH * visibleRows / total);
            int barY = y + 40;
            if (maxOffset > 0) {
                barY += (trackH - barH) * scrollOffset / maxOffset;
            }
            int barX = x + w - 8;
            g2.setColor(new Color(255,255,255,150));
            g2.fillRect(barX, barY, 4, barH);
        }

        // Tooltip khi hover
        Point m = gp.getMousePosition();
        if (m != null) {
            for (int i = 0; i < entries.length; i++) {
                if (entries[i].contains(m)) {
                    int idx = firstIndex + i;
                    if (idx < skills.size()) {
                        drawSkillTooltip(g2, m.x + 15, m.y + 15, skills.get(idx));
                    }
                    break;
                }
            }
        }
    }

    public boolean handleMousePress(int mx, int my, int button) {
        if (!visible) return false;
        if (button == MouseEvent.BUTTON1) {
            List<CultivationTechnique> list = gp.getPlayer().getTechniques();
            for (int i = 0; i < useBtns.length; i++) {
                int idx = firstIndex + i;
                if (idx >= list.size()) continue;
                if (useBtns[i].contains(mx, my)) {
                    list.get(idx).use(gp.getPlayer());
                    visible = false;
                    return true;
                }
                if (assignBtns[i].contains(mx, my)) {
                    gp.getPlayer().assignTechnique(list.get(idx));
                    visible = false;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Xử lý cuộn chuột.
     */
    public boolean handleMouseWheel(int rotation) {
        if (!visible) return false;
        int h = gp.getTileSize() * 6;
        int rowH = 40;
        int visibleRows = (h - 40) / rowH;
        int total = gp.getPlayer().getTechniques().size();
        int maxOffset = Math.max(0, total - visibleRows);
        scrollOffset += rotation;
        if (scrollOffset < 0) scrollOffset = 0;
        if (scrollOffset > maxOffset) scrollOffset = maxOffset;
        return true;
    }

    public void toggle() { visible = !visible; }
    public boolean isVisible() { return visible; }

    private void drawSkillTooltip(Graphics2D g2, int x, int y, CultivationTechnique t) {
        String line1 = t.getName();
        String line2 = "Cấp: " + t.getLevel();
        String line3 = "Phẩm: " + t.getGrade().getDisplay();
        String line4 = "Hồi chiêu: " + formatTime(gp.getPlayer().getCultivationCooldownRemaining());
        int padding = 10;
        int width = Math.max(Math.max(g2.getFontMetrics().stringWidth(line1), g2.getFontMetrics().stringWidth(line2)),
                Math.max(g2.getFontMetrics().stringWidth(line3), g2.getFontMetrics().stringWidth(line4))) + padding * 2;
        int height = 80 + padding * 2;
        HUDUtils.drawSubWindow(g2, x, y, width, height, new Color(40,40,40,200), new Color(200,200,200));
        g2.setColor(Color.WHITE);
        g2.drawString(line1, x + padding, y + padding + 15);
        g2.drawString(line2, x + padding, y + padding + 30);
        g2.drawString(line3, x + padding, y + padding + 45);
        g2.drawString(line4, x + padding, y + padding + 60);
    }

    private String formatTime(long ms) {
        long sec = ms / 1000;
        return (sec / 60) + ":" + String.format("%02d", sec % 60);
    }
}
