package game.enums;

/**
 * Cảnh giới tu luyện của nhân vật.
 * Hiện tại hỗ trợ 3 cấp cơ bản.
 */
public enum Realm {
    /** Cấp khởi đầu của người chơi. */
    PHAM_NHAN("Phàm nhân"),
    /** Sau khi đột phá từ phàm nhân. */
    LUYEN_THE("Luyện thể"),
    /** Đột phá từ luyện thể. */
    LUYEN_KHI("Luyện khí");

    private final String displayName;

    Realm(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
