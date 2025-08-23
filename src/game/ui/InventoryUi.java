package game.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.List;

import game.entity.item.Item;
import game.main.GamePanel;

/**
 * Handles rendering and interaction for the player's inventory.
 * Extracted from {@link Ui} to keep responsibilities separated.
 */
public class InventoryUi {
    private final GamePanel gp;
    private final ItemGridUi itemGrid;

    private int selectedSlot = -1;
    private int hoverSlot = -1;
    private boolean contextVisible = false;
    private String[] contextOptions = new String[0];
    private int contextSelection = 0;
    private int contextX, contextY;

    public InventoryUi(GamePanel gp) {
        this.gp = gp;
        this.itemGrid = new ItemGridUi(gp.getTileSize());
    }

    private int inventoryBaseX() {
        return gp.getTileSize() * 8;
    }

    private int inventoryBaseY() {
        return gp.getTileSize();
    }

    public void draw(Graphics2D g2) {
        // Draw character panel on the left
        characterScreen(g2);

        int x = inventoryBaseX(); // leave one tile gap after character panel
        int y = inventoryBaseY();

        var items = gp.getPlayer().getBag().all();
        handleInventoryInput(items, x, y);
        hoverSlot = computeSlotIndex(x, y, gp.getMousePosition());

        itemGrid.draw(g2, x, y, items, selectedSlot, hoverSlot);
        Dimension d = itemGrid.getPreferredSize();

        int infoIdx = hoverSlot >= 0 ? hoverSlot : selectedSlot;
        if (infoIdx >= 0 && infoIdx < items.size()) {
            drawItemTooltip(g2, x + d.width + 10, y, items.get(infoIdx));
        }

        drawContextMenu(g2);
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

    private void drawSubWindow(int x, int y, int width, int height, Graphics2D g2) {
        Color color = new Color(0, 0, 0, 210);
        g2.setColor(color);
        g2.fillRoundRect(x, y, width, height, 35, 35);

        color = new Color(255, 255, 255);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(x + 5, y + 5, width - 10, height - 10, 25, 25);
    }

    public boolean handleMousePress(int mx, int my, int button) {
        int baseX = inventoryBaseX();
        int baseY = inventoryBaseY();
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

    public ItemGridUi getItemGrid() {
        return itemGrid;
    }
}