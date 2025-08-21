package game.pathfinding;

public class Node {
    public int col;
    public int row;
    public int gCost;
    public int hCost;
    public int fCost;
    public boolean open;
    public boolean checked;
    public boolean solid;
    public Node parent;

    public Node(int col, int row) {
        this.col = col;
        this.row = row;
    }
}
