package game.enums;

/**
 * Phẩm cấp của kỹ năng/công pháp.
 */
public enum SkillGrade {
    HA("Hạ phẩm"),
    TRUNG("Trung phẩm"),
    THUONG("Thượng phẩm"),
    CUC("Cực phẩm");

    private final String display;

    SkillGrade(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }

    /**
     * Tìm enum theo chuỗi hiển thị.
     */
    public static SkillGrade fromDisplay(String text) {
        for (SkillGrade g : values()) {
            if (g.display.equalsIgnoreCase(text)) {
                return g;
            }
        }
        return HA;
    }
}
