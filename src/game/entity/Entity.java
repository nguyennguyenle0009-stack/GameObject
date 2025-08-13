package game.entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import game.main.GamePanel;

public abstract class Entity {
	GamePanel gp;
	
	private int x, y;
	private int speed;
	
	private BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;
	private String direction;
	
	private int spriteCouter;
	private int spriteNum;
	
	public Entity(GamePanel gp) {
		this.gp = gp;
	}
	
	public abstract  void  update();
	public abstract  void draw(Graphics2D g2);
	
	public int getX() {
		return x;
	}
	
	public Entity setX(int x) {
		this.x = x;
		return this;
	}
	
	public int getY() {
		return y;
	}
	
	
	public Entity setY(int y) {
		this.y = y;
		return this;
	}
	
	public int getSpeed() {
		return speed;
	}
	
	public Entity setSpeed(int speed) {
		this.speed = speed;
		return this;
	}

	public GamePanel getGp() {
		return gp;
	}

	public Entity setGp(GamePanel gp) {
		this.gp = gp;
		return this;
	}

	public BufferedImage getUp1() {
		return up1;
	}

	public Entity setUp1(BufferedImage up1) {
		this.up1 = up1;
		return this;
	}

	public BufferedImage getUp2() {
		return up2;
	}

	public Entity setUp2(BufferedImage up2) {
		this.up2 = up2;
		return this;
	}

	public BufferedImage getDown1() {
		return down1;
	}

	public Entity setDown1(BufferedImage down1) {
		this.down1 = down1;
		return this;
	}

	public BufferedImage getDown2() {
		return down2;
	}

	public Entity setDown2(BufferedImage down2) {
		this.down2 = down2;
		return this;
	}

	public BufferedImage getLeft1() {
		return left1;
	}

	public Entity setLeft1(BufferedImage left1) {
		this.left1 = left1;
		return this;
	}

	public BufferedImage getLeft2() {
		return left2;
	}

	public Entity setLeft2(BufferedImage left2) {
		this.left2 = left2;
		return this;
	}

	public BufferedImage getRight1() {
		return right1;
	}

	public Entity setRight1(BufferedImage right1) {
		this.right1 = right1;
		return this;
	}

	public BufferedImage getRight2() {
		return right2;
	}

	public Entity setRight2(BufferedImage right2) {
		this.right2 = right2;
		return this;
	}

	public String getDirection() {
		return direction;
	}

	public Entity setDirection(String direction) {
		this.direction = direction;
		return this;
	}

	public int getSpriteCouter() {
		return spriteCouter;
	}

	public Entity setSpriteCouter(int spriteCouter) {
		this.spriteCouter = spriteCouter;
		return this;
	}

	public int getSpriteNum() {
		return spriteNum;
	}

	public Entity setSpriteNum(int spriteNum) {
		this.spriteNum = spriteNum;
		return this;
	}
	
}
