package game.entity.item;

import java.util.EnumMap;
import java.util.Map;

import game.enums.Attr;
import game.enums.EquipType;

/**
 * Immutable definition of an equipment template stored in the database.
 * Each roll from a template can produce an {@link EquipmentItem} with
 * randomized bonuses.
 */
public class ItemTemplate {
    private final String id;
    private final String name;
    private final EquipType type;
    private final EnumMap<Attr, Integer> bonuses = new EnumMap<>(Attr.class);

    public ItemTemplate(String id, String name, EquipType type, Map<Attr, Integer> bonusMap) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.bonuses.putAll(bonusMap);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public EquipType getType() { return type; }
    public Map<Attr, Integer> getBonuses() { return Map.copyOf(bonuses); }
}
