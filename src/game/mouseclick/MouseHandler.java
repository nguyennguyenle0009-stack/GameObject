package game.mouseclick;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import game.enums.MoveMode;
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
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON3) {
			targetX = e.getX();
			targetY = e.getY();
			moving = true;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
