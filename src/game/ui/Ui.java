
package game.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.List;

import game.entity.Entity;
import game.enums.Attr;
import game.main.GamePanel;
import game.util.UtilityTool;

public class Ui {
    private final GamePanel gp;
    private Graphics2D g2;
    private final Font arial_40, arial_80B;
    private boolean messageOn = false;
	private String message;
	private int messageCouter;
	private String currentDialogue = "";
	
	public Ui(GamePanel gp) {
		this.gp = gp;
		this.arial_40 = new Font("Arial", Font.PLAIN, 40);
		this.arial_80B = new Font("Arial", Font.BOLD, 80);
	}
	
    public void showMessage(String text) {
        message = text;
        messageOn = true;
    }
	
    public void draw(Graphics2D g2) {
        this.g2 = g2;
        g2.setColor(Color.white);

        drawPlayerStatusBars(g2);

        if (gp.getGameState() == gp.getPlayState()) {
            drawInteractionHint(g2);
        }
        if(gp.getGameState() == gp.getPauseState()) {
            drawPauseScreen();
        }
        if(gp.getGameState() == gp.getDialogueState()) {
            drawDialogueScreen();
        }

        // Bật balo
        if(gp.keyH.isbPressed()) {
            drawCharacterScreen(g2);
        }
        }

    private void drawPlayerStatusBars(Graphics2D g2) {
        int x = 20;
        int y = 20;
        int width = gp.getTileSize() * 4;
        int height = 20;
        int gap = 10;

        drawBar(g2, x, y, width, height,
                gp.getPlayer().atts().get(Attr.HEALTH),
                gp.getPlayer().atts().get(Attr.HEALTH),
                Color.RED);

        y += height + gap;
        drawBar(g2, x, y, width, height,
                gp.getPlayer().cultivation().pep(),
                gp.getPlayer().atts().get(Attr.PEP),
                Color.BLUE);

        y += height + gap;
        drawBar(g2, x, y, width, height,
                gp.getPlayer().cultivation().spirit(),
                gp.getPlayer().cultivation().spiritMax(),
                Color.ORANGE);
    }

    private void drawBar(Graphics2D g2, int x, int y, int width, int height,
                         int value, int max, Color color) {
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(x, y, width, height);

        int filled = (int) ((double) value / Math.max(max, 1) * width);
        g2.setColor(color);
        g2.fillRect(x, y, filled, height);

        g2.setColor(color.darker());
        g2.drawRect(x, y, width, height);
    }

    private void drawCharacterScreen(Graphics2D g2) {
        int frameX = gp.getTileSize();
        int frameY = gp.getTileSize() * 2;
        int frameWidth = gp.getTileSize() * 12;
        int frameHeight = gp.getTileSize() * 7;

        // Outer yellow frame
        g2.setColor(Color.YELLOW);
        g2.drawRect(frameX, frameY, frameWidth, frameHeight);

        int innerPadding = 20;
        int infoWidth = (frameWidth - innerPadding * 3) / 2;
        int infoHeight = frameHeight - innerPadding * 2;

        // Character info box (black)
        int infoX = frameX + innerPadding;
        int infoY = frameY + innerPadding;
        g2.setColor(Color.BLACK);
        g2.drawRect(infoX, infoY, infoWidth, infoHeight);

        // Inventory box (green)
        int invX = infoX + infoWidth + innerPadding;
        g2.setColor(Color.GREEN);
        g2.drawRect(invX, infoY, infoWidth, infoHeight);

        // Display attributes inside the info box
        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 24F));
        int textX = infoX + 20;
        int textY = infoY + 40;
        game.entity.Attributes attrs = gp.getPlayer().atts();
        for (Attr a : Attr.values()) {
            g2.drawString(a.displayName() + ": " + attrs.get(a), textX, textY);
            textY += 30;
        }
    }
    
    // Vẽ khung hình tên đối tượng
	private void drawInteractionHint(Graphics2D g2) {
		List<Entity> nearbyNpcs = gp.getCheckCollision().getEntitiesInRange(gp.getPlayer(), gp.getNpcs(), 48);
		if (!nearbyNpcs.isEmpty()) {
		    g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 20F));

		    // Tính toán kích thước khung dựa theo số lượng NPC
		    int padding = 10;
		    int lineHeight = 25;
		    int boxWidth = 200;
		    int boxHeight = nearbyNpcs.size() * lineHeight + padding * 2;

		    int boxX = gp.getScreenWidth() - boxWidth - 20; // cách mép phải 20px
		    int boxY = 20; // cách mép trên 20px

		    // Vẽ nền mờ mờ
		    g2.setColor(new Color(0, 0, 0, 150)); // đen trong suốt
		    g2.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 15, 15);

		    // Vẽ viền
		    g2.setColor(Color.YELLOW);
		    g2.setStroke(new BasicStroke(2));
		    g2.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 15, 15);

		    // Vẽ tên NPC
		    g2.setColor(Color.WHITE);
		    int startY = boxY + padding + 20;
		    for (Entity npc : nearbyNpcs) {
		        g2.drawString(npc.getName(), boxX + padding, startY);
		        startY += lineHeight;
		    }
		}
    }
    
    private void drawPauseScreen() {
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 80F));

        String text = "PAUSED";
        int x = UtilityTool.getXForCenterOfText(text, gp, g2);
        int y = gp.getScreenHeight() / 2;

        g2.drawString(text, x, y);
    }
	
   private void drawDialogueScreen() {
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
	
	public boolean isMessageOn() { return messageOn; }
	public Ui setMessageOn(boolean messageOn) { this.messageOn = messageOn; return this; }
	public Font getArial_40() { return arial_40; }
	public Font getArial_80B() { return arial_80B; }
	public String getMessage() { return message; }
	public Ui setMessage(String message) { this.message = message; return this; }
	public int getMessageCouter() { return messageCouter; }
	public Ui setMessageCouter(int messageCouter) { this.messageCouter = messageCouter; return this; }
	public String getCurrentDialogue() { return currentDialogue; }
	public Ui setCurrentDialogue(String currentDialogue) { this.currentDialogue = currentDialogue; return this; }
}
