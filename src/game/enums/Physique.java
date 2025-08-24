package game.enums;

/**
 * Thể chất của nhân vật quyết định giới hạn cảnh giới và một số hiệu ứng đặc biệt.
 */
public enum Physique {
    NORMAL("Bình thường", 10),
    HU_KHONG("Hư không", 15),
    HU_KHONG_DAI_DE("Hư không đại đế", 20),
    THANH_THE("Thánh Thể", 10),
    TIEN_LINH_THE("Tiên Linh Thể", 10),
    THAN_THE("Thần Thể", 10),
    NGU_HANH_LINH_CAN("Ngũ hành linh căn", 10);

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
     * Số tầng tối đa có thể đạt được trong mỗi đại cảnh giới.
     */
    public int getMaxTier() {
        return maxTier;
    }
}
