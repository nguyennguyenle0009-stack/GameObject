package game.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.List;

import game.entity.item.Item;
import game.entity.item.equipment.Equipment;
import game.enums.Attr;
import game.enums.EquipSlot;
import game.main.GamePanel;

/**
 * Handles rendering and interaction for the player's inventory.
 * Extracted from {@link Ui} to keep responsibilities separated.
 */
public class InventoryUi {
    private final GamePanel gp;
    private final ItemGridUi itemGrid;

    private int selectedSlot = 0; // chỉ số item toàn cục đang chọn
    private int hoverSlot = -1;   // chỉ số item toàn cục đang trỏ vào
    private int scrollOffset = 0; // vị trí bắt đầu của trang hiện tại
    // Lưu lại toạ độ khung để dùng cho cuộn bằng chuột.
    private int lastGridX, lastGridY;
    private java.awt.Dimension lastDim = new java.awt.Dimension();
    private boolean contextVisible = false;
    private String[] contextOptions = new String[0];
    private int contextSelection = 0;
    private int contextX, contextY;
    private java.awt.Rectangle skillBtn = new java.awt.Rectangle();
    private Rectangle[] equipSlots = new Rectangle[EquipSlot.values().length];
    private int equipHover = -1;
    private static final EquipSlot[] SLOT_ORDER = {
        EquipSlot.HELMET, EquipSlot.ARMOR, EquipSlot.BOOTS,
        EquipSlot.PANTS, EquipSlot.NECKLACE, EquipSlot.TALISMAN,
        EquipSlot.RING1, EquipSlot.RING2, EquipSlot.WEAPON1, EquipSlot.WEAPON2
    };

    public InventoryUi(GamePanel gp) {
        this.gp = gp;
        this.itemGrid = new ItemGridUi(gp.getTileSize());
    }

