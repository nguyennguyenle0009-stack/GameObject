package game.entity.monster;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

import game.enums.Attr;
import game.main.GamePanel;
import game.util.CameraHelper;

/**
 * Orc monster that chases the player and attacks when close.
 */
public class Orc extends Monster {
    // Movement images
    private BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;
    // Attack images
    private BufferedImage attackUp1, attackUp2, attackDown1, attackDown2,
            attackLeft1, attackLeft2, attackRight1, attackRight2;

    // Attack control
    private final Rectangle attackArea;
    private boolean attacking = false;
    private int attackCounter = 0;
    private int attackCooldown = 0;
    private static final int ATTACK_COOLDOWN = 60; // frames
    private static final int ATTACK_DURATION = 20; // frames
    private final int attackDamage = 2; // damage dealt to player

    // Detection range (pixels)
    private final int detectRange;

    public Orc(GamePanel gp) {
        super(gp, 20); // max health 20
        setSpeed(2);
        setDirection("down");
        setScaleEntityX(gp.getTileSize());
        setScaleEntityY(gp.getTileSize());
        setCollisionArea(new Rectangle(8, 16, 32, 32));
        attackArea = new Rectangle(0, 0, gp.getTileSize(), gp.getTileSize());
        detectRange = gp.getTileSize() * 8; // 8 tiles
        loadImages();
    }

    /** Load all movement and attack sprites. */
    private void loadImages() {
        up1 = setup("/data/monster/orc/orc_up_1");
        up2 = setup("/data/monster/orc/orc_up_2");
        down1 = setup("/data/monster/orc/orc_down_1");
        down2 = setup("/data/monster/orc/orc_down_2");
        left1 = setup("/data/monster/orc/orc_left_1");
        left2 = setup("/data/monster/orc/orc_left_2");
        right1 = setup("/data/monster/orc/orc_right_1");
        right2 = setup("/data/monster/orc/orc_right_2");
        attackUp1 = setup("/data/monster/orc/orc_attack_up_1");
        attackUp2 = setup("/data/monster/orc/orc_attack_up_2");
        attackDown1 = setup("/data/monster/orc/orc_attack_down_1");
        attackDown2 = setup("/data/monster/orc/orc_attack_down_2");
        attackLeft1 = setup("/data/monster/orc/orc_attack_left_1");
        attackLeft2 = setup("/data/monster/orc/orc_attack_left_2");
        attackRight1 = setup("/data/monster/orc/orc_attack_right_1");
        attackRight2 = setup("/data/monster/orc/orc_attack_right_2");
    }

    @Override
    public void update() {
        super.update();
        setAction();
        checkCollision();
        moveIfCollisionNotDetected();
        checkAndChangeSpriteAnimation();
        handleAttack();
    }

    /** Decide current action: chase player or wander randomly. */
    private void setAction() {
        int dx = gp.getPlayer().getWorldX() - getWorldX();
        int dy = gp.getPlayer().getWorldY() - getWorldY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        if (distance < detectRange) {
            String dir = findPathDirection();
            if (dir != null) {
                setDirection(dir);
                return;
            }
        }
        // random wandering when player far away
        setActionLockCounter(getActionLockCounter() + 1);
        if (getActionLockCounter() >= 120) {
            int i = random.nextInt(100) + 1;
            if (i <= 25) setDirection("up");
            else if (i <= 50) setDirection("down");
            else if (i <= 75) setDirection("left");
            else setDirection("right");
            setActionLockCounter(0);
        }
    }

