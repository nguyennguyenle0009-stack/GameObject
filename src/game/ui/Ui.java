package game.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
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

        // Inventory interaction state
        private int selectedSlot = -1;
        private int hoverSlot = -1;
        private boolean contextVisible = false;
        private String[] contextOptions = new String[0];
        private int contextSelection = 0;
        private int contextX, contextY;

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
                }
        }

    private void drawInventory(Graphics2D g2) {
        int x = gp.getTileSize();
        int y = gp.getTileSize() * 6;

        var items = gp.getPlayer().getBag().all();
        handleInventoryInput(items, x, y);
        itemGrid.draw(g2, x, y, items );
        Dimension d = itemGrid.getPreferredSize();
        hoverSlot = computeSlotIndex(x, y, gp.getMousePosition());

        if(selectedSlot >= 0) {
            drawSlotHighlight(g2, x, y, selectedSlot, Color.YELLOW);
        }
        if(hoverSlot >= 0) {
            drawSlotHighlight(g2, x, y, hoverSlot, new Color(255,255,255,120));
        }

        int infoIdx = hoverSlot >= 0 ? hoverSlot : selectedSlot;
        if(infoIdx >= 0 && infoIdx < items.size()) {
            drawItemTooltip(g2, x + d.width + 10, y, items.get(infoIdx));
        }

        drawContextMenu(g2);
    }

    private void drawSlotHighlight(Graphics2D g2, int baseX, int baseY, int index, Color color) {
        int cols = itemGrid.getCols();
        int slotSize = itemGrid.getSlotSize();
        int gap = itemGrid.getGap();
        int padding = itemGrid.getPadding();

        int r = index / cols;
        int c = index % cols;
        int xx = baseX + padding + c * (slotSize + gap);
        int yy = baseY + padding + r * (slotSize + gap);

        g2.setColor(color);
        g2.setStroke(new BasicStroke(3f));
        g2.drawRoundRect(xx, yy, slotSize, slotSize, 10, 10);
    }

    private int computeSlotIndex(int originX, int originY, Point mouse) {
        if (mouse == null) return -1;
        int cols = itemGrid.getCols();
        int rows = itemGrid.getRows();
        int slotSize = itemGrid.getSlotSize();
        int gap = itemGrid.getGap();
        int padding = itemGrid.getPadding();
        int startX = originX + padding;
        int startY = originY + padding;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int xx = startX + c * (slotSize + gap);
                int yy = startY + r * (slotSize + gap);
                if (mouse.x >= xx && mouse.x < xx + slotSize && mouse.y >= yy && mouse.y < yy + slotSize) {
                    return r * cols + c;
                }
            }
        }
        return -1;
    }

    private void handleInventoryInput(List<Item> items, int baseX, int baseY) {
        var kh = gp.keyH;
        int totalSlots = itemGrid.getCols() * itemGrid.getRows();
        if (contextVisible) {
            if (kh.isUpPressed()) {
                contextSelection = (contextSelection - 1 + contextOptions.length) % contextOptions.length;
                kh.setUpPressed(false);
            }
            if (kh.isDownPressed()) {
                contextSelection = (contextSelection + 1) % contextOptions.length;
                kh.setDownPressed(false);
            }
            if (kh.isEnterPressed()) {
                if (selectedSlot >= 0 && selectedSlot < items.size()) {
                    Item it = items.get(selectedSlot);
                    it.performAction(gp.getPlayer(), contextOptions[contextSelection]);
                }
                contextVisible = false;
                kh.setEnterPressed(false);
            }
            return;
        }
        if (kh.isUpPressed()) {
            selectedSlot = (selectedSlot - itemGrid.getCols() + totalSlots) % totalSlots;
            kh.setUpPressed(false);
        }
        if (kh.isDownPressed()) {
            selectedSlot = (selectedSlot + itemGrid.getCols()) % totalSlots;
            kh.setDownPressed(false);
        }
        if (kh.isLeftPressed()) {
            selectedSlot = (selectedSlot - 1 + totalSlots) % totalSlots;
            kh.setLeftPressed(false);
        }
        if (kh.isRightPressed()) {
            selectedSlot = (selectedSlot + 1) % totalSlots;
            kh.setRightPressed(false);
        }
        if (kh.isEnterPressed()) {
            if (selectedSlot >= 0 && selectedSlot < items.size()) {
                openContextMenu(baseX, baseY, selectedSlot, items.get(selectedSlot));
            }
            kh.setEnterPressed(false);
        }
    }

    private void openContextMenu(int baseX, int baseY, int slotIndex, Item it) {
        int cols = itemGrid.getCols();
        int slotSize = itemGrid.getSlotSize();
        int gap = itemGrid.getGap();
        int padding = itemGrid.getPadding();
        int r = slotIndex / cols;
        int c = slotIndex % cols;
        contextX = baseX + padding + c * (slotSize + gap) + slotSize;
        contextY = baseY + padding + r * (slotSize + gap);
        contextOptions = it.getActions();
        contextSelection = 0;
        contextVisible = true;
    }

    public boolean handleInventoryMousePress(int mx, int my, int button) {
        int baseX = gp.getTileSize();
        int baseY = gp.getTileSize() * 6;
        int idx = computeSlotIndex(baseX, baseY, new Point(mx, my));
        var items = gp.getPlayer().getBag().all();
        if (idx >= 0 && idx < itemGrid.getCols() * itemGrid.getRows()) {
            selectedSlot = idx;
            if (button == MouseEvent.BUTTON3 && idx < items.size()) {
                openContextMenu(baseX, baseY, idx, items.get(idx));
            } else if (button == MouseEvent.BUTTON1) {
                contextVisible = false;
            }
            return true;
        }
        contextVisible = false;
        return false;
    }

    private void drawContextMenu(Graphics2D g2) {
        if (!contextVisible) return;
        int w = 120;
        int h = contextOptions.length * 20 + 10;
        HUDUtils.drawSubWindow(g2, contextX, contextY, w, h, new Color(40,40,40,200), new Color(200, 200, 200));
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 16f));
        for (int i = 0; i < contextOptions.length; i++) {
            int yy = contextY + 20 + i * 20;
            g2.setColor(i == contextSelection ? Color.YELLOW : Color.WHITE);
            g2.drawString(contextOptions[i], contextX + 10, yy);
        }
    }

    private void drawItemTooltip(Graphics2D g2, int x, int y, Item it) {
        String line1 = it.getName() + " x" + it.getQuantity();
        String line2 = it.getDecription();
        int padding = 10;
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 16f));
        int width = Math.max(g2.getFontMetrics().stringWidth(line1), g2.getFontMetrics().stringWidth(line2)) + padding * 2;
        int height = 40 + padding * 2;
        HUDUtils.drawSubWindow(g2, x, y, width, height, new Color(40,40,40,200), new Color(200, 200, 200));
        g2.setColor(Color.WHITE);
        g2.drawString(line1, x + padding, y + padding + 15);
        g2.drawString(line2, x + padding, y + padding + 35);
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

        public ItemGridUi getItemGrid() { return itemGrid; }

}