    public void draw(Graphics2D g2) {
        Font oldFont = g2.getFont();
        try {
        Dimension d = itemGrid.getPreferredSize();
        int gridX = gp.getTileSize() * 8;
        int gridY = gp.getTileSize() * 2;
        if (gridX + d.width > gp.getScreenWidth() - gp.getTileSize() / 2) {
            gridX = gp.getScreenWidth() - d.width - gp.getTileSize() / 2;
        }

        int attrH = gp.getTileSize() * 6;
        int outerX = gp.getTileSize() / 2;
        int outerY = gridY - gp.getTileSize() / 2;
        int outerW = gridX + d.width + gp.getTileSize() / 2 - outerX;
        int outerH = d.height + attrH + gp.getTileSize();
        HUDUtils.drawSubWindow(g2, outerX, outerY, outerW, outerH,
                new Color(40,40,40,180), Color.YELLOW);

        drawEquipmentPanel(g2, gp.getTileSize(), gridY);

        var items = gp.getPlayer().getBag().all();
        handleInventoryInput(items, gridX, gridY);
        int hoverLocal = computeSlotIndex(gridX, gridY, gp.getMousePosition());
        hoverSlot = (hoverLocal >= 0) ? scrollOffset + hoverLocal : -1;
        int capacity = gp.getPlayer().getBag().getCapacity();
        lastGridX = gridX;
        lastGridY = gridY;
        lastDim.setSize(d);
        itemGrid.draw(g2, gridX, gridY, items, selectedSlot, hoverSlot, scrollOffset, capacity);

        int infoIdx = hoverSlot;
        if (infoIdx >= 0 && infoIdx < items.size()) {
            Point m = gp.getMousePosition();
            int tipX = (m != null ? m.x + 15 : gridX + d.width + 10);
            int tipY = (m != null ? m.y + 15 : gridY);
            drawItemTooltip(g2, tipX, tipY, items.get(infoIdx));
        }

        if (equipHover >= 0) {
            Equipment eq = gp.getPlayer().getEquips().get(SLOT_ORDER[equipHover]);
            if (eq != null) {
                Point m = gp.getMousePosition();
                int tipX = (m != null ? m.x + 15 : gridX);
                int tipY = (m != null ? m.y + 15 : gridY);
                drawItemTooltip(g2, tipX, tipY, eq);
            }
        }

        int attrX = gridX;
        int attrY = gridY + d.height + gp.getTileSize() / 2;
        drawAttributePanel(g2, attrX, attrY, d.width, attrH);

        drawContextMenu(g2);
        } finally {
            g2.setFont(oldFont);
        }
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
        int cols = itemGrid.getCols();
        int rows = itemGrid.getRows();
        int visible = cols * rows;
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

        if (kh.isUpPressed()) { selectedSlot -= cols; kh.setUpPressed(false); }
        if (kh.isDownPressed()) { selectedSlot += cols; kh.setDownPressed(false); }
        if (kh.isLeftPressed()) { selectedSlot--; kh.setLeftPressed(false); }
        if (kh.isRightPressed()) { selectedSlot++; kh.setRightPressed(false); }

        int maxIndex = Math.max(0, gp.getPlayer().getBag().getCapacity() - 1);
        if (selectedSlot < 0) selectedSlot = 0;
        if (selectedSlot > maxIndex) selectedSlot = maxIndex;

        // Điều chỉnh offset để item được chọn luôn nằm trong trang
        if (selectedSlot < scrollOffset) {
            scrollOffset = (selectedSlot / cols) * cols;
        } else if (selectedSlot >= scrollOffset + visible) {
            scrollOffset = (selectedSlot / cols - rows + 1) * cols;
        }
        int maxOffset = Math.max(0, gp.getPlayer().getBag().getCapacity() - visible);
        if (scrollOffset > maxOffset) scrollOffset = maxOffset;
        if (scrollOffset < 0) scrollOffset = 0;

        if (kh.isEnterPressed()) {
            if (selectedSlot >= 0 && selectedSlot < items.size()) {
                openContextMenu(baseX, baseY, selectedSlot - scrollOffset, items.get(selectedSlot));
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
        if (x + width > gp.getScreenWidth()) {
            x = gp.getScreenWidth() - width - 10;
        }
        if (y + height > gp.getScreenHeight()) {
            y = gp.getScreenHeight() - height - 10;
        }
        HUDUtils.drawSubWindow(g2, x, y, width, height, new Color(40,40,40,200), new Color(200, 200, 200));
        g2.setColor(Color.WHITE);
        g2.drawString(line1, x + padding, y + padding + 15);
        g2.drawString(line2, x + padding, y + padding + 35);
    }

    /** Xử lý cuộn bằng con lăn chuột. */
    public void handleMouseWheel(int rotation, int mx, int my) {
        if (!(mx >= lastGridX && mx <= lastGridX + lastDim.width &&
              my >= lastGridY && my <= lastGridY + lastDim.height)) {
            return;
        }
        int cols = itemGrid.getCols();
        int rows = itemGrid.getRows();
        int visible = cols * rows;
        int total = gp.getPlayer().getBag().getCapacity();
        int maxOffset = Math.max(0, total - visible);
        scrollOffset += Integer.signum(rotation) * cols;
        if (scrollOffset < 0) scrollOffset = 0;
        if (scrollOffset > maxOffset) scrollOffset = maxOffset;
    }

    /**
     * Draws the character attribute box at a given vertical offset.
     *
     * @param topY starting Y position of the box
     */
    private void drawAttributePanel(Graphics2D g2, int x, int y, int width, int height) {
        drawSubWindow(x, y, width, height, g2);
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 12f));
        int textX = x + 10;
        int textY = y + 20;

        var p = gp.getPlayer();
        var attrs = p.atts();

        g2.drawString("Realm: " + p.getRealmName(), textX, textY); textY += 15;
        g2.drawString("Health: " + attrs.get(Attr.HEALTH) + "/" + attrs.getMax(Attr.HEALTH), textX, textY); textY += 15;
        g2.drawString("Pep: " + attrs.get(Attr.PEP) + "/" + attrs.getMax(Attr.PEP), textX, textY); textY += 15;
        g2.drawString("Spirit: " + attrs.get(Attr.SPIRIT) + "/" + p.getSpiritToNextLevel(), textX, textY); textY += 15;
        g2.drawString("Attack: " + attrs.get(Attr.ATTACK), textX, textY); textY += 15;
        g2.drawString("Def: " + attrs.get(Attr.DEF), textX, textY); textY += 15;
        g2.drawString("Strength: " + attrs.get(Attr.STRENGTH), textX, textY); textY += 15;
        g2.drawString("Sould: " + attrs.get(Attr.SOULD), textX, textY); textY += 15;
        var phys = p.getPhysique();
        String physName = (phys != null) ? phys.getDisplay() : "Unknown";
        g2.drawString("Physique: " + physName, textX, textY); textY += 15;
        g2.drawString("Affinity: " + p.getAffinityNames(), textX, textY); textY += 20;

        int btnW = gp.getTileSize() * 3;
        int btnH = gp.getTileSize() / 2;
        int btnX = x + (width - btnW) / 2;
        int btnY = y + height - btnH - 10;
        HUDUtils.drawSubWindow(g2, btnX, btnY, btnW, btnH, new Color(40,40,40,200), Color.WHITE);
        g2.setColor(Color.WHITE);
        g2.drawString("Công pháp", btnX + 10, btnY + btnH - 5);
        skillBtn.setBounds(btnX, btnY, btnW, btnH);
    }

