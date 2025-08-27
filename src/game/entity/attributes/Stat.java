package game.entity.attributes;

/**
 * Represents a single numeric attribute with base, bonus and maximum values.
 */
public class Stat {
    private int base;
    private int bonus;
    private int max;

    public Stat() {
        this(0,0,0);
    }

    public Stat(int base, int bonus, int max) {
        this.base = base;
        this.bonus = bonus;
        this.max = max;
    }

    /** @return base value without any bonuses. */
    public int getBase() { return base; }

    /** Set base value. Clamped to not exceed max if max>0. */
    public void setBase(int v) {
        base = v;
        if (max > 0 && base > max) base = max;
        if (base < 0) base = 0;
    }

    /** Increase base value by delta. */
    public void addBase(int d) { setBase(base + d); }

    /** @return bonus value applied on top of base. */
    public int getBonus() { return bonus; }

    /** Set bonus value. */
    public void setBonus(int v) { bonus = v; }

    /** Increase bonus by delta. */
    public void addBonus(int d) { bonus += d; }

    /** @return maximum allowed value (applied after bonus). */
    public int getMax() { return max; }

    /** Set maximum allowed value. */
    public void setMax(int v) {
        max = v;
        if (max > 0 && base > max) base = max;
    }

    /** Compute final value = min(base + bonus, max) if max>0. */
    public int getFinal() {
        int val = base + bonus;
        if (max > 0 && val > max) val = max;
        if (val < 0) val = 0;
        return val;
    }
}
