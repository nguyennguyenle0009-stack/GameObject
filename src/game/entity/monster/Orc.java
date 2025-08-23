package game.entity.monster;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;


import game.entity.GameActor;
import game.enums.Attr;
import game.interfaces.Damageable;
import game.main.GamePanel;
import game.util.CameraHelper;
import game.util.PathFinder;
import game.entity.item.elixir.HealthPotion;

/**
 * Monster sử dụng ảnh orc cùng với AI tìm đường.
 */
public class Orc extends GameActor implements Damageable {
    /** kích thước vùng tấn công */
    private final Rectangle attackArea = new Rectangle(0, 0, 48, 48);
    /** game panel tham chiếu */
    private final GamePanel gp;
    /** bộ tìm đường để né vật cản */
    private final PathFinder pathFinder;
    /** trạng thái đang tấn công */
    private boolean attacking = false;
    /** bộ đếm thời gian tấn công */
    private int attackCounter = 0;
    /** thời gian hồi chiêu tấn công hiện tại */
    private int attackCooldown = 0;
    /** thời gian hồi chiêu tối đa */
    private static final int ATTACK_COOLDOWN = 60;
    /** thời gian thực hiện một đòn tấn công */
    private static final int ATTACK_DURATION = 20;
    /** sát thương mỗi đòn */
    private final int attackDamage = 3;
    /** phạm vi đuổi theo nhân vật */
    private static final int CHASE_RANGE = 6 * 48;
    /** hiển thị thanh máu hay không */
    private boolean hpBarVisible = false;
    /** bộ đếm thời gian hiển thị thanh máu */
    private int hpBarCounter = 0;
    /** thời gian hiển thị thanh máu (2s) */
    private static final int HP_BAR_VISIBLE_TIME = 120;
    /** giá trị máu tối đa */
    private static final int MAX_HEALTH = 20;
    /** hình ảnh tấn công hướng lên */
    private BufferedImage attackUp1, attackUp2;
    /** hình ảnh tấn công hướng xuống */
    private BufferedImage attackDown1, attackDown2;
    /** hình ảnh tấn công hướng trái */
    private BufferedImage attackLeft1, attackLeft2;
    /** hình ảnh tấn công hướng phải */
    private BufferedImage attackRight1, attackRight2;
    /** bộ sinh số ngẫu nhiên cho rơi vật phẩm */
    private final Random random = new Random();

    /**
     * Khởi tạo orc.
     *
     * @param gp tham chiếu game panel
     */
    public Orc(GamePanel gp) {
        super(gp);
        this.gp = gp;
        this.pathFinder = new PathFinder(gp);
        setSpeed(2);
        setDirection("down");
        setScaleEntityX(gp.getTileSize());
        setScaleEntityY(gp.getTileSize());
        setCollisionArea(new Rectangle(8, 16, 32, 32));
        atts().set(Attr.HEALTH, MAX_HEALTH);
        loadImages();
    }

