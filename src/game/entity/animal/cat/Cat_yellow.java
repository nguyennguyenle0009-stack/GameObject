package game.entity.animal.cat;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Random;

import game.entity.Entity;
import game.main.GamePanel;
import game.util.CameraHelper;
import game.util.UtilityTool;

public class Cat_yellow extends Entity {
	GamePanel gp;
    public Cat_yellow(GamePanel gp) {
        super(gp);
        this.setSpeed(1);
        this.setDirection("down");
        this.setScaleEntityX(24);
        this.setScaleEntityY(24);
        this.setCollisionArea(new Rectangle( 7, 14, 10, 10));
        
        this.setSpriteNum(1);
        this.setSpriteCouter(0);

        getImageCat();
        setDialogue();
    }
    
	public void getImageCat() {
		try {
			setUp1(setup("/data/animal/cat/catYellow/Cat_yellow_up_4"));
			setUp2(setup("/data/animal/cat/catYellow/Cat_yellow_up_2")); 
			setDown1(setup("/data/animal/cat/catYellow/Cat_yellow_down_1")); 
			setDown2(setup("/data/animal/cat/catYellow/Cat_yellow_down_4")); 
			setLeft1(setup("/data/animal/cat/catYellow/Cat_yellow_left_2")); 
			setLeft2(setup("/data/animal/cat/catYellow/Cat_yellow_left_3")); 
			setRight1(setup("/data/animal/cat/catYellow/Cat_yellow_right_2")); 
			setRight2(setup("/data/animal/cat/catYellow/Cat_yellow_right_4")); 
		}catch (Exception e) {
			e.getStackTrace();
		}
	}

	@Override
	public void draw(Graphics2D g2, GamePanel gp) {
		Point screenPos = CameraHelper.worldToScreen(getWorldX(), getWorldY(), gp);

        if(UtilityTool.isInsidePlayerView(getWorldX(), getWorldY(), gp)) {
	        
	        g2.drawImage(getDirectionImage(), screenPos.x, screenPos.y, getScaleEntityX(), getScaleEntityY(), null);
			if(gp.keyH.isDrawRect() == true) {
		        g2.setColor(Color.RED);
		        g2.drawRect(screenPos.x + getCollisionArea().x, screenPos.y + getCollisionArea().y,
		                    getCollisionArea().width, getCollisionArea().height);
			}
	    }
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

	@Override
	public void update() {
		setAcction();
        checkCollision();
        moveIfCollisionNotDetected();
        checkAndChangeSpriteAnimation();
	}

	@Override
	public void draw(Graphics2D g2) {
		// TODO Auto-generated method stub
		
	}
	
	public void setAcction() {
	    setActionLockCounter(getActionLockCounter() + 1); // tăng bộ đếm mỗi frame

	    if(getActionLockCounter() >= 120) { // sau ~2 giây (60 FPS)
	        Random random = new Random();
	        int i = random.nextInt(100) + 1;

	        if(i <= 25) { setDirection("up"); }
	        else if(i <= 50) { setDirection("down"); }
	        else if(i <= 75) { setDirection("left"); }
	        else { setDirection("right"); }

	        setActionLockCounter(0);
	    }
	}
	

    public void setDialogue() {
        getDialogues()[0] = "Meow meow";
//        getDialogues()[1] = "";
//        getDialogues()[2] = "";
//        getDialogues()[3] = "";
    }
    @Override
    public void speak() {
        super.speak();
    }
}
