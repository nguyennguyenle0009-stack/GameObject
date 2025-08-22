package game.mouseclick;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import game.entity.item.Item;
import game.main.GamePanel;
import game.ui.ItemGridUi;

/**
 * Xử lý thao tác chuột cho cả việc di chuyển và tương tác kho đồ.
 */
public class MouseHandler implements MouseListener, MouseMotionListener {
    /** Tọa độ đích khi click phải để di chuyển */
    public int targetX, targetY;
    /** Trạng thái tự động di chuyển */
    public boolean moving = false;
    /** Panel game */
    GamePanel gp;
    /** Tọa độ chuột hiện tại */
    private int mouseX, mouseY;
    /** Hiển thị menu ngữ cảnh trong kho đồ */
    private boolean showContext;
    /** Vị trí vẽ menu ngữ cảnh */
    private int contextX, contextY;
    /** Item đang chọn trong menu ngữ cảnh */
    private Item contextItem;

    public MouseHandler(GamePanel gp) { this.gp = gp; }

    @Override
    public void mouseClicked(MouseEvent e) { }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        // Nếu đang mở kho đồ
        if (gp.keyH.isiPressed()) {
            if (e.getButton() == MouseEvent.BUTTON3) {
                // mở menu ngữ cảnh
                contextItem = findItemAt(mouseX, mouseY);
                if (contextItem != null) {
                    showContext = true;
                    contextX = mouseX;
                    contextY = mouseY;
                }
            } else if (e.getButton() == MouseEvent.BUTTON1 && showContext) {
                // click vào chữ "sử dụng"
                if (mouseX >= contextX && mouseX <= contextX + 60 &&
                    mouseY >= contextY && mouseY <= contextY + 20) {
                    gp.getPlayer().useItem(contextItem);
                }
                showContext = false;
            }
            return;
        }
        // click phải di chuyển khi không mở kho đồ
        if (e.getButton() == MouseEvent.BUTTON3) {
            int worldX = gp.getPlayer().getWorldX() - gp.getPlayer().getScreenX() + mouseX;
            int worldY = gp.getPlayer().getWorldY() - gp.getPlayer().getScreenY() + mouseY;
            targetX = worldX;
            targetY = worldY;
            moving = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }

    @Override
    public void mouseDragged(MouseEvent e) { }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    /** Tìm item ở vị trí chuột trong kho đồ */
    private Item findItemAt(int mx, int my) {
        ItemGridUi grid = gp.getUi().getItemGrid();
        int x = gp.getTileSize();
        int y = gp.getTileSize() * 6;
        int cols = grid.getCols();
        int rows = grid.getRows();
        int slot = grid.getSlotSize();
        int gap = grid.getGap();
        int pad = grid.getPadding();
        int width = cols * slot + (cols - 1) * gap + pad * 2;
        int height = rows * slot + (rows - 1) * gap + pad * 2;
        if (mx < x || mx > x + width || my < y || my > y + height) return null;
        int relX = mx - x - pad;
        int relY = my - y - pad;
        int col = relX / (slot + gap);
        int row = relY / (slot + gap);
        if (col < 0 || col >= cols || row < 0 || row >= rows) return null;
        int idx = row * cols + col;
        var items = gp.getPlayer().getBag().all();
        return (idx < items.size()) ? items.get(idx) : null;
    }

    public int getMouseX() { return mouseX; }
    public int getMouseY() { return mouseY; }
    public boolean isShowContext() { return showContext; }
    public int getContextX() { return contextX; }
    public int getContextY() { return contextY; }
    public Item getContextItem() { return contextItem; }
    public void hideContext() { showContext = false; }

    public int getTargetX() { return targetX; }
    public MouseHandler setTargetX(int targetX) { this.targetX = targetX; return this; }
    public int getTargetY() { return targetY; }
    public MouseHandler setTargetY(int targetY) { this.targetY = targetY; return this; }
    public boolean isMoving() { return moving; }
    public MouseHandler setMoving(boolean moving) { this.moving = moving; return this; }
}
