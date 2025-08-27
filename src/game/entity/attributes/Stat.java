package game.entity.attributes;

/**
 * Represents a single numeric stat with base and bonus values.
 * <p>Final value is {@code base + bonus} clamped by {@code max} if provided.
 * Current value is automatically kept within [0, final].
 */
public class Stat {
    private int base;
    private int bonus;
    private int max;
    private int current;

    public int getBase() { return base; }
    public void setBase(int base) {
        this.base = base;
        setCurrent(current); // ensure current within new final
    }

    public int getBonus() { return bonus; }
    public void addBonus(int delta) {
        this.bonus += delta;
        setCurrent(current); // clamp
    }
    public void clearBonus() {
        this.bonus = 0;
        setCurrent(current);
    }

    public int getMax() { return max; }
    public void setMax(int max) {
        this.max = Math.max(0, max);
        setCurrent(current);
    }

    public int getCurrent() { return current; }
    public void setCurrent(int current) {
        int finalVal = getFinal();
        this.current = Math.max(0, Math.min(current, finalVal));
    }
    public void addCurrent(int delta) { setCurrent(this.current + delta); }

    /**
     * Final value after applying bonus and clamping by max.
     */
    public int getFinal() {
        int val = base + bonus;
        if (max > 0) {
            val = Math.min(val, max);
        }
        return val;
    }
}