    private void drawEquipmentPanel(Graphics2D g2, int x, int y) {
        int padding = 8;
        int charSize = gp.getTileSize() * 3;
        int slotSize = itemGrid.getSlotSize();
        int gap = itemGrid.getGap();
        int width = charSize + padding * 2 + gap + (slotSize + gap) * 5;
        int height = Math.max(charSize, (slotSize + gap) * 2) + padding * 2;
        HUDUtils.drawSubWindow(g2, x, y, width, height, new Color(0,80,120,180), Color.CYAN);

        int charX = x + padding;
        int charY = y + padding;
        g2.setColor(new Color(128,0,128,200));
        g2.fillRect(charX, charY, charSize, charSize);
        g2.drawImage(gp.getPlayer().getDown1(), charX, charY, charSize, charSize, null);

        int startX = charX + charSize + gap;
        int startY = charY;
        Point mouse = gp.getMousePosition();
        equipHover = -1;
        for (int i = 0; i < SLOT_ORDER.length; i++) {
            int cx = startX + (i % 5) * (slotSize + gap);
            int cy = startY + (i / 5) * (slotSize + gap);
            Rectangle rect = new Rectangle(cx, cy, slotSize, slotSize);
            equipSlots[i] = rect;
            g2.setColor(new Color(90,90,90,220));
            g2.fillRoundRect(cx, cy, slotSize, slotSize, 10, 10);
            g2.setColor(new Color(0,0,0,160));
            g2.drawRoundRect(cx, cy, slotSize, slotSize, 10, 10);

            Equipment eq = gp.getPlayer().getEquips().get(SLOT_ORDER[i]);
            if (eq != null) {
                g2.drawImage(eq.getIcon(), cx + 4, cy + 4, slotSize - 8, slotSize - 8, null);
            }
            if (mouse != null && rect.contains(mouse)) {
                equipHover = i;
                g2.setColor(Color.YELLOW);
                g2.setStroke(new BasicStroke(3f));
                g2.drawRoundRect(cx, cy, slotSize, slotSize, 10, 10);
                g2.setStroke(new BasicStroke(1f));
            }
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

        for (int i = 0; i < equipSlots.length; i++) {
            Rectangle r = equipSlots[i];
            if (r != null && r.contains(mx, my)) {
                Equipment eq = gp.getPlayer().getEquips().get(SLOT_ORDER[i]);
                if (button == MouseEvent.BUTTON3 && eq != null) {
                    gp.getPlayer().unequip(SLOT_ORDER[i]);
                }
                return true;
            }
        }

        int local = computeSlotIndex(baseX, baseY, new Point(mx, my));
        var items = gp.getPlayer().getBag().all();
        if (local >= 0 && local < itemGrid.getCols() * itemGrid.getRows()) {
            int global = scrollOffset + local;
            selectedSlot = global;
            if (button == MouseEvent.BUTTON3 && global < items.size()) {
                openContextMenu(baseX, baseY, local, items.get(global));
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