package game.object;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import game.interfaces.DrawableEntity;
import game.main.GamePanel;
import game.util.CameraHelper;
import game.util.UtilityTool;

public class SuperObject implements DrawableEntity {
	
	private String name;
	private int index;
	private BufferedImage image;
	private boolean collision = false;
	private int worldX, worldY;
	private Rectangle collisionArea = new Rectangle( 0, 0, 0, 0);
	private int collisionDefaultX = 0;
	private int collisionDefaultY = 0;
	private int scaleObjectWidth = 1; // mặc định là 1
	private int scaleObjectHeight = 1;// mặc định là 1
	private int drawOffsetY = 0;

	public SuperObject() {}
	
	@Override
	public int getFootY() { int objFoot = worldY + collisionArea.y; return objFoot; }
	
	@Override
	public void draw(Graphics2D g2, GamePanel gp) {
		Point screenPos = CameraHelper.worldToScreen(worldX, worldY, gp);
	    
        if(UtilityTool.isInsidePlayerView(worldX, worldY, gp)) {
	        g2.drawImage(
	        		image, 
	        		screenPos.x, 
	        		screenPos.y - drawOffsetY,
	        		getScaleObjectWidth() *  gp.getTileSize(),
	        		getScaleObjectHeight() * gp.getTileSize(), 
	        		null);
	        g2.setColor(Color.RED);
	        g2.drawRect(
        		screenPos.x + collisionArea.x,
        		screenPos.y + collisionArea.y,
	            collisionArea.width,
	            collisionArea.height
	        );
	    }
	}
	
	public String getName() { return name; }
	public SuperObject setName(String name) { this.name = name; return this; }
	public int getIndex() { return index; }
	public SuperObject setIndex(int index) { this.index = index; return this; }
	public BufferedImage getImage() { return image; }
	public SuperObject setImage(BufferedImage image) { this.image = image; return this; }
	public boolean isCollision() { return collision; }
	public SuperObject setCollision(boolean collision) { this.collision = collision; return this; }
	public int getWorldX() { return worldX; }
	public SuperObject setWorldX(int worldX) { this.worldX = worldX; return this; }
	public int getWorldY() { return worldY; }
	public SuperObject setWorldY(int worldY) { this.worldY = worldY; return this; }
	public Rectangle getCollisionArea() { return collisionArea; }
	public SuperObject setCollisionArea(Rectangle collisionArea) { this.collisionArea = collisionArea; return this; }
	public int getCollisionDefaultX() { return collisionDefaultX; }
	public SuperObject setCollisionDefaultX(int collisionDefaultX) { this.collisionDefaultX = collisionDefaultX; return this; }
	public int getCollisionDefaultY() { return collisionDefaultY; }
	public SuperObject setCollisionDefaultY(int collisionDefaultY) { this.collisionDefaultY = collisionDefaultY; return this; }
	public int getScaleObjectWidth() { return scaleObjectWidth; }
	public SuperObject setScaleObjectWidth(int scaleObjectWidth) { this.scaleObjectWidth = scaleObjectWidth; return this; }
	public int getScaleObjectHeight() { return scaleObjectHeight; }
	public SuperObject setScaleObjectHeight(int scaleObjectHeight) { this.scaleObjectHeight = scaleObjectHeight; return this; }
	public int getDrawOffsetY() { return drawOffsetY; }
	public SuperObject setDrawOffsetY(int drawOffsetY) { this.drawOffsetY = drawOffsetY; return this; }
}
