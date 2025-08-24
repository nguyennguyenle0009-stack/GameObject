package game.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.EnumSet;
import java.util.Random;

import javax.imageio.ImageIO;

import game.entity.inventory.Inventory;
import game.entity.item.Item;
import game.interfaces.DrawableEntity;
import game.entity.monster.Monster;
import game.enums.Realm;
import game.enums.Physique;
import game.enums.Affinity;
import game.enums.Attr;

import game.main.GamePanel;
import game.util.CameraHelper;
import game.util.UtilityTool;

public class Player extends GameActor implements DrawableEntity {
	// Vị trí nhân vật trên màn hình (luôn ở giữa)
    private final int screenX;
    private final int screenY;
    
    private static final int INTERACTION_RANGE = 80;

    private final Inventory bag = new Inventory();
    private boolean invincible = false;
    private int invincibleCounter = 0;
    private final Rectangle attackArea;
    private boolean attacking = false;
    private int attackCounter = 0;
    private int attackCooldown = 0;
    private static final int ATTACK_COOLDOWN = 20;
    private static final int ATTACK_DURATION = 10;

    // Cultivation properties
    private Realm realm = Realm.PHAM_NHAN; // current major realm
    private int realmStage = 0;            // current minor stage within realm
    private Physique physique;            // innate physique
    private EnumSet<Affinity> affinities = EnumSet.noneOf(Affinity.class);

    // maximum resource values and level requirements
    private int maxHealth;
    private int maxPep;
    private int spiritRequirement;

    /** Random source used for physique/affinity generation */
    private static final Random RAND = new Random();

	public Player(GamePanel gp) {
		super(gp);
        this.screenX = gp.getScreenWidth() / 2 - (gp.getTileSize() / 2);//360
        this.screenY = gp.getScreenHeight() / 2 - (gp.getTileSize() / 2);//264
        setCollision();
        setDefaultValue();
        getImagePlayer();
        attackArea = new Rectangle(0, 0, gp.getTileSize(), gp.getTileSize());

        }
        public void setDefaultValue() {
                setWorldX(100);
                setWorldY(100);
                setSpeed(4);
                setDirection("down");
                setSpriteCouter(0);
                setSpriteNum(1);
                setName("Nguyeen pro");

                // base attributes for a mortal
                physique = randomPhysique();
                affinities = randomAffinities(physique);
                maxHealth = 100;
                maxPep = 100;
                spiritRequirement = (int)(100 / physique.getSpiritRequirementDivisor());

                atts().set(Attr.HEALTH, maxHealth);
                atts().set(Attr.PEP, 0);
                atts().set(Attr.SPIRIT, 0);
                atts().set(Attr.ATTACK, 10);
                atts().set(Attr.DEF, 5);
                atts().set(Attr.SOULD, 5);
                atts().set(Attr.STRENGTH, 1);

                setScaleEntityX(gp.getTileSize());
                setScaleEntityY(gp.getTileSize());
        }
	
    private void setCollision() {
        setCollisionArea(new Rectangle( 16, 32, 16, 16));
        setCollisionDefaultX(getCollisionArea().x);
        setCollisionDefaultY(getCollisionArea().y);
    }

    public Realm getRealm() {
        return realm;
    }

