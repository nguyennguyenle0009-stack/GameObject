package game.entity.attributes;

import java.util.EnumMap;
import java.util.Map;

import game.enums.Attr;

/**
 * Stores a collection of Stats for an actor. Each attribute consists of
 * base, bonus and optional max values. Final value is base + bonus clamped to
 * max.
 */
public class Attributes {

    private EnumMap<Attr, Stat> stats = new EnumMap<>(Attr.class);

    public Attributes() {
        for (Attr a : Attr.values()) {
            stats.put(a, new Stat());
        }
    }

    /** Return final value of the attribute. */
    public int get(Attr k) { return stats.get(k).getFinal(); }

    /** Return base value. */
    public int getBase(Attr k) { return stats.get(k).getBase(); }

    /** Set base value. */
    public void setBase(Attr k, int v) { stats.get(k).setBase(v); }

    /** Increase base by delta. */
    public void addBase(Attr k, int d) { stats.get(k).addBase(d); }

    /** Add bonus value (can be negative). */
    public void addBonus(Attr k, int d) { stats.get(k).addBonus(d); }

    /** Reset bonus for the attribute. */
    public void setBonus(Attr k, int v) { stats.get(k).setBonus(v); }

    /** Get bonus of attribute. */
    public int getBonus(Attr k) { return stats.get(k).getBonus(); }

    /** Get max value. */
    public int getMax(Attr k) { return stats.get(k).getMax(); }

    /** Set max value. */
    public void setMax(Attr k, int v) { stats.get(k).setMax(v); }

    /** View immutable copy of base values. */
    public Map<Attr, Integer> viewBase() {
        EnumMap<Attr, Integer> map = new EnumMap<>(Attr.class);
        for (var e : stats.entrySet()) {
            map.put(e.getKey(), e.getValue().getBase());
        }
        return Map.copyOf(map);
    }

    /** Copy base and max values from another Attributes. */
    public void copyBaseFrom(Attributes other) {
        for (Attr a : Attr.values()) {
            Stat src = other.stats.get(a);
            Stat dst = this.stats.get(a);
            dst.setBase(src.getBase());
            dst.setMax(src.getMax());
        }
    }
}
