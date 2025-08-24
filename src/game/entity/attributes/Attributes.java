package game.entity.attributes;

import java.util.EnumMap;
import java.util.Map;

import game.enums.Attr;

/**
 * Stores both current and maximum value for every attribute of an actor.
 * <p>
 * Previously the game only tracked a single value for each stat which caused
 * the max values to be lost after leveling or using items.  By keeping a
 * separate "max" map we can clamp consumable effects properly and display the
 * current/maximum pair in the UI.
 */
public class Attributes {
    // current value of each attribute
    private EnumMap<Attr, Integer> current = new EnumMap<>(Attr.class);
    // maximum value ("base" value) of each attribute
    private EnumMap<Attr, Integer> max = new EnumMap<>(Attr.class);

    /** Returns the current value of an attribute. */
    public int get(Attr k) { return current.getOrDefault(k, 0); }

    /** Returns the maximum (base) value of an attribute. */
    public int getMax(Attr k) { return max.getOrDefault(k, 0); }

    /** Sets the current value of an attribute. */
    public void set(Attr k, int v) { current.put(k, Math.max(0, v)); }

    /** Sets the maximum value of an attribute. */
    public void setMax(Attr k, int v) { max.put(k, Math.max(0, v)); }

    /** Convenience: set both current and max to the same value. */
    public void setBoth(Attr k, int v) { set(k, v); setMax(k, v); }

    /** Adds delta to current value (can be negative). */
    public void add(Attr k, int d) { set(k, get(k) + d); }

    /** Returns a read only view of the current stats map. */
    public Map<Attr, Integer> view() { return Map.copyOf(current); }
}
