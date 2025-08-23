package game.entity.monster;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

import javax.imageio.ImageIO;

import game.enums.Attr;
import game.main.GamePanel;
import game.util.CameraHelper;
import game.util.UtilityTool;

/**
 * Orc monster that chases the player and performs melee attacks.
 */
public class Orc extends Monster {
    /** Detection range to start chasing the player */
    private static final int DETECTION_RANGE = 5 * 48; // 5 tiles
    /** Area used to check attacks */
    private final Rectangle attackArea;
    /** True while attack animation plays */
    private boolean attacking = false;
    /** Counts frames of current attack */
    private int attackCounter = 0;
    /** Cooldown between attacks */
    private int attackCooldown = 0;
    /** Frames of cooldown after an attack */
    private static final int ATTACK_COOLDOWN = 60;
    /** Frames an attack lasts */
    private static final int ATTACK_DURATION = 20;
    /** Damage dealt to the player */
    private final int attackDamage = 2;

    // Movement and attack sprites
    private BufferedImage attackUp1, attackUp2, attackDown1, attackDown2,
            attackLeft1, attackLeft2, attackRight1, attackRight2;

    public Orc(GamePanel gp) {
        super(gp, 20);
        setSpeed(2);
        setDirection("down");
        setScaleEntityX(gp.getTileSize());
        setScaleEntityY(gp.getTileSize());
        setCollisionArea(new Rectangle(8, 16, 32, 32));
        attackArea = new Rectangle(0, 0, gp.getTileSize(), gp.getTileSize());
        loadImages();
    }

    /** Loads walking and attacking images */
    private void loadImages() {
        try {
            setUp1(load("/data/monster/orc/orc_up_1"));
            setUp2(load("/data/monster/orc/orc_up_2"));
            setDown1(load("/data/monster/orc/orc_down_1"));
            setDown2(load("/data/monster/orc/orc_down_2"));
            setLeft1(load("/data/monster/orc/orc_left_1"));
            setLeft2(load("/data/monster/orc/orc_left_2"));
            setRight1(load("/data/monster/orc/orc_right_1"));
            setRight2(load("/data/monster/orc/orc_right_2"));

            attackUp1 = load("/data/monster/orc/orc_attack_up_1");
            attackUp2 = load("/data/monster/orc/orc_attack_up_2");
            attackDown1 = load("/data/monster/orc/orc_attack_down_1");
            attackDown2 = load("/data/monster/orc/orc_attack_down_2");
            attackLeft1 = load("/data/monster/orc/orc_attack_left_1");
            attackLeft2 = load("/data/monster/orc/orc_attack_left_2");
            attackRight1 = load("/data/monster/orc/orc_attack_right_1");
            attackRight2 = load("/data/monster/orc/orc_attack_right_2");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BufferedImage load(String path) throws IOException {
        return UtilityTool.scaleImage(
                ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(path + ".png"))),
                gp.getTileSize(), gp.getTileSize());
    }

    @Override
    public void update() {
        updateHealthBar();
        chasePlayer();
        handleAttack();
    }

    /** Moves towards the player using simple BFS path-finding */
    private void chasePlayer() {
        int dx = Math.abs(getWorldX() - gp.getPlayer().getWorldX());
        int dy = Math.abs(getWorldY() - gp.getPlayer().getWorldY());
        if (dx + dy > DETECTION_RANGE) return; // too far
        String dir = findDirectionToPlayer();
        if (dir != null) setDirection(dir);
        checkCollision();
        moveIfCollisionNotDetected();
        checkAndChangeSpriteAnimation();
    }

    /** Simple BFS to find next step direction to player */
    private String findDirectionToPlayer() {
        int tileSize = gp.getTileSize();
        int startCol = getWorldX() / tileSize;
        int startRow = getWorldY() / tileSize;
        int goalCol = gp.getPlayer().getWorldX() / tileSize;
        int goalRow = gp.getPlayer().getWorldY() / tileSize;
        int maxCol = gp.getMaxWorldCol();
        int maxRow = gp.getMaxWorldRow();
        boolean[][] visited = new boolean[maxCol][maxRow];
        Queue<Node> q = new LinkedList<>();
        q.add(new Node(startCol, startRow, null));
        visited[startCol][startRow] = true;
        Node goal = null;
        while (!q.isEmpty()) {
            Node n = q.poll();
            if (n.col == goalCol && n.row == goalRow) { goal = n; break; }
            exploreNeighbor(n.col + 1, n.row, n, visited, q);
            exploreNeighbor(n.col - 1, n.row, n, visited, q);
            exploreNeighbor(n.col, n.row + 1, n, visited, q);
            exploreNeighbor(n.col, n.row - 1, n, visited, q);
        }
        if (goal == null) return null;
        Node current = goal;
        while (current.parent != null && current.parent.parent != null) {
            current = current.parent;
        }
        if (current.col > startCol) return "right";
        if (current.col < startCol) return "left";
        if (current.row > startRow) return "down";
        if (current.row < startRow) return "up";
        return null;
    }

