package game.entity.skill;

import game.entity.Player;
import game.enums.Affinity;

/**
 * Lớp cơ sở cho tất cả các loại kỹ năng trong game.
 * Các kỹ năng cụ thể (tấn công, tu luyện, luyện đan, ...)
 * sẽ kế thừa lớp này và triển khai hành vi riêng.
 */
public abstract class Skill {

    /** Loại kỹ năng để tiện quản lý. */
    public enum Type {
        ATTACK, CULTIVATION, ALCHEMY, FORGE_WEAPON, LOGGING, FARMING, MINING
    }

    private final String name;
    private final String description;
    private final Type type;
    private final Affinity element; // hệ/ngũ hành của kỹ năng

    protected Skill(String name, String description, Type type, Affinity element) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.element = element;
    }

    /**
     * Thực thi kỹ năng trên người chơi.
     */
    public abstract void activate(Player p);

    public String getName() { return name; }
    public String getDescription() { return description; }
    public Type getType() { return type; }
    public Affinity getElement() { return element; }
}