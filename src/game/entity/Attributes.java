package game.entity;

import java.util.HashMap;
import java.util.Map;

public class Attributes {
    public static final String HEALTH = "health";
    public static final String ATTACK = "attack";
    public static final String QI = "qi"; // energy used to perform skills
    public static final String SPIRIT = "spirit"; // energy for levelling up
    public static final String STRENGTH = "strength";
    public static final String SOUL = "soul";
    public static final String PHYSIQUE = "physique"; // talent indicator

    private final Map<String, Integer> stats = new HashMap<>();

    public Attributes() {
        stats.put(HEALTH, 100);
        stats.put(ATTACK, 10);
        stats.put(QI, 50);
        stats.put(SPIRIT, 0);
        stats.put(STRENGTH, 5);
        stats.put(SOUL, 5);
        stats.put(PHYSIQUE, 5);
    }

    public int get(String key) {
        return stats.getOrDefault(key, 0);
    }

    public void set(String key, int value) {
        stats.put(key, value);
    }

    public void add(String key, int delta) {
        stats.put(key, get(key) + delta);
    }

    public Map<String, Integer> getAll() {
        return stats;
    }
}