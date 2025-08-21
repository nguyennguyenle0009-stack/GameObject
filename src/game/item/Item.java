package game.item;

import game.entity.nguyen.Attributes;

public class Item {
    private final String name;
    private final Attributes bonus;

    public Item(String name) {
        this(name, new Attributes());
    }

    public Item(String name, Attributes bonus) {
        this.name = name;
        this.bonus = bonus;
    }

    public String getName() { return name; }
    public Attributes bonus() { return bonus; }
}

