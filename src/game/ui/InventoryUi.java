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
import game.enums.Attr;
import game.main.GamePanel;

/**
 * Handles rendering and interaction for the player's inventory.
 * Extracted from {@link Ui} to keep responsibilities separated.
 */
public class InventoryUi {
    private final GamePanel gp;
    private final ItemGridUi itemGrid;

    private int selectedSlot = -1; // chỉ số toàn bộ danh sách
    private int hoverSlot = -1;    // chỉ số toàn bộ danh sách
    private int scrollOffset = 0;  // vị trí phần tử đầu tiên đang hiển thị
    private boolean contextVisible = false;
    private String[] contextOptions = new String[0];
    private int contextSelection = 0;
    private int contextX, contextY;
    private java.awt.Rectangle skillBtn = new java.awt.Rectangle();

    public InventoryUi(GamePanel gp) {
        this.gp = gp;
        // thu nhỏ ô item để tránh đè giao diện
        this.itemGrid = new ItemGridUi(Math.min(gp.getTileSize(), 32));
    }

    public void draw(Graphics2D g2) {
        int charH = gp.getTileSize() * 8;
        Dimension d = itemGrid.getPreferredSize();
        int gridX = gp.getTileSize() * 8; // default position with one tile gap after character panel
        int gridY = gp.getTileSize() * 2;
        // ensure grid within screen
        if (gridX + d.width > gp.getScreenWidth() - gp.getTileSize() / 2) {
            gridX = gp.getScreenWidth() - d.width - gp.getTileSize() / 2;
        }

        int outerX = gp.getTileSize() / 2;
        int outerY = gridY - gp.getTileSize() / 2; // keep margin similar to original
        int outerW = gridX + d.width + gp.getTileSize() / 2 - outerX;
        int outerH = Math.max(charH, d.height) + gp.getTileSize();
        HUDUtils.drawSubWindow(g2, outerX, outerY, outerW, outerH,
                new Color(40,40,40,180), Color.YELLOW);

        // Draw character panel on the left
        characterScreen(g2, gridY);

        var items = gp.getPlayer().getBag().all();
        int capacity = itemGrid.getCols() * itemGrid.getRows();
        scrollOffset = Math.max(0, Math.min(scrollOffset, Math.max(0, items.size() - capacity)));
        handleInventoryInput(items, gridX, gridY);
        hoverSlot = computeSlotIndex(gridX, gridY, gp.getMousePosition());

        int start = scrollOffset;
        int end = Math.min(start + capacity, items.size());
        List<Item> sub = items.subList(start, end);
        itemGrid.draw(g2, gridX, gridY, sub, selectedSlot - start, hoverSlot - start);

        int infoIdx = hoverSlot;
        if (infoIdx >= 0 && infoIdx < items.size()) {
            Point m = gp.getMousePosition();
            int tipX = (m != null ? m.x + 15 : gridX + d.width + 10);
            int tipY = (m != null ? m.y + 15 : gridY);
            drawItemTooltip(g2, tipX, tipY, items.get(infoIdx));
        }

        if (items.size() > capacity) {
            drawScrollBar(g2, gridX, gridY, d.height, items.size(), capacity);
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
                    return scrollOffset + r * cols + c;
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
        int cols = itemGrid.getCols();
        if (kh.isUpPressed()) {
            selectedSlot = Math.max(0, selectedSlot - cols);
            kh.setUpPressed(false);
        }
        if (kh.isDownPressed()) {
            selectedSlot = Math.min(items.size() - 1, selectedSlot + cols);
            kh.setDownPressed(false);
        }
        if (kh.isLeftPressed()) {
            selectedSlot = Math.max(0, selectedSlot - 1);
            kh.setLeftPressed(false);
        }
        if (kh.isRightPressed()) {
            selectedSlot = Math.min(items.size() - 1, selectedSlot + 1);
            kh.setRightPressed(false);
        }
        if (selectedSlot < scrollOffset) {
            scrollOffset = selectedSlot;
        } else if (selectedSlot >= scrollOffset + totalSlots) {
            scrollOffset = selectedSlot - totalSlots + 1;
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
        int gridIdx = slotIndex - scrollOffset;
        int r = gridIdx / cols;
        int c = gridIdx % cols;
        contextX = baseX + padding + c * (slotSize + gap) + slotSize;
        contextY = baseY + padding + r * (slotSize + gap);
        contextOptions = it.getActions();
        contextSelection = 0;
        contextVisible = true;
    }

    private void drawContextMenu(Graphics2D g2) {
        if (!contextVisible) return;
        int w = 120;
        int h = contextOptions.length * 18 + 8;
        HUDUtils.drawSubWindow(g2, contextX, contextY, w, h, new Color(40,40,40,200), new Color(200, 200, 200));
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 12f));
        for (int i = 0; i < contextOptions.length; i++) {
            int yy = contextY + 15 + i * 18;
            g2.setColor(i == contextSelection ? Color.YELLOW : Color.WHITE);
            g2.drawString(contextOptions[i], contextX + 8, yy);
        }
    }

