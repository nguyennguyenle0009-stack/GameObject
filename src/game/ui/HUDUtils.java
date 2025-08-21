package game.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public final class HUDUtils {
	private HUDUtils() {}
	public static void drawSubWindow(Graphics2D g2, int x, int y, int w, int h, Color bg, Color border) {
		g2.setColor(bg);
		g2.fillRoundRect(x, y, w, h, 16, 16);
		g2.setStroke(new BasicStroke(3f));
		g2.setColor(border);
		g2.drawRoundRect(x, y, w, h, 16, 16);
	}
}
