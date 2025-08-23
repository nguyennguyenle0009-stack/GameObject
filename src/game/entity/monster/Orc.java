package game.entity.monster;

import java.awt.Rectangle;

import game.enums.Attr;
import game.main.GamePanel;

/**
 * Quái Orc sử dụng bộ ảnh ở thư mục data/monster/orc.
 */
public class Orc extends Monster {

    /** Khởi tạo Orc với các giá trị mặc định và nạp ảnh */
    public Orc(GamePanel gp) {
        super(gp);
        setDefaultValues();
        loadImages();
    }

    @Override
    protected void setDefaultValues() {
        setSpeed(2); // tốc độ di chuyển
        setDirection("down");
        setCollisionArea(new Rectangle(8, 16, 32, 32)); // vùng va chạm
        atts().set(Attr.HEALTH, 40); // máu cơ bản
        maxHealth = 40;
        attackDamage = 8; // sát thương gây ra
        ATTACK_COOLDOWN = 90; // 1.5 giây hồi chiêu
        ATTACK_DURATION = 20;
    }

    @Override
    protected void loadImages() {
        try {
            // Hình di chuyển
            setUp1(setup("/data/monster/orc/orc_up_1"));
            setUp2(setup("/data/monster/orc/orc_up_2"));
            setDown1(setup("/data/monster/orc/orc_down_1"));
            setDown2(setup("/data/monster/orc/orc_down_2"));
            setLeft1(setup("/data/monster/orc/orc_left_1"));
            setLeft2(setup("/data/monster/orc/orc_left_2"));
            setRight1(setup("/data/monster/orc/orc_right_1"));
            setRight2(setup("/data/monster/orc/orc_right_2"));

            // Hình tấn công
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
}
