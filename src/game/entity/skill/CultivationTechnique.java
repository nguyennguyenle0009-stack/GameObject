package game.entity.skill;

import game.entity.Player;
import game.enums.SkillGrade;

/**
 * Công pháp tu luyện, cho phép người chơi tu luyện để nhận SPIRIT theo thời gian.
 */
public class CultivationTechnique extends Skill {
    private final int durationMinutes;
    private final int spiritPerSecond;

    public CultivationTechnique(String name, SkillGrade grade, int level,
                                int durationMinutes, int spiritPerSecond) {
        super(name, "Công pháp tu luyện", grade, level);
        this.durationMinutes = durationMinutes;
        this.spiritPerSecond = spiritPerSecond;
    }

    public int getSpiritPerSecond() { return spiritPerSecond; }
    public long getDurationMillis() { return durationMinutes * 60L * 1000L; }

    @Override
    public void use(Player p) {
        p.startCultivating(this);
    }
}