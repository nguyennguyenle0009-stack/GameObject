package game.skill;

import game.entity.Player;

/**
 * Simple movement skill that increases player speed when applied.
 */
public class DashSkill implements Skill {

    @Override
    public String getName() {
        return "Dash";
    }

    @Override
    public void apply(Player player) {
        // Permanent small speed boost
        player.setSpeed(player.getSpeed() + 1);
    }
}

