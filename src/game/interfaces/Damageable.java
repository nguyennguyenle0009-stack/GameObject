package game.interfaces;

/**
 * Interface for objects that can receive damage.
 */
public interface Damageable {
    /**
     * Apply damage to the object.
     *
     * @param amount amount of damage to subtract from health
     */
    void takeDamage(int amount);
}
