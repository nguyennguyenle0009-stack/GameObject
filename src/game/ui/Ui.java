package game.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import game.entity.Entity;
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
		if (gp.getGameState() == gp.getPlayState()) {
//		    int npcIndex = gp.getCheckCollision().checkInteraction(gp.getPlayer(), gp.getNpcs(), 48);
//		    if (npcIndex != 999) {
//		        String npcName = gp.getNpcs().get(npcIndex).getName();
//		        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 20F));
//		        g2.setColor(Color.YELLOW);
//	            g2.setColor(new Color(0, 0, 0, 150)); // nền mờ
//		        g2.drawString(npcName, gp.getScreenWidth() - 250, 30);
//		    }
			drawInteractionHint1(g2);
		}
		if(gp.getGameState() == gp.getPauseState()) {
			drawPauseScreen();
		}
		if(gp.getGameState() == gp.getDialogueState()) {
			drawDialogueScreen();
		}
	}
    
	private void drawInteractionHint1(Graphics2D g2) {
	    int npcIndex = gp.getCheckCollision().checkInteraction(gp.getPlayer(), gp.getNpcs(), 48);
	    if (npcIndex != 999) {
            String text = gp.getNpcs().get(npcIndex).getName();
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 20F));
            g2.setColor(new Color(0, 0, 0, 150)); // nền mờ
            int textWidth = g2.getFontMetrics().stringWidth(text);
            int x = gp.getScreenWidth() - textWidth - 30;
            int y = 40;

            // Vẽ nền khung
            g2.fillRoundRect(x - 10, y - 20, textWidth + 20, 30, 10, 10);

            // Vẽ chữ
            g2.setColor(Color.white);
            g2.drawString(text, x, y);
        }
    }
    
    // Không dùng
    @SuppressWarnings("unused")
	private void drawInteractionHint(Graphics2D g2) {
        Entity npc = gp.getPlayer().getClosestNPCInRange(gp.getNpcs());
        if (npc != null) {
            String text = npc.getName();
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 20F));
            g2.setColor(new Color(0, 0, 0, 150)); // nền mờ
            int textWidth = g2.getFontMetrics().stringWidth(text);
            int x = gp.getScreenWidth() - textWidth - 30;
            int y = 40;

            // Vẽ nền khung
            g2.fillRoundRect(x - 10, y - 20, textWidth + 20, 30, 10, 10);

            // Vẽ chữ
            g2.setColor(Color.white);
            g2.drawString(text, x, y);
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























