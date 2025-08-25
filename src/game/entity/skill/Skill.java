package game.entity.skill;

import game.entity.Player;
import game.enums.SkillGrade;

/**
 * Lớp cơ sở cho mọi loại kỹ năng trong game.
 */
public abstract class Skill {
    private final String name;
    private final String description;
    private final SkillGrade grade;
    private int level;

    protected Skill(String name, String description, SkillGrade grade, int level) {
        this.name = name;
        this.description = description;
        this.grade = grade;
        this.level = level;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public SkillGrade getGrade() { return grade; }
    public int getLevel() { return level; }
    public void levelUp() { level++; }

    /**
     * Thực thi kỹ năng.
     */
    public abstract void use(Player p);
}