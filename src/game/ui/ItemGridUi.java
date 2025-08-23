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

public class ItemGridUi {
	private final int cols = 8;
	private final int rows = 3;
	private final int slotSize;
	private final int gap = 6;
	private final int padding; // Viền trong của khung lớn
	
	public ItemGridUi(int tileSize) {
		this.slotSize = tileSize;
		this.padding = tileSize / 4;
	}
	
	// Trả về kích thước khung (bug)
	
//Điểm cần nhớ:
//Đừng dùng idx++ rồi items.get(idx) ở ngoài kiểm tra.
//Dùng int idx = r * cols + c; và so sánh với size trước khi get.
//inventory.all() trả về danh sách immutable (OK), nhưng vẫn phải kiểm tra chỉ số.
//Thử thay đoạn vẽ theo code trên—lỗi sẽ hết. Nếu vẫn gặp lỗi, gửi mình dòng code hiện tại của ItemGridUi.java:54 để mình chỉnh đúng chỗ nhé.
        public Dimension getPreferredSize() {
                int w = cols * slotSize + (cols - 1) * gap + padding * 2;
                int h = rows * slotSize + (rows - 1) * gap + padding * 2;
                return new Dimension(w, h);
        }

        public void draw(Graphics2D g2, int x, int y, List<Item> items, int selectedSlot, int hoverSlot) {
            final int size = (items == null) ? 0 : items.size();

                Dimension d = getPreferredSize();

                // Khung lớn
                HUDUtils.drawSubWindow(g2, x, y, d.width, d.height,
                                new Color(40, 40, 40, 180), new Color(200, 200, 200));

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

                    // lấy item
                    int idx = r * cols + c;            // <— tính chỉ số tại chỗ
                    Item it = (idx < size) ? items.get(idx) : null;

                    // icon
                    if (it != null) {
                        BufferedImage icon = it.getIcon();
                        if (icon != null) {
                            int pad = 6, iw = slotSize - pad*2, ih = slotSize - pad*2;
                            g2.drawImage(icon, xx + pad, yy + pad, iw, ih, null);
                        }
                    }

                    // highlight first so quantity text draws over it
                    if (idx == selectedSlot) {
                        g2.setColor(Color.YELLOW);
                        g2.setStroke(new BasicStroke(3f));
                        g2.drawRoundRect(xx, yy, slotSize, slotSize, 10, 10);
                    } else if (idx == hoverSlot) {
                        g2.setColor(new Color(255,255,255,120));
                        g2.setStroke(new BasicStroke(3f));
                        g2.drawRoundRect(xx, yy, slotSize, slotSize, 10, 10);
                    }
                    // reset stroke before drawing quantity
                    g2.setStroke(new BasicStroke(1f));

                    // số lượng
                    if (it != null) {
                        String q = String.valueOf(it.getQuantity());
                        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 14f));
                        FontMetrics fm = g2.getFontMetrics();
                        int tw = fm.stringWidth(q), th = fm.getAscent();
                        int boxX = xx + slotSize - tw - 12;
                        int boxY = yy + slotSize - th - 8;
                        g2.setColor(new Color(0,0,0,160));
                        g2.fillRoundRect(boxX, boxY, tw + 8, th + 4, 8, 8);
                        g2.setColor(Color.WHITE);
                        g2.drawString(q, boxX + 4, boxY + th + 1);
                    }
                }
            }
        }

	public int getCols() { return cols; }
	public int getRows() { return rows; }
	public int getSlotSize() { return slotSize; }
	public int getGap() { return gap; }
	public int getPadding() { return padding; } 
	
}


