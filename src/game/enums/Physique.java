package game.enums;

/**
 * Unique physiques grant special bonuses and may alter cultivation limits.
 */
public enum Physique {
    NORMAL("Thể chất bình thường", 10, 1.0, 1.0, 1.0, 1.0),
    HU_KHONG("Hư không", 15, 1.0, 1.0, 1.0, 1.0),
    HU_KHONG_DAI_DE("Hư không đại đế", 20, 1.0, 1.0, 1.0, 1.0),
    THANH_THE("Thánh Thể", 10, 1.0, 2.0, 1.0, 1.0),
    TIEN_LINH_THE("Tiên Linh Thể", 10, 1.0, 1.0, 3.0, 1.0),
    THAN_THE("Thần Thể", 10, 10.0, 1.0, 1.0, 1.0),
    NGU_HANH_LINH_CAN("Ngũ hành linh căn", 10, 1.0, 1.0, 5.0, 5.0);

    private final String displayName;
    /** maximum minor stages allowed in a realm */
    private final int maxStage;
    /** extra damage multiplier applied to attacks */
    private final double attackDamageMultiplier;
    /** additional multiplier applied to DEF each stage */
    private final double defBonusMultiplier;
    /** divisor applied to spirit requirement (training speed) */
    private final double spiritRequirementDivisor;
    /** extra multiplier applied to all stats per stage */
    private final double statBonusMultiplier;

    Physique(String displayName, int maxStage, double attackDamageMultiplier,
             double defBonusMultiplier, double spiritRequirementDivisor,
             double statBonusMultiplier) {
        this.displayName = displayName;
        this.maxStage = maxStage;
        this.attackDamageMultiplier = attackDamageMultiplier;
        this.defBonusMultiplier = defBonusMultiplier;
        this.spiritRequirementDivisor = spiritRequirementDivisor;
        this.statBonusMultiplier = statBonusMultiplier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMaxStage() {
        return maxStage;
    }

    public double getAttackDamageMultiplier() {
        return attackDamageMultiplier;
    }

    public double getDefBonusMultiplier() {
        return defBonusMultiplier;
    }

    public double getSpiritRequirementDivisor() {
        return spiritRequirementDivisor;
    }

    public double getStatBonusMultiplier() {
        return statBonusMultiplier;
    }
}
