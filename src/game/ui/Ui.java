package game.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.List;
import java.util.Map;

import game.entity.Entity;
import game.main.GamePanel;
import game.object.SuperObject;
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
		if (gp.getGameState() == gp.getPlayState()) {
			drawInteractionHint(g2);
		}
        if(gp.getGameState() == gp.getPauseState()) {
            drawPauseScreen();
	    }
	    if(gp.getGameState() == gp.getDialogueState()) {
	            drawDialogueScreen();
	    }
	    if(gp.getGameState() == gp.getCharacterState()) {
	            drawCharacterScreen();
	    }
	}
	
    private void drawCharacterScreen() {
        int frameX = gp.getTileSize();
        int frameY = gp.getTileSize();
        int frameWidth = gp.getTileSize() * 6;
        int frameHeight = gp.getTileSize() * 8;
        drawSubWindow(frameX, frameY, frameWidth, frameHeight, g2);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 24F));
        int textX = frameX + gp.getTileSize() / 2;
        int textY = frameY + gp.getTileSize();
        for (Map.Entry<String, Integer> entry : gp.getPlayer().getAttributes().getAll().entrySet()) {
            g2.drawString(entry.getKey() + ": " + entry.getValue(), textX, textY);
            textY += 30;
        }

        int invX = frameX + frameWidth + gp.getTileSize();
        int invY = frameY;
        int invWidth = gp.getTileSize() * 6;
        int invHeight = gp.getTileSize() * 8;
        drawSubWindow(invX, invY, invWidth, invHeight, g2);

        g2.drawString("Inventory", invX + gp.getTileSize() / 2, invY + gp.getTileSize());
        int itemY = invY + gp.getTileSize() * 2;
        for (SuperObject item : gp.getPlayer().getInventory().getItems()) {
            g2.drawString(item.getName(), invX + gp.getTileSize() / 2, itemY);
            itemY += 30;
        }

        itemY += 10;
        g2.drawString("Weapons", invX + gp.getTileSize() / 2, itemY);
        itemY += 30;
        SuperObject[] slots = gp.getPlayer().getInventory().getWeaponSlots();
        for (int i = 0; i < slots.length; i++) {
            String name = (slots[i] != null) ? slots[i].getName() : "Empty";
            g2.drawString("Slot " + (i + 1) + ": " + name, invX + gp.getTileSize() / 2, itemY);
            itemY += 30;
        }
    }
    
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























