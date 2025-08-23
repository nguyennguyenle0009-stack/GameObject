package game.mouseclick;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import game.main.GamePanel;

public class MouseHandler implements MouseListener, MouseMotionListener {
    public int targetX, targetY;
    public boolean moving = false;
    GamePanel gp;
    private int mouseX, mouseY;

    public MouseHandler(GamePanel gp) { this.gp = gp; }

    @Override
    public void mouseClicked(MouseEvent e) { }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        if (gp.keyH.isiPressed()) {
            if (gp.getUi().handleInventoryMousePress(mouseX, mouseY, e.getButton())) {
                return;
            }
            return;
        }
        // Right mouse pressed
        if (e.getButton() == MouseEvent.BUTTON3) {
            //Tọa độ x,y trên màn hình
            int mouseX = e.getX();
            int mouseY = e.getY();
            // Chuyển từ tọa độ điểm đích từ màn hình sang tọa độ THẾ GIỚI (map)
            int worldX = gp.getPlayer().getWorldX() - gp.getPlayer().getScreenX() + mouseX;
            int worldY = gp.getPlayer().getWorldY() - gp.getPlayer().getScreenY() + mouseY;
            // Lưu điểm đích trong thế giới
            targetX = worldX;
            targetY = worldY;
            // Bật chế độ tự đi tới điểm đích
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
    public void mouseDragged(MouseEvent e) { mouseMoved(e); }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    public int getTargetX() { return targetX; }
    public MouseHandler setTargetX(int targetX) { this.targetX = targetX; return this; }
    public int getTargetY() { return targetY; }
    public MouseHandler setTargetY(int targetY) { this.targetY = targetY; return this; }
    public boolean isMoving() { return moving; }
    public MouseHandler setMoving(boolean moving) { this.moving = moving; return this; }

    public Point getMousePoint() { return new Point(mouseX, mouseY); }
}