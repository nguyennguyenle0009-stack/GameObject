package game.mouseclick;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import game.main.GamePanel;

public class MouseHandler implements MouseListener {
	
	public int targetX, targetY;
	public boolean moving = false;
	
	GamePanel gp;
	
	public MouseHandler(GamePanel gp) {
		this.gp = gp;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mousePressed(MouseEvent e) {
	    if (e.getButton() == MouseEvent.BUTTON3) {
	        int mouseX = e.getX();
	        int mouseY = e.getY();
	        // Chuyển đổi tọa độ màn hình sang tọa độ thế giới
	        int worldX = gp.getPlayer().getWorldX() - gp.getPlayer().getScreenX() + mouseX;
	        int worldY = gp.getPlayer().getWorldY() - gp.getPlayer().getScreenY() + mouseY;
	        targetX = worldX;
	        targetY = worldY;
	        moving = true;
	    }
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	public int getTargetX() {
		return targetX;
	}

	public MouseHandler setTargetX(int targetX) {
		this.targetX = targetX;
		return this;
	}

	public int getTargetY() {
		return targetY;
	}

	public MouseHandler setTargetY(int targetY) {
		this.targetY = targetY;
		return this;
	}

	public boolean isMoving() {
		return moving;
	}

	public MouseHandler setMoving(boolean moving) {
		this.moving = moving;
		return this;
	}

	
}
