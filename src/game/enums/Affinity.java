package game.enums;

/**
 * Elemental affinities that influence which techniques a character can learn.
 */
public enum Affinity {
    FIRE("Hỏa"),
    WOOD("Mộc"),
    WATER("Thủy"),
    METAL("Kim"),
    EARTH("Thổ"),
    THUNDER("Lôi");

    private final String displayName;

    Affinity(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
