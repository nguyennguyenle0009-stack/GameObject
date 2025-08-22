package game.entity.item;

public enum ItemAction {
    USE("Sử dụng"),
    DROP("Vứt bỏ"),
    WEAR("Mặc"),
    READ("Học"),
    PLACE("Sử dụng");

    private final String label;
    ItemAction(String label) { this.label = label; }
    public String label() { return label; }
}
