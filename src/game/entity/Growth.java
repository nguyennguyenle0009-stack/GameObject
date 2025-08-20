package game.entity;

public record Growth(int base, int perLv) {
    public int at(int level) {
        int lv = Math.max(1, level);
        return base + perLv * (lv - 1);
    }
}
