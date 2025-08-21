package game.pathfinding;

import java.util.ArrayList;
import java.util.List;

import game.main.GamePanel;

public class PathFinder {
    private final GamePanel gp;
    private final Node[][] nodes;
    private final List<Node> openList = new ArrayList<>();
    private final List<Node> pathList = new ArrayList<>();
    private Node startNode;
    private Node goalNode;
    private Node currentNode;
    private boolean goalReached;
    private int step;

    public PathFinder(GamePanel gp) {
        this.gp = gp;
        nodes = new Node[gp.getMaxWorldCol()][gp.getMaxWorldRow()];
        for (int col = 0; col < gp.getMaxWorldCol(); col++) {
            for (int row = 0; row < gp.getMaxWorldRow(); row++) {
                nodes[col][row] = new Node(col, row);
            }
        }
    }

    private void resetNodes() {
        for (int col = 0; col < gp.getMaxWorldCol(); col++) {
            for (int row = 0; row < gp.getMaxWorldRow(); row++) {
                Node node = nodes[col][row];
                node.open = false;
                node.checked = false;
                node.solid = false;
                node.parent = null;
                node.gCost = 0;
                node.hCost = 0;
                node.fCost = 0;
            }
        }
        openList.clear();
        pathList.clear();
        goalReached = false;
        step = 0;
    }

    public void setNodes(int startCol, int startRow, int goalCol, int goalRow) {
        resetNodes();

        startNode = nodes[startCol][startRow];
        currentNode = startNode;
        goalNode = nodes[goalCol][goalRow];
        openList.add(currentNode);

        for (int col = 0; col < gp.getMaxWorldCol(); col++) {
            for (int row = 0; row < gp.getMaxWorldRow(); row++) {
                int tileNum = gp.getTileManager().getMapTileNumber()[col][row];
                if (gp.getTileManager().getTile()[tileNum].isCollision()) {
                    nodes[col][row].solid = true;
                }
            }
        }
    }

    public boolean search() {
        while (!goalReached && step < gp.getMaxWorldCol() * gp.getMaxWorldRow()) {
            int col = currentNode.col;
            int row = currentNode.row;

            currentNode.checked = true;
            openList.remove(currentNode);

            openNode(col, row - 1); // up
            openNode(col - 1, row); // left
            openNode(col, row + 1); // down
            openNode(col + 1, row); // right

            int bestNodeIndex = 0;
            int bestNodeFCost = Integer.MAX_VALUE;

            for (int i = 0; i < openList.size(); i++) {
                Node node = openList.get(i);
                if (node.fCost < bestNodeFCost) {
                    bestNodeFCost = node.fCost;
                    bestNodeIndex = i;
                } else if (node.fCost == bestNodeFCost) {
                    if (node.gCost < openList.get(bestNodeIndex).gCost) {
                        bestNodeIndex = i;
                    }
                }
            }

            if (openList.isEmpty()) {
                break;
            }

            currentNode = openList.get(bestNodeIndex);

            if (currentNode == goalNode) {
                goalReached = true;
                trackPath();
            }

            step++;
        }
        return goalReached;
    }

    private void openNode(int col, int row) {
        if (col < 0 || row < 0 || col >= gp.getMaxWorldCol() || row >= gp.getMaxWorldRow()) {
            return;
        }
        Node node = nodes[col][row];
        if (node.open || node.checked || node.solid) {
            return;
        }
        node.open = true;
        node.parent = currentNode;
        node.gCost = Math.abs(col - startNode.col) + Math.abs(row - startNode.row);
        node.hCost = Math.abs(col - goalNode.col) + Math.abs(row - goalNode.row);
        node.fCost = node.gCost + node.hCost;
        openList.add(node);
    }

    private void trackPath() {
        Node current = goalNode;
        while (current != startNode) {
            pathList.add(0, current);
            current = current.parent;
        }
    }

    public List<Node> getPathList() {
        return pathList;
    }
}
