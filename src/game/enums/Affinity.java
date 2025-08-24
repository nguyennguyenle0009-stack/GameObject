package game.enums;

/**
 * Elements that define the player's spiritual roots.
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
