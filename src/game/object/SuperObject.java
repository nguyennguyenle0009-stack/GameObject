package game.object;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import game.main.GamePanel;

public class SuperObject {
	
	private String name;
	private int index;
	private BufferedImage image;
	private boolean collision = false;
	private int worldX, worldY;
	private Rectangle collisionArea = new Rectangle(0, 0, 48, 48);
	private int collisionDefaultX = 0;
	private int collisionDefaultY = 0;
	
//	public SuperObject() {
//	this.
//}
	
	public void draw(Graphics2D g2, GamePanel gp) {
	    int screenX = worldX - gp.getPlayer().getWorldX() + gp.getPlayer().getScreenX();
	    int screenY = worldY - gp.getPlayer().getWorldY() + gp.getPlayer().getScreenY();

	    // TÍNH OFFSET BIÊN GIỐNG TILE
	    int rightOffset  = gp.getScreenWidth()  - gp.getPlayer().getScreenX();
	    int bottomOffset = gp.getScreenHeight() - gp.getPlayer().getScreenY();

	    // KẸP THEO TRỤC X
	    if (gp.getPlayer().getScreenX() > gp.getPlayer().getWorldX()) {
	        // mép trái map: camera không cuộn được nữa
	        screenX = worldX;
	    } else if (rightOffset > gp.getWorldWidth() - gp.getPlayer().getWorldX()) {
	        // mép phải map
	        screenX = gp.getScreenWidth() - (gp.getWorldWidth() - worldX);
	    }

	    // KẸP THEO TRỤC Y
	    if (gp.getPlayer().getScreenY() > gp.getPlayer().getWorldY()) {
	        // mép trên map
	        screenY = worldY;
	    } else if (bottomOffset > gp.getWorldHeight() - gp.getPlayer().getWorldY()) {
	        // mép dưới map
	        screenY = gp.getScreenHeight() - (gp.getWorldHeight() - worldY);
	    }

	    // CHỈ VẼ KHI TRONG KHUNG HÌNH (giống điều kiện trong TileManager)
	    if (worldX + gp.getTileSize() > gp.getPlayer().getWorldX() - gp.getPlayer().getScreenX() &&
	        worldX - gp.getTileSize() < gp.getPlayer().getWorldX() + gp.getPlayer().getScreenX() &&
	        worldY + gp.getTileSize() > gp.getPlayer().getWorldY() - gp.getPlayer().getScreenY() &&
	        worldY - gp.getTileSize() < gp.getPlayer().getWorldY() + gp.getPlayer().getScreenY()) {

	        g2.drawImage(image, screenX, screenY, gp.getTileSize(), gp.getTileSize(), null);
	    }
	}
	
	public String getName() {
		return name;
	}
	public SuperObject setName(String name) {
		this.name = name;
		return this;
	}
	public int getIndex() {
		return index;
	}
	public SuperObject setIndex(int index) {
		this.index = index;
		return this;
	}
	public BufferedImage getImage() {
		return image;
	}
	public SuperObject setImage(BufferedImage image) {
		this.image = image;
		return this;
	}
	public boolean isCollision() {
		return collision;
	}
	public SuperObject setCollision(boolean collision) {
		this.collision = collision;
		return this;
	}
	public int getWorldX() {
		return worldX;
	}
	public SuperObject setWorldX(int worldX) {
		this.worldX = worldX;
		return this;
	}
	public int getWorldY() {
		return worldY;
	}
	public SuperObject setWorldY(int worldY) {
		this.worldY = worldY;
		return this;
	}
	public Rectangle getCollisionArea() {
		return collisionArea;
	}
	public SuperObject setCollisionArea(Rectangle collisionArea) {
		this.collisionArea = collisionArea;
		return this;
	}
	public int getCollisionDefaultX() {
		return collisionDefaultX;
	}
	public SuperObject setCollisionDefaultX(int collisionDefaultX) {
		this.collisionDefaultX = collisionDefaultX;
		return this;
	}
	public int getCollisionDefaultY() {
		return collisionDefaultY;
	}
	public SuperObject setCollisionDefaultY(int collisionDefaultY) {
		this.collisionDefaultY = collisionDefaultY;
		return this;
	}
	
}
