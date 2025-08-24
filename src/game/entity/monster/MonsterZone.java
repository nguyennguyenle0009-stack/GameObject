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

    public MonsterZone(GamePanel gp, Rectangle area,
                       Supplier<? extends Monster> factory,
                       int maxMonsters, int respawnTime) {
        this.gp = gp;
        this.area = area;
        this.factory = factory;
        this.maxMonsters = maxMonsters;
        this.respawnTime = respawnTime;
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
        int x = area.x + random.nextInt(Math.max(1, area.width - gp.getTileSize()));
        int y = area.y + random.nextInt(Math.max(1, area.height - gp.getTileSize()));
        m.setWorldX(x);
        m.setWorldY(y);
        m.setMovementArea(area);
        monsters.add(m);
        gp.getMonsters().add(m);
    }
}
