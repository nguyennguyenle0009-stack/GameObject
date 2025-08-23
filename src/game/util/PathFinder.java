package game.util;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import game.main.GamePanel;

/**
 * Simple breadth-first search path finding on the tile grid.
 * Uses the game's tile collision data to avoid solid tiles.
 */
public class PathFinder {
    /** reference to the game panel for tile data */
    private final GamePanel gp;
    /** number of columns in the world */
    private final int maxCol;
    /** number of rows in the world */
    private final int maxRow;

    /**
     * Create a path finder for the given game panel.
     *
     * @param gp game panel providing tile data
     */
    public PathFinder(GamePanel gp) {
        this.gp = gp;
        this.maxCol = gp.getMaxWorldCol();
        this.maxRow = gp.getMaxWorldRow();
    }

    /**
     * Find a path between two tile positions.
     *
     * @param start starting tile position
     * @param goal  goal tile position
     * @return list of tile positions from start to goal (excluding start)
     */
    public List<Point> findPath(Point start, Point goal) {
        boolean[][] visited = new boolean[maxCol][maxRow];
        Map<Point, Point> parent = new HashMap<>();
        Queue<Point> queue = new ArrayDeque<>();

        queue.add(start);
        visited[start.x][start.y] = true;

        int[][] dirs = { {0, -1}, {0, 1}, {-1, 0}, {1, 0} };
        while (!queue.isEmpty()) {
            Point p = queue.poll();
            if (p.equals(goal)) {
                break;
            }
            for (int[] d : dirs) {
                int nx = p.x + d[0];
                int ny = p.y + d[1];
                if (isWalkable(nx, ny) && !visited[nx][ny]) {
                    Point np = new Point(nx, ny);
                    queue.add(np);
                    visited[nx][ny] = true;
                    parent.put(np, p);
                }
            }
        }

        if (!visited[goal.x][goal.y]) {
            return Collections.emptyList();
        }
        List<Point> path = new ArrayList<>();
        Point current = goal;
        while (!current.equals(start)) {
            path.add(current);
            current = parent.get(current);
        }
        Collections.reverse(path);
        return path;
    }

    /**
     * Check if a tile is walkable.
     *
     * @param col tile column
     * @param row tile row
     * @return true if not solid and within bounds
     */
    private boolean isWalkable(int col, int row) {
        if (col < 0 || col >= maxCol || row < 0 || row >= maxRow) {
            return false;
        }
        int tileNum = gp.getTileManager().getMapTileNumber()[col][row];
        return !gp.getTileManager().getTile()[tileNum].isCollision();
    }
}
