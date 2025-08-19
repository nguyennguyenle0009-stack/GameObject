package game.check;

import java.awt.Rectangle;
import java.util.List;

import game.entity.Entity;
import game.main.GamePanel;
import game.object.SuperObject;

public class CollisionChecker {
	public final GamePanel gp;
	
	public CollisionChecker(GamePanel gp) {
		this.gp = gp;
	}
	
	// Check entity, player với tile
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
	
	// Check entity, player với object
	public int checkObject(Entity entity, boolean isPlayer) {
	    int index = 999;
	    for (SuperObject object : gp.getObjects()) {
	        if (object == null || !object.isCollision()) continue;
	        // Tạo vùng va chạm tạm cho entity
	        Rectangle entityArea = new Rectangle(
	            entity.getWorldX() + entity.getCollisionArea().x,
	            entity.getWorldY() + entity.getCollisionArea().y,
	            entity.getCollisionArea().width,
	            entity.getCollisionArea().height
	        );
	        // Tạo vùng va chạm tạm cho object
	        Rectangle objectArea = new Rectangle(
	            object.getWorldX() + object.getCollisionArea().x,
	            object.getWorldY() + object.getCollisionArea().y,
	            object.getCollisionArea().width,
	            object.getCollisionArea().height
	        );
	        // Di chuyển vùng va chạm entity theo hướng
	        switch (entity.getDirection()) {
	            case "up" -> entityArea.y -= entity.getSpeed();
	            case "down" -> entityArea.y += entity.getSpeed();
	            case "left" -> entityArea.x -= entity.getSpeed();
	            case "right" -> entityArea.x += entity.getSpeed();
	        }
	        // Kiểm tra va chạm
	        if (entityArea.intersects(objectArea)) {
	            entity.setCollisionOn(true);
	            if (isPlayer) {
	                index = object.getIndex();
	            }
	        }
	    }
	    return index;
	}
	
	// Check entity với entity, check player với entity
	public int checkEntity(Entity entity, List<Entity> targets) {
	    int index = 999;
	    for (Entity target : targets) {
	        if (target != null && target != entity) {
	            Rectangle entityArea = new Rectangle(
	                    entity.getWorldX() + entity.getCollisionArea().x,
	                    entity.getWorldY() + entity.getCollisionArea().y,
	                    entity.getCollisionArea().width,
	                    entity.getCollisionArea().height
	            );
	            Rectangle targetArea = new Rectangle(
	                    target.getWorldX() + target.getCollisionArea().x,
	                    target.getWorldY() + target.getCollisionArea().y,
	                    target.getCollisionArea().width,
	                    target.getCollisionArea().height
	            );
                switch (entity.getDirection()) {
                    case "up" -> entityArea.y -= entity.getSpeed();
                    case "down" -> entityArea.y += entity.getSpeed();
                    case "left" -> entityArea.x -= entity.getSpeed();
                    case "right" -> entityArea.x += entity.getSpeed();
                }
                if (entityArea.intersects(targetArea)) {
                    entity.setCollisionOn(true);
                    index = target.getIndex();
                }
	        }
	    }
	    return index;
	}
	
	// Check entity với player
	public void checkPlayer(Entity entity) {
		Rectangle entityArea = new Rectangle(
				entity.getWorldX() + entity.getCollisionArea().x,
				entity.getWorldY() + entity.getCollisionArea().y,
				entity.getCollisionArea().width,
				entity.getCollisionArea().height
		);
		Rectangle playerArea = new Rectangle(
				gp.getPlayer().getWorldX() + gp.getPlayer().getCollisionArea().x,
				gp.getPlayer().getWorldY() + gp.getPlayer().getCollisionArea().y,
				gp.getPlayer().getCollisionArea().width,
				gp.getPlayer().getCollisionArea().height
		);
        switch (entity.getDirection()) {
        case "up" -> entityArea.y -= entity.getSpeed();
        case "down" -> entityArea.y += entity.getSpeed();
        case "left" -> entityArea.x -= entity.getSpeed();
        case "right" -> entityArea.x += entity.getSpeed();
        }
        if (entityArea.intersects(playerArea)) {
            entity.setCollisionOn(true);
        }
	}
	
	// Check phạm vi tương tác
	public int checkInteraction(Entity entity, List<Entity> targets, int range) {
	    for (Entity target : targets) {
	        if (target != null && target != entity) {
	            int dx = Math.abs(entity.getWorldX() - target.getWorldX());
	            int dy = Math.abs(entity.getWorldY() - target.getWorldY());

	            if (dx < range && dy < range) {
	                return target.getIndex();
	            }
	        }
	    }
	    return 999;
	}
}




