    public void setRealm(Realm realm) {
        this.realm = realm;
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
	
	@Override
        public void update() {
            if (gp.keyH.isiPressed()) return;
            if (invincible) {
                invincibleCounter++;
                if (invincibleCounter > 60) {
                    invincible = false;
                    invincibleCounter = 0;
                }
            }
            updateKeyboard();
            handleAttack();
            checkLevelUp();
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
            if (attacking) {
                attackCounter++;
                if (attackCounter == 1) {
                    physicalAttack();
                }
                if (attackCounter > ATTACK_DURATION) {
                    attacking = false;
                    attackCounter = 0;
                    attackCooldown = ATTACK_COOLDOWN;
                }
            } else {
                if (attackCooldown > 0) attackCooldown--;
                if (gp.keyH.isAttackPressed() && attackCooldown == 0) {
                    attacking = true;
                }
            }
        }

        private Rectangle getAttackRectangle() {
            int attackX = getWorldX();
            int attackY = getWorldY();
            switch (getDirection()) {
                case "up" -> attackY -= attackArea.height;
                case "down" -> attackY += getScaleEntityY();
                case "left" -> attackX -= attackArea.width;
                case "right" -> attackX += getScaleEntityX();
            }
            return new Rectangle(attackX, attackY, attackArea.width, attackArea.height);
        }

        /**
         * Kiểm tra va chạm của đòn đánh và gây sát thương cho quái vật.
         */
        private void physicalAttack() {
            // Vùng tấn công của người chơi
            Rectangle attackRect = getAttackRectangle();
            // Duyệt qua toàn bộ quái vật
            for (int i = 0; i < gp.getMonsters().size(); i++) {
                Entity monster = gp.getMonsters().get(i);
                if (monster == null) continue;
                // Vùng va chạm của quái vật
                Rectangle monsterRect = new Rectangle(
                        monster.getWorldX() + monster.getCollisionArea().x,
                        monster.getWorldY() + monster.getCollisionArea().y,
                        monster.getCollisionArea().width,
                        monster.getCollisionArea().height
                );
                // Nếu đòn đánh trúng quái
                if (attackRect.intersects(monsterRect)) {
                    int damage = (int)(atts().get(Attr.ATTACK) * physique.getAttackDamageMultiplier());
                    if (monster instanceof Monster m) {
                        // Quái vật có quản lý máu riêng
                        if (m.takeDamage(damage)) {
                            m.dropItem();
                            gp.getMonsters().remove(i);
                            i--;
                        }
                    } else if (monster instanceof GameActor m) {
                        // Quái vật cũ chỉ trừ máu đơn giản
                        m.atts().add(Attr.HEALTH, -damage);
                        if (m.atts().get(Attr.HEALTH) <= 0) {
                            gp.getMonsters().remove(i);
                            i--;
                        }
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

        int monsterIndex = gp.getCheckCollision().checkEntity(this, gp.getMonsters());
        if (monsterIndex != 999 && !invincible) {
            atts().add(Attr.HEALTH, -1);
            gp.getUi().triggerDamageEffect();
            invincible = true;
        }
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
	public void draw(Graphics2D g2) { }
	
    @Override
    public void draw(Graphics2D g2, GamePanel gp) {
            Point screenPos = CameraHelper.worldToScreen(getWorldX(), getWorldY(), gp);
            g2.drawImage(getDirectionImage(), screenPos.x, screenPos.y, null);

            if (attacking) {
                Rectangle attackRect = getAttackRectangle();
                Point attackScreen = CameraHelper.worldToScreen(attackRect.x, attackRect.y, gp);
                g2.setColor(Color.RED);
                g2.drawRect(attackScreen.x, attackScreen.y, attackRect.width, attackRect.height);
            }

            if(gp.keyH.isDrawRect() == true) {
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
    
    // Sử dụng item
    public void useItem(Item i) {
        i.use(this);
        if(i.getQuantity() == 0) bag.remove(i);
    }

    /** Adds spirit (experience) and checks for level ups. */
    public void gainSpirit(int amount) {
        atts().add(Attr.SPIRIT, amount);
        checkLevelUp();
    }

    // ---------------------- Level System ----------------------

    /** Checks whether enough spirit has been accumulated to advance. */
    private void checkLevelUp() {
        while (atts().get(Attr.SPIRIT) >= spiritRequirement) {
            levelUp();
        }
    }

    /** Handles level up logic for all realms. */
    private void levelUp() {
        atts().add(Attr.SPIRIT, -spiritRequirement);
        if (realm == Realm.PHAM_NHAN) {
            realm = Realm.LUYEN_THE;
            realmStage = 1;
            applyLuyenTheStage();
            spiritRequirement = (int) (1000 / physique.getSpiritRequirementDivisor());
            return;
        }
        if (realm == Realm.LUYEN_THE) {
            if (realmStage >= physique.getMaxStage()) {
                // Breakthrough to Luyện khí
                realm = Realm.LUYEN_KHI;
                realmStage = 1;
                multiplyAllStats(2.0);
                spiritRequirement = (int) (spiritRequirement * 2 / physique.getSpiritRequirementDivisor());
            } else {
                realmStage++;
                applyLuyenTheStage();
                spiritRequirement = (int) (spiritRequirement * 2 / physique.getSpiritRequirementDivisor());
            }
            return;
        }
        if (realm == Realm.LUYEN_KHI) {
            if (realmStage >= physique.getMaxStage()) {
                realmStage = physique.getMaxStage();
                return;
            }
            int prevReq = spiritRequirement;
            realmStage++;
            applyLuyenKhiStage(prevReq);
            spiritRequirement = (int) (prevReq * 3 / physique.getSpiritRequirementDivisor());
        }
    }

    /** Applies stat changes for a Luyện thể stage. */
    private void applyLuyenTheStage() {
        maxHealth = (int) (maxHealth * 1.5 * physique.getStatBonusMultiplier());
        maxPep = (int) (maxPep * 1.5 * physique.getStatBonusMultiplier());
        atts().set(Attr.ATTACK, (int) (atts().get(Attr.ATTACK) * 1.5 * physique.getStatBonusMultiplier()));
        atts().set(Attr.DEF, (int) (atts().get(Attr.DEF) * 3 * physique.getDefBonusMultiplier() * physique.getStatBonusMultiplier()));
        atts().set(Attr.STRENGTH, atts().get(Attr.STRENGTH) * 2);
        refillResources();
    }

    /** Applies stat changes for a Luyện khí stage. */
    private void applyLuyenKhiStage(int prevSpiritReq) {
        maxHealth = (int) (maxHealth * 2 * physique.getStatBonusMultiplier());
        atts().set(Attr.ATTACK, (int) (atts().get(Attr.ATTACK) * 2 * physique.getStatBonusMultiplier()));
        maxPep += prevSpiritReq / 5;
        atts().set(Attr.DEF, (int) (atts().get(Attr.DEF) * 1.5 * physique.getDefBonusMultiplier() * physique.getStatBonusMultiplier()));
        refillResources();
    }

    /** Multiplies all main stats by the given factor. */
    private void multiplyAllStats(double factor) {
        maxHealth = (int) (maxHealth * factor * physique.getStatBonusMultiplier());
        maxPep = (int) (maxPep * factor * physique.getStatBonusMultiplier());
        atts().set(Attr.ATTACK, (int) (atts().get(Attr.ATTACK) * factor * physique.getStatBonusMultiplier()));
        atts().set(Attr.DEF, (int) (atts().get(Attr.DEF) * factor * physique.getDefBonusMultiplier() * physique.getStatBonusMultiplier()));
        refillResources();
    }

    /** Restores current health and pep to their new maximums. */
    private void refillResources() {
        atts().set(Attr.HEALTH, maxHealth);
        atts().set(Attr.PEP, maxPep);
    }

    // ---------------------- Random generation ----------------------

    private Physique randomPhysique() {
        int roll = RAND.nextInt(100);
        if (roll < 1) return Physique.HU_KHONG;
        if (roll < 2) return Physique.HU_KHONG_DAI_DE;
        if (roll < 3) return Physique.THANH_THE;
        if (roll < 4) return Physique.TIEN_LINH_THE;
        if (roll < 5) return Physique.THAN_THE;
        if (roll < 6) return Physique.NGU_HANH_LINH_CAN;
        return Physique.NORMAL;
    }

    private EnumSet<Affinity> randomAffinities(Physique phys) {
        EnumSet<Affinity> set = EnumSet.noneOf(Affinity.class);
        if (phys == Physique.NGU_HANH_LINH_CAN) {
            set = EnumSet.allOf(Affinity.class);
            Affinity[] vals = Affinity.values();
            set.remove(vals[RAND.nextInt(vals.length)]); // 5 of 6
            return set;
        }
        double r = RAND.nextDouble();
        int count;
        if (r < 0.10) count = 3;
        else if (r < 0.25) count = 2;
        else count = 1;
        while (set.size() < count) {
            set.add(randomAffinity());
        }
        return set;
    }

    private Affinity randomAffinity() {
        int r = RAND.nextInt(105);
        if (r < 20) return Affinity.FIRE;
        if (r < 40) return Affinity.WOOD;
        if (r < 60) return Affinity.WATER;
        if (r < 80) return Affinity.METAL;
        if (r < 100) return Affinity.EARTH;
        return Affinity.THUNDER;
    }

    // ---------------------- Getters ----------------------

    public int getScreenX() { return screenX; }
    public int getScreenY() { return screenY; }
    public static int getInteractionRange() { return INTERACTION_RANGE; }
    public Inventory getBag() { return bag; }
    public int getMaxHealth() { return maxHealth; }
    public int getMaxPep() { return maxPep; }
    public int getSpiritRequirement() { return spiritRequirement; }
    public int getRealmStage() { return realmStage; }
    public Physique getPhysique() { return physique; }
    public EnumSet<Affinity> getAffinities() { return affinities; }

    /** Returns affinity names joined by comma for display. */
    public String getAffinityNames() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Affinity a : affinities) {
            if (i++ > 0) sb.append(", ");
            sb.append(a.getDisplayName());
        }
        return sb.toString();
    }
}