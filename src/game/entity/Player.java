package game.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.sql.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.Arrays;

import javax.imageio.ImageIO;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import game.entity.inventory.Inventory;
import game.entity.item.Item;
import game.entity.item.EquipmentItem;
import game.entity.attributes.Attributes;
import game.interfaces.DrawableEntity;
import game.entity.monster.Monster;
import game.enums.Affinity;
import game.enums.Attr;
import game.enums.Physique;
import game.enums.Realm;
import game.enums.SkillGrade;
import game.enums.EquipSlot;
import game.enums.EquipType;
import game.entity.skill.CultivationTechnique;

import game.main.GamePanel;
import game.util.CameraHelper;
import game.util.UtilityTool;
import game.db.DBAccount;
import game.db.PlayerDao;
import game.db.PlayerBaseStatsDao;
import game.db.PlayerRuntimeDao;
import game.db.PlayerSkillDao;
import game.db.PlayerSkillBindingDao;

public class Player extends GameActor implements DrawableEntity {
	// Vị trí nhân vật trên màn hình (luôn ở giữa)
    private final int screenX;
    private final int screenY;
    
    private static final int INTERACTION_RANGE = 80;

    /** Database table storing serialized player profiles. */
    private static final String PROFILE_TABLE = "PlayerProfile";

    private final Inventory bag = new Inventory();
    private final EnumMap<EquipSlot, EquipmentItem> equipment = new EnumMap<>(EquipSlot.class);
    private final Attributes baseAtts = new Attributes();
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
    private int realmStage = 0;
    /** PlayerId from SQL database. */
    private String playerId;
    /**
     * Yêu cầu SPIRIT thực tế để lên cấp tiếp theo sau khi áp dụng hệ số thể chất.
     * Giá trị gốc trước khi áp dụng hệ số được lưu trong {@code baseSpiritRequirement}.
     */
    private int spiritToNextLevel = 1000;
    /**
     * Yêu cầu SPIRIT cơ bản chưa áp dụng hệ số thể chất. Dùng để tính toán
     * khi lên cấp nhằm tránh giảm dần yêu cầu SPIRIT ở các thể chất đặc biệt
     * (ví dụ Tiên Linh Thể có hệ số < 1).
     */
    private int baseSpiritRequirement = 1000;

    // Thể chất và linh căn
    private Physique physique = Physique.NORMAL;
    private EnumSet<Affinity> affinities = EnumSet.noneOf(Affinity.class);

    private final Random random = new Random();

    private LocalDate creationDate = LocalDate.now();
    private final List<String> realmLog = new ArrayList<>();

