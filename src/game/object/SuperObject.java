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
	
    public void draw(Graphics2D graphics2D, GamePanel gamePanel) {
        int screenX = getWorldX() - gamePanel.getPlayer().getWorldX() + gamePanel.getPlayer().getScreenX();
        int screenY = getWorldY() - gamePanel.getPlayer().getWorldY() + gamePanel.getPlayer().getScreenY();

        if (worldX + gamePanel.getTileSize() > gamePanel.getPlayer().getWorldX() - gamePanel.getPlayer().getScreenX() &&
            worldX - gamePanel.getTileSize() < gamePanel.getPlayer().getWorldX() + gamePanel.getPlayer().getScreenX() &&
            worldY + gamePanel.getTileSize() > gamePanel.getPlayer().getWorldY() - gamePanel.getPlayer().getScreenY() &&
            worldY - gamePanel.getTileSize() < gamePanel.getPlayer().getWorldY() + gamePanel.getPlayer().getScreenY()) {

            graphics2D.drawImage(image, screenX, screenY,3 *  gamePanel.getTileSize(),3 * gamePanel.getTileSize(), null);
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
