package game.keyhandler;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import game.main.GamePanel;

public class KeyHandler implements KeyListener {
	private boolean upPressed, downPressed, leftPressed, rightPressed;
	private boolean checkDrawTime = false;
	GamePanel gp;
	
	public KeyHandler(GamePanel gp) { this.gp = gp; }
	@Override
	
	public void keyTyped(KeyEvent e) { }
	@Override
	
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		if(code == KeyEvent.VK_W) { upPressed = true; }
		if(code == KeyEvent.VK_S) { downPressed = true; }
		if(code == KeyEvent.VK_A) { leftPressed = true;	 }
		if(code == KeyEvent.VK_D) { rightPressed = true; }
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();
		if(code == KeyEvent.VK_W) { upPressed = false; }
		if(code == KeyEvent.VK_S) { downPressed = false; }
		if(code == KeyEvent.VK_A) { leftPressed = false; }
		if(code == KeyEvent.VK_D) { rightPressed = false; }
		if(code == KeyEvent.VK_T) {
			if(checkDrawTime == false) { checkDrawTime = true; }
			else { checkDrawTime = false; }
		}
	}

	public boolean isUpPressed() { return upPressed; }
	public KeyHandler setUpPressed(boolean upPressed) {
		this.upPressed = upPressed;
		return this;
	}
	public boolean isDownPressed() { return downPressed; }
	public KeyHandler setDownPressed(boolean downPressed) {
		this.downPressed = downPressed;
		return this;
	}
	public boolean isLeftPressed() { return leftPressed; }
	public KeyHandler setLeftPressed(boolean leftPressed) {
		this.leftPressed = leftPressed;
		return this;
	}
	public boolean isRightPressed() { return rightPressed; }
	public KeyHandler setRightPressed(boolean rightPressed) {
		this.rightPressed = rightPressed;
		return this;
	}
	public boolean isCheckDrawTime() { return checkDrawTime; }
	public KeyHandler setCheckDrawTime(boolean checkDrawTime) {
		this.checkDrawTime = checkDrawTime;
		return this;
	}
}
