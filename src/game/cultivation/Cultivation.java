package game.cultivation;

import game.entity.Player;
import game.skill.Skill;
import java.util.List;

public class Cultivation {
    private Realm realm = Realm.LUYEN_KHI;
    private int stage = 1;
    private double currentQi = 0;
    private double neededQi = 1000;
    private double breakthroughChance = 0.0;
    private final Player player;
    private static final int MAX_STAGE = 10;

    public Cultivation(Player player) {
        this.player = player;
    }

    public void absorbQi(double amount) {
        currentQi += amount;
        while (currentQi >= neededQi) {
            currentQi -= neededQi;
            stage++;
            applyStageBonus();
            neededQi *= 2;
            if (stage > MAX_STAGE) {
                breakthroughRealm();
            }
        }
    }

    private void applyStageBonus() {
        player.setSpeed((int)Math.round(player.getSpeed() * 1.015));
        breakthroughChance += 0.001; // 0.1%
    }

    private void breakthroughRealm() {
        realm = realm.next();
        stage = 1;
        player.setSpeed((int)Math.round(player.getSpeed() * 3.0));
        player.setSpeed((int)Math.round(player.getSpeed() * 1.002));
    }

    public boolean learnSkill(Player p, Skill skill) {
        if (stage >= skill.getRequiredStage()) {
            List<Skill> list = p.getSkills();
            if (!list.contains(skill)) {
                list.add(skill);
            }
            return true;
        }
        return false;
    }

    public Realm getRealm() { return realm; }
    public int getStage() { return stage; }
    public double getCurrentQi() { return currentQi; }
    public double getNeededQi() { return neededQi; }
    public double getBreakthroughChance() { return breakthroughChance; }
}
