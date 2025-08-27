package game.entity.attributes;

/**
 * Represents a single attribute value with a base amount, bonus modifier and
 * optional maximum cap. The final value is {@code base + bonus} clamped to
 * {@code max} if it is greater than zero.
 */
public class Stat {
    private int base;
    private int bonus;
    private int max;
    private int current;

    /**
     * Set the base (unmodified) value.
     */
    public void setBase(int base) {
        this.base = base;
        if (current > getFinal()) {
            current = getFinal();
        }
    }

    /**
     * @return the base (unmodified) value.
     */
    public int getBase() {
        return base;
    }

    /**
     * Increase the bonus modifier by {@code d}.
     */
    public void addBonus(int d) {
        this.bonus += d;
        if (current > getFinal()) {
            current = getFinal();
        }
    }

    /**
     * Set the bonus modifier directly.
     */
    public void setBonus(int bonus) {
        this.bonus = bonus;
        if (current > getFinal()) {
            current = getFinal();
        }
    }

    /**
     * @return current bonus modifier.
     */
    public int getBonus() {
        return bonus;
    }

    /**
     * Reset bonus modifier to zero.
     */
    public void resetBonus() {
        this.bonus = 0;
        if (current > getFinal()) {
            current = getFinal();
        }
    }

    /**
     * Set maximum cap for this stat. A value ≤ 0 means no cap.
     */
    public void setMax(int max) {
        this.max = Math.max(0, max);
        if (current > getFinal()) {
            current = getFinal();
        }
    }

    /**
     * @return the maximum cap (0 if uncapped).
     */
    public int getMax() {
        return max;
    }

    /**
     * Set current value (bounded to [0,max] if max>0).
     */
    public void setCurrent(int v) {
        int cap = getFinal();
        if (max > 0) cap = Math.min(cap, max);
        this.current = Math.max(0, Math.min(v, cap));
    }

    /**
     * @return current value.
     */
    public int getCurrent() {
        return current;
    }

    /**
     * @return final value (base + bonus) clamped to max if >0.
     */
    public int getFinal() {
        int total = base + bonus;
        if (max > 0) {
            total = Math.min(total, max);
        }
        return total;
    }
}

