package game.entity.monster;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import game.entity.GameActor;
import game.enums.Attr;
import game.main.GamePanel;

/**
 * Lớp cơ sở cho tất cả quái vật trong game.
 * Cung cấp AI cơ bản, tấn công và thanh máu.
 */
public abstract class Monster extends GameActor {
    /** Tham chiếu đến GamePanel */
    protected final GamePanel gp;
    /** Vùng tấn công */
    protected Rectangle attackArea = new Rectangle();
    /** Cờ tấn công */
    protected boolean attacking = false;
    /** Bộ đếm khung hình khi tấn công */
    protected int attackCounter = 0;
    /** Thời gian hồi chiêu tấn công còn lại */
    protected int attackCooldown = 0;
    /** Sát thương gây cho người chơi */
    protected int attackDamage = 1;
    /** Phạm vi phát hiện người chơi (pixel) */
    protected int detectionRange = 0;
    /** Bộ đếm hiển thị thanh máu */
    protected int healthBarCounter = 0;
    /** Thời gian hiển thị thanh máu sau khi bị đánh */
    protected static final int HEALTH_BAR_TIME = 120;
    /** Máu tối đa dùng để vẽ thanh máu */
    protected int maxHealth = 1;
    /** Random dùng cho di chuyển và rơi vật phẩm */
    protected final Random random = new Random();
    /** Khu vực mà quái vật được phép di chuyển */
    protected Rectangle movementArea;

    /**
     * Tạo quái vật mới.
     *
     * @param gp tham chiếu game panel
     */
    public Monster(GamePanel gp) {
        super(gp);
        this.gp = gp;
        // Khởi tạo giá trị khung hình mặc định để quái vật có thể chuyển động
        // ngay lần đầu cập nhật. Nếu để giá trị mặc định (0), phương thức
        // checkAndChangeSpriteAnimation() sẽ không đổi spriteNum và khiến quái
        // vật không có hiệu ứng chuyển động.
        setSpriteNum(1);
        setSpriteCouter(0);
    }

    /**
     * Vòng lặp cập nhật chính của quái vật.
     */
    @Override
    public void update() {
        if (isPlayerInRange()) {
            chasePlayer();
        } else {
            wander();
        }
        handleAttack();
        if (healthBarCounter > 0) healthBarCounter--;
    }

    /**
     * Kiểm tra người chơi có trong phạm vi không.
     *
     * @return true nếu trong phạm vi
     */
    protected boolean isPlayerInRange() {
        if (movementArea != null &&
            !movementArea.contains(gp.getPlayer().getWorldX(), gp.getPlayer().getWorldY())) {
            return false;
        }
        int dx = gp.getPlayer().getWorldX() - getWorldX();
        int dy = gp.getPlayer().getWorldY() - getWorldY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance < detectionRange;
    }

    /**
     * Di chuyển ngẫu nhiên khi không phát hiện người chơi.
     */
    protected void wander() {
        setActionLockCounter(getActionLockCounter() + 1);
        if (getActionLockCounter() >= 120) {
            int i = random.nextInt(100);
            if (i < 25) setDirection("up");
            else if (i < 50) setDirection("down");
            else if (i < 75) setDirection("left");
            else setDirection("right");
            setActionLockCounter(0);
        }
        checkCollision();
        moveIfCollisionNotDetected();
        checkAndChangeSpriteAnimation();
    }

    /**
     * Đuổi theo người chơi sử dụng BFS đơn giản.
     */
    protected void chasePlayer() {
        List<Point> path = findPathToPlayer();
        if (!path.isEmpty()) {
            Point next = path.get(0);
            int nextWorldX = next.x * gp.getTileSize();
            int nextWorldY = next.y * gp.getTileSize();
            if (getWorldX() < nextWorldX) setDirection("right");
            else if (getWorldX() > nextWorldX) setDirection("left");
            else if (getWorldY() < nextWorldY) setDirection("down");
            else setDirection("up");
        }
        checkCollision();
        moveIfCollisionNotDetected();
        checkAndChangeSpriteAnimation();
    }

