package game.enums;

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