package game.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import game.entity.inventory.Inventory;
import game.entity.item.Item;
import game.entity.item.elixir.SpiritPotion;
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

    // File lưu trữ chỉ số nhân vật
    private static final Path SAVE_FILE = Paths.get("player_stats.properties");

    // Cảnh giới hiện tại của người chơi
    private Realm realm = Realm.PHAM_NHAN;
    // Tầng (tiểu cảnh giới) hiện tại trong đại cảnh giới
    private int realmStage = 0;
    // Thể chất của nhân vật
    private Physique physique = Physique.BINH_THUONG;
    // Danh sách linh căn (có thể nhiều hơn 1)
    private final List<Affinity> affinities = new ArrayList<>();

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
                // Thuộc tính cơ bản của cảnh giới phàm nhân
                atts().set(game.enums.Attr.HEALTH, 100);
                atts().set(game.enums.Attr.PEP, 100);
                atts().set(game.enums.Attr.SPIRIT, 0);
                atts().set(game.enums.Attr.ATTACK, 5);
                atts().set(game.enums.Attr.DEF, 4);
                atts().set(game.enums.Attr.SOULD, 5);
                atts().set(game.enums.Attr.STRENGTH, 1);
                // Thể chất và linh căn mặc định
                physique = Physique.BINH_THUONG;
                affinities.clear();
                affinities.add(Affinity.HOA);
                // Thêm item thử nghiệm tăng SPIRIT
                bag.add(new SpiritPotion(200, 3));
        setScaleEntityX(gp.getTileSize());
        setScaleEntityY(gp.getTileSize());
        // Tải chỉ số đã lưu nếu tồn tại
        loadStats();
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
     * Nhận thêm SPIRIT và kiểm tra lên cấp.
     */
    public void gainSpirit(int amount) {
        atts().add(game.enums.Attr.SPIRIT, amount);
        checkLevelUp();
        // Lưu lại chỉ số sau khi thay đổi
        saveStats();
    }

    private void checkLevelUp() {
        boolean leveled;
        do {
            leveled = false;
            int required = getRequiredSpirit();
            int current = atts().get(game.enums.Attr.SPIRIT);
            if (current >= required) {
                atts().add(game.enums.Attr.SPIRIT, -required);
                if (realm == Realm.PHAM_NHAN) {
                    realm = Realm.LUYEN_THE;
                    realmStage = 1;
                    // Thiết lập thuộc tính ban đầu của Luyện thể tầng 1
                    atts().set(game.enums.Attr.HEALTH, 150);
                    atts().set(game.enums.Attr.PEP, 150);
                    atts().set(game.enums.Attr.ATTACK, 10);
                    atts().set(game.enums.Attr.DEF, 5);
                    atts().set(game.enums.Attr.SOULD, 5);
                    atts().set(game.enums.Attr.STRENGTH, 2);
                } else if (realm == Realm.LUYEN_THE) {
                    realmStage++;
                    applyLuyenTheIncrease();
                    if (realmStage > physique.getMaxTier()) {
                        // Đột phá lên Luyện khí
                        realm = Realm.LUYEN_KHI;
                        realmStage = 1;
                        applyBreakThrough();
                    }
                } else if (realm == Realm.LUYEN_KHI) {
                    realmStage++;
                    applyLuyenKhiIncrease();
                }
                leveled = true;
            }
        } while (leveled);
    }

    private int getRequiredSpirit() {
        return switch (realm) {
            case PHAM_NHAN -> 100;
            case LUYEN_THE -> 1000;
            case LUYEN_KHI -> 10000;
        };
    }

    /**
     * Nhân chỉ số với hệ số, giới hạn ở {@link Integer#MAX_VALUE} để tránh tràn số.
     */
    private int safeMul(int value, double factor) {
        long result = Math.round(value * factor);
        return (int) Math.min(Integer.MAX_VALUE, result);
    }

    private void applyLuyenTheIncrease() {
        atts().set(game.enums.Attr.HEALTH, safeMul(atts().get(game.enums.Attr.HEALTH), 1.5));
        atts().set(game.enums.Attr.PEP, safeMul(atts().get(game.enums.Attr.PEP), 1.5));
        atts().set(game.enums.Attr.ATTACK, safeMul(atts().get(game.enums.Attr.ATTACK), 1.5));
        atts().set(game.enums.Attr.SPIRIT, safeMul(atts().get(game.enums.Attr.SPIRIT), 2));
        atts().set(game.enums.Attr.DEF, safeMul(atts().get(game.enums.Attr.DEF), 3));
    }

    private void applyBreakThrough() {
        for (game.enums.Attr a : game.enums.Attr.values()) {
            if (a == game.enums.Attr.PHYSIQUE || a == game.enums.Attr.AFFINITY) continue;
            atts().set(a, safeMul(atts().get(a), 2));
        }
    }

    private void applyLuyenKhiIncrease() {
        atts().set(game.enums.Attr.HEALTH, safeMul(atts().get(game.enums.Attr.HEALTH), 2));
        atts().set(game.enums.Attr.ATTACK, safeMul(atts().get(game.enums.Attr.ATTACK), 2));
        atts().add(game.enums.Attr.PEP, atts().get(game.enums.Attr.SPIRIT) / 5);
        atts().set(game.enums.Attr.SPIRIT, safeMul(atts().get(game.enums.Attr.SPIRIT), 3));
        atts().set(game.enums.Attr.DEF, safeMul(atts().get(game.enums.Attr.DEF), 1.5));
    }

    public Physique getPhysique() {
        return physique;
    }

    public List<Affinity> getAffinities() {
        return List.copyOf(affinities);
    }

    public int getRealmStage() {
        return realmStage;
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
        // Lưu lại chỉ số sau khi dùng item
        saveStats();
    }

    /**
     * Ghi lại các thuộc tính quan trọng của nhân vật ra file.
     */
    private void saveStats() {
        try {
            Properties props = new Properties();
            for (game.enums.Attr a : game.enums.Attr.values()) {
                props.setProperty(a.name(), String.valueOf(atts().get(a)));
            }
            props.setProperty("realm", realm.name());
            props.setProperty("realmStage", String.valueOf(realmStage));
            props.setProperty("physique", physique.name());
            String aff = affinities.stream().map(Affinity::name).collect(Collectors.joining(","));
            props.setProperty("affinities", aff);
            try (var out = Files.newOutputStream(SAVE_FILE)) {
                props.store(out, "player stats");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tải lại các thuộc tính đã lưu nếu file tồn tại.
     */
    private void loadStats() {
        if (!Files.exists(SAVE_FILE)) return;
        try {
            Properties props = new Properties();
            try (var in = Files.newInputStream(SAVE_FILE)) {
                props.load(in);
            }
            for (game.enums.Attr a : game.enums.Attr.values()) {
                String val = props.getProperty(a.name());
                if (val != null) {
                    atts().set(a, Integer.parseInt(val));
                }
            }
            realm = Realm.valueOf(props.getProperty("realm", realm.name()));
            realmStage = Integer.parseInt(props.getProperty("realmStage", String.valueOf(realmStage)));
            physique = Physique.valueOf(props.getProperty("physique", physique.name()));
            affinities.clear();
            String aff = props.getProperty("affinities");
            if (aff != null && !aff.isBlank()) {
                for (String s : aff.split(",")) {
                    try {
                        affinities.add(Affinity.valueOf(s));
                    } catch (IllegalArgumentException ignored) {}
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public int getScreenX() { return screenX; }
	public int getScreenY() { return screenY; }

	public static int getInteractionRange() { return INTERACTION_RANGE; }
	public Inventory getBag() { return bag; } 
}