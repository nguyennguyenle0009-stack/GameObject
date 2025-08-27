package game.entity.attributes;

import java.util.EnumMap;
import java.util.Map;

import game.enums.Attr;

/**
 * Container of all stats for an entity. Each attribute is represented by a
 * {@link Stat} holding base value and temporary bonus. The class also keeps
 * track of the current value for stats that fluctuate (e.g. HEALTH).
 */
public class Attributes {

    /** Map of stats keyed by attribute type. */
    private EnumMap<Attr, Stat> stats = new EnumMap<>(Attr.class);

    private Stat stat(Attr k) {
        return stats.computeIfAbsent(k, a -> new Stat());
    }

    /** Get current value of an attribute. */
    public int get(Attr k) { return stat(k).getValue(); }

    /** Set current value of an attribute (clamped). */
    public void set(Attr k, int v) { stat(k).setValue(v); }

    /** Increase/decrease current value. */
    public void add(Attr k, int d) { stat(k).addValue(d); }

    /** Get base (permanent) value. */
    public int getBase(Attr k) { return stat(k).getBase(); }

    /** Set base (permanent) value. */
    public void setBase(Attr k, int v) { stat(k).setBase(v); }

    /** Add bonus modifier to attribute. */
    public void addBonus(Attr k, int d) { stat(k).addBonus(d); }

    /** Get total bonus applied to attribute. */
    public int getBonus(Attr k) { return stat(k).getBonus(); }

    /** Clear all bonus modifiers. */
    public void clearBonuses() { stats.values().forEach(Stat::clearBonus); }

    /** Get final value (base + bonus). */
    public int getFinal(Attr k) { return stat(k).getFinal(); }

    /** Get maximum cap of an attribute. */
    public int getMax(Attr k) { return stat(k).getMax(); }

    /** Set maximum cap of an attribute. */
    public void setMax(Attr k, int v) { stat(k).setMax(v); }

    /** @return unmodifiable view of underlying stat map. */
    public Map<Attr, Stat> view() { return Map.copyOf(stats); }

    public EnumMap<Attr, Stat> getStats() { return stats; }

    public Attributes setStats(EnumMap<Attr, Stat> stats) { this.stats = stats; return this; }
}