    private void drawScrollBar(Graphics2D g2, int x, int y, int height, int itemCount, int capacity) {
        int padding = itemGrid.getPadding();
        int slotAreaHeight = height - padding * 2;
        int barHeight = (int) (slotAreaHeight * (capacity / (double) itemCount));
        int maxScroll = itemCount - capacity;
        int barY = (maxScroll > 0)
                ? (int) (slotAreaHeight * (scrollOffset / (double) maxScroll))
                : 0;
        int barX = x + itemGrid.getPreferredSize().width - 6;
        g2.setColor(new Color(200, 200, 200, 180));
        g2.fillRect(barX, y + padding + barY, 4, barHeight);
    }

    private void drawItemTooltip(Graphics2D g2, int x, int y, Item it) {
        String line1 = it.getName() + " x" + it.getQuantity();
        String line2 = it.getDecription();
        int padding = 10;
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 12f));
        int width = Math.max(g2.getFontMetrics().stringWidth(line1), g2.getFontMetrics().stringWidth(line2)) + padding * 2;
        int height = 32 + padding * 2;
        if (x + width > gp.getScreenWidth()) {
            x = gp.getScreenWidth() - width - 10;
        }
        if (y + height > gp.getScreenHeight()) {
            y = gp.getScreenHeight() - height - 10;
        }
        HUDUtils.drawSubWindow(g2, x, y, width, height, new Color(40,40,40,200), new Color(200, 200, 200));
        g2.setColor(Color.WHITE);
        g2.drawString(line1, x + padding, y + padding + 12);
        g2.drawString(line2, x + padding, y + padding + 28);
    }

    /**
     * Draws the character attribute box at a given vertical offset.
     *
     * @param topY starting Y position of the box
     */
    private void characterScreen(Graphics2D g2, int topY) {
        int x = gp.getTileSize();
        int y = topY;
        int width = x * 6;
        int height = gp.getTileSize() * 8; // keep character box size constant
        drawSubWindow(x, y, width, height, g2);
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 12f));
        int textX = x + 10;
        int textY = y + 20;

        var p = gp.getPlayer();
        var attrs = p.atts();

        int lineH = 15;
        g2.drawString("Realm: " + p.getRealmName(), textX, textY); textY += lineH;
        g2.drawString("Health: " + attrs.get(Attr.HEALTH) + "/" + attrs.getMax(Attr.HEALTH), textX, textY); textY += lineH;
        g2.drawString("Pep: " + attrs.get(Attr.PEP) + "/" + attrs.getMax(Attr.PEP), textX, textY); textY += lineH;
        g2.drawString("Spirit: " + attrs.get(Attr.SPIRIT) + "/" + p.getSpiritToNextLevel(), textX, textY); textY += lineH;
        g2.drawString("Attack: " + attrs.get(Attr.ATTACK), textX, textY); textY += lineH;
        g2.drawString("Def: " + attrs.get(Attr.DEF), textX, textY); textY += lineH;
        g2.drawString("Strength: " + attrs.get(Attr.STRENGTH), textX, textY); textY += lineH;
        g2.drawString("Sould: " + attrs.get(Attr.SOULD), textX, textY); textY += lineH;
        g2.drawString("Physique: " + p.getPhysique().getDisplay(), textX, textY); textY += lineH;
        g2.drawString("Affinity: " + p.getAffinityNames(), textX, textY); textY += lineH + 10;

        // Vẽ nút mở bảng công pháp
        int btnW = gp.getTileSize() * 4;
        int btnH = gp.getTileSize();
        int btnX = x + (width - btnW) / 2;
        int btnY = y + height - btnH - gp.getTileSize() / 2;
        HUDUtils.drawSubWindow(g2, btnX, btnY, btnW, btnH, new Color(40,40,40,200), Color.WHITE);
        g2.setColor(Color.WHITE);
        g2.drawString("Công pháp", btnX + 20, btnY + btnH - 10);
        skillBtn.setBounds(btnX, btnY, btnW, btnH);
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
        int baseX = gp.getTileSize() * 8;
        Dimension d = itemGrid.getPreferredSize();
        if (baseX + d.width > gp.getScreenWidth() - gp.getTileSize() / 2) {
            baseX = gp.getScreenWidth() - d.width - gp.getTileSize() / 2;
        }
        int baseY = gp.getTileSize() * 2; // match grid position in draw()
        if (skillBtn.contains(mx, my)) {
            gp.getUi().getSkillUi().toggle();
            return true;
        }

        int idx = computeSlotIndex(baseX, baseY, new Point(mx, my));
        var items = gp.getPlayer().getBag().all();
        if (idx >= 0 && idx < items.size()) {
            selectedSlot = idx;
            if (button == MouseEvent.BUTTON3) {
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