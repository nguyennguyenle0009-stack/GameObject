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
 * Bảng hiển thị và tương tác với danh sách công pháp.
 */
public class SkillUi {
    private final GamePanel gp;
    private boolean visible = false;

    /** Vị trí cuộn hiện tại. */
    private int scrollOffset = 0;

    /** Các ô kỹ năng đang được vẽ. */
    private Entry[] entries = new Entry[0];

    /** Chỉ số ô đang hover để hiển thị tooltip. */
    private int hoverIndex = -1;

    // Lưu toạ độ cuối cùng của khung để kiểm tra vị trí cuộn.
    private Rectangle lastBounds = new Rectangle();

    /** Thông tin hiển thị trên một dòng kỹ năng. */
    private static class Entry {
        Rectangle area;       // vùng cho toàn bộ dòng
        Rectangle useBtn;     // nút sử dụng
        Rectangle assignBtn;  // nút gán
        CultivationTechnique tech;
    }

    public SkillUi(GamePanel gp) { this.gp = gp; }

    /**
     * Vẽ bảng công pháp ở phía bên phải màn hình.
     */
    public void draw(Graphics2D g2) {
        if (!visible) return;

        int tile = gp.getTileSize();
        int w = tile * 8;
        int h = tile * 6;
        int x = gp.getScreenWidth() - w - tile; // dịch sang phải
        int y = tile;
        lastBounds.setBounds(x, y, w, h);

        HUDUtils.drawSubWindow(g2, x, y, w, h, new Color(0,0,0,200), Color.WHITE);

        List<CultivationTechnique> skills = gp.getPlayer().getTechniques();
        int lineH = 40;
        int visibleLines = Math.max(1, (h - 60) / lineH);

        // Giới hạn offset nằm trong [0, tổng - hiển thị]
        int maxOffset = Math.max(0, skills.size() - visibleLines);
        if (scrollOffset > maxOffset) scrollOffset = maxOffset;
        if (scrollOffset < 0) scrollOffset = 0;

        entries = new Entry[skills.size()];
        Point mouse = gp.getMousePosition();
        hoverIndex = -1;

        for (int i = scrollOffset; i < skills.size() && i < scrollOffset + visibleLines; i++) {
            CultivationTechnique tech = skills.get(i);
            int idx = i - scrollOffset;
            int yy = y + 40 + idx * lineH;

            Entry e = new Entry();
            e.tech = tech;
            e.area = new Rectangle(x + 20, yy - 25, w - 40, 30);
            int btnW = 60, btnH = 25;
            e.useBtn = new Rectangle(x + w - btnW*2 - 30, yy - 20, btnW, btnH);
            e.assignBtn = new Rectangle(x + w - btnW - 15, yy - 20, btnW, btnH);
            entries[i] = e;

            // Highlight khi hover
            if (mouse != null && e.area.contains(mouse)) {
                hoverIndex = i;
                g2.setColor(new Color(80,80,80,150));
                g2.fill(e.area);
            }

            g2.setColor(Color.WHITE);
            String name = tech.getName();
            long cd = gp.getPlayer().getCultivationCooldownRemaining();
            if (cd > 0) {
                long sec = cd / 1000;
                name += " (" + (sec/60) + ":" + String.format("%02d", sec%60) + ")";
            }
            g2.drawString(name, x + 30, yy);

            // Vẽ nút "Sử dụng"
            HUDUtils.drawSubWindow(g2, e.useBtn.x, e.useBtn.y, e.useBtn.width, e.useBtn.height,
                    new Color(50,50,50,180), Color.WHITE);
            g2.drawString("Dùng", e.useBtn.x + 12, e.useBtn.y + 18);

            // Vẽ nút "Gán"
            HUDUtils.drawSubWindow(g2, e.assignBtn.x, e.assignBtn.y, e.assignBtn.width, e.assignBtn.height,
                    new Color(50,50,50,180), Color.WHITE);
            g2.drawString("Gán", e.assignBtn.x + 15, e.assignBtn.y + 18);
        }

        // Thanh cuộn đơn giản
        if (skills.size() > visibleLines) {
            int trackH = h - 40;
            int barH = Math.max(20, visibleLines * trackH / skills.size());
            int barY = y + 20 + scrollOffset * (trackH - barH) / (skills.size() - visibleLines);
            g2.setColor(new Color(120,120,120));
            g2.fillRect(x + w - 15, y + 20, 8, trackH);
            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(x + w - 15, barY, 8, barH);
        }

        // Tooltip khi hover
        if (hoverIndex >= 0 && hoverIndex < skills.size()) {
            CultivationTechnique tech = skills.get(hoverIndex);
            String line1 = tech.getName();
            String line2 = "Cấp: " + tech.getLevel();
            String line3 = "Phẩm cấp: " + tech.getGrade().getDisplay();
            long sec = gp.getPlayer().getCultivationCooldownRemaining() / 1000;
            String line4 = "Hồi chiêu: " + (sec/60) + ":" + String.format("%02d", sec%60);

            int padding = 10;
            int width = Math.max(Math.max(g2.getFontMetrics().stringWidth(line1),
                                         g2.getFontMetrics().stringWidth(line2)),
                                 Math.max(g2.getFontMetrics().stringWidth(line3),
                                          g2.getFontMetrics().stringWidth(line4))) + padding*2;
            int height = 80 + padding*2;
            int tipX = (mouse != null ? mouse.x + 15 : x + w + 10);
            int tipY = (mouse != null ? mouse.y + 15 : y + 20);
            if (tipX + width > gp.getScreenWidth()) tipX = gp.getScreenWidth() - width - 10;
            if (tipY + height > gp.getScreenHeight()) tipY = gp.getScreenHeight() - height - 10;
            HUDUtils.drawSubWindow(g2, tipX, tipY, width, height, new Color(40,40,40,200), Color.YELLOW);
            g2.setColor(Color.WHITE);
            g2.drawString(line1, tipX + padding, tipY + padding + 15);
            g2.drawString(line2, tipX + padding, tipY + padding + 35);
            g2.drawString(line3, tipX + padding, tipY + padding + 55);
            g2.drawString(line4, tipX + padding, tipY + padding + 75);
        }
    }

    /**
     * Xử lý click chuột cho bảng công pháp.
     */
    public boolean handleMousePress(int mx, int my, int button) {
        if (!visible) return false;
        if (button == MouseEvent.BUTTON1) {
            for (Entry e : entries) {
                if (e == null) continue;
                if (e.useBtn.contains(mx, my)) {
                    e.tech.use(gp.getPlayer());
                    visible = false;
                    return true;
                }
                if (e.assignBtn.contains(mx, my)) {
                    gp.getPlayer().assignTechnique(e.tech);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Cuộn danh sách bằng con lăn chuột.
     */
    public void handleMouseWheel(int rotation, int mx, int my) {
        if (!visible) return;
        if (!lastBounds.contains(mx, my)) return;
        scrollOffset += Integer.signum(rotation);
    }

    public void toggle() { visible = !visible; }
    public boolean isVisible() { return visible; }
}

