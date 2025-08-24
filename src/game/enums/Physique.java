package game.enums;

/**
 * Special body constitutions that modify leveling behaviour.
 */
public enum Physique {
    NORMAL("Bình thường"),
    HU_KHONG("Hư không"),
    HU_KHONG_DAI_DE("Hư không đại đế"),
    THANH_THE("Thánh Thể"),
    TIEN_LINH_THE("Tiên Linh Thể"),
    THAN_THE("Thần Thể");

    private final String displayName;

    Physique(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
