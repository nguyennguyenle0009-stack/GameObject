package game.entity.attributes;

import java.util.EnumMap;
import java.util.Map;

import game.enums.Attr;

/**
 * Lưu trữ thuộc tính của nhân vật.
 * Gồm giá trị gốc (base) và giá trị hiện tại (current).
 */
public class Attributes {
    private EnumMap<Attr, Integer> base = new EnumMap<>(Attr.class);
    private EnumMap<Attr, Integer> current = new EnumMap<>(Attr.class);

    /**
     * Lấy giá trị hiện tại của thuộc tính.
     */
    public int get(Attr k) { return current.getOrDefault(k, 0); }

    /**
     * Thiết lập giá trị hiện tại của thuộc tính.
     */
    public void set(Attr k, int v) { current.put(k, Math.max(0, v)); }

    /**
     * Cộng thêm vào giá trị hiện tại của thuộc tính.
     */
    public void add(Attr k, int d) { set(k, get(k) + d); }

    /**
     * Lấy giá trị gốc (tối đa) của thuộc tính.
     */
    public int getBase(Attr k) { return base.getOrDefault(k, 0); }

    /**
     * Thiết lập giá trị gốc của thuộc tính và tự động
     * đồng bộ giá trị hiện tại nếu đang lớn hơn giá trị gốc mới.
     */
    public void setBase(Attr k, int v) {
        base.put(k, Math.max(0, v));
        current.put(k, Math.min(get(k), v));
    }

    /**
     * Cộng thêm vào giá trị gốc của thuộc tính.
     */
    public void addBase(Attr k, int d) { setBase(k, getBase(k) + d); }

    /**
     * Đặt giá trị hiện tại bằng giá trị gốc.
     */
    public void reset(Attr k) { current.put(k, getBase(k)); }

    /**
     * Đặt toàn bộ giá trị hiện tại bằng giá trị gốc.
     */
    public void resetAll() {
        for (Attr a : Attr.values()) {
            reset(a);
        }
    }

    public Map<Attr, Integer> viewCurrent() { return Map.copyOf(current); }
}
