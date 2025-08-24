package game.entity.monster;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import game.main.GamePanel;

/**
 * Khu vực sinh quái vật và quản lý hồi sinh.
 */
public class MonsterZone {
    private final GamePanel gp;
    private final Rectangle area;
    private final Supplier<? extends Monster> factory;
    private final int maxMonsters;
    private final int respawnTime; // tính theo frame
    private final List<Monster> monsters = new ArrayList<>();
    private final Random random = new Random();
    private int respawnCounter = 0;
    private final boolean detectInSpawnArea;
    private boolean canAttackMonsters;

    /**
     * Tạo một khu vực sinh quái.
     *
     * @param gp          tham chiếu game panel
     * @param startCol    cột bắt đầu (tile)
     * @param startRow    hàng bắt đầu (tile)
     * @param endCol      cột kết thúc (tile)
     * @param endRow      hàng kết thúc (tile)
     * @param factory     hàm tạo quái vật
     * @param maxMonsters số lượng quái tối đa
     * @param respawnMin  thời gian hồi sinh (phút)
     */
    public MonsterZone(GamePanel gp,
                       int startCol, int startRow,
                       int endCol, int endRow,
                       Supplier<? extends Monster> factory,
                       int maxMonsters,
                       int respawnMin,
                       boolean detectInSpawnArea,
                       boolean canAttackMonsters) {
        this.gp = gp;
        int tile = gp.getTileSize();
        int x = startCol * tile;
        int y = startRow * tile;
        int width = Math.max(1, (endCol - startCol + 1) * tile);
        int height = Math.max(1, (endRow - startRow + 1) * tile);
        this.area = new Rectangle(x, y, width, height);
        this.factory = factory;
        this.maxMonsters = maxMonsters;
        this.respawnTime = respawnMin * 60 * 60; // 60 FPS * 60s
        this.detectInSpawnArea = detectInSpawnArea;
        this.canAttackMonsters = canAttackMonsters;
        for (int i = 0; i < maxMonsters; i++) {
            spawn();
        }
    }

    /** Cập nhật bộ đếm và sinh quái nếu thiếu. */
    public void update() {
        monsters.removeIf(m -> !gp.getMonsters().contains(m));
        if (monsters.size() < maxMonsters) {
            respawnCounter++;
            if (respawnCounter >= respawnTime) {
                spawn();
                respawnCounter = 0;
            }
        } else {
            respawnCounter = 0;
        }
    }

    /** Sinh một quái vật trong khu vực. */
    private void spawn() {
        Monster m = factory.get();
        int x = area.x + random.nextInt(Math.max(1, area.width - gp.getTileSize() + 1));
        int y = area.y + random.nextInt(Math.max(1, area.height - gp.getTileSize() + 1));
        m.setWorldX(x);
        m.setWorldY(y);
        m.setMovementArea(area);
        m.setDetectInSpawnArea(detectInSpawnArea);
        m.setCanAttackMonsters(canAttackMonsters);
        monsters.add(m);
        gp.getMonsters().add(m);
    }

    /** Update whether monsters in this zone can attack each other. */
    public void setCanAttackMonsters(boolean value) {
        this.canAttackMonsters = value;
        for (Monster m : monsters) {
            m.setCanAttackMonsters(value);
        }
    }
}