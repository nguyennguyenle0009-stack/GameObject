package game.entity.nguyen;

import game.entity.Player;
import game.enums.Attr;

/**
 * Basic cultivation system with two major realms and Qi accumulation.
 */
public class Cultivation {
    public enum MajorRealm { LUYEN_KHI, TRUC_CO }

    private static final int SUB_LEVELS = 9;
    private final Player player;
    private MajorRealm realm = MajorRealm.LUYEN_KHI;
    private int subLevel = 1;
    private int currentQi = 0;
    private int requiredQi = 1000;

    public Cultivation(Player player) {
        this.player = player;
    }

    /** Gain qi and attempt breakthroughs when enough qi is accumulated. */
    public void gainQi(int amount) {
        currentQi += amount;
        while (currentQi >= requiredQi) {
            currentQi -= requiredQi;
            breakthrough();
        }
    }

    private void breakthrough() {
        int prevRequirement = requiredQi;
        subLevel++;

        // Increase attributes by 1.5%
        for (Attr attr : Attr.values()) {
            int val = player.atts().get(attr);
            val = (int) Math.ceil(val * 1.015);
            player.atts().set(attr, val);
        }
        // Increase movement speed by 0.1%
        player.setSpeed((int) Math.ceil(player.getSpeed() * 1.001));
        // After breakthrough energy equals qi requirement of previous stage
        player.atts().set(Attr.PEP, prevRequirement);
        // Next level requirement triples
        requiredQi = prevRequirement * 3;

        if (subLevel > SUB_LEVELS) {
            subLevel = 1;
            realm = nextRealm();
            // Major realm breakthrough: triple speed and add 0.2%
            player.setSpeed((int) Math.ceil(player.getSpeed() * 3 * 1.002));
        }
    }

    private MajorRealm nextRealm() {
        return switch (realm) {
            case LUYEN_KHI -> MajorRealm.TRUC_CO;
            case TRUC_CO -> MajorRealm.TRUC_CO; // highest realm for now
        };
    }

    public MajorRealm getRealm() { return realm; }
    public int getSubLevel() { return subLevel; }
    public int getCurrentQi() { return currentQi; }
    public int getRequiredQi() { return requiredQi; }
}