    /** tải toàn bộ ảnh di chuyển và tấn công */
    private void loadImages() {
        try {
            setUp1(setup("/data/monster/orc/orc_up_1"));
            setUp2(setup("/data/monster/orc/orc_up_2"));
            setDown1(setup("/data/monster/orc/orc_down_1"));
            setDown2(setup("/data/monster/orc/orc_down_2"));
            setLeft1(setup("/data/monster/orc/orc_left_1"));
            setLeft2(setup("/data/monster/orc/orc_left_2"));
            setRight1(setup("/data/monster/orc/orc_right_1"));
            setRight2(setup("/data/monster/orc/orc_right_2"));
            attackUp1 = setup("/data/monster/orc/orc_attack_up_1");
            attackUp2 = setup("/data/monster/orc/orc_attack_up_2");
            attackDown1 = setup("/data/monster/orc/orc_attack_down_1");
            attackDown2 = setup("/data/monster/orc/orc_attack_down_2");
            attackLeft1 = setup("/data/monster/orc/orc_attack_left_1");
            attackLeft2 = setup("/data/monster/orc/orc_attack_left_2");
            attackRight1 = setup("/data/monster/orc/orc_attack_right_1");
            attackRight2 = setup("/data/monster/orc/orc_attack_right_2");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        handleAttack();
        if (!attacking) {
            chasePlayer();
        }
        if (hpBarVisible) {
            hpBarCounter++;
            if (hpBarCounter > HP_BAR_VISIBLE_TIME) {
                hpBarVisible = false;
                hpBarCounter = 0;
            }
        }
    }

    /** di chuyển đến người chơi bằng tìm đường */
    private void chasePlayer() {
        int dx = Math.abs(gp.getPlayer().getWorldX() - getWorldX());
        int dy = Math.abs(gp.getPlayer().getWorldY() - getWorldY());
        if (dx + dy < CHASE_RANGE) {
            Point start = new Point(getWorldX() / gp.getTileSize(), getWorldY() / gp.getTileSize());
            Point goal = new Point(gp.getPlayer().getWorldX() / gp.getTileSize(), gp.getPlayer().getWorldY() / gp.getTileSize());
            List<Point> path = pathFinder.findPath(start, goal);
            if (!path.isEmpty()) {
                Point next = path.get(0);
                if (next.x > start.x) setDirection("right");
                else if (next.x < start.x) setDirection("left");
                else if (next.y > start.y) setDirection("down");
                else if (next.y < start.y) setDirection("up");
                checkCollision();
                moveIfCollisionNotDetected();
                checkAndChangeSpriteAnimation();
            }
        } else {
            // nếu người chơi ở xa, đứng yên
            resestSpriteToDefault();
        }
    }

    /** xử lý logic tấn công */
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
            Rectangle attackRect = getAttackRectangle();
            Rectangle playerRect = new Rectangle(
                gp.getPlayer().getWorldX() + gp.getPlayer().getCollisionArea().x,
                gp.getPlayer().getWorldY() + gp.getPlayer().getCollisionArea().y,
                gp.getPlayer().getCollisionArea().width,
                gp.getPlayer().getCollisionArea().height
            );
            if (attackCooldown == 0 && attackRect.intersects(playerRect)) {
                attacking = true;
            }
        }
    }

    /** lấy vùng tấn công theo hướng hiện tại */
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

    /** thực hiện gây sát thương cho người chơi */
    private void physicalAttack() {
        Rectangle attackRect = getAttackRectangle();
        Rectangle playerRect = new Rectangle(
            gp.getPlayer().getWorldX() + gp.getPlayer().getCollisionArea().x,
            gp.getPlayer().getWorldY() + gp.getPlayer().getCollisionArea().y,
            gp.getPlayer().getCollisionArea().width,
            gp.getPlayer().getCollisionArea().height
        );
        if (attackRect.intersects(playerRect)) {
            gp.getPlayer().atts().add(Attr.HEALTH, -attackDamage);
            gp.getUi().triggerDamageEffect();
        }
    }

    @Override
    public void draw(Graphics2D g2, GamePanel gp) {
        Point screenPos = CameraHelper.worldToScreen(getWorldX(), getWorldY(), gp);
        g2.drawImage(getCurrentImage(), screenPos.x, screenPos.y, null);
        if (hpBarVisible) {
            drawHealthBar(g2, screenPos);
        }
    }

    /** chọn hình ảnh phù hợp cho trạng thái hiện tại */
    private BufferedImage getCurrentImage() {
        if (attacking) {
            return switch (getDirection()) {
                case "up" -> attackCounter < ATTACK_DURATION / 2 ? attackUp1 : attackUp2;
                case "down" -> attackCounter < ATTACK_DURATION / 2 ? attackDown1 : attackDown2;
                case "left" -> attackCounter < ATTACK_DURATION / 2 ? attackLeft1 : attackLeft2;
                case "right" -> attackCounter < ATTACK_DURATION / 2 ? attackRight1 : attackRight2;
                default -> getDown1();
            };
        }
        return switch (getDirection()) {
            case "up" -> getSpriteNum() == 1 ? getUp1() : getUp2();
            case "down" -> getSpriteNum() == 1 ? getDown1() : getDown2();
            case "left" -> getSpriteNum() == 1 ? getLeft1() : getLeft2();
            case "right" -> getSpriteNum() == 1 ? getRight1() : getRight2();
            default -> getDown1();
        };
    }

    /** vẽ thanh máu lên trên đầu */
    private void drawHealthBar(Graphics2D g2, Point screenPos) {
        double hpPercent = (double) atts().get(Attr.HEALTH) / MAX_HEALTH;
        int barWidth = gp.getTileSize();
        int barHeight = 5;
        int filled = (int) (barWidth * hpPercent);
        g2.setColor(Color.red);
        g2.fillRect(screenPos.x, screenPos.y - 10, barWidth, barHeight);
        g2.setColor(Color.green);
        g2.fillRect(screenPos.x, screenPos.y - 10, filled, barHeight);
    }

    @Override
    public void takeDamage(int amount) {
        atts().add(Attr.HEALTH, -amount);
        hpBarVisible = true;
        hpBarCounter = 0;
        if (atts().get(Attr.HEALTH) <= 0) {
            if (random.nextInt(100) < 30) {
                gp.getPlayer().getBag().add(new HealthPotion(20, 1));
            }
            gp.getMonsters().remove(this);
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        // not used
    }
}
