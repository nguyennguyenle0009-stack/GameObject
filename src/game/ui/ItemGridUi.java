package game.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

import game.entity.item.Item;

/**
 * Grid hiển thị item trong kho.
 * Thu nhỏ ô để chứa được nhiều item và hỗ trợ thanh cuộn đơn giản.
 */
public class ItemGridUi {
    private final int cols = 8;
    private final int rows = 4;
    private final int slotSize;
    private final int gap = 4;
    private final int padding = 8;

    public ItemGridUi(int tileSize) {
        // Ô nhỏ hơn tile để tránh đè lên giao diện khác
        this.slotSize = Math.min(32, tileSize);
    }

    public Dimension getPreferredSize() {
        int w = cols * slotSize + (cols - 1) * gap + padding * 2;
        int h = rows * slotSize + (rows - 1) * gap + padding * 2;
        return new Dimension(w, h);
    }

    /**
     * Vẽ lưới item.
     * @param items   danh sách toàn bộ item
     * @param selected chỉ số item đang chọn (theo danh sách toàn bộ)
     * @param hover    chỉ số item đang hover (theo danh sách toàn bộ)
     * @param offset   vị trí bắt đầu hiển thị trong danh sách
     */
    public void draw(Graphics2D g2, int x, int y, List<Item> items,
                     int selected, int hover, int offset) {
        int total = (items == null) ? 0 : items.size();
        Dimension d = getPreferredSize();

        // Khung lớn
        HUDUtils.drawSubWindow(g2, x, y, d.width, d.height,
                new Color(20, 120, 20, 180), new Color(0, 70, 0));

        int startX = x + padding;
        int startY = y + padding;

        for (int r = 0; r < rows; r++) {
            int yy = startY + r * (slotSize + gap);
            for (int c = 0; c < cols; c++) {
                int xx = startX + c * (slotSize + gap);

                // vẽ nền ô
                g2.setColor(new Color(90,90,90,220));
                g2.fillRoundRect(xx, yy, slotSize, slotSize, 10, 10);
                // vẽ khung ô
                g2.setColor(new Color(0,0,0,160));
                g2.drawRoundRect(xx, yy, slotSize, slotSize, 10, 10);

                int idx = offset + r * cols + c;
                Item it = (idx < total) ? items.get(idx) : null;

                if (it != null) {
                    BufferedImage icon = it.getIcon();
                    if (icon != null) {
                        int pad = 4, iw = slotSize - pad*2, ih = slotSize - pad*2;
                        g2.drawImage(icon, xx + pad, yy + pad, iw, ih, null);
                    }
                }

                if (idx == selected) {
                    g2.setColor(Color.YELLOW);
                    g2.setStroke(new BasicStroke(3f));
                    g2.drawRoundRect(xx, yy, slotSize, slotSize, 10, 10);
                } else if (idx == hover) {
                    g2.setColor(new Color(255,255,255,120));
                    g2.setStroke(new BasicStroke(3f));
                    g2.drawRoundRect(xx, yy, slotSize, slotSize, 10, 10);
                }
                g2.setStroke(new BasicStroke(1f));

                if (it != null) {
                    String q = String.valueOf(it.getQuantity());
                    g2.setFont(g2.getFont().deriveFont(Font.BOLD, 12f));
                    FontMetrics fm = g2.getFontMetrics();
                    int tw = fm.stringWidth(q), th = fm.getAscent();
                    int boxX = xx + slotSize - tw - 8;
                    int boxY = yy + slotSize - th - 4;
                    g2.setColor(new Color(0,0,0,160));
                    g2.fillRoundRect(boxX, boxY, tw + 6, th + 4, 6, 6);
                    g2.setColor(Color.WHITE);
                    g2.drawString(q, boxX + 3, boxY + th);
                }
            }
        }

        // Vẽ thanh cuộn nếu có nhiều item
        int visible = cols * rows;
        if (total > visible) {
            int trackH = d.height - padding * 2;
            int barH = Math.max(20, trackH * visible / total);
            int maxOff = total - visible;
            int barY = y + padding;
            if (maxOff > 0) {
                barY += (trackH - barH) * offset / maxOff;
            }
            int barX = x + d.width - 5;
            g2.setColor(new Color(255,255,255,150));
            g2.fillRect(barX, barY, 3, barH);
        }
    }

    public int getCols() { return cols; }
    public int getRows() { return rows; }
    public int getSlotSize() { return slotSize; }
    public int getGap() { return gap; }
    public int getPadding() { return padding; }
}
