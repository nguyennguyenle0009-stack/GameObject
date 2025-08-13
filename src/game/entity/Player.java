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
		
		setSpriteCouter(0);
		setSpriteNum(1);
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
		if(keyH.isUpPressed() == true || keyH.isDownPressed() == true 
				|| keyH.isLeftPressed() == true || keyH.isRightPressed() == true) {
			if(keyH.isUpPressed() == true) { 
				setDirection("up");
				setY(getY() - getSpeed()); 
			}
			if(keyH.isDownPressed() == true) { 
				setDirection("down");
				setY(getY() + getSpeed()); 
			}
			if(keyH.isLeftPressed() == true) { 
				setDirection("left");
				setX(getX() - getSpeed()); 
			}
			if(keyH.isRightPressed() == true) { 
				setDirection("right");
				setX(getX() + getSpeed()); 
			}
			checkAndChangeSpriteAnimation();
		}
	}
	
	private void updateClickMove() {
			float dx = mouseH.targetX - getX();
			float dy = mouseH.targetY - getY();
			float dist = (float)Math.sqrt(dx * dx + dy * dy);
		    if (Math.abs(dx) > Math.abs(dy)) {
		        if (dx < 0) {
		            setDirection("left");
		        } else {
		            setDirection("right");
		        }
		    } else {
		        if (dy < 0) {
		            setDirection("up");
		        } else {
		            setDirection("down");
		        }
		    }
	        if (dist > getSpeed()) {
	        	setX(getX() + (int)(dx / dist * getSpeed()));
	        	setY(getY() + (int)(dy / dist * getSpeed()));
	        } else {
	        	setX(mouseH.targetX);
	        	setY(mouseH.targetY);
	            mouseH.moving = false;
	        }
	        checkAndChangeSpriteAnimation();
		if(keyH.isUpPressed() == true || keyH.isDownPressed() == true 
				|| keyH.isLeftPressed() == true || keyH.isRightPressed() == true) {
			mouseH.moving = false;
		}
	}
	
	public void checkAndChangeSpriteAnimation() {
		setSpriteCouter(getSpriteCouter() + 1);
		if(getSpriteCouter() > 10) {
			if(getSpriteNum() == 1) {
				setSpriteNum(2);
			}
			else if(getSpriteNum() == 2) {
				setSpriteNum(1);
			}
			setSpriteCouter(0);
		}
	}
	
	@Override
	public void draw(Graphics2D g2) {
		g2.drawImage(getDirectionImage(), getX(), getY(), gp.tileSize, gp.tileSize, null);
	}
	

	private BufferedImage getDirectionImage() {
		BufferedImage image = null;
		switch(getDirection()) {
		case "up":
			if(getSpriteNum() == 1) { image = getUp1(); }
			if(getSpriteNum() == 2) { image = getUp2(); }
			break;
		case "down":
			if(getSpriteNum() == 1) { image = getDown1(); }
			if(getSpriteNum() == 2) { image = getDown2(); }
			break;
		case "left":
			if(getSpriteNum() == 1) { image = getLeft1(); }
			if(getSpriteNum() == 2) { image = getLeft2(); }
			break;
		case "right":
			if(getSpriteNum() == 1) { image = getRight1(); }
			if(getSpriteNum() == 2) { image = getRight2(); }
			break;
		}
		return image;
	}
}















