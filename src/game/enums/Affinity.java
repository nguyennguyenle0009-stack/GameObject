package game.enums;

/**
 * Linh căn (thuộc tính nguyên tố) của nhân vật.
 */
public enum Affinity {
    HOA("Hỏa"),
    MOC("Mộc"),
    THUY("Thủy"),
    KIM("Kim"),
    THO("Thổ"),
    LOI("Lôi");

    private final String displayName;

    Affinity(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
