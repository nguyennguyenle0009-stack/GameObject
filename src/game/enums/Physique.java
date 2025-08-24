package game.enums;

/**
 * Các loại thể chất của nhân vật.
 * Ảnh hưởng tới số tầng tối đa của mỗi đại cảnh giới
 * và có thể cung cấp thêm hiệu ứng đặc biệt.
 */
public enum Physique {
    HU_KHONG("Hư không", 15),
    HU_KHONG_DAI_DE("Hư không đại đế", 20),
    THANH_THE("Thánh Thể", 10),
    TIEN_LINH_THE("Tiên Linh Thể", 10),
    THAN_THE("Thần Thể", 10),
    NGU_HANH_LINH_CAN("Ngũ hành linh căn", 10),
    BINH_THUONG("Bình thường", 10);

    private final String displayName;
    private final int maxTier;

    Physique(String displayName, int maxTier) {
        this.displayName = displayName;
        this.maxTier = maxTier;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Số tiểu cảnh giới tối đa mỗi đại cảnh giới mà thể chất cho phép đạt được.
     */
    public int getMaxTier() {
        return maxTier;
    }
}
