package game.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import game.entity.Entity;
import game.entity.item.Item;
import game.entity.item.ItemAction;
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
	
        private final ItemGridUi itemGrid;
        private int selectedIndex = -1;
        private boolean menuVisible = false;
        private int menuX, menuY, menuSlotIdx;
        private List<ItemAction> menuActions = new ArrayList<>();
	
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
        drawPlayerStatus(g2);
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
        }
    }

    private void drawPlayerStatus(Graphics2D g2) {
        int boxSize = gp.getTileSize() * 3;
        int x = 10;
        int y = 10;
        HUDUtils.drawSubWindow(g2, x, y, boxSize, boxSize,
                new Color(0, 0, 0, 150), Color.white);
        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 20F));
        int textX = x + 10;
        int textY = y + 30;
        int lineH = 25;
        int hp = gp.getPlayer().atts().get(Attr.HEALTH);
        g2.drawString("HP: " + hp, textX, textY);
        textY += lineH;
        g2.drawString("EXP: " + gp.getPlayer().getExp(), textX, textY);
        textY += lineH;
        g2.drawString("LVL: " + gp.getPlayer().getLevel(), textX, textY);
    }
    
    private void drawInventory(Graphics2D g2) {
        int x = gp.getTileSize();
        int y = gp.getTileSize() * 6;

        var items = gp.getPlayer().getBag().all();
        itemGrid.draw(g2, x, y, items, selectedIndex);

        Point mp = gp.getMousePosition();
        int hoverIdx = -1;
        if (mp != null) {
            hoverIdx = itemGrid.indexFromPoint(mp.x, mp.y, x, y);
            if (hoverIdx >= 0) selectedIndex = hoverIdx;
        }
        int infoIdx = hoverIdx >= 0 ? hoverIdx : selectedIndex;
        if (infoIdx >= 0 && infoIdx < items.size()) {
            drawItemInfo(g2, items.get(infoIdx), mp != null ? mp.x : x, mp != null ? mp.y : y);
        }
        if (menuVisible) {
            drawContextMenu(g2);
        }
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

        public void moveCursor(int dx, int dy) {
            int cols = itemGrid.getCols();
            int rows = itemGrid.getRows();
            if (selectedIndex < 0) selectedIndex = 0;
            int col = selectedIndex % cols;
            int row = selectedIndex / cols;
            col = Math.max(0, Math.min(cols - 1, col + dx));
            row = Math.max(0, Math.min(rows - 1, row + dy));
            selectedIndex = row * cols + col;
        }

        public void activateSelected() {
            var items = gp.getPlayer().getBag().all();
            if (selectedIndex >= 0 && selectedIndex < items.size()) {
                Item it = items.get(selectedIndex);
                it.perform(gp.getPlayer(), it.actions().get(0));
            }
        }

        public void handleMouse(MouseEvent e) {
            int x = gp.getTileSize();
            int y = gp.getTileSize() * 6;
            var items = gp.getPlayer().getBag().all();
            int slot = itemGrid.indexFromPoint(e.getX(), e.getY(), x, y);
            if (slot >= 0 && slot < items.size()) {
                selectedIndex = slot;
                if (e.getButton() == MouseEvent.BUTTON3) {
                    menuVisible = true;
                    menuX = e.getX();
                    menuY = e.getY();
                    menuSlotIdx = slot;
                    menuActions = new ArrayList<>(items.get(slot).actions());
                } else if (e.getButton() == MouseEvent.BUTTON1 && menuVisible) {
                    handleMenuClick(e.getX(), e.getY());
                }
            } else {
                menuVisible = false;
            }
        }

        private void handleMenuClick(int mx, int my) {
            int w = 120;
            int lineH = 20;
            int h = lineH * menuActions.size();
            if (mx < menuX || mx > menuX + w || my < menuY || my > menuY + h) {
                menuVisible = false;
                return;
            }
            int idx = (my - menuY) / lineH;
            if (idx >= 0 && idx < menuActions.size()) {
                Item it = gp.getPlayer().getBag().all().get(menuSlotIdx);
                it.perform(gp.getPlayer(), menuActions.get(idx));
                menuVisible = false;
            }
        }

        private void drawItemInfo(Graphics2D g2, Item item, int x, int y) {
            String text = item.getName() + "\n" + item.getDecription();
            String[] lines = text.split("\n");
            int padding = 8;
            int width = 0;
            int lineH = g2.getFontMetrics().getHeight();
            for (String line : lines) {
                width = Math.max(width, g2.getFontMetrics().stringWidth(line));
            }
            int height = lineH * lines.length + padding * 2;
            HUDUtils.drawSubWindow(g2, x, y, width + padding * 2, height,
                    new Color(40,40,40,200), new Color(200,200,200));
            int yy = y + padding + g2.getFontMetrics().getAscent();
            for (String line : lines) {
                g2.setColor(Color.WHITE);
                g2.drawString(line, x + padding, yy);
                yy += lineH;
            }
        }

        private void drawContextMenu(Graphics2D g2) {
            int w = 120;
            int lineH = 20;
            int h = lineH * menuActions.size();
            HUDUtils.drawSubWindow(g2, menuX, menuY, w, h,
                    new Color(50,50,50,220), new Color(200,200,200));
            int yy = menuY + lineH - 5;
            for (ItemAction act : menuActions) {
                g2.setColor(Color.WHITE);
                g2.drawString(act.label(), menuX + 5, yy);
                yy += lineH;
            }
        }
	
}























