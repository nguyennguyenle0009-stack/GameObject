package game.entity.attributes;

/**
 * Represents a single numeric attribute consisting of a base value and a bonus.
 * The final value is {@code base + bonus} clamped to {@code max}.
 */
public class Stat {
    private int base;
    private int bonus;
    private int max;

    /**
     * Create a stat with zero base and unlimited max.
     */
    public Stat() {
        this(0, Integer.MAX_VALUE);
    }

    /**
     * Create a stat with the specified base and max value.
     *
     * @param base starting base value
     * @param max  maximum allowed value
     */
    public Stat(int base, int max) {
        this.base = base;
        this.max = max;
    }

    /** @return base value without bonuses. */
    public int getBase() { return base; }

    /** Set the base value clamped between 0 and max. */
    public void setBase(int v) { this.base = Math.max(0, Math.min(v, max)); }

    /** @return current bonus value. */
    public int getBonus() { return bonus; }

    /** Set bonus directly. */
    public void setBonus(int b) { this.bonus = b; }

    /** Add to the bonus value. */
    public void addBonus(int b) { this.bonus += b; }

    /** @return maximum allowed value. */
    public int getMax() { return max; }

    /** Set the maximum allowed value. */
    public void setMax(int m) {
        this.max = Math.max(0, m);
        setBase(base); // re-clamp base
    }

    /**
     * @return final value after applying bonus, clamped to max.
     */
    public int getFinal() {
        long val = (long) base + bonus;
        if (val > max) return max;
        if (val < 0) return 0;
        return (int) val;
    }
}

