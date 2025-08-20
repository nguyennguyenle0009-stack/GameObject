package game.entity.LV;

import game.enums.MajorRealm;

public final class CultivationConfig {
    private CultivationConfig() {}

    // Combat XP cần cho từng tiểu cảnh giới trong một đại cảnh giới
    public static int requiredCombatXp(MajorRealm realm, int stage) {
        // ví dụ: realm càng cao, yêu cầu càng lớn
        int base = switch (realm) {
            case LUYEN_KHI -> 100;
            case TRUC_CO   -> 200;
            case KIM_DAN   -> 400;
            case NGUYEN_ANH-> 800;
        };
        int perStage = base / 2;
        return base + perStage * (Math.max(1, stage) - 1);
    }

    // Nếu bạn muốn mỗi đại cảnh giới có SpiritMax riêng, đã có trong enum
    public static int spiritMaxOf(MajorRealm realm) {
        return realm.spiritMax;
    }

    // Chuyển hoá: khi đột phá, toàn bộ SPIRIT sang PEP (tỉ lệ 1:1)
    public static int spiritToPepOnBreakthrough(int spirit) {
        return spirit; // 1 spirit = 1 pep
    }
}