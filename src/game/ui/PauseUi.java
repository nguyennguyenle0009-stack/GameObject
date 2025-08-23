package game.ui;

import java.awt.Font;
import java.awt.Graphics2D;

import game.main.GamePanel;
import game.util.UtilityTool;

public class PauseUi {
	private Graphics2D g2;
	private GamePanel gp;
	
    public void drawPauseScreen() {
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 80F));
        String text = "PAUSED";
        int x = UtilityTool.getXForCenterOfText(text, gp, g2);
        int y = gp.getScreenHeight() / 2;
        g2.drawString(text, x, y);
    }
}
