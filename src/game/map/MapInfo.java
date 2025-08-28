package game.map;

import java.util.HashSet;
import java.util.Set;

/**
 * Metadata for a game map including permission information.
 */
public class MapInfo {
    private final String id;
    private String name;
    private String info;
    private final Set<String> allowedPlayers = new HashSet<>();
    private final Set<String> managers = new HashSet<>();

    public MapInfo(String id, String name, String info) {
        this.id = id;
        this.name = name;
        this.info = info;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public MapInfo setName(String name) { this.name = name; return this; }
    public String getInfo() { return info; }
    public MapInfo setInfo(String info) { this.info = info; return this; }
    public Set<String> getAllowedPlayers() { return allowedPlayers; }
    public Set<String> getManagers() { return managers; }
}
