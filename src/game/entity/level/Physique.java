package game.entity.level;

/**
 * Phân loại thể chất của nhân vật.
 * Ảnh hưởng đến giới hạn sử dụng đan dược và điều kiện đột phá.
 */
public enum Physique {
    /** Thể chất bình thường */
    NORMAL(10),
    /** Thể chất hấp thụ - dùng được 20 viên */
    ABSORB(20),
    /** Thể chất hấp thụ 1 - dùng được 30 viên */
    ABSORB_I(30),
    /** Thể chất hấp thụ viên mãn - dùng được 40 viên */
    ABSORB_FULL(40),
    /** Thể chất hư không - cần cho tiểu cảnh giới 15 */
    VOID(10),
    /** Thể chất hư không đại đế - cần cho tiểu cảnh giới 20 */
    VOID_EMPEROR(10);

    /**
     * Số lượng đan tăng chỉ số tối đa có thể sử dụng trong mỗi đại cảnh giới.
     */
    private final int maxPillPerRealm;

    Physique(int maxPillPerRealm) {
        this.maxPillPerRealm = maxPillPerRealm;
    }

    /**
     * @return giới hạn số viên đan tăng chỉ số có thể dùng trong mỗi đại cảnh giới.
     */
    public int getMaxPillPerRealm() {
        return maxPillPerRealm;
    }

    /**
     * Kiểm tra có phải thể chất hư không.
     */
    public boolean isVoidType() {
        return this == VOID || this == VOID_EMPEROR;
    }

    /**
     * Kiểm tra có phải thể chất hư không đại đế.
     */
    public boolean isVoidEmperor() {
        return this == VOID_EMPEROR;
    }
}
