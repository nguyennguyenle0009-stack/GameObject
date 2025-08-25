package game.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

public final class HUDUtils {
	private HUDUtils() {}
        public static void drawSubWindow(Graphics2D g2, int x, int y, int w, int h, Color bg, Color border) {
                Color oldColor = g2.getColor();
                Stroke oldStroke = g2.getStroke();

                g2.setColor(bg);
                g2.fillRoundRect(x, y, w, h, 16, 16);
                g2.setStroke(new BasicStroke(3f));
                g2.setColor(border);
                g2.drawRoundRect(x, y, w, h, 16, 16);

                g2.setStroke(oldStroke);
                g2.setColor(oldColor);
        }
}
