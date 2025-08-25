package game.entity.item.manual;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import game.entity.Player;
import game.entity.item.Item;
import game.enums.Physique;

/**
 * Item đại diện cho một cuốn công pháp tu luyện.
 * Mỗi cuốn có phẩm chất khác nhau quyết định thời gian tu luyện
 * và lượng SPIRIT nhận được mỗi giây.
 */
public class CultivationManual extends Item {

    /** Các phẩm chất của công pháp. */
    public enum Grade {
        HA_PHAM, TRUNG_PHAM, THUONG_PHAM, CUC_PHAM
    }

    private static BufferedImage icon;

    private final Grade grade;
    private final long durationMs; // thời gian tu luyện tính bằng mili giây
    private final int spiritPerSecond; // SPIRIT nhận mỗi giây (chưa cộng thể chất)

    static {
        try {
            // Hình ảnh sách công pháp do người dùng cung cấp
            icon = ImageIO.read(CultivationManual.class.getResourceAsStream(
                    "/data/item/manual/cultivation_book.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CultivationManual(Grade grade, int quantity) {
        super("Công pháp " + gradeName(grade),
              "Dùng để tu luyện", quantity, 1);
        this.grade = grade;
        // Thiết lập thời gian và tốc độ tu luyện theo phẩm chất
        switch (grade) {
            case HA_PHAM -> {
                durationMs = 5 * 60 * 1000L; // 5 phút
                spiritPerSecond = 1;
            }
            case TRUNG_PHAM -> {
                durationMs = 10 * 60 * 1000L; // 10 phút
                spiritPerSecond = 2;
            }
            case THUONG_PHAM -> {
                durationMs = 15 * 60 * 1000L; // 15 phút
                spiritPerSecond = 3;
            }
            case CUC_PHAM -> {
                durationMs = 25 * 60 * 1000L; // 25 phút
                spiritPerSecond = 5;
            }
            default -> {
                durationMs = 0;
                spiritPerSecond = 0;
            }
        }
    }

    /**
     * Khi sử dụng công pháp, người chơi bắt đầu trạng thái tu luyện.
     */
    @Override
    public void use(Player p) {
        p.startCultivation(this);
        decreaseQuantity(1);
    }

    @Override
    public Item copyWithQuantity(int qty) {
        return new CultivationManual(grade, qty);
    }

    @Override
    public BufferedImage getIcon() {
        return icon;
    }

    public Grade getGrade() { return grade; }
    public long getDurationMs() { return durationMs; }

    /**
     * @return lượng SPIRIT nhận mỗi giây sau khi xét thể chất tiên linh thể.
     */
    public int getSpiritPerSecond(Player p) {
        int base = spiritPerSecond;
        // Thể chất tiên linh thể giúp tốc độ tu luyện x3
        if (p.getPhysique() == Physique.TIEN_LINH_THE) {
            base *= 3;
        }
        return base;
    }

    private static String gradeName(Grade g) {
        return switch (g) {
            case HA_PHAM -> "hạ phẩm";
            case TRUNG_PHAM -> "trung phẩm";
            case THUONG_PHAM -> "thượng phẩm";
            case CUC_PHAM -> "cực phẩm";
        };
    }
}