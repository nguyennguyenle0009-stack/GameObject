package game.entity.monster;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.*;

import game.entity.GameActor;
import game.entity.item.elixir.HealthPotion;
import game.enums.Attr;
import game.main.GamePanel;
import game.util.CameraHelper;

/**
 * Lớp cơ sở cho tất cả quái vật trong game.
 * Cung cấp sẵn các chức năng: đi lại, tìm đường, tấn công,
 * hiện thanh máu và rơi vật phẩm khi chết.
 */
public abstract class Monster extends GameActor {

    /** Đối tượng game chính để truy cập các thành phần khác */
    protected final GamePanel gp;
    /** Số ngẫu nhiên dùng cho nhiều chức năng (đi lang thang, rơi vật phẩm) */
    protected final Random random = new Random();

    /** Phạm vi hình chữ nhật dùng để kiểm tra tấn công */
    protected final Rectangle attackArea;
    /** Quái đang ở trạng thái tấn công hay không */
    protected boolean attacking = false;
    /** Bộ đếm thời gian cho hoạt ảnh tấn công */
    protected int attackCounter = 0;
    /** Bộ đếm hồi chiêu tấn công */
    protected int attackCooldown = 0;
    /** Thời gian hồi chiêu (tính bằng frame) */
    protected int ATTACK_COOLDOWN = 60;
    /** Thời gian thực hiện một đòn tấn công */
    protected int ATTACK_DURATION = 20;
    /** Sát thương gây ra cho người chơi */
    protected int attackDamage = 1;

    /** Quái có đang hiển thị thanh máu hay không */
    protected boolean showHealthBar = false;
    /** Bộ đếm thời gian hiện thanh máu */
    protected int healthBarCounter = 0;
    /** Máu tối đa của quái để tính tỉ lệ thanh máu */
    protected int maxHealth = 1;
    /** Thời gian hiển thị thanh máu sau khi bị đánh (frame) */
    protected static final int HEALTH_BAR_TIME = 120; // ~2 giây với 60 FPS

    /** Khoảng cách phát hiện người chơi (pixel) */
    protected int detectRange;
    /** Đường đi tới người chơi (danh sách các tile) */
    protected List<Point> path = new ArrayList<>();

    // Các ảnh tấn công theo bốn hướng
    protected BufferedImage attackUp1, attackUp2, attackDown1, attackDown2,
            attackLeft1, attackLeft2, attackRight1, attackRight2;

    /**
     * Khởi tạo quái cơ bản.
     * @param gp GamePanel để truy cập thông tin bản đồ, người chơi...
     */
    public Monster(GamePanel gp) {
        super(gp);
        this.gp = gp;
        setScaleEntityX(gp.getTileSize());
        setScaleEntityY(gp.getTileSize());
        attackArea = new Rectangle(0, 0, gp.getTileSize(), gp.getTileSize());
        detectRange = gp.getTileSize() * 6; // phát hiện trong bán kính 6 ô
    }

    /** Cài đặt các chỉ số ban đầu của quái (được viết bởi lớp con). */
    protected abstract void setDefaultValues();
    /** Nạp ảnh cho quái (được viết bởi lớp con). */
    protected abstract void loadImages();

    @Override
    public void update() {
        setAction();
        checkCollision();
        moveIfCollisionNotDetected();
        checkAndChangeSpriteAnimation();
        handleAttack();
        updateHealthBar();
    }

    /**
     * Xác định hành động mỗi frame.
     * Mặc định: nếu người chơi ở gần thì đuổi, ngược lại đi lang thang.
     */
    protected void setAction() {
        if (isPlayerWithinRange()) {
            followPlayer();
        } else {
            wander();
        }
    }

