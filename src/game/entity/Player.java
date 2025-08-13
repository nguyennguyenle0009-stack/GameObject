package game.entity;

import java.awt.Color;
import java.awt.Graphics2D;

import game.keyhandler.KeyHandler;
import game.main.GamePanel;
import game.mouseclick.MouseHandler;

public class Player extends Entity {
	
	KeyHandler keyH = new KeyHandler(gp);
	MouseHandler mouseH = new MouseHandler(gp);

	public Player(GamePanel gp, KeyHandler keyH, MouseHandler mounseH) {
		super(gp);
		
		this.keyH = keyH;
		this.mouseH = mounseH;
		
		setDefaultValue();
	}
	
	public void setDefaultValue() {
		setX(100); 
		setY(100);
		setSpeed(4);
	}
	
	@Override
	public void update() {
	    if(mouseH.moving == true) {
	    	updateClickMove();
	    }
	    else {
	    	updateKeyboard();
	    }
	}
	
	private void updateKeyboard() {
		if(keyH.upPressed == true) { setY(getY() - getSpeed()); }
		if(keyH.downPressed == true) { setY(getY() + getSpeed()); }
		if(keyH.leftPressed == true) { setX(getX() - getSpeed()); }
		if(keyH.rightPressed == true) { setX(getX() + getSpeed()); }
	}
	
	private void updateClickMove() {

			float dx = mouseH.targetX - getX();
			float dy = mouseH.targetY - getY();
			float dist = (float)Math.sqrt(dx * dx + dy * dy);
			
	        if (dist > speed) {
	        	setX(getX() + (int)(dx / dist * speed));
	        	setY(getY() + (int)(dy / dist * speed));
	        } else {
	        	setX(mouseH.targetX);
	        	setY(mouseH.targetY);
	            mouseH.moving = false;
	        }
		
		if(keyH.upPressed == true || keyH.downPressed == true || keyH.leftPressed == true || keyH.rightPressed == true) {
			mouseH.moving = false;
		}
	}
	
	@Override
	public void draw(Graphics2D g2) {
		g2.setColor(Color.white);
		g2.fillRect(getX(), getY(), gp.tileSize, gp.tileSize);
	}
}
