package game.entity.LV;

import game.enums.MajorRealm;

public final class CultivationStage {
    public final MajorRealm realm;
    public final int stage; // 1..realm.stages

    public CultivationStage(MajorRealm realm, int stage) {
        this.realm = realm;
        this.stage = Math.max(1, Math.min(stage, realm.stages));
    }

    public boolean isMaxStageInRealm() {
        return stage >= realm.stages;
    }

    /** Lên tiểu cảnh giới (trong cùng đại cảnh giới) */
    public CultivationStage nextStageInRealm() {
        int s = Math.min(stage + 1, realm.stages);
        return new CultivationStage(realm, s);
    }

    /** Đột phá sang đại cảnh giới tiếp theo, reset tiểu cảnh giới = 1 */
    public CultivationStage breakthroughTo(MajorRealm nextRealm) {
        return new CultivationStage(nextRealm, 1);
    }
}