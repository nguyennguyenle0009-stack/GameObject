package game.enums;

/**
 * Thể chất đặc biệt của nhân vật.
 * Một số thể chất ảnh hưởng đến số tầng tối đa hoặc chỉ số khi lên cấp.
 */
public enum Physique {
    NORMAL("Bình thường", 10, 1.0, 1.0, 1.0),
    HU_KHONG("Hư không", 15, 1.0, 1.0, 1.0),
    HU_KHONG_DAI_DE("Hư không đại đế", 20, 1.0, 1.0, 1.0),
    THANH_THE("Thánh Thể", 10, 1.0, 2.0, 1.0),
    TIEN_LINH_THE("Tiên Linh Thể", 10, 1.0/3.0, 1.0, 1.0),
    THAN_THE("Thần Thể", 10, 1.0, 1.0, 1.0),
    // Ngũ Hành: chỉ số gấp 5 lần nên yêu cầu SPIRIT cũng gấp 5
    NGU_HANH("Ngũ Hành Linh Căn", 10, 5.0, 1.0, 5.0);

    private final String display;
    private final int maxStage;
    private final double spiritReqFactor;
    private final double defFactor;
    private final double statFactor;

    Physique(String display, int maxStage, double spiritReqFactor,
             double defFactor, double statFactor) {
        this.display = display;
        this.maxStage = maxStage;
        this.spiritReqFactor = spiritReqFactor;
        this.defFactor = defFactor;
        this.statFactor = statFactor;
    }

    /** @return tên hiển thị của thể chất */
    public String getDisplay() { return display; }
    /** @return số tầng tối đa trong mỗi đại cảnh giới */
    public int getMaxStage() { return maxStage; }
    /** @return hệ số giảm yêu cầu Spirit */
    public double getSpiritReqFactor() { return spiritReqFactor; }
    /** @return hệ số cộng dồn cho DEF */
    public double getDefFactor() { return defFactor; }
    /** @return hệ số cộng dồn cho toàn bộ chỉ số */
    public double getStatFactor() { return statFactor; }
}