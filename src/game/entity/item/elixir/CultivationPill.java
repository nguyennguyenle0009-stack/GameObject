package game.entity.item.elixir;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import game.entity.Player;
import game.entity.item.Item;

/**
 * Đan dược tăng tốc độ tu luyện trong thời gian ngắn.
 */
public class CultivationPill extends Item {

    /** Các phẩm chất của đan dược. */
    public enum Grade {
        HA_PHAM(1), TRUNG_PHAM(2), THUONG_PHAM(3), CUC_PHAM(4);
        private final int bonus;
        Grade(int b) { this.bonus = b; }
        public int bonus() { return bonus; }
    }

    private static BufferedImage icon;

    private final Grade grade;

    static {
        try {
            // Tạm dùng lại hình ảnh bình thuốc có sẵn
            icon = ImageIO.read(CultivationPill.class.getResourceAsStream(
                    "/data/item/elixir/HealthPotion.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CultivationPill(Grade grade, int quantity) {
        super("Tu luyện đan " + gradeName(grade),
              "Tăng tốc độ tu luyện", quantity, 100);
        this.grade = grade;
    }

    @Override
    public void use(Player p) {
        // Khi dùng đan dược, áp dụng hiệu ứng trong 10 phút
        p.applyCultivationPill(getName(), grade.bonus());
        decreaseQuantity(1);
    }

    @Override
    public Item copyWithQuantity(int qty) {
        return new CultivationPill(grade, qty);
    }

    @Override
    public boolean isSameStack(Item other) {
        if (!(other instanceof CultivationPill cp)) return false;
        return this.grade == cp.grade;
    }

    @Override
    public BufferedImage getIcon() {
        return icon;
    }

    public Grade getGrade() { return grade; }

    private static String gradeName(Grade g) {
        return switch (g) {
            case HA_PHAM -> "hạ phẩm";
            case TRUNG_PHAM -> "trung phẩm";
            case THUONG_PHAM -> "thượng phẩm";
            case CUC_PHAM -> "cực phẩm";
        };
    }
}