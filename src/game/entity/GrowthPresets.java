package game.entity;

import game.enums.Attr;

public final class GrowthPresets {
    private GrowthPresets() {}

    // Ví dụ 2 archetype cho Player
    public static final StatGrowth WARRIOR = new StatGrowth()
        .set(Attr.HEALTH,   60,  8)
        .set(Attr.ATTACK,   10,  2)
        .set(Attr.DEF,      5,   1)
        .set(Attr.PEP,      10,  1)
        .set(Attr.SPIRIT,   2,   1)
        .set(Attr.STRENGTH, 6,   2)
        .set(Attr.SOUL,     1,   1)
        .set(Attr.PHYSIQUE, 4,   2);

    public static final StatGrowth MAGE = new StatGrowth()
        .set(Attr.HEALTH,   45,  6)
        .set(Attr.ATTACK,   5,   1)
        .set(Attr.DEF,      3,   1)
        .set(Attr.PEP,      30,  5)
        .set(Attr.SPIRIT,   8,   3)
        .set(Attr.STRENGTH, 2,   1)
        .set(Attr.SOUL,     5,   2)
        .set(Attr.PHYSIQUE, 3,   1);

    // Ví dụ cho Monster
    public static final StatGrowth SLIME = new StatGrowth()
        .set(Attr.HEALTH,  18, 4)
        .set(Attr.ATTACK,   4, 1)
        .set(Attr.DEF,      1, 1);

    public static final StatGrowth GOBLIN = new StatGrowth()
        .set(Attr.HEALTH,  35, 7)
        .set(Attr.ATTACK,   7, 2)
        .set(Attr.DEF,      2, 1);
}