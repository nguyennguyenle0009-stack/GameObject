package game.entity;

import java.util.EnumMap;
import java.util.Map;

import game.enums.Attr;

public class Attributes {
    private final EnumMap<Attr, Integer> stats = new EnumMap<>(Attr.class);

    public int get(Attr key) {
        return stats.getOrDefault(key, 0);
    }

    public void set(Attr key, int value) {
        stats.put(key, Math.max(0, value)); // clamp >= 0 (có thể chỉnh tùy ý)
    }

    public void add(Attr key, int delta) {
        set(key, get(key) + delta);
    }

    public Map<Attr, Integer> view() { // read-only view nếu cần
        return Map.copyOf(stats);
    }
}