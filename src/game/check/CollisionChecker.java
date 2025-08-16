package game.check;

import game.entity.Entity;
import game.main.GamePanel;
import game.object.SuperObject;

public class CollisionChecker {
	public final GamePanel gp;
	
	public CollisionChecker(GamePanel gp) {
		this.gp = gp;
	}
	
	public void checkObject(SuperObject obj) {

	}
	
	public void checkTile(Entity entity) {
		//Tính tọa độ tuyệt đối của 4 cạnh vùng va chạm trong một entity
		int entityLeftWorldX = entity.getWorldX() + entity.getCollisionArea().x;
		int entityRightWorldX = entityLeftWorldX + entity.getCollisionArea().width;
		int entityTopWorldY = entity.getWorldY() + entity.getCollisionArea().y;
		int entityBotWorldY = entityTopWorldY + entity.getCollisionArea().height;
		
		// Tính chỉ số cột và hàng tuyệt đối của 4 cạnh vùng va chạm trong bản đồ
        int entityLeftCol = entityLeftWorldX / gp.getTileSize();
        int entityRightCol = entityRightWorldX / gp.getTileSize();
        int entityTopRow = entityTopWorldY / gp.getTileSize();
        int entityBotRow = entityBotWorldY / gp.getTileSize();
        
        int tileNum1, tileNum2;
        
        switch(entity.getDirection()) {
        case "up":
        	entityTopRow = (entityTopWorldY - entity.getSpeed()) / gp.getTileSize();
        	tileNum1 = gp.getTileManager().getMapTileNumber()[entityLeftCol][entityTopRow];
        	tileNum2 = gp.getTileManager().getMapTileNumber()[entityRightCol][entityTopRow];
        	if(gp.getTileManager().getTile()[tileNum1].isCollision() 
        			|| gp.getTileManager().getTile()[tileNum2].isCollision()) {
        		entity.setCollisionOn(true);
        	}
        	break;
        case "down":
        	entityBotRow = (entityBotWorldY + entity.getSpeed()) / gp.getTileSize();
        	tileNum1 = gp.getTileManager().getMapTileNumber()[entityLeftCol][entityBotRow];
        	tileNum2 = gp.getTileManager().getMapTileNumber()[entityRightCol][entityBotRow];
        	if(gp.getTileManager().getTile()[tileNum1].isCollision() 
        			|| gp.getTileManager().getTile()[tileNum2].isCollision()) {
        		entity.setCollisionOn(true);
        	}
        	break;
        case "left":
        	entityLeftCol = (entityLeftWorldX - entity.getSpeed()) / gp.getTileSize();
        	tileNum1 = gp.getTileManager().getMapTileNumber()[entityLeftCol][entityTopRow];
        	tileNum2 = gp.getTileManager().getMapTileNumber()[entityLeftCol][entityBotRow];
        	if(gp.getTileManager().getTile()[tileNum1].isCollision() 
        			|| gp.getTileManager().getTile()[tileNum2].isCollision()) {
        		entity.setCollisionOn(true);
        	}
        	break;
        case "right":
        	entityRightCol = (entityRightWorldX + entity.getSpeed()) / gp.getTileSize();
        	tileNum1 = gp.getTileManager().getMapTileNumber()[entityRightCol][entityTopRow];
        	tileNum2 = gp.getTileManager().getMapTileNumber()[entityRightCol][entityBotRow];
        	if(gp.getTileManager().getTile()[tileNum1].isCollision() 
        			|| gp.getTileManager().getTile()[tileNum2].isCollision()) {
        		entity.setCollisionOn(true);
        	}
        	break;
        }
	}
	
	 public int checkObject(Entity entity, boolean isPlayer) {
	        int index = 999;

	        for (SuperObject object : gp.getObjects()) {

	            if (object != null) {
	                entity.getCollisionArea().x = entity.getWorldX() + entity.getCollisionArea().x;
	                entity.getCollisionArea().y = entity.getWorldY() + entity.getCollisionArea().y;

	                object.getCollisionArea().x = object.getWorldX() + object.getCollisionArea().x;
	                object.getCollisionArea().y = object.getWorldY() + object.getCollisionArea().y;

	                switch (entity.getDirection()) {
	                    case "up" -> {
	                        entity.getCollisionArea().y -= entity.getSpeed();

	                        if (entity.getCollisionArea().intersects(object.getCollisionArea())) {
	                            if (object.isCollision()) {
	                                entity.setCollisionOn(true);
	                            }

	                            if (isPlayer) {
	                                index = object.getIndex();
	                            }
	                        }
	                    }
	                    case "down" -> {
	                        entity.getCollisionArea().y += entity.getSpeed();

	                        if (entity.getCollisionArea().intersects(object.getCollisionArea())) {
	                            if (object.isCollision()) {
	                                entity.setCollisionOn(true);
	                            }

	                            if (isPlayer) {
	                                index = object.getIndex();
	                            }
	                        }
	                    }
	                    case "left" -> {
	                        entity.getCollisionArea().x -= entity.getSpeed();

	                        if (entity.getCollisionArea().intersects(object.getCollisionArea())) {
	                            if (object.isCollision()) {
	                                entity.setCollisionOn(true);
	                            }

	                            if (isPlayer) {
	                                index = object.getIndex();
	                            }
	                        }
	                    }
	                    case "right" -> {
	                        entity.getCollisionArea().x += entity.getSpeed();

	                        if (entity.getCollisionArea().intersects(object.getCollisionArea())) {
	                            if (object.isCollision()) {
	                                entity.setCollisionOn(true);
	                            }

	                            if (isPlayer) {
	                                index = object.getIndex();
	                            }
	                        }
	                    }
	                }

	                entity.getCollisionArea().x = entity.getCollisionDefaultX();
	                entity.getCollisionArea().y = entity.getCollisionDefaultY();
	                object.getCollisionArea().x = object.getCollisionDefaultX();
	                object.getCollisionArea().y = object.getCollisionDefaultY();
	            }
	        }

	        return index;
	    }
	
}




















