package game.enums;

/**
 * Linh căn của nhân vật, quyết định hệ công pháp phù hợp.
 */
public enum Affinity {
    FIRE("Hỏa"),
    WOOD("Mộc"),
    WATER("Thủy"),
    METAL("Kim"),
    EARTH("Thổ"),
    LIGHTNING("Lôi");

    private final String displayName;

    Affinity(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