    /** Adds neighbor tile to BFS queue if it is walkable */
    private void exploreNeighbor(int col, int row, Node parent, boolean[][] visited, Queue<Node> q) {
        if (col < 0 || row < 0 || col >= gp.getMaxWorldCol() || row >= gp.getMaxWorldRow()) return;
        if (visited[col][row]) return;
        int tileNum = gp.getTileManager().getMapTileNumber()[col][row];
        if (gp.getTileManager().getTile()[tileNum].isCollision()) return;
        visited[col][row] = true;
        q.add(new Node(col, row, parent));
    }

    /** Node used for BFS */
    private static class Node {
        final int col, row; Node parent; Node(int c,int r,Node p){col=c;row=r;parent=p;}
    }

    /** Handles attack timing and damage */
    private void handleAttack() {
        if (attacking) {
            attackCounter++;
            if (attackCounter == 1) physicalAttack();
            if (attackCounter > ATTACK_DURATION) {
                attacking = false;
                attackCounter = 0;
                attackCooldown = ATTACK_COOLDOWN;
            }
        } else {
            if (attackCooldown > 0) attackCooldown--;
            Rectangle attackRect = getAttackRectangle();
            Rectangle playerRect = new Rectangle(
                gp.getPlayer().getWorldX() + gp.getPlayer().getCollisionArea().x,
                gp.getPlayer().getWorldY() + gp.getPlayer().getCollisionArea().y,
                gp.getPlayer().getCollisionArea().width,
                gp.getPlayer().getCollisionArea().height);
            if (attackCooldown == 0 && attackRect.intersects(playerRect)) {
                attacking = true;
            }
        }
    }

    /** Rectangle representing the area of attack based on direction */
    private Rectangle getAttackRectangle() {
        int attackX = getWorldX();
        int attackY = getWorldY();
        switch (getDirection()) {
            case "up" -> attackY -= attackArea.height;
            case "down" -> attackY += getScaleEntityY();
            case "left" -> attackX -= attackArea.width;
            case "right" -> attackX += getScaleEntityX();
        }
        return new Rectangle(attackX, attackY, attackArea.width, attackArea.height);
    }

    /** Applies damage to the player if within attack range */
    private void physicalAttack() {
        Rectangle attackRect = getAttackRectangle();
        Rectangle playerRect = new Rectangle(
            gp.getPlayer().getWorldX() + gp.getPlayer().getCollisionArea().x,
            gp.getPlayer().getWorldY() + gp.getPlayer().getCollisionArea().y,
            gp.getPlayer().getCollisionArea().width,
            gp.getPlayer().getCollisionArea().height);
        if (attackRect.intersects(playerRect)) {
            gp.getPlayer().atts().add(Attr.HEALTH, -attackDamage);
            gp.getUi().triggerDamageEffect();
        }
    }

    @Override
    public void draw(Graphics2D g2, GamePanel gp) {
        Point screenPos = CameraHelper.worldToScreen(getWorldX(), getWorldY(), gp);
        g2.drawImage(getDirectionImage(), screenPos.x, screenPos.y, null);
        if (attacking) {
            Rectangle attackRect = getAttackRectangle();
            Point attackScreen = CameraHelper.worldToScreen(attackRect.x, attackRect.y, gp);
            g2.setColor(Color.RED);
            g2.drawRect(attackScreen.x, attackScreen.y, attackRect.width, attackRect.height);
        }
        drawHealthBar(g2);
    }

    /** Chooses correct sprite based on direction and state */
    private BufferedImage getDirectionImage() {
        if (attacking) {
            return switch (getDirection()) {
                case "up" -> attackCounter < ATTACK_DURATION / 2 ? attackUp1 : attackUp2;
                case "down" -> attackCounter < ATTACK_DURATION / 2 ? attackDown1 : attackDown2;
                case "left" -> attackCounter < ATTACK_DURATION / 2 ? attackLeft1 : attackLeft2;
                case "right" -> attackCounter < ATTACK_DURATION / 2 ? attackRight1 : attackRight2;
                default -> attackDown1;
            };
        }
        return switch (getDirection()) {
            case "up" -> getSpriteNum() == 1 ? getUp1() : getUp2();
            case "down" -> getSpriteNum() == 1 ? getDown1() : getDown2();
            case "left" -> getSpriteNum() == 1 ? getLeft1() : getLeft2();
            case "right" -> getSpriteNum() == 1 ? getRight1() : getRight2();
            default -> getDown1();
        };
    }

    @Override
    public void draw(Graphics2D g2) { }
}
