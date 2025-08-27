package game.entity.attributes;

import java.util.EnumMap;
import java.util.Map;

import game.enums.Attr;

/**
 * Stores player attributes using {@link Stat} objects that separate base and
 * bonus values.
 */
public class Attributes {

    /** All tracked stats. */
    private EnumMap<Attr, Stat> stats = new EnumMap<>(Attr.class);

    private Stat getStat(Attr k) {
        return stats.computeIfAbsent(k, key -> new Stat());
    }

    /**
     * Current value of the attribute (after bonuses).
     */
    public int get(Attr k) { return getStat(k).getCurrent(); }

    /**
     * Set current value of the attribute, clamped by its final value.
     */
    public void set(Attr k, int v) { getStat(k).setCurrent(v); }

    /**
     * Add to the current value of the attribute.
     */
    public void add(Attr k, int d) { getStat(k).addCurrent(d); }

    /**
     * Maximum value for attributes like HP/PEP.
     */
    public int getMax(Attr k) { return getStat(k).getMax(); }

    /**
     * Define maximum value for the attribute.
     */
    public void setMax(Attr k, int v) { getStat(k).setMax(v); }

    /** Base value without bonuses. */
    public int getBase(Attr k) { return getStat(k).getBase(); }
    /** Set base value. */
    public void setBase(Attr k, int v) { getStat(k).setBase(v); }

    /** Increase base value by delta. */
    public void addBase(Attr k, int d) {
        Stat s = getStat(k);
        s.setBase(s.getBase() + d);
    }

    /** Set both base and current values. */
    public void setBoth(Attr k, int v) {
        setBase(k, v);
        set(k, v);
    }

    /** Add to both base and current values. */
    public void addBoth(Attr k, int d) {
        addBase(k, d);
        add(k, d);
    }

    /** Add bonus to this attribute. */
    public void addBonus(Attr k, int v) { getStat(k).addBonus(v); }
    /** Remove all bonuses for this attribute. */
    public void clearBonus(Attr k) { getStat(k).clearBonus(); }

    /** Final value = base + bonus clamped by max. */
    public int getFinal(Attr k) { return getStat(k).getFinal(); }

    /**
     * Copy base and max values from another Attributes.
     */
    public void copyBaseFrom(Attributes other) {
        for (Map.Entry<Attr, Stat> e : other.stats.entrySet()) {
            Stat s = getStat(e.getKey());
            s.setBase(e.getValue().getBase());
            s.setMax(e.getValue().getMax());
            s.setCurrent(e.getValue().getCurrent());
        }
    }

    /** View of final values. */
    public Map<Attr, Integer> view() {
        EnumMap<Attr, Integer> view = new EnumMap<>(Attr.class);
        for (Attr a : Attr.values()) {
            view.put(a, getFinal(a));
        }
        return Map.copyOf(view);
    }

    public EnumMap<Attr, Stat> getStats() { return stats; }
    public Attributes setStats(EnumMap<Attr, Stat> stats) { this.stats = stats; return this; }
}
