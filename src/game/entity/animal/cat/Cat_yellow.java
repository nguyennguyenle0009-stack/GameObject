package game.entity.animal.cat;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Random;

import game.entity.Entity;
import game.main.GamePanel;
import game.util.UtilityTool;

public class Cat_yellow extends Entity {

    public Cat_yellow(GamePanel gp) {
        super(gp);
        this.setWorldX(200);
        this.setWorldY(200);
        this.setSpeed(1);
        this.setDirection("down");
        this.setCollisionArea(new Rectangle( 19, 38, 10, 10));
        
        this.setSpriteNum(1);
        this.setSpriteCouter(0);

        getImageCat();
    }
    
	public void getImageCat() {
		try {
			setUp1(setup("/data/animal/cat/catYellow/Cat_yellow_up_1"));
			setUp2(setup("/data/animal/cat/catYellow/Cat_yellow_up_2")); 
			setDown1(setup("/data/animal/cat/catYellow/Cat_yellow_down_1")); 
			setDown2(setup("/data/animal/cat/catYellow/Cat_yellow_down_2")); 
			setLeft1(setup("/data/animal/cat/catYellow/Cat_yellow_left_1")); 
			setLeft2(setup("/data/animal/cat/catYellow/Cat_yellow_left_2")); 
			setRight1(setup("/data/animal/cat/catYellow/Cat_yellow_right_1")); 
			setRight2(setup("/data/animal/cat/catYellow/Cat_yellow_right_2")); 
		}catch (Exception e) {
			e.getStackTrace();
		}
	}

	@Override
	public void draw(Graphics2D g2, GamePanel gp) {
	    int screenX = getWorldX() - gp.getPlayer().getWorldX() + gp.getPlayer().getScreenX();
	    int screenY = getWorldY() - gp.getPlayer().getWorldY() + gp.getPlayer().getScreenY();
	    // TÍNH OFFSET BIÊN GIỐNG TILE
	    int rightOffset  = gp.getScreenWidth()  - gp.getPlayer().getScreenX();
	    int bottomOffset = gp.getScreenHeight() - gp.getPlayer().getScreenY();
	    // KẸP THEO TRỤC X
	    if (gp.getPlayer().getScreenX() > gp.getPlayer().getWorldX()) {
	        // mép trái map: camera không cuộn được nữa
	        screenX = getWorldX();
	    } else if (rightOffset > gp.getWorldWidth() - gp.getPlayer().getWorldX()) {
	        // mép phải map
	        screenX = gp.getScreenWidth() - (gp.getWorldWidth() - getWorldX());
	    }
	    // KẸP THEO TRỤC Y
	    if (gp.getPlayer().getScreenY() > gp.getPlayer().getWorldY()) {
	        // mép trên map
	        screenY = getWorldY();
	    } else if (bottomOffset > gp.getWorldHeight() - gp.getPlayer().getWorldY()) {
	        // mép dưới map
	        screenY = gp.getScreenHeight() - (gp.getWorldHeight() - getWorldY());
	    }
	    // CHỈ VẼ KHI TRONG KHUNG HÌNH (giống điều kiện trong TileManager)
        if(UtilityTool.isInsidePlayerView(getWorldX(), getWorldY(), gp)) {
	        
	        // ✅ Vẽ hình ảnh mèo theo sprite
	        g2.drawImage(getDirectionImage(), screenX, screenY, gp.getTileSize(), gp.getTileSize(), null);

	        // (Debug) Vẽ hitbox
	        g2.setColor(Color.RED);
	        g2.drawRect(screenX + getCollisionArea().x, screenY + getCollisionArea().y,
	                    getCollisionArea().width, getCollisionArea().height);
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
}
