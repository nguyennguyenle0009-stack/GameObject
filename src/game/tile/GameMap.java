package game.tile;

/**
 * Represents a tile-based map loaded from a resource file.
 */
public class GameMap {
    private final String id;
    private final String name;
    private final String info;
    private final int[][] tileNumbers;

    public GameMap(String id, String name, String info, int[][] tileNumbers) {
        this.id = id;
        this.name = name;
        this.info = info;
        this.tileNumbers = tileNumbers;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getInfo() {
        return info;
    }

    public int[][] getTileNumbers() {
        return tileNumbers;
    }

    public int getCols() {
        return tileNumbers.length;
    }

    public int getRows() {
        return tileNumbers[0].length;
    }
}
