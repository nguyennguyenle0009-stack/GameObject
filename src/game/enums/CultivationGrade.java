package game.enums;

/**
 * Chất lượng của công pháp và đan dược tu luyện.
 * Mỗi cấp xác định thời gian, tốc độ tu luyện và hiệu quả của đan dược.
 */
public enum CultivationGrade {
    HA("Hạ phẩm", 5 * 60, 1, 1),
    TRUNG("Trung phẩm", 10 * 60, 2, 2),
    THUONG("Thượng phẩm", 15 * 60, 3, 3),
    CUC("Cực phẩm", 25 * 60, 5, 4);

    private final String display;
    private final int manualDurationSeconds;
    private final int spiritPerSecond;
    private final int pillBonus;

    CultivationGrade(String display, int manualDurationSeconds, int spiritPerSecond, int pillBonus) {
        this.display = display;
        this.manualDurationSeconds = manualDurationSeconds;
        this.spiritPerSecond = spiritPerSecond;
        this.pillBonus = pillBonus;
    }

    /** @return tên hiển thị tiếng Việt */
    public String getDisplay() { return display; }
    /** @return thời gian tu luyện của công pháp (giây) */
    public int getManualDurationSeconds() { return manualDurationSeconds; }
    /** @return số Spirit nhận được mỗi giây khi tu luyện */
    public int getSpiritPerSecond() { return spiritPerSecond; }
    /** @return lượng Spirit cộng thêm mỗi giây từ đan dược */
    public int getPillBonus() { return pillBonus; }
}