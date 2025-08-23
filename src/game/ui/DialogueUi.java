package game.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import game.main.GamePanel;

public class DialogueUi {
	private GamePanel gp;
	private Graphics2D g2;
	private String currentDialogue = "";
	
	public DialogueUi(GamePanel gp) {
		this.gp = gp;
	}

    public void drawDialogueScreen() {
        int x = gp.getTileSize() * 2;
        int y = gp.getTileSize() / 2;
        int width = gp.getScreenWidth() - (gp.getTileSize() * 4);
        int height = gp.getTileSize() * 4;

        drawSubWindow(x, y, width, height, g2);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 28F));
        x += gp.getTileSize();
        y += gp.getTileSize();

        for (String line : currentDialogue.split("\n")) {
            g2.drawString(line, x, y);
            y += 40;
        }
    }

    public void drawSubWindow(int x, int y, int width, int height, Graphics2D g2) {
        Color color = new Color(0, 0, 0, 210);
        g2.setColor(color);
        g2.fillRoundRect(x, y, width, height, 35, 35);

        color = new Color(255, 255, 255);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(x + 5, y + 5, width - 10, height - 10, 25, 25);
    }
    
    public String getCurrentDialogue() { return currentDialogue; }
    public DialogueUi setCurrentDialogue(String currentDialogue) { this.currentDialogue = currentDialogue; return this; }
}