    /**
     * Tìm đường đến người chơi bằng BFS.
     *
     * @return danh sách điểm trên đường đi
     */
    protected List<Point> findPathToPlayer() {
        int startCol = getWorldX() / gp.getTileSize();
        int startRow = getWorldY() / gp.getTileSize();
        int goalCol = gp.getPlayer().getWorldX() / gp.getTileSize();
        int goalRow = gp.getPlayer().getWorldY() / gp.getTileSize();
        int cols = gp.getMaxWorldCol();
        int rows = gp.getMaxWorldRow();
        boolean[][] visited = new boolean[cols][rows];
        Point[][] parent = new Point[cols][rows];
        Queue<Point> queue = new ArrayDeque<>();
        queue.add(new Point(startCol, startRow));
        visited[startCol][startRow] = true;
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        while (!queue.isEmpty()) {
            Point p = queue.remove();
            if (p.x == goalCol && p.y == goalRow) break;
            for (int[] d : dirs) {
                int nx = p.x + d[0];
                int ny = p.y + d[1];
                if (nx < 0 || ny < 0 || nx >= cols || ny >= rows) continue;
                if (visited[nx][ny]) continue;
                int tileNum = gp.getTileManager().getMapTileNumber()[nx][ny];
                if (gp.getTileManager().getTile()[tileNum].isCollision()) continue;
                visited[nx][ny] = true;
                parent[nx][ny] = p;
                queue.add(new Point(nx, ny));
            }
        }
        List<Point> path = new ArrayList<>();
        if (!visited[goalCol][goalRow]) return path;
        Point step = new Point(goalCol, goalRow);
        while (parent[step.x][step.y] != null && !(step.x == startCol && step.y == startRow)) {
            path.add(0, step);
            step = parent[step.x][step.y];
        }
        return path;
    }

    /**
     * Xử lý tấn công và gây sát thương cho người chơi.
     */
    protected void handleAttack() {
        if (attacking) {
            attackCounter++;
            if (attackCounter == 1) {
                physicalAttack();
            }
            if (attackCounter > getAttackDuration()) {
                attacking = false;
                attackCounter = 0;
                attackCooldown = getAttackCooldown();
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

    /**
     * @return hình chữ nhật vùng tấn công hiện tại
     */
    protected Rectangle getAttackRectangle() {
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
     * Gây sát thương vật lý cho người chơi.
     */
    protected void physicalAttack() {
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

    /**
     * Vẽ thanh máu trên đầu quái vật.
     *
     * @param g2        context đồ họa
     * @param screenPos vị trí trên màn hình
     */
    protected void drawHealthBar(Graphics2D g2, Point screenPos) {
        if (healthBarCounter <= 0) return;
        int barWidth = getScaleEntityX();
        int barHeight = 5;
        int hp = atts().get(Attr.HEALTH);
        int width = (int) ((float) hp / maxHealth * barWidth);
        g2.setColor(Color.RED);
        g2.fillRect(screenPos.x, screenPos.y - 10, width, barHeight);
        g2.setColor(Color.GRAY);
        g2.drawRect(screenPos.x, screenPos.y - 10, barWidth, barHeight);
    }

    /**
     * Nhận sát thương.
     *
     * @param amount lượng sát thương
     * @return true nếu quái vật chết
     */
    public boolean takeDamage(int amount) {
        atts().add(Attr.HEALTH, -amount);
        healthBarCounter = HEALTH_BAR_TIME;
        return atts().get(Attr.HEALTH) <= 0;
    }

    /**
     * Rơi vật phẩm khi chết.
     */
    public void dropItem() {
        if (random.nextInt(100) < getDropChance()) {
            gp.getPlayer().getBag().add(new game.entity.item.elixir.HealthPotion(30, 1));
        }
    }

    /**
     * @return tỉ lệ rơi vật phẩm
     */
    protected int getDropChance() { return 30; }

    /**
     * @return thời gian hồi chiêu tấn công
     */
    protected int getAttackCooldown() { return 60; }

    /**
     * @return thời gian thực hiện 1 đòn tấn công
     */
    protected int getAttackDuration() { return 20; }

    /**
     * Thiết lập khu vực di chuyển giới hạn.
     *
     * @param area hình chữ nhật đại diện khu vực
     */
    public void setMovementArea(Rectangle area) { this.movementArea = area; }

    /**
     * Di chuyển nhưng không vượt ra ngoài khu vực cho phép.
     */
    @Override
    public void moveIfCollisionNotDetected() {
        if (!isCollisionOn()) {
            int nextX = getWorldX();
            int nextY = getWorldY();
            switch (getDirection()) {
                case "up" -> nextY -= getSpeed();
                case "down" -> nextY += getSpeed();
                case "left" -> nextX -= getSpeed();
                case "right" -> nextX += getSpeed();
            }
            if (movementArea == null ||
                (movementArea.contains(nextX, nextY) &&
                 movementArea.contains(nextX + getScaleEntityX() - 1,
                                       nextY + getScaleEntityY() - 1))) {
                setWorldX(nextX);
                setWorldY(nextY);
            }
        }
    }
}