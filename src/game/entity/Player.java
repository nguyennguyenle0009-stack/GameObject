package game.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import game.entity.inventory.Inventory;
import game.entity.item.Item;
import game.interfaces.DrawableEntity;
import game.entity.monster.Monster;
import game.enums.Affinity;
import game.enums.Attr;
import game.enums.Physique;
import game.enums.Realm;
import game.enums.SkillGrade;
import game.skill.CultivationTechnique;

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
    private int realmStage = 0;
    private int spiritToNextLevel = 1000;

    // Thể chất và linh căn
    private Physique physique;
    private EnumSet<Affinity> affinities = EnumSet.noneOf(Affinity.class);

    private final Random random = new Random();

    private LocalDate creationDate = LocalDate.now();
    private final List<String> realmLog = new ArrayList<>();

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

        if (!loadProfile()) {
            // Thuộc tính cơ bản
            atts().setMax(Attr.HEALTH, 100);
            atts().set(Attr.HEALTH, 100);
            atts().setMax(Attr.PEP, 100);
            atts().set(Attr.PEP, 100);
            atts().setMax(Attr.SPIRIT, spiritToNextLevel);
            atts().set(Attr.SPIRIT, 0);
            // Attack/Def không còn giới hạn max mặc định để có thể tăng khi lên cấp
            atts().set(Attr.ATTACK, 5);
            atts().set(Attr.DEF, 4);
            atts().set(Attr.STRENGTH, 1);
            atts().set(Attr.SOULD, 5);

            // Thiết lập thể chất và linh căn ngẫu nhiên
            physique = randomPhysique();
            affinities = randomAffinities();

            // Ngũ Hành Linh Căn có chỉ số gấp 5 lần nên yêu cầu SPIRIT cũng gấp 5
            spiritToNextLevel = (int) (spiritToNextLevel * physique.getSpiritReqFactor());
            atts().setMax(Attr.SPIRIT, spiritToNextLevel);

            // Thêm vài item test: bình hồi máu & tinh thần
            addItem(new game.entity.item.elixir.HealthPotion(50, 3));
            addItem(new game.entity.item.elixir.SpiritPotion(200, 3));

            // Các sách công pháp và đan dược tu luyện để thử nghiệm
            var low = new CultivationTechnique("Công pháp hạ phẩm", SkillGrade.HA, 1, 5, 1);
            var mid = new CultivationTechnique("Công pháp trung phẩm", SkillGrade.TRUNG, 1, 10, 2);
            var high = new CultivationTechnique("Công pháp thượng phẩm", SkillGrade.THUONG, 1, 15, 3);
            var top = new CultivationTechnique("Công pháp cực phẩm", SkillGrade.CUC, 1, 25, 5);
            addItem(new game.entity.item.book.CultivationBook(low));
            addItem(new game.entity.item.book.CultivationBook(mid));
            addItem(new game.entity.item.book.CultivationBook(high));
            addItem(new game.entity.item.book.CultivationBook(top));
            addItem(new game.entity.item.elixir.CultivationPill("Đan hạ phẩm", 1, 1));
            addItem(new game.entity.item.elixir.CultivationPill("Đan trung phẩm", 2, 1));
            addItem(new game.entity.item.elixir.CultivationPill("Đan thượng phẩm", 3, 1));
            addItem(new game.entity.item.elixir.CultivationPill("Đan cực phẩm", 4, 1));

            logRealmState();
            saveProfile();
        }

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
    
    // Thêm item vào túi và lưu
    public void addItem(Item item) {
        bag.add(item);
        saveProfile();
    }

    // Sử dụng item
    public void useItem(Item i) {
        i.use(this);
        if(i.getQuantity() == 0) bag.remove(i);
        saveProfile();
    }

    /**
     * Tăng Spirit và tự động kiểm tra lên cấp.
     */
    public void gainSpirit(int amount) {
        atts().add(Attr.SPIRIT, amount);
        while (atts().get(Attr.SPIRIT) >= spiritToNextLevel) {
            atts().add(Attr.SPIRIT, -spiritToNextLevel);
            levelUp();
        }
    }

    // ------------ Hệ thống kỹ năng & tu luyện ------------

    /** Người chơi học một công pháp mới. */
    public void learnSkill(CultivationTechnique tech) {
        techniques.add(tech);
        // Ghi lại vào nhật ký để lưu ra file
        logRealmState();
        saveProfile();
    }

    public List<CultivationTechnique> getTechniques() { return List.copyOf(techniques); }

    public String getSkillSummary() {
        if (techniques.isEmpty()) return "None";
        return techniques.stream()
                .map(t -> t.getName() + "(" + t.getLevel() + ")(" + t.getGrade().getDisplay() + ")")
                .collect(Collectors.joining(", "));
    }

    public void startCultivating(CultivationTechnique tech) {
        long now = System.currentTimeMillis();
        if (now < cultivationCooldownEnd) return;
        cultivating = true;
        activeTechnique = tech;
        lastSpiritTick = now;
        cultivationEndTime = now + tech.getDurationMillis();
    }

    public void cancelCultivation() {
        cultivating = false;
        cultivationCooldownEnd = System.currentTimeMillis() + 3600_000L; // 1h
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
            cultivationCooldownEnd = now + 3600_000L;
            return;
        }
        if (now - lastSpiritTick >= 1000) {
            lastSpiritTick += 1000;
            gainSpirit(activeTechnique.getSpiritPerSecond() + pillSpiritBonus);
        }
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
     * @return tên công pháp đang tu luyện, hoặc {@code null} nếu không tu luyện.
     */
    public CultivationTechnique getActiveTechnique() { return activeTechnique; }

    /**
     * @return thời gian còn lại của lần tu luyện hiện tại (ms).
     */
    public long getCultivationTimeLeft() {
        return Math.max(0, cultivationEndTime - System.currentTimeMillis());
    }

    /**
     * @return thời gian hồi chiêu còn lại trước khi có thể tu luyện tiếp (ms).
     */
    public long getCultivationCooldownLeft() {
        return Math.max(0, cultivationCooldownEnd - System.currentTimeMillis());
    }

    /**
     * Thực hiện lên cấp theo cảnh giới hiện tại.
     */
    private void levelUp() {
        int oldReq = spiritToNextLevel;
        switch (realm) {
            case PHAM_NHAN -> {
                realm = Realm.LUYEN_THE;
                realmStage = 1;
                initializeLuyenThe();
                spiritToNextLevel = (int) ((oldReq + oldReq / 2) * physique.getSpiritReqFactor());
                atts().add(Attr.SOULD, 10);
            }
            case LUYEN_THE -> {
                realmStage++;
                if (realmStage > physique.getMaxStage()) {
                    breakThroughToLuyenKhi(oldReq);
                    atts().add(Attr.SOULD, 10);
                } else {
                    applyStageGrowth();
                    spiritToNextLevel = (int) ((oldReq + oldReq / 2) * physique.getSpiritReqFactor());
                }
            }
            case LUYEN_KHI -> {
                realmStage++;
                if (realmStage > physique.getMaxStage()) {
                    realmStage = physique.getMaxStage();
                } else {
                    applyStageGrowth();
                    spiritToNextLevel = (int) ((oldReq + oldReq / 2) * physique.getSpiritReqFactor());
                }
            }
        }
        // Cập nhật max Spirit cho HUD
        atts().setMax(Attr.SPIRIT, spiritToNextLevel);
        logRealmState();
        saveProfile();
    }

    /**
     * Thiết lập chỉ số cơ bản khi bước vào Luyện thể tầng 1.
     */
    private void initializeLuyenThe() {
        int hp = (int) (150 * physique.getStatFactor());
        atts().setMax(Attr.HEALTH, hp);
        atts().set(Attr.HEALTH, hp);

        int pep = (int) (150 * physique.getStatFactor());
        atts().setMax(Attr.PEP, pep);
        atts().set(Attr.PEP, pep);

        atts().set(Attr.ATTACK, (int) (10 * physique.getStatFactor()));
        atts().set(Attr.DEF, (int) (5 * physique.getDefFactor()));
        atts().set(Attr.STRENGTH, (int) (2 * physique.getStatFactor()));
    }

    /**
     * Tăng chỉ số khi thăng một tiểu cảnh giới.
     * Áp dụng cho cả Luyện thể và Luyện khí.
     */
    private void applyStageGrowth() {
        int stage = realmStage;

        // HEALTH & PEP: cộng 50 cho mỗi tiểu cảnh giới
        int hpInc = (int) (stage * 50 * physique.getStatFactor());
        int newHp = atts().getMax(Attr.HEALTH) + hpInc;
        atts().setMax(Attr.HEALTH, newHp);
        atts().set(Attr.HEALTH, newHp);

        int pepInc = (int) (stage * 50 * physique.getStatFactor());
        int newPep = atts().getMax(Attr.PEP) + pepInc;
        atts().setMax(Attr.PEP, newPep);
        atts().set(Attr.PEP, newPep);

        // ATTACK: +1, riêng bội số của 3 cộng thêm chính số đó
        int atkInc = (stage % 3 == 0) ? stage : 1;
        atts().add(Attr.ATTACK, (int) (atkInc * physique.getStatFactor()));

        // DEF: +1, bội số của 3 cộng thêm stage/2 (làm tròn lên)
        int defInc = 1;
        if (stage % 3 == 0) {
            defInc = (stage + 1) / 2;
        }
        atts().add(Attr.DEF, (int) (defInc * physique.getDefFactor()));

        // STRENGTH: +1 ở bội số của 3
        if (stage % 3 == 0) {
            atts().add(Attr.STRENGTH, (int) (1 * physique.getStatFactor()));
        }
    }

    /**
     * Đột phá từ Luyện thể sang Luyện khí.
     */
    private void breakThroughToLuyenKhi(int oldReq) {
        realm = Realm.LUYEN_KHI;
        realmStage = 1;

        int hp = (int) (atts().getMax(Attr.HEALTH) * 2 * physique.getStatFactor());
        atts().setMax(Attr.HEALTH, hp);
        atts().set(Attr.HEALTH, hp);

        int pep = (int) (atts().getMax(Attr.PEP) * 2 * physique.getStatFactor());
        atts().setMax(Attr.PEP, pep);
        atts().set(Attr.PEP, pep);

        int atk = (int) (atts().get(Attr.ATTACK) * 2 * physique.getStatFactor());
        atts().set(Attr.ATTACK, atk);

        int def = (int) (atts().get(Attr.DEF) * 3 * physique.getDefFactor());
        atts().set(Attr.DEF, def);

        int str = (int) (atts().get(Attr.STRENGTH) * 2 * physique.getStatFactor());
        atts().set(Attr.STRENGTH, str);

        int soul = (int) (atts().get(Attr.SOULD) * 2 * physique.getStatFactor());
        atts().set(Attr.SOULD, soul);

        spiritToNextLevel = (int) (oldReq * 2 * physique.getSpiritReqFactor());
    }

    private void logRealmState() {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        StringBuilder sb = new StringBuilder();
        sb.append("=============================\n");
        sb.append("cảnh giới " + getRealmName().toLowerCase() + " - " + time + "\n");
        sb.append("HEALTH: " + atts().getMax(Attr.HEALTH) + "\n");
        sb.append("ATTACK: " + atts().get(Attr.ATTACK) + "\n");
        sb.append("PEP: " + atts().getMax(Attr.PEP) + "\n");
        sb.append("DEF: " + atts().get(Attr.DEF) + "\n");
        sb.append("SOULD: " + atts().get(Attr.SOULD) + "\n");
        sb.append("SPIRIT " + spiritToNextLevel + "\n");
        sb.append("STRENGTH: " + atts().get(Attr.STRENGTH) + "\n");
        sb.append("PHYSIQUE: " + physique.getDisplay() + "\n");
        sb.append("AFFINITY: " + getAffinityNames() + "\n");
        sb.append("SKILL: " + getSkillSummary() + "\n");
        realmLog.add(sb.toString());
    }

    // Lưu trạng thái người chơi ra file để dùng lại ở lần chơi sau
    public void saveProfile() {
        try {
            Path file = getProfilePath();
            List<String> lines = new ArrayList<>();
            String fileName = file.getFileName().toString();
            lines.add("============" + fileName + "==============");
            String items = bag.all().stream()
                    .map(it -> it.getName() + " (" + it.getQuantity() + ")")
                    .collect(Collectors.joining(", "));
            lines.add("Itemcủa nhân vật: " + items);
            lines.addAll(realmLog);
            Files.write(file, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Path getProfilePath() {
        String safeName = getName().replaceAll("\\s+", "_");
        String date = creationDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return Paths.get("player." + safeName + "." + date + ".txt");
    }

    private boolean loadProfile() {
        try {
            Path file = findExistingProfile();
            if (file == null) return false;

            List<String> lines = Files.readAllLines(file);
            if (lines.size() < 2) return false;

            // cập nhật ngày tạo từ tên file
            String fileName = file.getFileName().toString();
            String[] parts = fileName.split("\\.");
            if (parts.length >= 3) {
                creationDate = LocalDate.parse(parts[2], DateTimeFormatter.ofPattern("yyyyMMdd"));
            }

            // Items
            String itemLine = lines.get(1);
            String prefix = "Itemcủa nhân vật: ";
            if (itemLine.startsWith(prefix)) {
                String items = itemLine.substring(prefix.length()).trim();
                bag.clear();
                if (!items.isEmpty()) {
                    String[] tokens = items.split(",\\s*");
                    for (String token : tokens) {
                        int idx = token.lastIndexOf(" (");
                        int end = token.lastIndexOf(")");
                        if (idx > 0 && end > idx) {
                            String name = token.substring(0, idx).trim();
                            int qty = Integer.parseInt(token.substring(idx + 2, end));
                            Item it = createItemByName(name, qty);
                            if (it != null) bag.add(it);
                        }
                    }
                }
            }

            // Realm log
            realmLog.clear();
            if (lines.size() > 2) {
                realmLog.addAll(lines.subList(2, lines.size()));
            }

            // Parse last block for stats
            int last = -1;
            for (int i = 2; i < lines.size(); i++) {
                if (lines.get(i).startsWith("=============================")) {
                    last = i;
                }
            }
            if (last == -1 || last + 1 >= lines.size()) return true;

            List<String> block = lines.subList(last, lines.size());
            if (block.size() < 2) return true;

            String realmLine = block.get(1);
            String realmPart = realmLine.substring("cảnh giới ".length(), realmLine.indexOf(" - ")).trim();
            String lower = realmPart.toLowerCase();
            if (lower.startsWith("phàm nhân")) {
                realm = Realm.PHAM_NHAN;
                realmStage = 0;
            } else if (lower.startsWith("luyện thể")) {
                realm = Realm.LUYEN_THE;
                int idx = lower.lastIndexOf("tầng");
                if (idx >= 0) realmStage = Integer.parseInt(lower.substring(idx + 4).trim());
            } else if (lower.startsWith("luyện khí")) {
                realm = Realm.LUYEN_KHI;
                int idx = lower.lastIndexOf("tầng");
                if (idx >= 0) realmStage = Integer.parseInt(lower.substring(idx + 4).trim());
            }

            for (int i = 2; i < block.size(); i++) {
                String line = block.get(i);
                if (line.startsWith("HEALTH: ")) {
                    int v = Integer.parseInt(line.substring(8).trim());
                    atts().setMax(Attr.HEALTH, v);
                    atts().set(Attr.HEALTH, v);
                } else if (line.startsWith("ATTACK: ")) {
                    int v = Integer.parseInt(line.substring(8).trim());
                    atts().set(Attr.ATTACK, v);
                } else if (line.startsWith("PEP: ")) {
                    int v = Integer.parseInt(line.substring(5).trim());
                    atts().setMax(Attr.PEP, v);
                    atts().set(Attr.PEP, v);
                } else if (line.startsWith("DEF: ")) {
                    int v = Integer.parseInt(line.substring(5).trim());
                    atts().set(Attr.DEF, v);
                } else if (line.startsWith("SOULD: ")) {
                    int v = Integer.parseInt(line.substring(7).trim());
                    atts().set(Attr.SOULD, v);
                } else if (line.startsWith("SPIRIT ")) {
                    spiritToNextLevel = Integer.parseInt(line.substring(7).trim());
                    atts().setMax(Attr.SPIRIT, spiritToNextLevel);
                    atts().set(Attr.SPIRIT, 0);
                } else if (line.startsWith("STRENGTH: ")) {
                    int v = Integer.parseInt(line.substring(10).trim());
                    atts().set(Attr.STRENGTH, v);
                } else if (line.startsWith("PHYSIQUE: ")) {
                    physique = parsePhysique(line.substring(10).trim());
                } else if (line.startsWith("AFFINITY: ")) {
                    affinities = parseAffinities(line.substring(10).trim());
                } else if (line.startsWith("SKILL: ")) {
                    techniques.clear();
                    String list = line.substring(7).trim();
                    if (!list.isBlank() && !list.equalsIgnoreCase("None")) {
                        String[] toks = list.split(",\\s*");
                        for (String tk : toks) {
                            int i1 = tk.indexOf('(');
                            int i2 = tk.indexOf(')', i1);
                            int i3 = tk.indexOf('(', i2);
                            int i4 = tk.indexOf(')', i3);
                            String name = (i1 > 0) ? tk.substring(0, i1).trim() : tk.trim();
                            int lvl = (i1 > 0 && i2 > i1) ? Integer.parseInt(tk.substring(i1 + 1, i2)) : 1;
                            String gradeStr = (i3 > i2 && i4 > i3) ? tk.substring(i3 + 1, i4) : SkillGrade.HA.getDisplay();
                            techniques.add(new CultivationTechnique(name, SkillGrade.fromDisplay(gradeStr), lvl, 0, 0));
                        }
                    }
                }
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Path findExistingProfile() throws IOException {
        String safeName = getName().replaceAll("\\s+", "_");
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(Paths.get("."), "player." + safeName + ".*.txt")) {
            for (Path p : ds) {
                return p;
            }
        }
        return null;
    }

    private Physique parsePhysique(String display) {
        for (Physique p : Physique.values()) {
            if (p.getDisplay().equals(display)) return p;
        }
        return Physique.NORMAL;
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
            case "Sách Công pháp hạ phẩm" -> new game.entity.item.book.CultivationBook(new CultivationTechnique("Công pháp hạ phẩm", SkillGrade.HA, 1, 5, 1));
            case "Sách Công pháp trung phẩm" -> new game.entity.item.book.CultivationBook(new CultivationTechnique("Công pháp trung phẩm", SkillGrade.TRUNG, 1, 10, 2));
            case "Sách Công pháp thượng phẩm" -> new game.entity.item.book.CultivationBook(new CultivationTechnique("Công pháp thượng phẩm", SkillGrade.THUONG, 1, 15, 3));
            case "Sách Công pháp cực phẩm" -> new game.entity.item.book.CultivationBook(new CultivationTechnique("Công pháp cực phẩm", SkillGrade.CUC, 1, 25, 5));
            default -> null;
        };
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

    public int getScreenX() { return screenX; }
    public int getScreenY() { return screenY; }

    public static int getInteractionRange() { return INTERACTION_RANGE; }
    public Inventory getBag() { return bag; }
}