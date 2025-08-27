package game.entity.attributes;

import java.util.EnumMap;
import java.util.Map;

import game.enums.Attr;

/**
 * Container for character attributes. Each attribute is represented by a
 * {@link Stat} storing base, bonus, max and current values.
 */
public class Attributes {

    /** Map of all attribute stats indexed by {@link Attr}. */
    private final EnumMap<Attr, Stat> stats = new EnumMap<>(Attr.class);

    /** Ensure a stat entry exists for the given key. */
    private Stat stat(Attr k) {
        return stats.computeIfAbsent(k, a -> new Stat());
    }

    /**
     * Get the current value of an attribute.
     */
    public int get(Attr k) { return stat(k).getCurrent(); }

    /**
     * Set the current value of an attribute.
     */
    public void set(Attr k, int v) { stat(k).setCurrent(v); }

    /**
     * Increase/decrease the current value of an attribute.
     */
    public void add(Attr k, int d) { stat(k).setCurrent(stat(k).getCurrent() + d); }

    /**
     * Get the maximum cap of an attribute.
     */
    public int getMax(Attr k) { return stat(k).getMax(); }

    /**
     * Set the maximum cap of an attribute.
     */
    public void setMax(Attr k, int v) { stat(k).setMax(v); }

    /**
     * Get base (unmodified) value of an attribute.
     */
    public int getBase(Attr k) { return stat(k).getBase(); }

    /**
     * Set base (unmodified) value of an attribute.
     */
    public void setBase(Attr k, int v) { stat(k).setBase(v); }

    /**
     * Add a bonus modifier to the attribute.
     */
    public void addBonus(Attr k, int v) { stat(k).addBonus(v); }

    /**
     * Get current bonus modifier of the attribute.
     */
    public int getBonus(Attr k) { return stat(k).getBonus(); }

    /**
     * Compute final value (base + bonus) respecting max cap.
     */
    public int getFinal(Attr k) { return stat(k).getFinal(); }

    /**
     * Reset all bonus modifiers to zero.
     */
    public void resetBonuses() { stats.values().forEach(Stat::resetBonus); }

    /**
     * Copy base/max/current values from another {@link Attributes} instance.
     */
    public void copyFrom(Attributes other) {
        for (Attr a : Attr.values()) {
            Stat src = other.stats.get(a);
            if (src != null) {
                Stat dst = stat(a);
                dst.setBase(src.getBase());
                dst.setMax(src.getMax());
                dst.setCurrent(src.getCurrent());
            }
        }
    }

    /**
     * @return unmodifiable view of all stats.
     */
    public Map<Attr, Stat> view() { return Map.copyOf(stats); }
}

