package game.entity.attributes;

import java.util.EnumMap;
import java.util.Map;

import game.enums.Attr;

/**
 * Container for all combat related attributes of an entity.
 * <p>
 * Each attribute consists of a base value (permanent), a bonus value coming
 * from equipment or other temporary sources and an optional maximum cap.
 */
public class Attributes {

    /** Stats keyed by attribute type. */
    private final EnumMap<Attr, Stat> stats = new EnumMap<>(Attr.class);

    /** Retrieve the final value of an attribute (base + bonus). */
    public int get(Attr k) { return stats.getOrDefault(k, new Stat()).getFinal(); }

    /** Retrieve the base value of an attribute. */
    public int getBase(Attr k) { return stats.getOrDefault(k, new Stat()).getBase(); }

    /** Retrieve the accumulated bonus of an attribute. */
    public int getBonus(Attr k) { return stats.getOrDefault(k, new Stat()).getBonus(); }

    /** Set the base value of an attribute. */
    public void set(Attr k, int v) { stats.computeIfAbsent(k, kk -> new Stat()).setBase(v); }

    /** Increase/decrease the base value of an attribute. */
    public void add(Attr k, int d) { stats.computeIfAbsent(k, kk -> new Stat()).addBase(d); }

    /** Add a bonus modifier to an attribute. */
    public void addBonus(Attr k, int d) { stats.computeIfAbsent(k, kk -> new Stat()).addBonus(d); }

    /** Clear all bonus modifiers. */
    public void clearBonuses() { stats.values().forEach(Stat::clearBonus); }

    /** Get the maximum cap for an attribute. */
    public int getMax(Attr k) { return stats.getOrDefault(k, new Stat()).getMax(); }

    /** Set the maximum cap for an attribute. */
    public void setMax(Attr k, int v) { stats.computeIfAbsent(k, kk -> new Stat()).setMax(v); }

    /**
     * Return an immutable view of final attribute values.
     */
    public Map<Attr, Integer> view() {
        EnumMap<Attr, Integer> out = new EnumMap<>(Attr.class);
        stats.forEach((k, s) -> out.put(k, s.getFinal()));
        return Map.copyOf(out);
    }

    /**
     * Export base values for persistence.
     */
    public EnumMap<Attr, Integer> getStarts() {
        EnumMap<Attr, Integer> out = new EnumMap<>(Attr.class);
        stats.forEach((k, s) -> out.put(k, s.getBase()));
        return out;
    }

    /**
     * Replace all base values with the provided map (bonuses are cleared).
     */
    public Attributes setStarts(EnumMap<Attr, Integer> starts) {
        stats.clear();
        starts.forEach((k, v) -> {
            Stat s = new Stat();
            s.setBase(v);
            stats.put(k, s);
        });
        return this;
    }
}

