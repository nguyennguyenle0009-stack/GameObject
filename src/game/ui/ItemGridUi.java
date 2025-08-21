package game.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
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
	
	// Trả về kích thước khung
	public Dimension getPreferredSize() {
		int w = cols * slotSize + (cols - 1) * gap + padding * 2;
		int h = rows * slotSize + (cols - 1) * gap + padding * 2;
		return new Dimension(w, h);
	}
	
	// Vẽ khung túi và ô trống
	public void draw(Graphics2D g2, int x, int y, List<Item> items) {
		Dimension d = getPreferredSize();
		
		// Khung lớn
		HUDUtils.drawSubWindow(g2, x, y, d.width, d.height,
				new Color(40, 40, 40, 180), new Color(200, 200, 200));
		
		int startX = x + padding;
		int startY = y + padding;
		
		int idx = 0;
		for(int r = 0; r < rows; r++) {
			int yy = startY + r * (slotSize + gap);
			for(int c = 0; c < cols; c++) {
				int xx = startX +c * (slotSize + gap);
				
				g2.setColor(new Color(90, 90, 90, 220));
				g2.fillRoundRect(xx, yy, slotSize, slotSize, 10, 10);
				g2.setColor(new Color(0, 0, 0, 160));
				g2.drawRoundRect(xx, yy, slotSize, slotSize, 10, 10);
				
				idx++;
			}
		}
	}

	public int getCols() { return cols; }
	public int getRows() { return rows; }
	public int getSlotSize() { return slotSize; }
	public int getGap() { return gap; }
	public int getPadding() { return padding; } 
	
}















