package game.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import javax.imageio.ImageIO;

import game.interfaces.DrawableEntity;
import game.keyhandler.KeyHandler;
import game.main.GamePanel;
import game.mouseclick.MouseHandler;
import game.util.CameraHelper;
import game.util.UtilityTool;

public class Player extends Entity implements DrawableEntity {
	// Dùng để xử lý bàn phím và chuột
	KeyHandler keyH = new KeyHandler(gp);
	MouseHandler mouseH = new MouseHandler(gp);
	// Vị trí nhân vật trên màn hình (luôn ở giữa)
    private final int screenX;
    private final int screenY;
    private static final int INTERACTION_RANGE = 80;

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
        setCollisionArea(new Rectangle( 16, 32, 16, 16));
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
	    boolean moving = keyH.isUpPressed() || keyH.isDownPressed() 
	                  || keyH.isLeftPressed() || keyH.isRightPressed();

	    // Nếu có di chuyển → xử lý di chuyển
	    if (moving) {
	        if (keyH.isUpPressed())    setDirection("up");
	        if (keyH.isDownPressed())  setDirection("down");
	        if (keyH.isLeftPressed())  setDirection("left");
	        if (keyH.isRightPressed()) setDirection("right");

	        checkCollision();
	        moveIfCollisionNotDetected();
	        checkAndChangeSpriteAnimation();
	    } else {
	        resestSpriteToDefault();
	    }

	    // Xử lý đối thoại riêng, KHÔNG phụ thuộc di chuyển
	    if (keyH.isDialoguePressed()) {
	        Entity npc = getClosestNPCInRange(gp.getNpcs());
	        if (npc != null) {
	            gp.setGameState(gp.getDialogueState());
	            npc.speak();
	        }
	        keyH.setDialoguePressed(false); // reset flag
	    }
	}
	
	@Override
	public void checkCollision() {
		setCollisionOn(false);
		gp.getCheckCollision().checkTile(this);
        gp.getCheckCollision().checkObject(this, false);
        gp.getCheckCollision().checkEntity(this, gp.getNpcs());
        int npcIndex = gp.getCheckCollision().checkInteraction(this, gp.getNpcs(), 48);
        interactWithNPC(npcIndex);
	}
	
	// check NPC trong phạm vi
	public Entity getClosestNPCInRange(List<Entity> npcs) {
	    for (Entity npc : npcs) {
	        if (npc != null) {
	            int dx = Math.abs(this.getWorldX() - npc.getWorldX());
	            int dy = Math.abs(this.getWorldY() - npc.getWorldY());
	            double distance = Math.sqrt(dx * dx + dy * dy);

	            if (distance < INTERACTION_RANGE) {
	                return npc;
	            }
	        }
	    }
	    return null;
	}
	
	//check NPC gần nhất, chưa dùng
	public Entity getClosestNPCInRange1(List<Entity> npcs) {
	    Entity closestNpc = null;
	    double minDistance = Double.MAX_VALUE;

	    for (Entity npc : npcs) {
	        if (npc != null) {
	            int dx = this.getWorldX() - npc.getWorldX();
	            int dy = this.getWorldY() - npc.getWorldY();
	            double distance = Math.sqrt(dx * dx + dy * dy);

	            if (distance < INTERACTION_RANGE && distance < minDistance) {
	                minDistance = distance;
	                closestNpc = npc;
	            }
	        }
	    }

	    return closestNpc;
	}
	
	private void interactWithNPC(int index) {
		List<Entity> nearbyNpcs = gp.getCheckCollision().getEntitiesInRange(this, gp.getNpcs(), 48);
		if (!nearbyNpcs.isEmpty() && gp.keyH.isDialoguePressed()) {
		    Entity npc = nearbyNpcs.get(0); // lấy NPC đầu tiên (bạn có thể chọn theo khoảng cách gần nhất)
		    gp.setGameState(gp.getDialogueState());
		    npc.speak();
		    gp.keyH.setDialoguePressed(false);
		}
	}
	
	@Override
	public void draw(Graphics2D g2) {
		Point screenPos = CameraHelper.worldToScreen(getWorldX(), getWorldY(), gp);
		g2.drawImage(getDirectionImage(), screenPos.x, screenPos.y, null);
        g2.setColor(Color.BLUE);
        g2.drawRect(screenPos.x+getCollisionArea().x, screenPos.y + getCollisionArea().y, 
        		getCollisionArea().width, getCollisionArea().height);
	}
	
	@Override
	public void draw(Graphics2D g2, GamePanel gp) {
		Point screenPos = CameraHelper.worldToScreen(getWorldX(), getWorldY(), gp);
		g2.drawImage(getDirectionImage(), screenPos.x, screenPos.y, null);
		if(keyH.isDrawRect() == true) {
	        g2.setColor(Color.BLUE);
	        g2.drawRect(screenPos.x+getCollisionArea().x, screenPos.y + getCollisionArea().y, 
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

	public static int getInteractionRange() { return INTERACTION_RANGE; }
	
}















