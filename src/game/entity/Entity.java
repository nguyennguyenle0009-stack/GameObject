package game.entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import javax.imageio.ImageIO;

import game.interfaces.DrawableEntity;
import game.main.GamePanel;
import game.util.UtilityTool;

public abstract class Entity implements DrawableEntity {
	GamePanel gp;
	
	// Vị trí nhân vật trong bản đồ
	private int worldX, worldY;
	// Tốc độ
	private int speed;
	private BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;
	private String direction;
	private int spriteCouter;
	private int spriteNum;
	//Khu vựa va chạm
	private Rectangle collisionArea;
	private boolean collisionOn = false;
	private int collisionDefaultX, collisionDefaultY;
    private int resestTime;
    private int actionLockCounter;
    private int scaleEntityX;
    private int scaleEntityY;
    private String[] dialogues = new String[20];
    private int dialogueIndex = 0;
    private int index;
    private String name = "nguyen";
	
	public Entity(GamePanel gp) {
		this.gp = gp;
	}
	
	public abstract  void  update();
	public abstract  void draw(Graphics2D g2);
	
	   public void speak() {
	        if (dialogues[dialogueIndex] == null) {
	            setDialogueIndex(0);
	        }

	        gp.getUi().setCurrentDialogue(getDialogues()[dialogueIndex]);
	        dialogueIndex++;

	        switch (gp.getPlayer().getDirection()) {
	            case "up" -> setDirection("down");
	            case "down" -> setDirection("up");
	            case "left" -> setDirection("right");
	            case "right" -> setDirection("left");
	        }
	    }
	
	public void checkCollision() {
		setCollisionOn(false);
		gp.getCheckCollision().checkTile(this);
        gp.getCheckCollision().checkObject(this, false);
        gp.getCheckCollision().checkEntity(this, gp.getNpcs());
        gp.getCheckCollision().checkPlayer(this);
	}
	
	public void moveIfCollisionNotDetected() {
		if(isCollisionOn() == false) {
			switch(getDirection()) {
			case "up": setWorldY(getWorldY() - getSpeed()); break;
			case "down": setWorldY(getWorldY() + getSpeed()); break;
			case "left": setWorldX(getWorldX() - getSpeed()); break;
			case "right": setWorldX(getWorldX() + getSpeed()); break;
			
			}
		}
	}
	
	public void checkAndChangeSpriteAnimation() {
		setSpriteCouter(getSpriteCouter() + 1);
		if(getSpriteCouter() > 10) {
			if(getSpriteNum() == 1) { setSpriteNum(2); }
			else if(getSpriteNum() == 2) { setSpriteNum(1); }
			setSpriteCouter(0);
		}
	}
	
	// Resest sprite khi đứng im
	public void resestSpriteToDefault() {
		resestTime++;
		if(resestTime == 20) { setSpriteNum(1); resestTime = 0; }
	}
	
        public BufferedImage setup(String imagePath) {
            BufferedImage image = null;
            try {
                image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(imagePath + ".png")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return UtilityTool.scaleImage(image, gp.getTileSize(), gp.getTileSize());
        }

        public BufferedImage setup(String imagePath, int width, int height) {
            BufferedImage image = null;
            try {
                image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(imagePath + ".png")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return UtilityTool.scaleImage(image, width, height);
        }

	
	@Override
	public int getFootY(){
		int chaFoot = getWorldY() + getCollisionArea().y;
		return chaFoot;
	}
	
	public int getWorldX() { return worldX; }
	public Entity setWorldX(int x) { this.worldX = x; return this; }
	public int getWorldY() { return worldY; }
	public Entity setWorldY(int y) { this.worldY = y; return this; }
	public int getSpeed() { return speed; }
	public Entity setSpeed(int speed) { this.speed = speed; return this; }
	public BufferedImage getUp1() { return up1; }
	public Entity setUp1(BufferedImage up1) { this.up1 = up1; return this; }
	public BufferedImage getUp2() { return up2; }
	public Entity setUp2(BufferedImage up2) { this.up2 = up2; return this; }
	public BufferedImage getDown1() { return down1; }
	public Entity setDown1(BufferedImage down1) { this.down1 = down1; return this; }
	public BufferedImage getDown2() { return down2; }
	public Entity setDown2(BufferedImage down2) { this.down2 = down2; return this; }
	public BufferedImage getLeft1() { return left1; }
	public Entity setLeft1(BufferedImage left1) { this.left1 = left1; return this; }
	public BufferedImage getLeft2() { return left2; }
	public Entity setLeft2(BufferedImage left2) { this.left2 = left2; return this; }
	public BufferedImage getRight1() { return right1; }
	public Entity setRight1(BufferedImage right1) { this.right1 = right1; return this; }
	public BufferedImage getRight2() { return right2; }
	public Entity setRight2(BufferedImage right2) { this.right2 = right2; return this; }
	public String getDirection() { return direction; }
	public Entity setDirection(String direction) { this.direction = direction; return this; }
	public int getSpriteCouter() { return spriteCouter; }
	public Entity setSpriteCouter(int spriteCouter) { this.spriteCouter = spriteCouter; return this; }
	public int getSpriteNum() { return spriteNum; }
	public Entity setSpriteNum(int spriteNum) { this.spriteNum = spriteNum; return this; }
	public Rectangle getCollisionArea() { return collisionArea; }
	public Entity setCollisionArea(Rectangle collisionArea) { this.collisionArea = collisionArea; return this; }
	public boolean isCollisionOn() { return collisionOn; } 
	public Entity setCollisionOn(boolean collision) { this.collisionOn = collision; return this; } 
	public int getCollisionDefaultX() { return collisionDefaultX; } 
	public Entity setCollisionDefaultX(int collisionDefaultX) { this.collisionDefaultX = collisionDefaultX; return this; }
	public int getCollisionDefaultY() { return collisionDefaultY; }
	public Entity setCollisionDefaultY(int collisionDefaultY) { this.collisionDefaultY = collisionDefaultY; return this; }
	public int getResestTime() { return resestTime; }
	public Entity setResestTime(int resestTime) { this.resestTime = resestTime; return this; }
	public int getActionLockCounter() { return actionLockCounter; }
	public Entity setActionLockCounter(int actionLockCounter) { this.actionLockCounter = actionLockCounter; return this; }
	public int getScaleEntityX() { return scaleEntityX; }
	public Entity setScaleEntityX(int scaleEntityX) { this.scaleEntityX = scaleEntityX; return this; }
	public int getScaleEntityY() { return scaleEntityY; }
	public Entity setScaleEntityY(int scaleEntityY) { this.scaleEntityY = scaleEntityY; return this; }
	public String[] getDialogues() { return dialogues; } 
	public Entity setDialogues(String[] dialogues) { this.dialogues = dialogues; return this; } 
	public int getDialogueIndex() { return dialogueIndex; } 
	public Entity setDialogueIndex(int dialogueIndex) { this.dialogueIndex = dialogueIndex; return this; }
	public int getIndex() { return index; } 
	public Entity setIndex(int index) { this.index = index; return this; }
	public String getName() { return name; }
	public Entity setName(String name) { this.name = name; return this; }
	
}