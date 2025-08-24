package game.entity.monster;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import game.enums.Attr;
import game.main.GamePanel;
import game.util.CameraHelper;

/**
 * Quái vật Orc, đuổi theo và tấn công cận chiến người chơi.
 */
public class Orc extends Monster {
    /** Ảnh tấn công lên trên khung 1 */
    private BufferedImage attackUp1;
    /** Ảnh tấn công lên trên khung 2 */
    private BufferedImage attackUp2;
    /** Ảnh tấn công xuống dưới khung 1 */
    private BufferedImage attackDown1;
    /** Ảnh tấn công xuống dưới khung 2 */
    private BufferedImage attackDown2;
    /** Ảnh tấn công sang trái khung 1 */
    private BufferedImage attackLeft1;
    /** Ảnh tấn công sang trái khung 2 */
    private BufferedImage attackLeft2;
    /** Ảnh tấn công sang phải khung 1 */
    private BufferedImage attackRight1;
    /** Ảnh tấn công sang phải khung 2 */
    private BufferedImage attackRight2;

    /**
     * Khởi tạo Orc.
     *
     * @param gp tham chiếu game panel
     */
    public Orc(GamePanel gp) {
        super(gp);
        setSpeed(2);
        setDirection("down");
        setScaleEntityX(gp.getTileSize());
        setScaleEntityY(gp.getTileSize());
        setCollisionArea(new Rectangle(8, 16, 32, 32));
        atts().set(Attr.HEALTH, 20);
        maxHealth = 20;
        attackDamage = 4;
        attackArea = new Rectangle(0, 0, gp.getTileSize(), gp.getTileSize());
        detectionRange = 8 * gp.getTileSize();
        loadImages();
    }

    /** Tải hình ảnh di chuyển và tấn công */
    private void loadImages() {
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
    }

    /**
     * Vẽ Orc và thanh máu.
     */
    @Override
    public void draw(Graphics2D g2, GamePanel gp) {
        Point screenPos = CameraHelper.worldToScreen(getWorldX(), getWorldY(), gp);
        g2.drawImage(getCurrentImage(), screenPos.x, screenPos.y, null);
        drawHealthBar(g2, screenPos);
    }

    /**
     * Lấy hình ảnh hiện tại tùy theo trạng thái.
     *
     * @return ảnh phù hợp
     */
    private BufferedImage getCurrentImage() {
        if (attacking) {
            return switch (getDirection()) {
                case "up" -> (attackCounter < getAttackDuration() / 2) ? attackUp1 : attackUp2;
                case "down" -> (attackCounter < getAttackDuration() / 2) ? attackDown1 : attackDown2;
                case "left" -> (attackCounter < getAttackDuration() / 2) ? attackLeft1 : attackLeft2;
                default -> (attackCounter < getAttackDuration() / 2) ? attackRight1 : attackRight2;
            };
        } else {
            return switch (getDirection()) {
                case "up" -> (getSpriteNum() == 1) ? getUp1() : getUp2();
                case "down" -> (getSpriteNum() == 1) ? getDown1() : getDown2();
                case "left" -> (getSpriteNum() == 1) ? getLeft1() : getLeft2();
                default -> (getSpriteNum() == 1) ? getRight1() : getRight2();
            };
        }
    }
}