package game.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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

    // Cảnh giới hiện tại và tầng tu luyện
    private Realm realm = Realm.PHAM_NHAN;
    private int realmLevel = 0;
    // Thể chất và linh căn
    private Physique physique = Physique.NORMAL;
    private List<Affinity> affinities = new ArrayList<>();
    // Giá trị tối đa của một số thuộc tính
    private int maxHealth = 100;
    private int maxPep = 100;
    private int requiredSpirit = 100; // SPIRIT cần để lên cấp tiếp theo

    private static final Path SAVE_PATH = Path.of("player_stats.properties");

    public Player(GamePanel gp) {
        super(gp);
        this.screenX = gp.getScreenWidth() / 2 - (gp.getTileSize() / 2);//360
        this.screenY = gp.getScreenHeight() / 2 - (gp.getTileSize() / 2);//264
        setCollision();
        setDefaultValue();
        loadStats();
        getImagePlayer();
        attackArea = new Rectangle(0, 0, gp.getTileSize(), gp.getTileSize());
    }

    /**
     * Khởi tạo các giá trị mặc định cho nhân vật.
     * Nếu đã có file lưu thì các giá trị sẽ được ghi đè khi {@link #loadStats()} gọi.
     */
    public void setDefaultValue() {
        setWorldX(100);
        setWorldY(100);
        setSpeed(4);
        setDirection("down");
        setSpriteCouter(0);
        setSpriteNum(1);
        setName("Nguyeen pro");
        // thuộc tính cơ bản
        atts().set(game.enums.Attr.HEALTH, 100);
        atts().set(game.enums.Attr.PEP, 100);
        atts().set(game.enums.Attr.SPIRIT, 0);
        atts().set(game.enums.Attr.ATTACK, 10);
        atts().set(game.enums.Attr.DEF, 5);
        atts().set(game.enums.Attr.SOULD, 5);
        atts().set(game.enums.Attr.STRENGTH, 1);
        // các giá trị tối đa tương ứng
        maxHealth = 100;
        maxPep = 100;
        requiredSpirit = 100;
        realm = Realm.PHAM_NHAN;
        realmLevel = 0;
        // random thể chất và linh căn
        physique = Physique.randomPhysique();
        affinities = Affinity.randomAffinities();
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
     * @return tên hiển thị của cảnh giới kèm tầng hiện tại.
     */
    public String getRealmName() {
        return realm == Realm.PHAM_NHAN ? realm.getDisplayName()
                : realm.getDisplayName() + " tầng " + realmLevel;
    }

    public int getRealmLevel() { return realmLevel; }
    public Physique getPhysique() { return physique; }
    public List<Affinity> getAffinities() { return affinities; }
    public String getAffinityDisplay() { return Affinity.joinDisplay(affinities); }
    public int getMaxHealth() { return maxHealth; }
    public int getMaxPep() { return maxPep; }
    public int getRequiredSpirit() { return requiredSpirit; }

    /**
     * Tăng SPIRIT và kiểm tra lên cấp.
     */
    public void gainSpirit(int amount) {
        atts().add(game.enums.Attr.SPIRIT, amount);
        checkLevelUp();
        saveStats();
    }

    /**
     * Kiểm tra và thực hiện việc tăng cấp dựa trên SPIRIT hiện có.
     */
    private void checkLevelUp() {
        while (atts().get(game.enums.Attr.SPIRIT) >= requiredSpirit) {
            atts().add(game.enums.Attr.SPIRIT, -requiredSpirit);
            levelUp();
        }
    }

    /**
     * Thực hiện tăng cấp theo quy tắc từng cảnh giới.
     */
    private void levelUp() {
        switch (realm) {
            case PHAM_NHAN -> {
                realm = Realm.LUYEN_THE;
                realmLevel = 1;
                applyLuyenTheIncrease();
                requiredSpirit = 1000;
            }
            case LUYEN_THE -> {
                realmLevel++;
                int maxLevel = getMaxLayerForRealm(Realm.LUYEN_THE);
                if (realmLevel > maxLevel) {
                    realm = Realm.LUYEN_KHI;
                    realmLevel = 1;
                    doubleAllStats();
                    requiredSpirit *= 2; // chuyển cảnh giới
                } else {
                    applyLuyenTheIncrease();
                    requiredSpirit *= 2;
                }
            }
            case LUYEN_KHI -> {
                realmLevel++;
                applyLuyenKhiIncrease();
                requiredSpirit *= 3;
            }
        }
        atts().set(game.enums.Attr.HEALTH, maxHealth);
        atts().set(game.enums.Attr.PEP, maxPep);
    }

    private int getMaxLayerForRealm(Realm r) {
        int base = 10;
        if (physique == Physique.HU_KHONG) base = 15;
        if (physique == Physique.HU_KHONG_DAI_DE) base = 20;
        return base;
    }

    /**
     * Tăng chỉ số khi lên tầng Luyện thể.
     */
    private void applyLuyenTheIncrease() {
        maxHealth = (int) (maxHealth * 1.5);
        maxPep = (int) (maxPep * 1.5);
        atts().set(game.enums.Attr.ATTACK, (int) (atts().get(game.enums.Attr.ATTACK) * 1.5));
        atts().set(game.enums.Attr.DEF, (int) (atts().get(game.enums.Attr.DEF) * 3));
    }

    /**
     * Tăng chỉ số khi lên tầng Luyện khí.
     */
    private void applyLuyenKhiIncrease() {
        maxHealth *= 2;
        atts().set(game.enums.Attr.ATTACK, atts().get(game.enums.Attr.ATTACK) * 2);
        maxPep += requiredSpirit / 5;
        atts().set(game.enums.Attr.DEF, (int) (atts().get(game.enums.Attr.DEF) * 1.5));
    }

    /**
     * Khi đột phá sang cảnh giới mới, tất cả chỉ số nhân đôi.
     */
    private void doubleAllStats() {
        maxHealth *= 2;
        maxPep *= 2;
        atts().set(game.enums.Attr.ATTACK, atts().get(game.enums.Attr.ATTACK) * 2);
        atts().set(game.enums.Attr.DEF, atts().get(game.enums.Attr.DEF) * 2);
        atts().set(game.enums.Attr.SOULD, atts().get(game.enums.Attr.SOULD) * 2);
        atts().set(game.enums.Attr.STRENGTH, atts().get(game.enums.Attr.STRENGTH) * 2);
    }

    /**
     * Lưu trạng thái nhân vật vào file.
     */
    public void saveStats() {
        try (OutputStream out = Files.newOutputStream(SAVE_PATH)) {
            java.util.Properties props = new java.util.Properties();
            props.setProperty("realm", realm.name());
            props.setProperty("realmLevel", Integer.toString(realmLevel));
            props.setProperty("maxHealth", Integer.toString(maxHealth));
            props.setProperty("maxPep", Integer.toString(maxPep));
            props.setProperty("requiredSpirit", Integer.toString(requiredSpirit));
            props.setProperty("attack", Integer.toString(atts().get(game.enums.Attr.ATTACK)));
            props.setProperty("def", Integer.toString(atts().get(game.enums.Attr.DEF)));
            props.setProperty("sould", Integer.toString(atts().get(game.enums.Attr.SOULD)));
            props.setProperty("strength", Integer.toString(atts().get(game.enums.Attr.STRENGTH)));
            props.setProperty("spirit", Integer.toString(atts().get(game.enums.Attr.SPIRIT)));
            props.setProperty("physique", physique.name());
            props.setProperty("affinities", affinities.stream().map(Enum::name).collect(java.util.stream.Collectors.joining(",")));
            props.store(out, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Đọc trạng thái nhân vật từ file lưu.
     */
    private void loadStats() {
        if (Files.exists(SAVE_PATH)) {
            java.util.Properties props = new java.util.Properties();
            try (InputStream in = Files.newInputStream(SAVE_PATH)) {
                props.load(in);
                realm = Realm.valueOf(props.getProperty("realm", realm.name()));
                realmLevel = Integer.parseInt(props.getProperty("realmLevel", "0"));
                maxHealth = Integer.parseInt(props.getProperty("maxHealth", "100"));
                maxPep = Integer.parseInt(props.getProperty("maxPep", "100"));
                requiredSpirit = Integer.parseInt(props.getProperty("requiredSpirit", "100"));
                atts().set(game.enums.Attr.ATTACK, Integer.parseInt(props.getProperty("attack", "10")));
                atts().set(game.enums.Attr.DEF, Integer.parseInt(props.getProperty("def", "5")));
                atts().set(game.enums.Attr.SOULD, Integer.parseInt(props.getProperty("sould", "5")));
                atts().set(game.enums.Attr.STRENGTH, Integer.parseInt(props.getProperty("strength", "1")));
                atts().set(game.enums.Attr.SPIRIT, Integer.parseInt(props.getProperty("spirit", "0")));
                physique = Physique.valueOf(props.getProperty("physique", physique.name()));
                String aff = props.getProperty("affinities", "");
                if (!aff.isEmpty()) {
                    affinities = Arrays.stream(aff.split(",")).map(Affinity::valueOf).collect(java.util.stream.Collectors.toList());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            saveStats();
        }
        atts().set(game.enums.Attr.HEALTH, maxHealth);
        atts().set(game.enums.Attr.PEP, maxPep);
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