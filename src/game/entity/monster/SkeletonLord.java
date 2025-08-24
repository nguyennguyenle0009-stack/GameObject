package game.entity.monster;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import game.enums.Attr;
import game.main.GamePanel;
import game.util.CameraHelper;

/**
 * Quái vật Skeleton Lord.
 */
public class SkeletonLord extends Monster {
    /** Ảnh tấn công lên 1 */
    private BufferedImage attackUp1;
    /** Ảnh tấn công lên 2 */
    private BufferedImage attackUp2;
    /** Ảnh tấn công xuống 1 */
    private BufferedImage attackDown1;
    /** Ảnh tấn công xuống 2 */
    private BufferedImage attackDown2;
    /** Ảnh tấn công trái 1 */
    private BufferedImage attackLeft1;
    /** Ảnh tấn công trái 2 */
    private BufferedImage attackLeft2;
    /** Ảnh tấn công phải 1 */
    private BufferedImage attackRight1;
    /** Ảnh tấn công phải 2 */
    private BufferedImage attackRight2;

    /**
     * Khởi tạo Skeleton Lord.
     *
     * @param gp tham chiếu game panel
     */
    public SkeletonLord(GamePanel gp) {
        super(gp);
        setSpeed(2);
        setDirection("down");
        setScaleEntityX(gp.getTileSize());
        setScaleEntityY(gp.getTileSize());
        setCollisionArea(new Rectangle(8, 16, 32, 32));
        atts().set(Attr.HEALTH, 30);
        maxHealth = 30;
        attackDamage = 5;
        attackArea = new Rectangle(0, 0, gp.getTileSize(), gp.getTileSize());
        detectionRange = 10 * gp.getTileSize();
        loadImages();
    }

    /** Tải ảnh di chuyển và tấn công */
    private void loadImages() {
        setUp1(setup("/data/monster/skeleton/skeletonlord_up_1"));
        setUp2(setup("/data/monster/skeleton/skeletonlord_up_2"));
        setDown1(setup("/data/monster/skeleton/skeletonlord_down_1"));
        setDown2(setup("/data/monster/skeleton/skeletonlord_down_2"));
        setLeft1(setup("/data/monster/skeleton/skeletonlord_left_1"));
        setLeft2(setup("/data/monster/skeleton/skeletonlord_left_2"));
        setRight1(setup("/data/monster/skeleton/skeletonlord_right_1"));
        setRight2(setup("/data/monster/skeleton/skeletonlord_right_2"));
        attackUp1 = setup("/data/monster/skeleton/skeletonlord_attack_up_1", gp.getTileSize(), gp.getTileSize() * 2);
        attackUp2 = setup("/data/monster/skeleton/skeletonlord_attack_up_2", gp.getTileSize(), gp.getTileSize() * 2);
        attackDown1 = setup("/data/monster/skeleton/skeletonlord_attack_down_1", gp.getTileSize(), gp.getTileSize() * 2);
        attackDown2 = setup("/data/monster/skeleton/skeletonlord_attack_down_2", gp.getTileSize(), gp.getTileSize() * 2);
        attackLeft1 = setup("/data/monster/skeleton/skeletonlord_attack_left_1", gp.getTileSize() * 2, gp.getTileSize());
        attackLeft2 = setup("/data/monster/skeleton/skeletonlord_attack_left_2", gp.getTileSize() * 2, gp.getTileSize());
        attackRight1 = setup("/data/monster/skeleton/skeletonlord_attack_right_1", gp.getTileSize() * 2, gp.getTileSize());
        attackRight2 = setup("/data/monster/skeleton/skeletonlord_attack_right_2", gp.getTileSize() * 2, gp.getTileSize());
    }

    /**
     * Vẽ Skeleton Lord cùng thanh máu.
     */
    @Override
    public void draw(Graphics2D g2, GamePanel gp) {
        Point screenPos = CameraHelper.worldToScreen(getWorldX(), getWorldY(), gp);
        int screenX = screenPos.x;
        int screenY = screenPos.y;
        if (attacking) {
            switch (getDirection()) {
                case "up" -> screenY -= gp.getTileSize();
                case "left" -> screenX -= gp.getTileSize();
            }
        }
        g2.drawImage(getCurrentImage(), screenX, screenY, null);
        drawHealthBar(g2, new Point(screenX, screenY));
    }

    /** Lấy ảnh hiện tại theo trạng thái */
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