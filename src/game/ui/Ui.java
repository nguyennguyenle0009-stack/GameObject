package game.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.List;

import game.entity.Entity;
import game.entity.item.Item;
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
	
	private final ItemGridUi itemGrid;
	
	public Ui(GamePanel gp) {
		this.gp = gp;
		this.arial_40 = new Font("Arial", Font.PLAIN, 40);
		this.arial_80B = new Font("Arial", Font.BOLD, 80);
		
		this.itemGrid = new ItemGridUi(gp.getTileSize());
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
                if(gp.keyH.isiPressed() == true) {
                        drawInventory(g2);
                        characterScreen(g2);
                }
	}
    
    private void drawInventory(Graphics2D g2) {
        int x = gp.getTileSize();
        int y = gp.getTileSize() * 6;

        var items = gp.getPlayer().getBag().all();
        itemGrid.draw(g2, x, y, items );

        // Tooltip khi rê chuột
        var mh = gp.getMouseH();
        int mx = mh.getMouseX();
        int my = mh.getMouseY();
        var hover = getItemAt(mx, my, x, y, items);
        if (hover != null) {
            int w = 200; int h = 60;
            drawSubWindow(mx, my - h, w, h, g2);
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 16f));
            g2.drawString(hover.getName(), mx + 10, my - h + 20);
            g2.drawString(hover.getDecription(), mx + 10, my - h + 40);
        }

        // Menu ngữ cảnh "Sử dụng"
        if (mh.isShowContext()) {
            int cx = mh.getContextX();
            int cy = mh.getContextY();
            drawSubWindow(cx, cy, 60, 20, g2);
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 14f));
            g2.drawString("Sử dụng", cx + 5, cy + 15);
        }
    }

    /** Lấy item tại vị trí chuột trong bảng kho */
    private Item getItemAt(int mx, int my, int x, int y, java.util.List<Item> items) {
        int cols = itemGrid.getCols();
        int rows = itemGrid.getRows();
        int slot = itemGrid.getSlotSize();
        int gap = itemGrid.getGap();
        int pad = itemGrid.getPadding();
        int width = cols * slot + (cols - 1) * gap + pad * 2;
        int height = rows * slot + (rows - 1) * gap + pad * 2;
        if (mx < x || mx > x + width || my < y || my > y + height) return null;
        int relX = mx - x - pad;
        int relY = my - y - pad;
        int col = relX / (slot + gap);
        int row = relY / (slot + gap);
        if (col < 0 || col >= cols || row < 0 || row >= rows) return null;
        int idx = row * cols + col;
        return (idx < items.size()) ? items.get(idx) : null;
    }
    
    private void characterScreen(Graphics2D g2) {
    	int x = gp.getTileSize();
    	int y = gp.getTileSize();
    	int width = x * 6;
    	int height = y * 8;
    	drawSubWindow(x, y, width, height, g2);
    	g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 24F));
    	int textX = x + gp.getTileSize();
    	int textY = y + gp.getTileSize();
    	
    	var attrs = gp.getPlayer().atts();
    	for(game.enums.Attr a : game.enums.Attr.values()) {
    		g2.drawString(a.displayerName() + ": " + attrs.get(a), textX, textY);
    		textY += 30;
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

//	public Graphics2D getG2() { return g2; }
//	public Ui setG2(Graphics2D g2) { this.g2 = g2; return this; }
//	public GamePanel getGp() { return gp; }
	
	public ItemGridUi getItemGrid() { return itemGrid; }
	
}























