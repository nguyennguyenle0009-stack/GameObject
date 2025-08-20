package game.entity;

import game.interfaces.creature.HasAttributes;
import game.main.GamePanel;

public abstract class GameActor extends Entity implements HasAttributes {
    private final Attributes attrs = new Attributes();
    private StatGrowth growth; // bảng tăng trưởng áp cho actor
    private int level = 1;

    public GameActor(GamePanel gp, StatGrowth growth, int initialLevel) {
        super(gp);
        this.growth = (growth != null) ? growth : new StatGrowth();
        setLevel(initialLevel);
    }

    @Override
    public Attributes atts() {
        return attrs;
    }

    public int level() { return level; }

    public void setLevel(int newLevel) {
        this.level = Math.max(1, newLevel);
        // mỗi lần đổi level: ghi đè lại stats base từ growth
        this.growth.applyTo(attrs, this.level);
        // Nếu bạn có MaxHP/HP hiện tại, nhớ cập nhật ở đây (tuỳ hệ thống)
    }

    public void setGrowth(StatGrowth g) {
        this.growth = (g != null) ? g : new StatGrowth();
        this.growth.applyTo(attrs, this.level);
    }

    public StatGrowth growth() { return growth; }
}