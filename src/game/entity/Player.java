package game.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

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
		getImagePlayer();
	}
	
	public void getImagePlayer() {
		try {
			setUp1(ImageIO.read(getClass().getResourceAsStream("/data/player/kh_up_1.png")));
			setUp2(ImageIO.read(getClass().getResourceAsStream("/data/player/kh_up_2.png"))); 
			setDown1(ImageIO.read(getClass().getResourceAsStream("/data/player/kh_down_1.png"))); 
			setDown2(ImageIO.read(getClass().getResourceAsStream("/data/player/kh_down_2.png"))); 
			setLeft1(ImageIO.read(getClass().getResourceAsStream("/data/player/kh_left_1.png"))); 
			setLeft2(ImageIO.read(getClass().getResourceAsStream("/data/player/kh_left_2.png"))); 
			setRight1(ImageIO.read(getClass().getResourceAsStream("/data/player/kh_right_1.png"))); 
			setRight2(ImageIO.read(getClass().getResourceAsStream("/data/player/kh_right_2.png"))); 
		}catch (Exception e) {
			e.getStackTrace();
		}
	}
	
	public void setDefaultValue() {
		setX(100); 
		setY(100);
		setSpeed(4);
		setDirection("down");
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
		if(keyH.upPressed == true) { 
			setDirection("up");
			setY(getY() - getSpeed()); 
			}
		if(keyH.downPressed == true) { 
			setDirection("down");
			setY(getY() + getSpeed()); 
			}
		if(keyH.leftPressed == true) { 
			setDirection("left");
			setX(getX() - getSpeed()); 
			}
		if(keyH.rightPressed == true) { 
			setDirection("right");
			setX(getX() + getSpeed()); 
			}
	}
	
	private void updateClickMove() {

			float dx = mouseH.targetX - getX();
			float dy = mouseH.targetY - getY();
			float dist = (float)Math.sqrt(dx * dx + dy * dy);
			
	        if (dist > getSpeed()) {
	        	setX(getX() + (int)(dx / dist * getSpeed()));
	        	setY(getY() + (int)(dy / dist * getSpeed()));
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
		BufferedImage image = null;
		switch(getDirection()) {
		case "up":
			image = getUp1();
			break;
		case "down":
			image = getDown1();
			break;
		case "left":
			image = getLeft1();
			break;
		case "right":
			image = getRight1();
			break;
		}
		g2.drawImage(image, getX(), getY(), gp.tileSize, gp.tileSize, null);
	}
}















