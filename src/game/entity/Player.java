package game.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import javax.imageio.ImageIO;

import game.interfaces.DrawableEntity;
import game.keyhandler.KeyHandler;
import game.main.GamePanel;
import game.mouseclick.MouseHandler;
import game.util.UtilityTool;

public class Player extends Entity implements DrawableEntity {
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
        this.screenX = gp.getScreenWidth() / 2 - (gp.getTileSize() / 2);//360
        this.screenY = gp.getScreenHeight() / 2 - (gp.getTileSize() / 2);//264
        setCollision();
		setDefaultValue();
		getImagePlayer();
	}
	
    private void setCollision() {
        setCollisionArea(new Rectangle( 19, 38, 10, 10));
        setCollisionDefaultX(getCollisionArea().x);
        setCollisionDefaultY(getCollisionArea().y);
    }
	
	public void getImagePlayer() {
		try {
			setUp1(setup("/data/player/kh_up_1"));
			setUp2(setup("/data/player/kh_up_2")); 
			setDown1(setup("/data/player/kh_down_1")); 
			setDown2(setup("/data/player/kh_down_2")); 
			setLeft1(setup("/data/player/kh_left_1")); 
			setLeft2(setup("/data/player/kh_left_2")); 
			setRight1(setup("/data/player/kh_right_1")); 
			setRight2(setup("/data/player/kh_right_2")); 
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
	    updateKeyboard();
	}
	
	private void updateKeyboard() {
		if(keyH.isUpPressed() == true || keyH.isDownPressed() == true 
				|| keyH.isLeftPressed() == true || keyH.isRightPressed() == true) {
			if(keyH.isUpPressed() == true) {  setDirection("up"); }
			if(keyH.isDownPressed() == true) {  setDirection("down"); }
			if(keyH.isLeftPressed() == true) {  setDirection("left"); }
			if(keyH.isRightPressed() == true) {  setDirection("right"); }
			checkCollision();
			moveIfCollisionNotDetected();
			checkAndChangeSpriteAnimation();
		} 
		else { resestSpriteToDefault(); }
	}
	
//  private void pickUpObject(int index) { 
//	  if (index != 999) { }
//  }
	
	@Override
	public void draw(Graphics2D g2) {
		int rightOffset = gp.getScreenWidth() - screenX;
		int x = checkCharacterPositionAtXAxis(rightOffset);
		int botOffSet = gp.getScreenHeight() - screenY;
		int y = checkCharacterPositionAtYAxis(botOffSet);
		g2.drawImage(getDirectionImage(), x, y, gp.getTileSize(), gp.getTileSize(), null);
        g2.setColor(Color.BLUE);
        g2.drawRect(x+getCollisionArea().x, y + getCollisionArea().y, getCollisionArea().width, getCollisionArea().height);

	}
	
	@Override
	public void draw(Graphics2D g2, GamePanel gp) {
		int rightOffset = gp.getScreenWidth() - screenX;
		int x = checkCharacterPositionAtXAxis(rightOffset);
		int botOffSet = gp.getScreenHeight() - screenY;
		int y = checkCharacterPositionAtYAxis(botOffSet);
		g2.drawImage(getDirectionImage(), x, y, null);
        g2.setColor(Color.BLUE);
        g2.drawRect(x+getCollisionArea().x, y + getCollisionArea().y, 
        		getCollisionArea().width, getCollisionArea().height);
	}
	
	//Kiểm tra màn hình có bị tràn ra khỏi bản đồ theo trục ngang không
	private int checkCharacterPositionAtXAxis(int rightOffset) {
		// Giới hạn khi nhân vật ở quá sát rìa trái bản đồ (camera không thể dịch sang trái hơn)
		if(screenX > getWorldX()) {
			// giới hạn tọa độ bên phải của nhân vật/camera trong bản đồ
			return getWorldX();
		}
		// rightOffset = khoảng cách từ nhân vật đến mép phải màn hình
		// Giới hạn khi camera ở quá sát rìa phải bản đồ (màn hình tràn ra ngoài)
		if(rightOffset > gp.getWorldWidth() - getWorldX()) {
			// vị trí nhân vật trên màn hình khi camera bị ghim ở mép phải bản đồ (bù trừ phần màn hình bị tràn).
			return gp.getScreenWidth() - (gp.getWorldWidth() - getWorldX());
		}
		// Nếu màn hình không bị tràn ra khỏi bản đồ trả về bình thường
		return screenX;
	}
	
	private int checkCharacterPositionAtYAxis(int botOffset) {
		if(screenY > getWorldY()) { return getWorldY(); }
		if(botOffset > gp.getWorldHeight() - getWorldY()) {
			return gp.getScreenHeight()	- (gp.getWorldHeight() - getWorldY());
		}
		return screenY;
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
	
    public BufferedImage setup(String imagePath) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(imagePath + ".png")));
        } 
        catch (IOException e) { e.printStackTrace(); }
        return UtilityTool.scaleImage(image, gp.getTileSize(), gp.getTileSize());
    }

	public int getScreenX() { return screenX; }
	public int getScreenY() { return screenY; }
}















