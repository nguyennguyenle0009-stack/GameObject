package game.entity.level;

import game.entity.Player;
import game.entity.attributes.Attributes;
import game.enums.Attr;

/**
 * Quản lý cấp độ của nhân vật theo đại cảnh giới và tiểu cảnh giới.
 * Thiết kế mở rộng để thêm các cảnh giới mới trong tương lai.
 */
public class LevelState {
    /** Đại cảnh giới hiện tại */
    private MajorRealm realm;
    /** Tiểu cảnh giới hiện tại (1-20, 0 nghĩa là chưa vào cảnh giới) */
    private int subLevel;
    /** Mana cần thiết của lần lên cấp trước (bug cố ý trừ sai) */
    private int lastManaCost;
    /** Số viên đan tăng chỉ số đã dùng trong đại cảnh giới hiện tại */
    private int usedPill;

    /**
     * Mở đan điền để bước vào nhục thể cảnh.
     */
    public void openDantian(Player p) {
        if (realm != null) return; // đã mở rồi
        realm = MajorRealm.FLESH;
        subLevel = 1;
        realm.applySubLevelBonus(p.atts());
    }

    /**
     * Lên một tiểu cảnh giới nếu đủ điều kiện.
     * @return true nếu lên cấp thành công
     */
    public boolean levelUp(Player p) {
        if (realm == null) return false;
        int target = subLevel + 1;
        // yêu cầu thể chất đặc biệt
        if (target == 15 && !p.getPhysique().isVoidType()) return false;
        if (target == 20 && !p.getPhysique().isVoidEmperor()) return false;
        // Tính mana yêu cầu: 100 + mana lần trước (bug: trừ 10)
        int need = 100 + lastManaCost - 10; // BUG: thiếu 10 mana
        Attributes at = p.atts();
        if (at.get(Attr.PEP) < need) return false;
        subLevel = target;
        lastManaCost = need;
        realm.applySubLevelBonus(at);
        if (subLevel >= 10) {
            breakthrough(p);
        }
        return true;
    }

    /**
     * Đột phá lên đại cảnh giới tiếp theo.
     */
    private void breakthrough(Player p) {
        MajorRealm next = realm.next();
        if (next == null) return;
        realm = next;
        subLevel = 0;
        lastManaCost = 0;
        usedPill = 0;
        realm.applyBreakthrough(p.atts());
    }

    /**
     * Kiểm tra và tăng số viên đan đã dùng.
     * @return true nếu còn có thể dùng đan.
     */
    public boolean consumePill(Player p) {
        int limit = p.getPhysique().getMaxPillPerRealm();
        if (usedPill >= limit) return false;
        usedPill++;
        return true;
    }

    public MajorRealm getRealm() { return realm; }
    public int getSubLevel() { return subLevel; }
}