    /** Handle attack timing and apply damage to player. */
    private void handleAttack() {
        if (attacking) {
            attackCounter++;
            if (attackCounter == 1) {
                physicalAttack();
            }
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
                    gp.getPlayer().getCollisionArea().height
            );
            if (attackCooldown == 0 && attackRect.intersects(playerRect)) {
                attacking = true;
            }
        }
    }

    /** Perform physical attack on the player. */
    private void physicalAttack() {
        Rectangle attackRect = getAttackRectangle();
        Rectangle playerRect = new Rectangle(
                gp.getPlayer().getWorldX() + gp.getPlayer().getCollisionArea().x,
                gp.getPlayer().getWorldY() + gp.getPlayer().getCollisionArea().y,
                gp.getPlayer().getCollisionArea().width,
                gp.getPlayer().getCollisionArea().height
        );
        if (attackRect.intersects(playerRect)) {
            gp.getPlayer().atts().add(Attr.HEALTH, -attackDamage);
            gp.getUi().triggerDamageEffect();
        }
    }

    /** Calculate the rectangle representing attack area. */
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

    /**
     * Find next direction towards the player using BFS pathfinding.
     * Returns null if no path is found.
     */
    private String findPathDirection() {
        int startCol = getWorldX() / gp.getTileSize();
        int startRow = getWorldY() / gp.getTileSize();
        int goalCol = gp.getPlayer().getWorldX() / gp.getTileSize();
        int goalRow = gp.getPlayer().getWorldY() / gp.getTileSize();

        Deque<int[]> queue = new ArrayDeque<>();
        queue.add(new int[] { startCol, startRow });
        Set<String> visited = new HashSet<>();
        visited.add(startCol + "," + startRow);
        int[][] parentX = new int[gp.getMaxWorldCol()][gp.getMaxWorldRow()];
        int[][] parentY = new int[gp.getMaxWorldCol()][gp.getMaxWorldRow()];
        boolean found = false;

        int[][] dirs = { {0,-1}, {0,1}, {-1,0}, {1,0} }; // up,down,left,right
        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            int c = cur[0];
            int r = cur[1];
            if (c == goalCol && r == goalRow) {
                found = true;
                break;
            }
            for (int[] d : dirs) {
                int nc = c + d[0];
                int nr = r + d[1];
                String key = nc + "," + nr;
                if (nc < 0 || nr < 0 || nc >= gp.getMaxWorldCol() || nr >= gp.getMaxWorldRow()) continue;
                if (visited.contains(key)) continue;
                if (!isPassable(nc, nr)) continue;
                queue.add(new int[] { nc, nr });
                visited.add(key);
                parentX[nc][nr] = c;
                parentY[nc][nr] = r;
            }
        }

        if (!found) return null;
        int c = goalCol;
        int r = goalRow;
        while (parentX[c][r] != startCol || parentY[c][r] != startRow) {
            int pc = parentX[c][r];
            int pr = parentY[c][r];
            c = pc;
            r = pr;
        }
        if (c > startCol) return "right";
        if (c < startCol) return "left";
        if (r > startRow) return "down";
        return "up";
    }

    /** Check if tile at (col,row) is walkable. */
    private boolean isPassable(int col, int row) {
        int tileNum = gp.getTileManager().getMapTileNumber()[col][row];
        return !gp.getTileManager().getTile()[tileNum].isCollision();
    }

    @Override
    public void draw(Graphics2D g2, GamePanel gp) {
        Point screenPos = CameraHelper.worldToScreen(getWorldX(), getWorldY(), gp);
        g2.drawImage(getCurrentImage(), screenPos.x, screenPos.y, null);
        if (attacking) {
            Rectangle attackRect = getAttackRectangle();
            Point attackScreen = CameraHelper.worldToScreen(attackRect.x, attackRect.y, gp);
            g2.setColor(Color.RED);
            g2.drawRect(attackScreen.x, attackScreen.y, attackRect.width, attackRect.height);
        }
        drawHealthBar(g2, gp);
    }

    /** Select proper sprite based on direction and attack state. */
    private BufferedImage getCurrentImage() {
        if (attacking) {
            return switch (getDirection()) {
                case "up" -> (getSpriteNum() == 1 ? attackUp1 : attackUp2);
                case "down" -> (getSpriteNum() == 1 ? attackDown1 : attackDown2);
                case "left" -> (getSpriteNum() == 1 ? attackLeft1 : attackLeft2);
                default -> (getSpriteNum() == 1 ? attackRight1 : attackRight2);
            };
        } else {
            return switch (getDirection()) {
                case "up" -> (getSpriteNum() == 1 ? up1 : up2);
                case "down" -> (getSpriteNum() == 1 ? down1 : down2);
                case "left" -> (getSpriteNum() == 1 ? left1 : left2);
                default -> (getSpriteNum() == 1 ? right1 : right2);
            };
        }
    }

    @Override
    protected void dropItem() {
        // 30% chance to give player a health potion directly
        if (random.nextInt(100) < 30) {
            gp.getPlayer().getBag().add(new game.entity.item.elixir.HealthPotion(30, 1));
        }
    }
}
