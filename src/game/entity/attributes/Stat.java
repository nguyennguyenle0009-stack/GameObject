package game.entity.attributes;

/**
 * Represents a single numeric stat composed of a base value and a bonus
 * modifier. The final value is {@code base + bonus} and is capped by
 * an optional maximum.
 */
public class Stat {
    private int base;
    private int bonus;
    private int max;

    /** Create a stat with zero values and unlimited maximum. */
    public Stat() {
        this(0, 0, Integer.MAX_VALUE);
    }

    /**
     * @param base  base value for the stat
     * @param bonus bonus modifier applied on top of the base
     * @param max   upper bound of the final value
     */
    public Stat(int base, int bonus, int max) {
        this.base = base;
        this.bonus = bonus;
        this.max = max;
    }

    /** @return base (unmodified) value */
    public int getBase() { return base; }

    /** Set the base value clamped to [0, max]. */
    public void setBase(int v) { base = Math.max(0, Math.min(v, max)); }

    /** Increase the base value. */
    public void addBase(int d) { setBase(base + d); }

    /** @return accumulated bonus modifier */
    public int getBonus() { return bonus; }

    /** Add to the bonus modifier. */
    public void addBonus(int d) { bonus += d; }

    /** Clear the bonus modifier. */
    public void clearBonus() { bonus = 0; }

    /** @return maximum allowed final value */
    public int getMax() { return max; }

    /** Set the maximum cap for the final value. */
    public void setMax(int m) {
        max = Math.max(0, m);
        if (base > max) base = max;
    }

    /** @return final value after applying bonus and clamping to max. */
    public int getFinal() { return Math.min(base + bonus, max); }
}

