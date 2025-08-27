package game.entity.attributes;

import java.util.EnumMap;
import java.util.Map;

import game.enums.Attr;

/**
 * Stores combat attributes using {@link Stat} objects that separate base and bonus
 * values. The final stat is {@code base + bonus} capped by the defined max.
 */
public class Attributes {

    /** Map of attribute to stat container. */
    private EnumMap<Attr, Stat> stats = new EnumMap<>(Attr.class);

    /** Retrieve the final value of an attribute. */
    public int get(Attr k) { return stats.getOrDefault(k, new Stat()).getFinal(); }

    /** Retrieve the base value of an attribute. */
    public int getBase(Attr k) { return stats.getOrDefault(k, new Stat()).getBase(); }

    /** Set the base value of an attribute. */
    public void set(Attr k, int v) { stats.computeIfAbsent(k, a -> new Stat()).setBase(v); }

    /** Increase/decrease the base value of an attribute. */
    public void add(Attr k, int d) { set(k, getBase(k) + d); }

    /** Retrieve the max value of an attribute. */
    public int getMax(Attr k) { return stats.getOrDefault(k, new Stat()).getMax(); }

    /** Set the max value of an attribute. */
    public void setMax(Attr k, int v) { stats.computeIfAbsent(k, a -> new Stat()).setMax(v); }

    /** Add a temporary bonus to an attribute. */
    public void addBonus(Attr k, int b) { stats.computeIfAbsent(k, a -> new Stat()).addBonus(b); }

    /** Get current bonus of an attribute. */
    public int getBonus(Attr k) { return stats.getOrDefault(k, new Stat()).getBonus(); }

    /** Reset all bonuses back to zero. */
    public void resetBonuses() { stats.values().forEach(s -> s.setBonus(0)); }

    /** Convenience wrapper for {@link #get(Attr)}. */
    public int getFinal(Attr k) { return get(k); }

    /**
     * Copy base values and max values from another {@link Attributes} instance
     * while clearing all bonuses.
     */
    public void copyFrom(Attributes other) {
        stats.clear();
        for (Map.Entry<Attr, Stat> e : other.stats.entrySet()) {
            Stat src = e.getValue();
            Stat dst = new Stat(src.getBase(), src.getMax());
            stats.put(e.getKey(), dst);
        }
    }

    /** @return unmodifiable view of internal stat map. */
    public Map<Attr, Stat> view() { return Map.copyOf(stats); }
}
