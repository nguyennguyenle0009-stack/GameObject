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
 * Hiển thị thời gian hồi chiêu, cho phép sử dụng/gán và hỗ trợ cuộn.
 */
public class SkillUi {
    private final GamePanel gp;
    private boolean visible = false;
    private Rectangle[] entryRects = new Rectangle[0];
    private Rectangle[] useBtns = new Rectangle[0];
    private Rectangle[] assignBtns = new Rectangle[0];
    private int scrollOffset = 0;
    private int hoverIndex = -1;

    public SkillUi(GamePanel gp) { this.gp = gp; }

    /** Vẽ khung danh sách công pháp. */
    public void draw(Graphics2D g2) {
        if (!visible) return;
        int tile = gp.getTileSize();
        int w = tile * 6;
        int h = tile * 6;
        int x = gp.getScreenWidth() - w - tile; // nằm bên phải màn hình
        int y = tile;
        HUDUtils.drawSubWindow(g2, x, y, w, h, new Color(0,0,0,200), Color.WHITE);

        List<CultivationTechnique> skills = gp.getPlayer().getTechniques();
        int entryH = 40;
        int visibleCount = (h - 40) / entryH;
        int total = skills.size();
        entryRects = new Rectangle[Math.min(visibleCount, total)];
        useBtns = new Rectangle[entryRects.length];
        assignBtns = new Rectangle[entryRects.length];

        Point m = gp.getMousePosition();
        hoverIndex = -1;

        for (int i = 0; i < visibleCount; i++) {
            int idx = scrollOffset + i;
            if (idx >= total) break;
            int yy = y + 40 + i * entryH;
            CultivationTechnique tech = skills.get(idx);
            String name = tech.getName();
            long cdMs = gp.getPlayer().getCultivationCooldownRemaining();
            if (cdMs > 0) {
                long sec = cdMs / 1000;
                name += " (" + (sec / 60) + ":" + String.format("%02d", sec % 60) + ")";
            }
            g2.setColor(Color.WHITE);
            g2.drawString(name, x + 15, yy);

            entryRects[i] = new Rectangle(x + 10, yy - 25, w - 20, 30);
            int bw = 50, bh = 20;
            int bxAssign = x + w - bw - 10;
            int bxUse = bxAssign - bw - 10;
            useBtns[i] = new Rectangle(bxUse, yy - 20, bw, bh);
            assignBtns[i] = new Rectangle(bxAssign, yy - 20, bw, bh);

            HUDUtils.drawSubWindow(g2, bxUse, yy - 20, bw, bh, new Color(60,60,60,200), Color.WHITE);
            HUDUtils.drawSubWindow(g2, bxAssign, yy - 20, bw, bh, new Color(60,60,60,200), Color.WHITE);
            g2.setColor(Color.WHITE);
            g2.drawString("Use", bxUse + 10, yy - 5);
            g2.drawString("Gán", bxAssign + 10, yy - 5);

            if (m != null && entryRects[i].contains(m)) {
                hoverIndex = idx;
            }
        }

        // Vẽ thanh cuộn
        if (total > visibleCount) {
            int trackH = h - 40;
            int barH = Math.max(20, trackH * visibleCount / total);
            int maxOff = total - visibleCount;
            int barY = y + 20;
            if (maxOff > 0) {
                barY += (trackH - barH) * scrollOffset / maxOff;
            }
            int barX = x + w - 5;
            g2.setColor(new Color(255,255,255,150));
            g2.fillRect(barX, barY, 3, barH);
        }

        // Tooltip khi hover
        if (hoverIndex >= 0 && hoverIndex < total) {
            drawTooltip(g2, m, skills.get(hoverIndex));
        }
    }

    /** Vẽ tooltip thông tin công pháp. */
    private void drawTooltip(Graphics2D g2, Point m, CultivationTechnique tech) {
        if (m == null) return;
        String l1 = tech.getName();
        String l2 = "Cấp: " + tech.getLevel();
        String l3 = "Phẩm: " + tech.getGrade().getDisplay();
        long cdMs = gp.getPlayer().getCultivationCooldownRemaining();
        String l4 = "Hồi chiêu: ";
        if (cdMs > 0) {
            long sec = cdMs / 1000;
            l4 += (sec / 60) + ":" + String.format("%02d", sec % 60);
        } else {
            l4 += "0";
        }
        int pad = 10;
        int width = Math.max(Math.max(g2.getFontMetrics().stringWidth(l1), g2.getFontMetrics().stringWidth(l2)),
                Math.max(g2.getFontMetrics().stringWidth(l3), g2.getFontMetrics().stringWidth(l4))) + pad * 2;
        int height = 100;
        int x = m.x + 15;
        int y = m.y + 15;
        if (x + width > gp.getScreenWidth()) x = gp.getScreenWidth() - width - 10;
        if (y + height > gp.getScreenHeight()) y = gp.getScreenHeight() - height - 10;
        HUDUtils.drawSubWindow(g2, x, y, width, height, new Color(40,40,40,200), Color.WHITE);
        g2.setColor(Color.WHITE);
        g2.drawString(l1, x + pad, y + 20);
        g2.drawString(l2, x + pad, y + 40);
        g2.drawString(l3, x + pad, y + 60);
        g2.drawString(l4, x + pad, y + 80);
    }

    /** Xử lý click chuột. */
    public boolean handleMousePress(int mx, int my, int button) {
        if (!visible) return false;
        if (button == MouseEvent.BUTTON1) {
            for (int i = 0; i < entryRects.length; i++) {
                int idx = scrollOffset + i;
                if (idx >= gp.getPlayer().getTechniques().size()) continue;
                if (useBtns[i].contains(mx, my)) {
                    gp.getPlayer().getTechniques().get(idx).use(gp.getPlayer());
                    return true;
                }
                if (assignBtns[i].contains(mx, my)) {
                    gp.getPlayer().assignTechnique(gp.getPlayer().getTechniques().get(idx));
                    return true;
                }
            }
        }
        return false;
    }

    /** Cuộn danh sách bằng con lăn chuột. */
    public void handleMouseWheel(int rotation) {
        if (!visible) return;
        List<CultivationTechnique> skills = gp.getPlayer().getTechniques();
        int h = gp.getTileSize() * 6;
        int visibleCount = (h - 40) / 40;
        int maxOff = Math.max(0, skills.size() - visibleCount);
        scrollOffset += rotation;
        if (scrollOffset < 0) scrollOffset = 0;
        if (scrollOffset > maxOff) scrollOffset = maxOff;
    }

    public void toggle() { visible = !visible; }
    public boolean isVisible() { return visible; }
}
