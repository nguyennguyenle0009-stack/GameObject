package game.entity;

import java.util.EnumMap;

import game.enums.Attr;

public class StatGrowth {
    private final EnumMap<Attr, Growth> table = new EnumMap<>(Attr.class);

    public StatGrowth set(Attr k, int base, int perLv) {
        table.put(k, new Growth(base, perLv));
        return this; // fluent
    }

    public Growth get(Attr k) {
        return table.getOrDefault(k, new Growth(0, 0));
    }

    /** Ghi đè toàn bộ stats trong Attributes theo level */
    public void applyTo(Attributes a, int level) {
        for (Attr k : Attr.values()) {
            a.set(k, get(k).at(level));
        }
    }
}