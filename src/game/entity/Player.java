package game.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import game.entity.inventory.Inventory;
import game.entity.item.Item;
import game.interfaces.DrawableEntity;
import game.entity.monster.Monster;
import game.enums.Realm;
import game.enums.Physique;
import game.enums.Affinity;

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

    // Cảnh giới hiện tại của người chơi
    private Realm realm = Realm.PHAM_NHAN;
    // Tầng trong cảnh giới hiện tại
    private int realmTier = 0;
    // Số tầng tối đa của cảnh giới hiện tại (phụ thuộc thể chất)
    private int maxTier = 10;
    // Spirit cần để lên tầng tiếp theo
    private int spiritThreshold = 100;

    // Thể chất và linh căn
    private Physique physique = Physique.NORMAL;
    private EnumSet<Affinity> affinities = EnumSet.of(Affinity.HOA);

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

                // Thiết lập thuộc tính gốc và hiện tại
                atts().setBase(game.enums.Attr.HEALTH, 100);
                atts().setBase(game.enums.Attr.PEP, 100);
                atts().setBase(game.enums.Attr.SPIRIT, 0);
                atts().setBase(game.enums.Attr.ATTACK, 10);
                atts().setBase(game.enums.Attr.DEF, 5);
                atts().setBase(game.enums.Attr.SOULD, 5);
                atts().setBase(game.enums.Attr.STRENGTH, 1);
                atts().resetAll();

                maxTier = physique.getMaxTier();
                spiritThreshold = (int)(100 * physique.getSpiritFactor());

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

    /**
     * Trả về tên cảnh giới kèm tầng hiện tại.
     */
    public String getRealmDisplay() {
        if (realm == Realm.PHAM_NHAN) return realm.getDisplayName();
        return realm.getDisplayName() + " tầng " + realmTier;
    }

    public Physique getPhysique() { return physique; }
    public EnumSet<Affinity> getAffinities() { return affinities; }

    /**
     * Gộp tên các linh căn thành chuỗi để hiển thị.
     */
    public String getAffinityNames() {
        return affinities.stream()
                .map(Affinity::getDisplayName)
                .collect(Collectors.joining(", "));
    }

    /**
     * @return lượng Spirit cần để lên tầng tiếp theo.
     */
    public int getSpiritThreshold() { return spiritThreshold; }

    /**
     * Cộng Spirit và kiểm tra lên cấp.
     */
    public void gainSpirit(int amount) {
        atts().add(game.enums.Attr.SPIRIT, amount);
        checkLevelUp();
    }

    // Kiểm tra điều kiện lên cấp
    private void checkLevelUp() {
        while (atts().get(game.enums.Attr.SPIRIT) >= spiritThreshold) {
            atts().add(game.enums.Attr.SPIRIT, -spiritThreshold);
            levelUp();
        }
    }

    // Xử lý lên cấp/đột phá
    private void levelUp() {
        if (realm == Realm.PHAM_NHAN) {
            realm = Realm.LUYEN_THE;
            realmTier = 1;
            applyLuyenTheBase();
            spiritThreshold = (int)(1000 * physique.getSpiritFactor());
            maxTier = physique.getMaxTier();
        } else if (realm == Realm.LUYEN_THE) {
            realmTier++;
            applyLuyenTheGrowth();
            spiritThreshold = (int)(spiritThreshold * 2 * physique.getSpiritFactor());
            if (physique == Physique.THANH_THE) {
                atts().setBase(game.enums.Attr.DEF, atts().getBase(game.enums.Attr.DEF) * 2);
            }
            if (physique == Physique.NGU_HANH_LINH_CAN) {
                multiplyAllBase(5);
            }
            if (realmTier > maxTier) {
                breakthroughToLuyenKhi();
            }
        } else if (realm == Realm.LUYEN_KHI) {
            realmTier++;
            applyLuyenKhiGrowth();
            spiritThreshold = (int)(spiritThreshold * 3 * physique.getSpiritFactor());
            if (physique == Physique.THANH_THE) {
                atts().setBase(game.enums.Attr.DEF, atts().getBase(game.enums.Attr.DEF) * 2);
            }
            if (physique == Physique.NGU_HANH_LINH_CAN) {
                multiplyAllBase(5);
            }
        }

        atts().resetAll();
    }

    private void applyLuyenTheBase() {
        atts().setBase(game.enums.Attr.HEALTH, 150);
        atts().setBase(game.enums.Attr.ATTACK, 10);
        atts().setBase(game.enums.Attr.PEP, 150);
        atts().setBase(game.enums.Attr.DEF, 5);
        atts().setBase(game.enums.Attr.SOULD, 5);
        atts().setBase(game.enums.Attr.STRENGTH, 2);
    }

    private void applyLuyenTheGrowth() {
        atts().setBase(game.enums.Attr.HEALTH, (int)(atts().getBase(game.enums.Attr.HEALTH) * 1.5));
        atts().setBase(game.enums.Attr.PEP, (int)(atts().getBase(game.enums.Attr.PEP) * 1.5));
        atts().setBase(game.enums.Attr.ATTACK, (int)(atts().getBase(game.enums.Attr.ATTACK) * 1.5));
        atts().setBase(game.enums.Attr.DEF, atts().getBase(game.enums.Attr.DEF) * 3);
    }

    private void breakthroughToLuyenKhi() {
        realm = Realm.LUYEN_KHI;
        realmTier = 1;
        atts().setBase(game.enums.Attr.HEALTH, atts().getBase(game.enums.Attr.HEALTH) * 2);
        atts().setBase(game.enums.Attr.ATTACK, atts().getBase(game.enums.Attr.ATTACK) * 2);
        atts().setBase(game.enums.Attr.PEP, atts().getBase(game.enums.Attr.PEP) * 2);
        atts().setBase(game.enums.Attr.DEF, atts().getBase(game.enums.Attr.DEF) * 2);
        atts().setBase(game.enums.Attr.STRENGTH, atts().getBase(game.enums.Attr.STRENGTH) * 2);
        atts().setBase(game.enums.Attr.SOULD, atts().getBase(game.enums.Attr.SOULD) * 2);
        spiritThreshold = (int)(spiritThreshold * 2 * physique.getSpiritFactor());
        maxTier = physique.getMaxTier();
    }

    private void applyLuyenKhiGrowth() {
        atts().setBase(game.enums.Attr.HEALTH, atts().getBase(game.enums.Attr.HEALTH) * 2);
        atts().setBase(game.enums.Attr.ATTACK, atts().getBase(game.enums.Attr.ATTACK) * 2);
        atts().setBase(game.enums.Attr.PEP, atts().getBase(game.enums.Attr.PEP) + spiritThreshold / 5);
        atts().setBase(game.enums.Attr.DEF, (int)(atts().getBase(game.enums.Attr.DEF) * 1.5));
    }

    // Nhân toàn bộ thuộc tính cơ bản lên một hệ số
    private void multiplyAllBase(int factor) {
        for (game.enums.Attr a : new game.enums.Attr[]{game.enums.Attr.HEALTH, game.enums.Attr.PEP,
                game.enums.Attr.ATTACK, game.enums.Attr.DEF, game.enums.Attr.SPIRIT,
                game.enums.Attr.STRENGTH, game.enums.Attr.SOULD}) {
            atts().setBase(a, atts().getBase(a) * factor);
        }
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
                    int damage = atts().get(game.enums.Attr.ATTACK);
                    if (physique == Physique.THAN_THE) {
                        damage *= 10; // Thần Thể tăng uy lực tấn công
                    }
                    if (monster instanceof Monster m) {
                        // Quái vật có quản lý máu riêng
                        if (m.takeDamage(damage)) {
                            m.dropItem();
                            gp.getMonsters().remove(i);
                            i--;
                        }
                    } else if (monster instanceof GameActor m) {
                        // Quái vật cũ chỉ trừ máu đơn giản
                        m.atts().add(game.enums.Attr.HEALTH, -damage);
                        if (m.atts().get(game.enums.Attr.HEALTH) <= 0) {
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
            atts().add(game.enums.Attr.HEALTH, -1);
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

	public int getScreenX() { return screenX; }
	public int getScreenY() { return screenY; }

	public static int getInteractionRange() { return INTERACTION_RANGE; }
	public Inventory getBag() { return bag; } 
}