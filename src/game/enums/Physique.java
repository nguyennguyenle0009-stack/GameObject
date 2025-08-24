package game.enums;

import java.util.Random;

/**
 * Thể chất của nhân vật, quyết định một số ưu đãi khi tu luyện.
 */
public enum Physique {
    /** Thể chất bình thường, không có gì nổi bật */
    NORMAL("Thể chất bình thường"),
    /** Cho phép tu luyện tối đa 15 tầng mỗi đại cảnh giới */
    HU_KHONG("Hư không"),
    /** Cho phép tu luyện tối đa 20 tầng mỗi đại cảnh giới */
    HU_KHONG_DAI_DE("Hư không đại đế"),
    /** Mỗi khi tăng tầng DEF tăng đặc biệt */
    THANH_THE("Thánh Thể"),
    /** Tốc độ tu luyện x3 */
    TIEN_LINH_THE("Tiên Linh Thể"),
    /** Uy lực tấn công tăng mạnh */
    THAN_THE("Thần Thể"),
    /** Sở hữu ngũ hành linh căn */
    NGU_HANH_LINH_CAN("Ngũ hành linh căn");

    private final String displayName;

    Physique(String displayName) {
        this.displayName = displayName;
    }

    /**
     * @return tên hiển thị của thể chất
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Random thể chất dựa trên tỉ lệ xuất hiện.
     * Tất cả thể chất đặc biệt có tỉ lệ 1%, còn lại là bình thường.
     */
    public static Physique randomPhysique() {
        double r = new Random().nextDouble() * 100;
        if (r < 1) return HU_KHONG;
        if (r < 2) return HU_KHONG_DAI_DE;
        if (r < 3) return THANH_THE;
        if (r < 4) return TIEN_LINH_THE;
        if (r < 5) return THAN_THE;
        if (r < 6) return NGU_HANH_LINH_CAN;
        return NORMAL;
    }
}
