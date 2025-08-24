package game.enums;

/**
 * Các loại linh căn (Affinity) mà nhân vật có thể sở hữu.
 */
public enum Affinity {
    HOA("Hỏa"),
    MOC("Mộc"),
    THUY("Thủy"),
    KIM("Kim"),
    THO("Thổ"),
    LOI("Lôi");

    private final String display;

    Affinity(String display) {
        this.display = display;
    }

    /**
     * @return tên hiển thị của linh căn.
     */
    public String getDisplay() {
        return display;
    }
}
