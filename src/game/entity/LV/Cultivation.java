package game.entity.LV;

import java.util.EnumMap;
import java.util.Map;

import game.enums.MajorRealm;

public class Cultivation {
    private CultivationStage current;
    private int spirit;     // thanh SPIRIT hiện tại
    private int pep;        // thanh PEP hiện tại (mana)
    private long combatXp;  // tích lũy sát thương gây ra ở TIỂU cảnh giới hiện tại

    // Công pháp đã học theo từng đại cảnh giới
    private final Map<MajorRealm, Boolean> learnedTechnique = new EnumMap<>(MajorRealm.class);

    public Cultivation(MajorRealm startRealm, int startStage) {
        this.current = new CultivationStage(startRealm, startStage);
        this.spirit = 0;
        this.pep = 0;
        this.combatXp = 0;
    }

    // --------- Getters ----------
    public CultivationStage stage() { return current; }
    public int spirit() { return spirit; }
    public int pep() { return pep; }
    public long combatXp() { return combatXp; }
    public int spiritMax() { return CultivationConfig.spiritMaxOf(current.realm); }
    public int combatXpRequired() { return CultivationConfig.requiredCombatXp(current.realm, current.stage); }
    public boolean hasTechniqueForCurrentRealm() { return learnedTechnique.getOrDefault(current.realm, false); }

    // --------- Actions ----------
    /** Học công pháp (dùng sách); mới học sẽ cho phép tu luyện/lên cấp ở đại cảnh giới đó */
    public void learnTechnique(MajorRealm realm) {
        learnedTechnique.put(realm, true);
    }

    /** Thêm SPIRIT (do thiền/tu luyện). Không thể vượt quá spiritMax. Cần có công pháp để cho phép tu luyện. */
    public void gainSpirit(int amount) {
        if (amount <= 0) return;
        if (!hasTechniqueForCurrentRealm()) return; // chưa có công pháp, không tu được
        this.spirit = Math.min(spirit + amount, spiritMax());
    }

    /** Khi gây sát thương trong combat, cộng Combat XP bằng đúng damage (>=0) */
    public void gainCombatXpFromDamage(int damage) {
        if (damage > 0) this.combatXp += damage;
    }

    /** Lên TIỂU cảnh giới (trong cùng đại cảnh giới) nếu đủ Combat XP và có công pháp */
    public boolean levelUpStageIfPossible() {
        if (!hasTechniqueForCurrentRealm()) return false;
        if (combatXp < combatXpRequired()) return false;
        if (current.isMaxStageInRealm()) return false; // đã tối đa trong đại cảnh giới

        current = current.nextStageInRealm();
        combatXp = 0; // reset kinh nghiệm chiến đấu khi lên cấp
        return true;
    }

    /** Có thể ĐỘT PHÁ sang đại cảnh giới kế tiếp? Cần: SPIRIT đầy + có công pháp */
    public boolean canBreakthrough(MajorRealm nextRealm) {
        return hasTechniqueForCurrentRealm()
            && spirit >= spiritMax()
            && nextRealm != null;
    }

    /** Thực hiện đột phá: chuyển toàn bộ SPIRIT -> PEP, reset SPIRIT và Combat XP, sang đại cảnh giới mới. */
    public boolean breakthroughTo(MajorRealm nextRealm) {
        if (!canBreakthrough(nextRealm)) return false;

        int gainedPep = CultivationConfig.spiritToPepOnBreakthrough(spirit);
        this.pep += gainedPep;  // cộng PEP
        this.spirit = 0;        // reset SPIRIT
        this.combatXp = 0;      // reset Combat XP khi lên đại cảnh giới mới

        this.current = current.breakthroughTo(nextRealm);

        // lưu ý: phải học công pháp cho đại cảnh giới mới mới tu tiếp được
        return true;
    }

    /** (Tùy chọn) tiêu hao PEP để dùng kỹ năng */
    public boolean consumePep(int amount) {
        if (amount <= 0) return true;
        if (pep < amount) return false;
        pep -= amount;
        return true;
    }
}