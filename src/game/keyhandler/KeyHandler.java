package game.keyhandler;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import game.enums.MoveMode;
import game.main.GamePanel;

public class KeyHandler implements KeyListener {
	
	public boolean upPressed, downPressed, leftPressed, rightPressed;
	GamePanel gp;
	
	public KeyHandler(GamePanel gp) {
		this.gp = gp;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		if(code == KeyEvent.VK_W) {
			upPressed = true;
			gp.moveMode = MoveMode.KEYBOARD;
		}
		if(code == KeyEvent.VK_S) {
			downPressed = true;
			gp.moveMode = MoveMode.KEYBOARD;
		}
		if(code == KeyEvent.VK_A) {
			leftPressed = true;	
			gp.moveMode = MoveMode.KEYBOARD;
		}
		if(code == KeyEvent.VK_D) {
			rightPressed = true;
			gp.moveMode = MoveMode.KEYBOARD;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();
		if(code == KeyEvent.VK_W) {
			upPressed = false;
		}
		if(code == KeyEvent.VK_S) {
			downPressed = false;
		}
		if(code == KeyEvent.VK_A) {
			leftPressed = false;	
		}
		if(code == KeyEvent.VK_D) {
			rightPressed = false;
		}
		
	}
}
