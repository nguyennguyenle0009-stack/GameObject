package game.enums;

public enum Attr {
    HEALTH("Health"),
    ATTACK("Attack"),
    DEF("Defense"),
    PEP("Mana"),
    SPIRIT("Spirit"),
    STRENGTH("Strength"),
    SOUL("Soul"),
    PHYSIQUE("Physique");

    private final String display;
    Attr(String display) { this.display = display; }
    public String displayName() { return display; }
}
