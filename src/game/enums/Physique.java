package game.enums;

/**
 * Thể chất của nhân vật ảnh hưởng tới tốc độ tu luyện
 * và số tầng tối đa có thể đạt trong mỗi cảnh giới.
 */
public enum Physique {
    NORMAL("Bình thường", 10, 1.0),
    HU_KHONG("Hư không", 15, 1.0),
    HU_KHONG_DAI_DE("Hư không đại đế", 20, 1.0),
    THANH_THE("Thánh Thể", 10, 1.0),
    TIEN_LINH_THE("Tiên Linh Thể", 10, 1.0/3.0),
    THAN_THE("Thần Thể", 10, 1.0),
    NGU_HANH_LINH_CAN("Ngũ hành linh căn", 10, 5.0);

    private final String displayName;
    private final int maxTier; // Số tầng tối đa trong mỗi cảnh giới
    private final double spiritFactor; // Hệ số giảm yêu cầu Spirit (1 => bình thường)

    Physique(String displayName, int maxTier, double spiritFactor) {
        this.displayName = displayName;
        this.maxTier = maxTier;
        this.spiritFactor = spiritFactor;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * @return số tầng tối đa trong mỗi đại cảnh giới.
     */
    public int getMaxTier() {
        return maxTier;
    }

    /**
     * @return hệ số nhân với yêu cầu Spirit. Giá trị nhỏ hơn 1 nghĩa là
     *         tu luyện nhanh hơn.
     */
    public double getSpiritFactor() {
        return spiritFactor;
    }
}
