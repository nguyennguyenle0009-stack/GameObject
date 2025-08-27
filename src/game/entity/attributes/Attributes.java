package game.entity.attributes;

import java.util.EnumMap;
import java.util.Map;

import game.enums.Attr;

/**
 * Lưu trữ các thuộc tính chiến đấu của nhân vật.
 * <p>
 * Mỗi thuộc tính có giá trị hiện tại và giá trị tối đa (để hiển thị thanh máu, năng lượng...).
 * Một số thuộc tính như Attack/Def có thể dùng chung giá trị max hiện tại.
 */
public class Attributes {

    /** Giá trị hiện tại của các thuộc tính */
    private EnumMap<Attr, Integer> stats = new EnumMap<>(Attr.class);

    /** Giá trị tối đa của các thuộc tính (máu tối đa, pep tối đa, exp cần để lên cấp...) */
    private EnumMap<Attr, Integer> maxStats = new EnumMap<>(Attr.class);

    /**
     * Lấy giá trị hiện tại của thuộc tính.
     */
    public int get(Attr k) { return stats.getOrDefault(k, 0); }

    /**
     * Gán giá trị hiện tại của thuộc tính (đã clamp ≥0 và ≤ max nếu có).
     */
    public void set(Attr k, int v) {
        int max = maxStats.getOrDefault(k, Integer.MAX_VALUE);
        stats.put(k, Math.max(0, Math.min(v, max)));
    }

    /**
     * Tăng/giảm giá trị thuộc tính, tự động kẹp trong khoảng [0, max].
     */
    public void add(Attr k, int d) { set(k, get(k) + d); }

    /**
     * Lấy giá trị tối đa của thuộc tính.
     */
    public int getMax(Attr k) { return maxStats.getOrDefault(k, 0); }

    /**
     * Gán giá trị tối đa của thuộc tính.
     */
    public void setMax(Attr k, int v) {
        maxStats.put(k, Math.max(0, v));
        // đảm bảo giá trị hiện tại không vượt quá max mới
        set(k, get(k));
    }

    /**
     * Trả về bản sao không thể chỉnh sửa của map thuộc tính hiện tại.
     */
    public Map<Attr, Integer> view() { return Map.copyOf(stats); }

    public EnumMap<Attr, Integer> getStarts() { return stats; }
    public Attributes setStarts(EnumMap<Attr, Integer> starts) { this.stats = starts; return this; }
}