    // Tự động lưu hồ sơ
    private final ScheduledExecutorService autoSaveExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });

    // Danh sách công pháp đã học
    private final List<CultivationTechnique> techniques = new ArrayList<>();
    // Trạng thái tu luyện
    private boolean cultivating = false;
    private CultivationTechnique activeTechnique;
    private long cultivationEndTime = 0;
    private long cultivationCooldownEnd = 0;
    private long lastSpiritTick = 0;
    // Buff từ đan dược tăng tốc tu luyện
    private String activePillName;
    private int pillSpiritBonus = 0;
    private long pillBuffEnd = 0;

    /** Identifier of the current map the player is located in. */
    private String mapId = "world01";

	public Player(GamePanel gp) {
		super(gp);
        this.screenX = gp.getScreenWidth() / 2 - (gp.getTileSize() / 2);//360
        this.screenY = gp.getScreenHeight() / 2 - (gp.getTileSize() / 2);//264
        setCollision();
        setDefaultValue();
        getImagePlayer();
        attackArea = new Rectangle(0, 0, gp.getTileSize(), gp.getTileSize());

        startAutoSave();
        }
        public void setDefaultValue() {
                setWorldX(100);
                setWorldY(100);
                setSpeed(4);
                setDirection("down");
                setSpriteCouter(0);
        setSpriteNum(1);
        setName("Nguyeen pro");

        if (!loadProfile()) {
            // Thuộc tính cơ bản
            baseAtts.setMax(Attr.HEALTH, 100);
            baseAtts.set(Attr.HEALTH, 100);
            baseAtts.setMax(Attr.PEP, 100);
            baseAtts.set(Attr.PEP, 100);
            // Attack/Def không còn giới hạn max mặc định để có thể tăng khi lên cấp
            baseAtts.set(Attr.ATTACK, 5);
            baseAtts.set(Attr.DEF, 4);
            baseAtts.set(Attr.STRENGTH, 1);
            baseAtts.set(Attr.SOULD, 5);

            // Thiết lập thể chất và linh căn ngẫu nhiên
            physique = randomPhysique();
            affinities = randomAffinities();

            // Tính lại yêu cầu SPIRIT dựa trên hệ số thể chất
            baseSpiritRequirement = 1000;
            spiritToNextLevel = (int) Math.round(baseSpiritRequirement * physique.getSpiritReqFactor());
            baseAtts.setMax(Attr.SPIRIT, spiritToNextLevel);
            baseAtts.set(Attr.SPIRIT, 0);

            // Thêm vài item test: bình hồi máu & tinh thần
            addItem(new game.entity.item.elixir.HealthPotion(50, 3));
            addItem(new game.entity.item.elixir.SpiritPotion(200, 100));
            addItem(new game.entity.item.elixir.SpiritPotion(2000, 100));
            addItem(new game.entity.item.elixir.SpiritPotion(20000, 100));

            // Các sách công pháp và đan dược tu luyện để thử nghiệm
            var low = new CultivationTechnique("Công pháp hạ phẩm", SkillGrade.HA, 1, 1);
            var mid = new CultivationTechnique("Công pháp trung phẩm", SkillGrade.TRUNG, 1, 2);
            var high = new CultivationTechnique("Công pháp thượng phẩm", SkillGrade.THUONG, 1, 3);
            var top = new CultivationTechnique("Công pháp cực phẩm", SkillGrade.CUC, 1, 5);
            addItem(new game.entity.item.book.CultivationBook(low));
            addItem(new game.entity.item.book.CultivationBook(mid));
            addItem(new game.entity.item.book.CultivationBook(high));
            addItem(new game.entity.item.book.CultivationBook(top));
            addItem(new game.entity.item.elixir.CultivationPill("Đan hạ phẩm", 1, 1));
            addItem(new game.entity.item.elixir.CultivationPill("Đan trung phẩm", 2, 1));
            addItem(new game.entity.item.elixir.CultivationPill("Đan thượng phẩm", 3, 1));
            addItem(new game.entity.item.elixir.CultivationPill("Đan cực phẩm", 4, 1));
            
            addItem(new EquipmentItem("Áo giáp", "+3 DEF", "/data/item/equipment/armor.png", EquipType.ARMOR));
            addItem(new EquipmentItem("Mũ sắt", "+3 DEF", "/data/item/equipment/helmet.png", EquipType.HELMET));
            addItem(new EquipmentItem("Quần vải", "+3 DEF", "/data/item/equipment/pants.png", EquipType.PANTS));
            addItem(new EquipmentItem("Giày da", "+3 DEF", "/data/item/equipment/shoes.png", EquipType.SHOES));
            addItem(new EquipmentItem("Kiếm gỗ", "+10 ATTACK", "/data/item/equipment/sword.png", EquipType.WEAPON));
            addItem(new EquipmentItem("Kiếm sắt", "+10 ATTACK", "/data/item/equipment/sword.png", EquipType.WEAPON));
            addItem(new EquipmentItem("Dây chuyền", "+10 SOULD", "/data/item/equipment/ring.png", EquipType.NECKLACE));
            addItem(new EquipmentItem("Nhẫn đá", "+10 ô kho", "/data/item/equipment/ring.png", EquipType.RING));
            addItem(new EquipmentItem("Nhẫn bạc", "+10 ô kho", "/data/item/equipment/ring.png", EquipType.RING));
            addItem(new EquipmentItem("Bùa hộ mệnh", "Chưa có tác dụng", "/data/item/equipment/d_1.png", EquipType.AMULET));

            saveState();
        }

        refreshStats();

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
            updateCultivation();
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
            if (cultivating) {
                // Đang tu luyện: không cho di chuyển
                resestSpriteToDefault();
                return;
            }

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
                    int damage = atts().get(Attr.ATTACK);
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
            baseAtts.add(Attr.HEALTH, -1);
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
    
    // Thêm item vào túi và lưu, trả về true nếu thành công
    public boolean addItem(Item item) {
        boolean added = bag.add(item);
        saveProfile();
        return added;
    }

    // Sử dụng item
    public void useItem(Item i) {
        i.use(this);
        if(i.getQuantity() == 0) bag.remove(i);
        saveState();
    }

    /**
     * Tăng Spirit và tự động kiểm tra lên cấp.
     */
    public void gainSpirit(int amount) {
        // Tiên Linh Thể tu luyện nhanh gấp 3 lần
        int modified = (int) Math.round(amount * physique.getCultivationSpeedFactor());
        baseAtts.add(Attr.SPIRIT, modified);
        while (baseAtts.get(Attr.SPIRIT) >= spiritToNextLevel) {
            baseAtts.add(Attr.SPIRIT, -spiritToNextLevel);
            levelUp();
        }
        refreshStats();
    }

    // ------------ Hệ thống kỹ năng & tu luyện ------------

    /** Người chơi học một công pháp mới. */
    public void learnSkill(CultivationTechnique tech) {
        techniques.add(tech);
        // Lưu lại tiến trình ngay sau khi học để đảm bảo
        // công pháp tồn tại khi thoát game.
        saveState();
    }

    // Bản đồ phím -> công pháp được gán.
    private final Map<Integer, CultivationTechnique> techniqueBindings = new HashMap<>();

    /** Gán một công pháp cho phím. */
    public void bindTechnique(int keyCode, CultivationTechnique tech) {
        techniqueBindings.put(keyCode, tech);
        saveState();
    }

    /** Lấy công pháp được gán cho phím. */
    public CultivationTechnique getTechniqueForKey(int keyCode) {
        return techniqueBindings.get(keyCode);
    }

    /** Tìm phím đã gán cho công pháp, trả về null nếu chưa gán. */
    public Integer getKeyForTechnique(CultivationTechnique tech) {
        for (Map.Entry<Integer, CultivationTechnique> e : techniqueBindings.entrySet()) {
            if (e.getValue().equals(tech)) return e.getKey();
        }
        return null;
    }

    public Map<Integer, CultivationTechnique> getTechniqueBindings() {
        return Map.copyOf(techniqueBindings);
    }

    public List<CultivationTechnique> getTechniques() { return List.copyOf(techniques); }

    public String getSkillSummary() {
        if (techniques.isEmpty()) return "None";
        return techniques.stream()
                .map(t -> t.getName() + "(" + t.getLevel() + ")(" + t.getGrade().getDisplay() + ")")
                .collect(Collectors.joining(", "));
    }

    private CultivationTechnique findTechniqueByName(String name) {
        for (CultivationTechnique t : techniques) {
            if (t.getName().equals(name)) return t;
        }
        return null;
    }

    public void startCultivating(CultivationTechnique tech) {
        long now = System.currentTimeMillis();
        if (cultivating || now < cultivationCooldownEnd) return;
        cultivating = true;
        activeTechnique = tech;
        lastSpiritTick = now;
        cultivationEndTime = now + 3600_000L;
        cultivationCooldownEnd = now + 7200_000L;
    }

    public void cancelCultivation() {
        cultivating = false;
        activeTechnique = null;
    }

    private void updateCultivation() {
        long now = System.currentTimeMillis();
        if (pillSpiritBonus > 0 && now > pillBuffEnd) {
            pillSpiritBonus = 0;
            activePillName = null;
        }
        if (!cultivating) return;
        if (now >= cultivationEndTime) {
            cultivating = false;
            activeTechnique = null;
            return;
        }
        if (now - lastSpiritTick >= 1000) {
            lastSpiritTick += 1000;
            gainSpirit(activeTechnique.getSpiritPerSecond() + pillSpiritBonus);
        }
    }

    public long getCultivationCooldownRemaining() {
        return Math.max(0, cultivationCooldownEnd - System.currentTimeMillis());
    }

    public long getCultivationTimeLeft() {
        if (!cultivating) return 0;
        return Math.max(0, cultivationEndTime - System.currentTimeMillis());
    }

    public boolean isCultivating() { return cultivating; }

    public void consumeCultivationPill(String name, int bonus, long durationMs) {
        activePillName = name;
        pillSpiritBonus = bonus;
        pillBuffEnd = System.currentTimeMillis() + durationMs;
    }

    public int getPillSpiritBonus() { return pillSpiritBonus; }
    public String getActivePillName() { return activePillName; }
    public long getPillTimeLeft() { return Math.max(0, pillBuffEnd - System.currentTimeMillis()); }

    /**
     * Thực hiện lên cấp theo cảnh giới hiện tại.
     */
    private void levelUp() {
        switch (realm) {
            case PHAM_NHAN -> {
                realm = Realm.LUYEN_THE;
                realmStage = 1;
                initializeLuyenThe();
                baseSpiritRequirement += baseSpiritRequirement / 2;
                spiritToNextLevel = (int) Math.round(baseSpiritRequirement * physique.getSpiritReqFactor());
                baseAtts.add(Attr.SOULD, 10);
            }
            case LUYEN_THE -> {
                realmStage++;
                if (realmStage > physique.getMaxStage()) {
                    breakThroughToLuyenKhi();
                    baseAtts.add(Attr.SOULD, 10);
                } else {
                    applyStageGrowth();
                    baseSpiritRequirement += baseSpiritRequirement / 2;
                    spiritToNextLevel = (int) Math.round(baseSpiritRequirement * physique.getSpiritReqFactor());
                }
            }
            case LUYEN_KHI -> {
                realmStage++;
                if (realmStage > physique.getMaxStage()) {
                    realmStage = physique.getMaxStage();
                } else {
                    applyStageGrowth();
                    baseSpiritRequirement += baseSpiritRequirement / 2;
                    spiritToNextLevel = (int) Math.round(baseSpiritRequirement * physique.getSpiritReqFactor());
                }
            }
        }
        // Cập nhật max Spirit cho HUD
        baseAtts.setMax(Attr.SPIRIT, spiritToNextLevel);
        refreshStats();
        saveState();
    }

    /**
     * Thiết lập chỉ số cơ bản khi bước vào Luyện thể tầng 1.
     */
    private void initializeLuyenThe() {
        int hp = (int) (150 * physique.getStatFactor());
        baseAtts.setMax(Attr.HEALTH, hp);
        baseAtts.set(Attr.HEALTH, hp);

        int pep = (int) (150 * physique.getStatFactor());
        baseAtts.setMax(Attr.PEP, pep);
        baseAtts.set(Attr.PEP, pep);

        baseAtts.set(Attr.ATTACK, (int) (10 * physique.getStatFactor()));
        baseAtts.set(Attr.DEF, (int) (5 * physique.getDefFactor()));
        baseAtts.set(Attr.STRENGTH, (int) (2 * physique.getStatFactor()));
    }

    /**
     * Tăng chỉ số khi thăng một tiểu cảnh giới.
     * Áp dụng cho cả Luyện thể và Luyện khí.
     */
    private void applyStageGrowth() {
        int stage = realmStage;

        // HEALTH & PEP: cộng 50 cho mỗi tiểu cảnh giới
        int hpInc = (int) (stage * 50 * physique.getStatFactor());
        int newHp = baseAtts.getMax(Attr.HEALTH) + hpInc;
        baseAtts.setMax(Attr.HEALTH, newHp);
        baseAtts.set(Attr.HEALTH, newHp);

        int pepInc = (int) (stage * 50 * physique.getStatFactor());
        int newPep = baseAtts.getMax(Attr.PEP) + pepInc;
        baseAtts.setMax(Attr.PEP, newPep);
        baseAtts.set(Attr.PEP, newPep);

        // ATTACK: +1, riêng bội số của 3 cộng thêm chính số đó
        int atkInc = (stage % 3 == 0) ? stage : 1;
        baseAtts.add(Attr.ATTACK, (int) (atkInc * physique.getStatFactor()));

        // DEF: +1, bội số của 3 cộng thêm stage/2 (làm tròn lên)
        int defInc = 1;
        if (stage % 3 == 0) {
            defInc = (stage + 1) / 2;
        }
        baseAtts.add(Attr.DEF, (int) (defInc * physique.getDefFactor()));

        // STRENGTH: +1 ở bội số của 3
        if (stage % 3 == 0) {
            baseAtts.add(Attr.STRENGTH, (int) (1 * physique.getStatFactor()));
        }
    }

    /**
     * Đột phá từ Luyện thể sang Luyện khí.
     */
    private void breakThroughToLuyenKhi() {
        realm = Realm.LUYEN_KHI;
        realmStage = 1;

        int hp = (int) (baseAtts.getMax(Attr.HEALTH) * 2 * physique.getStatFactor());
        baseAtts.setMax(Attr.HEALTH, hp);
        baseAtts.set(Attr.HEALTH, hp);

        int pep = (int) (baseAtts.getMax(Attr.PEP) * 2 * physique.getStatFactor());
        baseAtts.setMax(Attr.PEP, pep);
        baseAtts.set(Attr.PEP, pep);

        int atk = (int) (baseAtts.get(Attr.ATTACK) * 2 * physique.getStatFactor());
        baseAtts.set(Attr.ATTACK, atk);

        int def = (int) (baseAtts.get(Attr.DEF) * 3 * physique.getDefFactor());
        baseAtts.set(Attr.DEF, def);

        int str = (int) (baseAtts.get(Attr.STRENGTH) * 2 * physique.getStatFactor());
        baseAtts.set(Attr.STRENGTH, str);

        int soul = (int) (baseAtts.get(Attr.SOULD) * 2 * physique.getStatFactor());
        baseAtts.set(Attr.SOULD, soul);

        baseSpiritRequirement *= 2;
        spiritToNextLevel = (int) Math.round(baseSpiritRequirement * physique.getSpiritReqFactor());
    }

    private void logRealmState() {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        StringBuilder sb = new StringBuilder();
        sb.append("=============================\n");
        sb.append("cảnh giới " + getRealmName().toLowerCase() + " - " + time + "\n");
        sb.append("--------------------------\n");
        sb.append("Thuộc tính gốc:\n");
        sb.append("HEALTH: " + baseAtts.get(Attr.HEALTH) + "/" + baseAtts.getMax(Attr.HEALTH) + "\n");
        sb.append("ATTACK: " + baseAtts.get(Attr.ATTACK) + "\n");
        sb.append("PEP: " + baseAtts.get(Attr.PEP) + "/" + baseAtts.getMax(Attr.PEP) + "\n");
        sb.append("DEF: " + baseAtts.get(Attr.DEF) + "\n");
        sb.append("SOULD: " + baseAtts.get(Attr.SOULD) + "\n");
        sb.append("SPIRIT: " + baseAtts.get(Attr.SPIRIT) + "/" + spiritToNextLevel + "\n");
        sb.append("STRENGTH: " + baseAtts.get(Attr.STRENGTH) + "\n");
        sb.append("Thuộc tính sau khi mặc đồ:\n");
        sb.append("HEALTH: " + atts().get(Attr.HEALTH) + "/" + atts().getMax(Attr.HEALTH) + "\n");
        int atkBonus = atts().get(Attr.ATTACK) - baseAtts.get(Attr.ATTACK);
        sb.append("ATTACK: " + baseAtts.get(Attr.ATTACK) + (atkBonus > 0 ? " +" + atkBonus : "") + "\n");
        sb.append("PEP: " + atts().get(Attr.PEP) + "/" + atts().getMax(Attr.PEP) + "\n");
        int defBonus = atts().get(Attr.DEF) - baseAtts.get(Attr.DEF);
        sb.append("DEF: " + baseAtts.get(Attr.DEF) + (defBonus > 0 ? " +" + defBonus : "") + "\n");
        int soulBonus = atts().get(Attr.SOULD) - baseAtts.get(Attr.SOULD);
        sb.append("SOULD: " + baseAtts.get(Attr.SOULD) + (soulBonus > 0 ? " +" + soulBonus : "") + "\n");
        int strBonus = atts().get(Attr.STRENGTH) - baseAtts.get(Attr.STRENGTH);
        sb.append("STRENGTH: " + baseAtts.get(Attr.STRENGTH) + (strBonus > 0 ? " +" + strBonus : "") + "\n");
        sb.append("--------------------------\n");
        sb.append("SPIRIT: " + atts().get(Attr.SPIRIT) + "/" + spiritToNextLevel + "\n");
        sb.append("PHYSIQUE: " + physique.getDisplay() + "\n");
        sb.append("AFFINITY: " + getAffinityNames() + "\n");
        sb.append("SKILL: " + getSkillSummary() + "\n");
        realmLog.add(sb.toString());
    }

    public synchronized void saveState() {
        // Sync runtime attributes before persisting
        baseAtts.set(Attr.HEALTH, atts().get(Attr.HEALTH));
        baseAtts.set(Attr.PEP, atts().get(Attr.PEP));
        logRealmState();
        saveStats();
        saveProfile();
    }

    private void startAutoSave() {
        autoSaveExecutor.scheduleAtFixedRate(this::saveState, 10, 10, TimeUnit.MINUTES);
    }

    public void stopAutoSave() {
        autoSaveExecutor.shutdownNow();
        saveState();
    }

    /** Persist core stats to SQL Server tables. */
    private void saveStats() {
        try {
            PlayerDao pdao = new PlayerDao();
            playerId = pdao.upsert(getName(), realm.name(), realmStage, physique.name(), getAffinityNames());

            PlayerBaseStatsDao bsDao = new PlayerBaseStatsDao();
            bsDao.upsert(playerId, baseAtts);

            PlayerRuntimeDao rtDao = new PlayerRuntimeDao();
            rtDao.upsert(playerId, atts().get(Attr.HEALTH), atts().get(Attr.PEP), 0,
                    mapId, getWorldX(), getWorldY());

            PlayerSkillDao skDao = new PlayerSkillDao();
            skDao.replaceAll(playerId, techniques);

            PlayerSkillBindingDao bindDao = new PlayerSkillBindingDao();
            Map<Integer, String> map = new HashMap<>();
            for (Map.Entry<Integer, CultivationTechnique> e : techniqueBindings.entrySet()) {
                map.put(e.getKey(), e.getValue().getName());
            }
            bindDao.replaceAll(playerId, map);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    /** Persist current player profile to SQL Server. */
    private void saveProfile() {
        try (Connection conn = DBAccount.getConnectDB()) {
            try (Statement st = conn.createStatement()) {
                st.execute(
                    "IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'" +
                    PROFILE_TABLE + "') AND type = N'U') " +
                    "CREATE TABLE " + PROFILE_TABLE +
                    " (name NVARCHAR(100) PRIMARY KEY, profile NVARCHAR(MAX))"
                );
            }

            List<String> lines = new ArrayList<>();
            lines.add("CREATION_DATE: " + creationDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            String items = bag.all().stream()
                    .map(it -> it.getName() + " (" + it.getQuantity() + ")")
                    .collect(Collectors.joining(", "));
            lines.add("Itemcủa nhân vật: " + items);

            lines.add("===EQUIPMENT===");
            EquipSlot[] order = {
                EquipSlot.ARMOR,
                EquipSlot.HELMET,
                EquipSlot.PANTS,
                EquipSlot.SHOES,
                EquipSlot.WEAPON1,
                EquipSlot.WEAPON2,
                EquipSlot.NECKLACE,
                EquipSlot.RING1,
                EquipSlot.RING2,
                EquipSlot.AMULET
            };
            for (EquipSlot slot : order) {
                EquipmentItem eq = equipment.get(slot);
                if (eq != null) {
                    lines.add("- " + slot.name() + ": " + eq.getId() + "|" + eq.getName() + "|" + eq.getDecription());
                } else {
                    lines.add("- " + slot.name() + ": none");
                }
            }

            lines.addAll(realmLog);
            String profileData = String.join("\n", lines);

            String sql = "MERGE " + PROFILE_TABLE + " AS target " +
                    "USING (SELECT ? AS name, ? AS profile) AS src " +
                    "ON target.name = src.name " +
                    "WHEN MATCHED THEN UPDATE SET profile = src.profile " +
                    "WHEN NOT MATCHED THEN INSERT (name, profile) VALUES (src.name, src.profile);";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, getName());
                ps.setString(2, profileData);
                ps.executeUpdate();
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    /** Load player profile from SQL Server. */
    private boolean loadProfile() {
        try {
            PlayerDao pdao = new PlayerDao();
            PlayerDao.PlayerRecord rec = pdao.load(getName());
            if (rec == null) return false;
            playerId = rec.playerId;
            if (rec.realm != null) {
                try { realm = Realm.valueOf(rec.realm); } catch (IllegalArgumentException ignored) {}
            }
            realmStage = rec.realmStage;
            if (rec.physique != null) {
                try { physique = Physique.valueOf(rec.physique); } catch (IllegalArgumentException ignored) {}
            }
            if (rec.affinity != null) {
                affinities = parseAffinities(rec.affinity);
            }
            if (rec.createdAt != null) {
                creationDate = rec.createdAt.toLocalDate();
            }

            PlayerBaseStatsDao bsDao = new PlayerBaseStatsDao();
            PlayerBaseStatsDao.BaseStats bs = bsDao.load(playerId);
            if (bs == null) return false;
            baseAtts.set(Attr.ATTACK, bs.atk);
            baseAtts.set(Attr.DEF, bs.def);
            baseAtts.setMax(Attr.HEALTH, bs.healthMax);
            baseAtts.setMax(Attr.PEP, bs.pepMax);
            baseAtts.set(Attr.SOULD, bs.sould);
            baseAtts.set(Attr.SPIRIT, bs.spirit);
            baseAtts.setMax(Attr.SPIRIT, bs.spiritMax);
            baseAtts.set(Attr.STRENGTH, bs.strength);
            baseSpiritRequirement = bs.spiritMax;
            spiritToNextLevel = bs.spiritMax;

            PlayerRuntimeDao rtDao = new PlayerRuntimeDao();
            PlayerRuntimeDao.RuntimeStats rt = rtDao.load(playerId);
            if (rt != null) {
                baseAtts.set(Attr.HEALTH, rt.currentHP);
                baseAtts.set(Attr.PEP, rt.currentPep);
                setWorldX(rt.posX);
                setWorldY(rt.posY);
                if (rt.mapId != null) {
                    mapId = rt.mapId;
                }
            }

            PlayerSkillDao skDao = new PlayerSkillDao();
            techniques.clear();
            techniques.addAll(skDao.load(playerId));

            // Load technique key bindings
            PlayerSkillBindingDao bindDao = new PlayerSkillBindingDao();
            techniqueBindings.clear();
            for (Map.Entry<Integer, String> e : bindDao.load(playerId).entrySet()) {
                CultivationTechnique t = findTechniqueByName(e.getValue());
                if (t != null) techniqueBindings.put(e.getKey(), t);
            }

            // Load inventory/equipment from serialized profile if present
            try (Connection conn = DBAccount.getConnectDB();
                 PreparedStatement ps = conn.prepareStatement(
                         "SELECT profile FROM " + PROFILE_TABLE + " WHERE name = ?")) {
                ps.setString(1, getName());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String profileData = rs.getString("profile");
                        List<String> lines = Arrays.asList(profileData.split("\\r?\\n"));
                        int idxLine = 0;

                        if (idxLine < lines.size() && lines.get(idxLine).startsWith("CREATION_DATE:")) {
                            String dateStr = lines.get(idxLine).substring("CREATION_DATE:".length()).trim();
                            creationDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));
                            idxLine++;
                        }

                        if (idxLine < lines.size()) {
                            String itemLine = lines.get(idxLine);
                            String prefix = "Itemcủa nhân vật: ";
                            if (itemLine.startsWith(prefix)) {
                                String items = itemLine.substring(prefix.length()).trim();
                                bag.clear();
                                if (!items.isEmpty()) {
                                    String[] tokens = items.split(",\\s*");
                                    for (String token : tokens) {
                                        int idxTok = token.lastIndexOf(" (");
                                        int end = token.lastIndexOf(")");
                                        if (idxTok > 0 && end > idxTok) {
                                            String name = token.substring(0, idxTok).trim();
                                            int qty = Integer.parseInt(token.substring(idxTok + 2, end));
                                            Item it = createItemByName(name, qty);
                                            if (it != null) bag.add(it);
                                        }
                                    }
                                }
                            }
                            idxLine++;
                        }

                        if (idxLine < lines.size() && lines.get(idxLine).trim().equals("===EQUIPMENT===")) {
                            idxLine++;
                            equipment.clear();
                            while (idxLine < lines.size()) {
                                String line = lines.get(idxLine).trim();
                                if (!line.startsWith("-")) break;
                                line = line.substring(1).trim();
                                int colon = line.indexOf(":");
                                if (colon < 0) { idxLine++; continue; }
                                String slotName = line.substring(0, colon).trim();
                                String data = line.substring(colon + 1).trim();
                                EquipSlot slot;
                                try {
                                    slot = EquipSlot.valueOf(slotName);
                                } catch (IllegalArgumentException e) {
                                    idxLine++; continue;
                                }
                                if (!data.equalsIgnoreCase("none")) {
                                    String[] partsEq = data.split("\\|");
                                    String id = partsEq.length > 0 ? partsEq[0].trim() : "";
                                    String nameEq = partsEq.length > 1 ? partsEq[1].trim() : "";
                                    String descEq = partsEq.length > 2 ? partsEq[2].trim() : "";
                                    EquipmentItem eq = createEquipmentFromSlot(id, nameEq, descEq, slot);
                                    if (eq != null) {
                                        equipment.put(slot, eq);
                                        if (eq.getType() == EquipType.RING) bag.increaseCapacity(10);
                                    }
                                }
                                idxLine++;
                            }
                            refreshStats();
                        }

                        if (idxLine < lines.size()) {
                            realmLog.clear();
                            for (; idxLine < lines.size(); idxLine++) {
                                realmLog.add(lines.get(idxLine));
                            }
                        }
                    }
                }
            }
            return true;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    private Physique parsePhysique(String display) {
        for (Physique p : Physique.values()) {
            if (p.getDisplay().equals(display)) return p;
        }
        return Physique.NORMAL;
    }

    /**
     * Tính toán yêu cầu SPIRIT cơ bản (chưa áp dụng hệ số thể chất) dựa trên
     * cảnh giới và tầng hiện tại.
     */
    private int computeBaseSpiritRequirement(Realm realm, int stage) {
        int base = 1000;
        if (realm == Realm.PHAM_NHAN) return base;

        int luyenTheStages = (realm == Realm.LUYEN_THE) ? stage : physique.getMaxStage();
        for (int i = 1; i <= luyenTheStages; i++) {
            base += base / 2;
        }
        if (realm == Realm.LUYEN_KHI) {
            base *= 2; // đột phá đại cảnh giới
            for (int i = 1; i <= stage; i++) {
                base += base / 2;
            }
        }
        return base;
    }

    private EnumSet<Affinity> parseAffinities(String list) {
        EnumSet<Affinity> set = EnumSet.noneOf(Affinity.class);
        if (list == null || list.isBlank() || list.equalsIgnoreCase("None")) return set;
        String[] parts = list.split(",\\s*");
        for (String part : parts) {
            for (Affinity a : Affinity.values()) {
                if (a.getDisplay().equals(part)) {
                    set.add(a);
                }
            }
        }
        return set;
    }

    private Item createItemByName(String name, int qty) {
        return switch (name) {
            case "Đan dược hồi máu" -> new game.entity.item.elixir.HealthPotion(50, qty);
            case "Đan dược tinh thần" -> new game.entity.item.elixir.SpiritPotion(200, qty);
            case "Đan hạ phẩm" -> new game.entity.item.elixir.CultivationPill("Đan hạ phẩm", 1, qty);
            case "Đan trung phẩm" -> new game.entity.item.elixir.CultivationPill("Đan trung phẩm", 2, qty);
            case "Đan thượng phẩm" -> new game.entity.item.elixir.CultivationPill("Đan thượng phẩm", 3, qty);
            case "Đan cực phẩm" -> new game.entity.item.elixir.CultivationPill("Đan cực phẩm", 4, qty);
            case "Sách Công pháp hạ phẩm" -> new game.entity.item.book.CultivationBook(new CultivationTechnique("Công pháp hạ phẩm", SkillGrade.HA, 1, 1));
            case "Sách Công pháp trung phẩm" -> new game.entity.item.book.CultivationBook(new CultivationTechnique("Công pháp trung phẩm", SkillGrade.TRUNG, 1, 2));
            case "Sách Công pháp thượng phẩm" -> new game.entity.item.book.CultivationBook(new CultivationTechnique("Công pháp thượng phẩm", SkillGrade.THUONG, 1, 3));
            
            case "Sách Công pháp cực phẩm" -> new game.entity.item.book.CultivationBook(new CultivationTechnique("Công pháp cực phẩm", SkillGrade.CUC, 1, 5));
            case "Áo giáp" -> new EquipmentItem("Áo giáp", "+3 DEF", "/data/item/equipment/armor.png", EquipType.ARMOR);
            case "Mũ sắt" -> new EquipmentItem("Mũ sắt", "+3 DEF", "/data/item/equipment/helmet.png", EquipType.HELMET);
            case "Quần vải" -> new EquipmentItem("Quần vải", "+3 DEF", "/data/item/equipment/pants.png", EquipType.PANTS);
            case "Giày da" -> new EquipmentItem("Giày da", "+3 DEF", "/data/item/equipment/shoes.png", EquipType.SHOES);
            case "Kiếm gỗ" -> new EquipmentItem("Kiếm gỗ", "+10 ATTACK", "/data/item/equipment/sword.png", EquipType.WEAPON);
            case "Kiếm sắt" -> new EquipmentItem("Kiếm sắt", "+10 ATTACK", "/data/item/equipment/sword.png", EquipType.WEAPON);
            case "Dây chuyền" -> new EquipmentItem("Dây chuyền", "+10 SOULD", "/data/item/equipment/ring.png", EquipType.NECKLACE);
            case "Nhẫn đá" -> new EquipmentItem("Nhẫn đá", "+10 ô kho", "/data/item/equipment/ring.png", EquipType.RING);
            case "Nhẫn bạc" -> new EquipmentItem("Nhẫn bạc", "+10 ô kho", "/data/item/equipment/ring.png", EquipType.RING);
            case "Bùa hộ mệnh" -> new EquipmentItem("Bùa hộ mệnh", "Chưa có tác dụng", "/data/item/equipment/d_1.png", EquipType.AMULET);
            
            default -> null;
        };
    }

    private EquipmentItem createEquipmentFromSlot(String id, String name, String desc, EquipSlot slot) {
        EquipType type = switch (slot) {
            case ARMOR -> EquipType.ARMOR;
            case HELMET -> EquipType.HELMET;
            case PANTS -> EquipType.PANTS;
            case SHOES -> EquipType.SHOES;
            case NECKLACE -> EquipType.NECKLACE;
            case AMULET -> EquipType.AMULET;
            case RING1, RING2 -> EquipType.RING;
            case WEAPON1, WEAPON2 -> EquipType.WEAPON;
        };
        String icon = switch (slot) {
            case ARMOR -> "/data/item/equipment/armor.png";
            case HELMET -> "/data/item/equipment/helmet.png";
            case PANTS -> "/data/item/equipment/pants.png";
            case SHOES -> "/data/item/equipment/shoes.png";
            case NECKLACE -> "/data/item/equipment/ring.png";
            case AMULET -> "/data/item/equipment/d_1.png";
            case RING1, RING2 -> "/data/item/equipment/ring.png";
            case WEAPON1, WEAPON2 -> "/data/item/equipment/sword.png";
        };
        if (desc == null || desc.isEmpty()) {
            desc = switch (type) {
                case ARMOR, HELMET, PANTS, SHOES -> "+3 DEF";
                case WEAPON -> "+10 ATTACK";
                case NECKLACE -> "+10 SOULD";
                case RING -> "+10 ô kho";
                case AMULET -> "Chưa có tác dụng";
            };
        }
        return new EquipmentItem(id, name, desc, icon, type);
    }

    // -------- Random Physique/Affinity ---------

    private Physique randomPhysique() {
        double roll = random.nextDouble() * 100;
        if ((roll -= 1) < 0) return Physique.HU_KHONG;
        if ((roll -= 1) < 0) return Physique.HU_KHONG_DAI_DE;
        if ((roll -= 1) < 0) return Physique.THANH_THE;
        if ((roll -= 1) < 0) return Physique.TIEN_LINH_THE;
        if ((roll -= 1) < 0) return Physique.THAN_THE;
        if ((roll -= 1) < 0) return Physique.NGU_HANH;
        return Physique.NORMAL;
    }

    private EnumSet<Affinity> randomAffinities() {
        int count = 1;
        double r = random.nextDouble() * 100;
        if (r < 10) count = 3;
        else if (r < 25) count = 2;

        EnumSet<Affinity> set = EnumSet.noneOf(Affinity.class);
        while (set.size() < count) {
            set.add(randomAffinity());
        }
        return set;
    }

    private Affinity randomAffinity() {
        double roll = random.nextDouble() * 100;
        if ((roll -= 20) < 0) return Affinity.HOA;
        if ((roll -= 20) < 0) return Affinity.MOC;
        if ((roll -= 20) < 0) return Affinity.THUY;
        if ((roll -= 20) < 0) return Affinity.KIM;
        if ((roll -= 20) < 0) return Affinity.THO;
        return Affinity.LOI;
    }

    // -------- Getter helpers ---------

    public int getRealmStage() { return realmStage; }
    public int getSpiritToNextLevel() { return spiritToNextLevel; }
    public Physique getPhysique() { return physique; }
    public EnumSet<Affinity> getAffinities() { return affinities; }

    /**
     * @return tên cảnh giới + tầng hiện tại để hiển thị.
     */
    public String getRealmName() {
        return switch (realm) {
            case PHAM_NHAN -> realm.getDisplayName();
            case LUYEN_THE, LUYEN_KHI -> realm.getDisplayName() + " tầng " + realmStage;
        };
    }

    /**
     * @return danh sách tên linh căn, cách nhau bằng dấu phẩy.
     */
    public String getAffinityNames() {
        return affinities.stream()
                .map(Affinity::getDisplay)
                .reduce((a, b) -> a + ", " + b)
                .orElse("None");
    }

    private void refreshStats() {
        atts().setStarts(new EnumMap<>(baseAtts.getStarts()));
        for (Attr a : Attr.values()) {
            int max = baseAtts.getMax(a);
            if (max > 0) {
                atts().setMax(a, max);
            } else {
                atts().setMax(a, Integer.MAX_VALUE);
            }
        }
        for (EquipmentItem eq : equipment.values()) {
            switch (eq.getType()) {
                case HELMET, ARMOR, SHOES, PANTS -> atts().add(Attr.DEF, 3);
                case WEAPON -> atts().add(Attr.ATTACK, 10);
                case NECKLACE -> atts().add(Attr.SOULD, 10);
                default -> {}
            }
        }
    }

    // -------- Equipment handling ---------

    public EquipmentItem getEquipment(EquipSlot slot) {
        return equipment.get(slot);
    }

    public EquipmentItem equip(EquipmentItem item) {
        EquipSlot slot = switch (item.getType()) {
            case HELMET -> EquipSlot.HELMET;
            case ARMOR -> EquipSlot.ARMOR;
            case SHOES -> EquipSlot.SHOES;
            case PANTS -> EquipSlot.PANTS;
            case NECKLACE -> EquipSlot.NECKLACE;
            case AMULET -> EquipSlot.AMULET;
            case RING -> equipment.get(EquipSlot.RING1) == null ? EquipSlot.RING1 : EquipSlot.RING2;
            case WEAPON -> equipment.get(EquipSlot.WEAPON1) == null ? EquipSlot.WEAPON1 : EquipSlot.WEAPON2;
        };
        EquipmentItem prev = equipment.put(slot, item);
        if (item.getType() == EquipType.RING) bag.increaseCapacity(10);
        if (prev != null && prev.getType() == EquipType.RING) bag.decreaseCapacity(10);
        refreshStats();
        return prev;
    }

    public EquipmentItem unequip(EquipSlot slot) {
        EquipmentItem prev = equipment.remove(slot);
        if (prev != null && prev.getType() == EquipType.RING) bag.decreaseCapacity(10);
        refreshStats();
        return prev;
    }

    public int getScreenX() { return screenX; }
    public int getScreenY() { return screenY; }

    public static int getInteractionRange() { return INTERACTION_RANGE; }
    public Inventory getBag() { return bag; }
}