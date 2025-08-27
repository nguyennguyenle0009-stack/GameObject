package game.entity.attributes;

/**
 * Represents a single numeric attribute broken down into base value and
 * temporary bonus. The final value is {@code base + bonus} and is capped by
 * {@code max}. A separate {@code value} tracks the current amount for
 * attributes such as HEALTH that fluctuate during gameplay.
 */
public class Stat {
    private int base;
    private int bonus;
    private int max;
    private int value;

    /** Set the base (permanent) value. */
    public void setBase(int base) {
        this.base = base;
        recalc();
    }

    /** Get the base value. */
    public int getBase() { return base; }

    /** Set the bonus value. */
    public void setBonus(int bonus) {
        this.bonus = bonus;
        recalc();
    }

    /** Add to the bonus component. */
    public void addBonus(int d) {
        this.bonus += d;
        recalc();
    }

    /** Remove all bonus modifiers. */
    public void clearBonus() {
        this.bonus = 0;
        recalc();
    }

    /** Get the current accumulated bonus. */
    public int getBonus() { return bonus; }

    /** Set maximum cap for the value. */
    public void setMax(int max) {
        this.max = max;
        recalc();
    }

    /** Get the maximum cap. */
    public int getMax() { return max; }

    /** @return final value after applying bonus. */
    public int getFinal() { return base + bonus; }

    /** Set the current value, clamped to [0, final]. */
    public void setValue(int v) {
        value = clamp(v);
    }

    /** Get the current value. */
    public int getValue() { return value; }

    /** Increase/decrease current value. */
    public void addValue(int d) {
        setValue(value + d);
    }

    private int clamp(int v) {
        int cap = max > 0 ? Math.min(max, getFinal()) : getFinal();
        return Math.max(0, Math.min(v, cap));
    }

    private void recalc() {
        value = clamp(value);
    }
}