    /** Đi lang thang ngẫu nhiên giống slime. */
    protected void wander() {
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

    /** Kiểm tra người chơi có nằm trong phạm vi phát hiện hay không */
    protected boolean isPlayerWithinRange() {
        int dx = gp.getPlayer().getWorldX() - getWorldX();
        int dy = gp.getPlayer().getWorldY() - getWorldY();
        return Math.hypot(dx, dy) < detectRange;
    }

    /** Theo đường đi đã tìm để tới người chơi */
    protected void followPlayer() {
        if (path.isEmpty()) {
            path = bfsToPlayer();
        }
        if (!path.isEmpty()) {
            Point nextTile = path.get(0);
            int targetX = nextTile.x * gp.getTileSize();
            int targetY = nextTile.y * gp.getTileSize();
            if (getWorldX() == targetX && getWorldY() == targetY) {
                path.remove(0);
                if (path.isEmpty()) return;
                nextTile = path.get(0);
                targetX = nextTile.x * gp.getTileSize();
                targetY = nextTile.y * gp.getTileSize();
            }
            if (getWorldY() > targetY) setDirection("up");
            else if (getWorldY() < targetY) setDirection("down");
            else if (getWorldX() > targetX) setDirection("left");
            else if (getWorldX() < targetX) setDirection("right");
        }
    }

    /** Tìm đường đi ngắn nhất tới người chơi bằng BFS */
    protected List<Point> bfsToPlayer() {
        int cols = gp.getMaxWorldCol();
        int rows = gp.getMaxWorldRow();
        boolean[][] visited = new boolean[cols][rows];
        Point[][] parent = new Point[cols][rows];
        Queue<Point> queue = new ArrayDeque<>();

        int startCol = getWorldX() / gp.getTileSize();
        int startRow = getWorldY() / gp.getTileSize();
        int goalCol = gp.getPlayer().getWorldX() / gp.getTileSize();
        int goalRow = gp.getPlayer().getWorldY() / gp.getTileSize();

        queue.add(new Point(startCol, startRow));
        visited[startCol][startRow] = true;
        int[][] dirs = { {0,-1}, {0,1}, {-1,0}, {1,0} };
        while (!queue.isEmpty()) {
            Point p = queue.poll();
            if (p.x == goalCol && p.y == goalRow) break;
            for (int[] d : dirs) {
                int nc = p.x + d[0];
                int nr = p.y + d[1];
                if (nc < 0 || nr < 0 || nc >= cols || nr >= rows) continue;
                if (visited[nc][nr]) continue;
                int tileNum = gp.getTileManager().getMapTileNumber()[nc][nr];
                if (gp.getTileManager().getTile()[tileNum].isCollision()) continue;
                visited[nc][nr] = true;
                parent[nc][nr] = p;
                queue.add(new Point(nc, nr));
            }
        }
        List<Point> result = new ArrayList<>();
        Point cur = new Point(goalCol, goalRow);
        if (parent[cur.x][cur.y] == null) return result; // không tìm được đường
        while (!(cur.x == startCol && cur.y == startRow)) {
            result.add(0, cur);
            cur = parent[cur.x][cur.y];
        }
        return result;
    }

    /** Xử lý logic tấn công */
    protected void handleAttack() {
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

    /** Lấy hình chữ nhật tấn công dựa theo hướng */
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

    /** Thực sự gây sát thương lên người chơi nếu chạm */
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

    /** Cập nhật bộ đếm thanh máu */
    protected void updateHealthBar() {
        if (showHealthBar) {
            healthBarCounter++;
            if (healthBarCounter > HEALTH_BAR_TIME) {
                showHealthBar = false;
            }
        }
    }

    /**
     * Gọi khi quái bị sát thương.
     * @param damage lượng sát thương nhận
     * @return true nếu quái chết sau khi nhận sát thương
     */
    public boolean takeDamage(int damage) {
        atts().add(Attr.HEALTH, -damage);
        showHealthBar = true;
        healthBarCounter = 0;
        if (atts().get(Attr.HEALTH) <= 0) {
            dropItem();
            return true;
        }
        return false;
    }

    /**
     * Rơi vật phẩm – mặc định có 30% rơi bình máu vào túi người chơi.
     * Lớp con có thể override để rơi vật phẩm khác.
     */
    protected void dropItem() {
        if (random.nextInt(100) < 30) {
            gp.getPlayer().getBag().add(new HealthPotion(30, 1));
        }
    }

    @Override
    public void draw(Graphics2D g2, GamePanel gp) {
        Point screenPos = CameraHelper.worldToScreen(getWorldX(), getWorldY(), gp);
        g2.drawImage(getCurrentImage(), screenPos.x, screenPos.y, null);
        if (showHealthBar) {
            double ratio = (double) atts().get(Attr.HEALTH) / maxHealth;
            int barWidth = (int) (getScaleEntityX() * ratio);
            g2.setColor(Color.RED);
            g2.fillRect(screenPos.x, screenPos.y - 6, getScaleEntityX(), 4);
            g2.setColor(Color.GREEN);
            g2.fillRect(screenPos.x, screenPos.y - 6, barWidth, 4);
        }
        if (attacking) {
            Rectangle attackRect = getAttackRectangle();
            Point attackScreen = CameraHelper.worldToScreen(attackRect.x, attackRect.y, gp);
            g2.setColor(Color.RED);
            g2.drawRect(attackScreen.x, attackScreen.y, attackRect.width, attackRect.height);
        }
    }

    @Override
    public void draw(Graphics2D g2) { }

    /** Lấy ảnh tương ứng với trạng thái hiện tại */
    protected BufferedImage getCurrentImage() {
        if (attacking) {
            return getAttackImage();
        }
        return getMoveImage();
    }

    /** Ảnh dùng khi di chuyển */
    protected BufferedImage getMoveImage() {
        BufferedImage image = null;
        switch (getDirection()) {
            case "up" -> image = (getSpriteNum() == 1) ? getUp1() : getUp2();
            case "down" -> image = (getSpriteNum() == 1) ? getDown1() : getDown2();
            case "left" -> image = (getSpriteNum() == 1) ? getLeft1() : getLeft2();
            case "right" -> image = (getSpriteNum() == 1) ? getRight1() : getRight2();
        }
        return image;
    }

    /** Ảnh dùng khi tấn công */
    protected BufferedImage getAttackImage() {
        BufferedImage image = null;
        switch (getDirection()) {
            case "up" -> image = (attackCounter < ATTACK_DURATION / 2) ? attackUp1 : attackUp2;
            case "down" -> image = (attackCounter < ATTACK_DURATION / 2) ? attackDown1 : attackDown2;
            case "left" -> image = (attackCounter < ATTACK_DURATION / 2) ? attackLeft1 : attackLeft2;
            case "right" -> image = (attackCounter < ATTACK_DURATION / 2) ? attackRight1 : attackRight2;
        }
        return image;
    }
}
