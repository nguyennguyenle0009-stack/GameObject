package game.enums;

/**
 * Cảnh giới tu luyện của nhân vật.
 * Hiện tại mới chỉ có cấp "Phàm nhân" mặc định.
 */
public enum Realm {
    PHAM_NHAN("Phàm nhân");

    private final String displayName;

    Realm(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
