package game.enums;

/**
 * Cultivation realms. Each realm contains several minor stages.
 */
public enum Realm {
    PHAM_NHAN("Phàm nhân"),
    LUYEN_THE("Luyện thể"),
    LUYEN_KHI("Luyện khí");

    private final String displayName;

    Realm(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
