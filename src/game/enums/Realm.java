package game.enums;

/**
 * Các đại cảnh giới tu luyện chính của nhân vật.
 * Mỗi cảnh giới có nhiều tầng (tiểu cảnh giới).
 */
public enum Realm {
    /** Trạng thái phàm nhân khởi đầu */
    PHAM_NHAN("Phàm nhân"),
    /** Luyện thể kỳ */
    LUYEN_THE("Luyện thể"),
    /** Luyện khí kỳ */
    LUYEN_KHI("Luyện khí");

    private final String displayName;

    Realm(String displayName) {
        this.displayName = displayName;
    }

    /**
     * @return tên hiển thị của cảnh giới
     */
    public String getDisplayName() {
        return displayName;
    }
}
