package game.entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import game.keyhandler.KeyHandler;
import game.main.GamePanel;
import game.mouseclick.MouseHandler;

public class Player extends Entity {
	
	// Dùng để xử lý bàn phím và chuột
	KeyHandler keyH = new KeyHandler(gp);
	MouseHandler mouseH = new MouseHandler(gp);
	
	// Vị trí nhân vật trên màn hình (luôn ở giữa)
    private final int screenX;
    private final int screenY;

	public Player(GamePanel gp, KeyHandler keyH, MouseHandler mounseH) {
		super(gp);
		
		this.keyH = keyH;
		this.mouseH = mounseH;
		
        this.screenX = gp.getScreenWidth() / 2 - (gp.getTileSize() / 2);
        this.screenY = gp.getScreenHeight() / 2 - (gp.getTileSize() / 2);
		
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
		setWorldX(100); 
		setWorldY(100);
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
				setWorldY(getWorldY() - getSpeed()); 
			}
			if(keyH.isDownPressed() == true) { 
				setDirection("down");
				setWorldY(getWorldY() + getSpeed()); 
			}
			if(keyH.isLeftPressed() == true) { 
				setDirection("left");
				setWorldX(getWorldX() - getSpeed()); 
			}
			if(keyH.isRightPressed() == true) { 
				setDirection("right");
				setWorldX(getWorldX() + getSpeed()); 
			}
			checkAndChangeSpriteAnimation();
		}
	}
	
	private void updateClickMove() {
	    float dx = mouseH.targetX - getWorldX();
	    float dy = mouseH.targetY - getWorldY();
	    float dist = (float)Math.sqrt(dx * dx + dy * dy);

	    if (Math.abs(dx) > Math.abs(dy)) {
	        setDirection(dx < 0 ? "left" : "right");
	    } else {
	        setDirection(dy < 0 ? "up" : "down");
	    }

	    if (dist > getSpeed()) {
	        setWorldX(getWorldX() + (int)(dx / dist * getSpeed()));
	        setWorldY(getWorldY() + (int)(dy / dist * getSpeed()));
	    } else {
	        setWorldX(mouseH.targetX);
	        setWorldY(mouseH.targetY);
	        mouseH.moving = false;
	    }

	    checkAndChangeSpriteAnimation();

	    if (keyH.isUpPressed() || keyH.isDownPressed() || keyH.isLeftPressed() || keyH.isRightPressed()) {
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
		g2.drawImage(getDirectionImage(), screenX, screenY, gp.getTileSize(), gp.getTileSize(), null);
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

	public int getScreenX() {
		return screenX;
	}

	public int getScreenY() {
		return screenY;
	}
	
	
}















