package game.skill;

import game.entity.Player;

/**
 * Represents a skill that a player can learn and use.
 */
public interface Skill {
    /**
     * @return name of the skill
     */
    String getName();

    /**
     * Apply the skill effect to the player.
     * @param player player receiving the effect
     */
    void apply(Player player);
}

