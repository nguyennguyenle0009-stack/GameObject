package game.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.imageio.ImageIO;

import game.interfaces.DrawableEntity;
import game.main.GamePanel;
import game.util.CameraHelper;
import game.util.UtilityTool;

public class Player extends Entity implements DrawableEntity {
	// Vị trí nhân vật trên màn hình (luôn ở giữa)
    private final int screenX;
    private final int screenY;
    private static final int INTERACTION_RANGE = 80;
    
    // Character attributes, item storage and skills
    private final Attributes attributes;
    private final Inventory inventory;
    private final List<String> skills = new ArrayList<>();
    private final List<String> techniques = new ArrayList<>();
    
    private boolean attacking = false;
    private BufferedImage attack1, attack2, attack3;
    private int attackSpriteCounter;
    private int attackSpriteNum;
    private Rectangle attackArea = new Rectangle(0, 0, 36, 36);
    private boolean damageApplied;

	public Player(GamePanel gp) {
		super(gp);
        this.screenX = gp.getScreenWidth() / 2 - (gp.getTileSize() / 2);//360
        this.screenY = gp.getScreenHeight() / 2 - (gp.getTileSize() / 2);//264
        setCollision();
		setDefaultValue();
		getImagePlayer();
		
        this.attributes = new Attributes();
        this.inventory = new Inventory();
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
			
            setAttack1(setup("/data/player/t1"));
            setAttack2(setup("/data/player/t2"));
            setAttack3(setup("/data/player/t3"));
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
    if (attacking) {
        handleAttack();
    } else {
        updateKeyboard();
        if (gp.keyH.isAttackPressed()) {
            attacking = true;
            attackSpriteCounter = 0;
            attackSpriteNum = 1;
            damageApplied = false;
            gp.keyH.setAttackPressed(false);
        }
    }
}
	
	private void updateKeyboard() {
	    boolean moving = gp.keyH.isUpPressed() || gp.keyH.isDownPressed() 
	                  || gp.keyH.isLeftPressed() || gp.keyH.isRightPressed();

	    // Nếu có di chuyển → xử lý di chuyển
	    if (moving) {
	        if (gp.keyH.isUpPressed())    setDirection("up");
	        if (gp.keyH.isDownPressed())  setDirection("down");
	        if (gp.keyH.isLeftPressed())  setDirection("left");
	        if (gp.keyH.isRightPressed()) setDirection("right");

	        checkCollision();
	        moveIfCollisionNotDetected();
	        checkAndChangeSpriteAnimation();
	    } else {
	        resestSpriteToDefault();
	    }

	    // Xử lý đối thoại riêng, KHÔNG phụ thuộc di chuyển
	    if (gp.keyH.isDialoguePressed()) {
	        Entity npc = getClosestNPCInRange(gp.getNpcs());
	        if (npc != null) {
	            gp.setGameState(gp.getDialogueState());
	            npc.speak();
	        }
	        gp.keyH.setDialoguePressed(false); // reset flag
	    }
	}
	
	private void handleAttack() {
        attackSpriteCounter++;
        if (attackSpriteCounter <= 5) {
            attackSpriteNum = 1;
            performDash();
        } else if (attackSpriteCounter <= 10) {
            attackSpriteNum = 2;
            if (!damageApplied) {
                dealDamage();
                damageApplied = true;
            }
        } else if (attackSpriteCounter <= 15) {
            attackSpriteNum = 3;
        } else {
            attackSpriteCounter = 0;
            attackSpriteNum = 1;
            attacking = false;
        }
    }

    private void performDash() {
        int originalSpeed = getSpeed();
        setSpeed(originalSpeed * 2);
        checkCollision();
        moveIfCollisionNotDetected();
        setSpeed(originalSpeed);
    }

    private Rectangle getAttackRectangle() {
        int x = getWorldX();
        int y = getWorldY();
        int size = gp.getTileSize();
        int aw = attackArea.width;
        int ah = attackArea.height;
        switch (getDirection()) {
            case "up" -> {
                y -= ah;
                x += (size - aw) / 2;
            }
            case "down" -> {
                y += size;
                x += (size - aw) / 2;
            }
            case "left" -> {
                x -= aw;
                y += (size - ah) / 2;
            }
            case "right" -> {
                x += size;
                y += (size - ah) / 2;
            }
        }
        return new Rectangle(x, y, aw, ah);
    }

    private void dealDamage() {
        Rectangle attackRect = getAttackRectangle();
        for (Entity npc : gp.getNpcs()) {
            if (npc != null) {
                Rectangle npcRect = new Rectangle(
                        npc.getWorldX() + npc.getCollisionArea().x,
                        npc.getWorldY() + npc.getCollisionArea().y,
                        npc.getCollisionArea().width,
                        npc.getCollisionArea().height);
                if (attackRect.intersects(npcRect) && npc instanceof game.entity.animal.cat.Cat_yellow cat) {
                    cat.takeDamage(1);
                }
            }
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
		BufferedImage image = attacking ? getAttackImage() : getDirectionImage();
        g2.drawImage(image, screenPos.x, screenPos.y, null);
		if(gp.keyH.isDrawRect() == true) {
            g2.setColor(Color.BLUE);
            g2.drawRect(screenPos.x+getCollisionArea().x, screenPos.y + getCollisionArea().y,
                            getCollisionArea().width, getCollisionArea().height);
            if (attacking) {
                Rectangle attackRect = getAttackRectangle();
                Point atkScreen = CameraHelper.worldToScreen(attackRect.x, attackRect.y, gp);
                g2.setColor(Color.RED);
                g2.drawRect(atkScreen.x, atkScreen.y, attackRect.width, attackRect.height);
            }
		}
    }
	
    private BufferedImage getAttackImage() {
        if (attackSpriteNum == 1) {
            return attack1;
        } else if (attackSpriteNum == 2) {
            return attack2;
        } else {
            return attack3;
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
	
    public Attributes getAttributes() {
        return attributes;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void learnSkill(String skill) {
        skills.add(skill);
    }

    public List<String> getSkills() {
        return skills;
    }

    public void learnTechnique(String technique) {
        techniques.add(technique);
    }

    public List<String> getTechniques() {
        return techniques;
    }

    public boolean canUseTechnique(String technique, int weaponSlot) {
        return techniques.contains(technique) && inventory.getWeapon(weaponSlot) != null;
    }
    
    public BufferedImage getAttack1() { return attack1; }
    public Player setAttack1(BufferedImage attack1) { this.attack1 = attack1; return this; }
    public BufferedImage getAttack2() { return attack2; }
    public Player setAttack2(BufferedImage attack2) { this.attack2 = attack2; return this; }
    public BufferedImage getAttack3() { return attack3; }
    public Player setAttack3(BufferedImage attack3) { this.attack3 = attack3; return this; }
}















