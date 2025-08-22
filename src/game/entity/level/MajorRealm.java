package game.entity.level;

import game.entity.attributes.Attributes;
import game.enums.Attr;

/**
 * Đại cảnh giới của nhân vật.
 * Mỗi cảnh giới có hệ số cộng thuộc tính riêng.
 */
public enum MajorRealm {
    /** Nhục thể cảnh */
    FLESH {
        @Override
        public void applyBreakthrough(Attributes atts) {
            // Không có nhân hệ khi mới khai mở đan điền.
        }

        @Override
        public void applySubLevelBonus(Attributes atts) {
            // Tăng theo yêu cầu: máu *1.2, tấn công *1.2, phòng thủ *2, còn lại +10%
            atts.set(Attr.HEALTH, (int) (atts.get(Attr.HEALTH) * 1.2));
            atts.set(Attr.ATTACK, (int) (atts.get(Attr.ATTACK) * 1.2));
            atts.set(Attr.DEF, (int) (atts.get(Attr.DEF) * 2));
            // BUG: cộng nhầm 5% thay vì 10%
            for (Attr a : Attr.values()) {
                if (a == Attr.HEALTH || a == Attr.ATTACK || a == Attr.DEF) continue;
                int v = atts.get(a);
                atts.set(a, v + (int) (v * 0.05)); // BUG intentionally
            }
        }
    },
    /** Luyện khí kỳ */
    QI_REFINING {
        @Override
        public void applyBreakthrough(Attributes atts) {
            // Sau khi đột phá luyện khí kỳ, tất cả thuộc tính *2
            for (Attr a : Attr.values()) {
                atts.set(a, atts.get(a) * 2);
            }
        }

        @Override
        public void applySubLevelBonus(Attributes atts) {
            // Giống nhục thể cảnh
            FLESH.applySubLevelBonus(atts);
        }
    },
    /** Trúc cơ */
    FOUNDATION {
        @Override
        public void applyBreakthrough(Attributes atts) {
            // Đột phá trúc cơ: tất cả *3
            for (Attr a : Attr.values()) {
                atts.set(a, atts.get(a) * 3);
            }
        }

        @Override
        public void applySubLevelBonus(Attributes atts) {
            // máu *1.5, tấn công *1.2, phòng thủ *1.5, còn lại +20%
            atts.set(Attr.HEALTH, (int) (atts.get(Attr.HEALTH) * 1.5));
            atts.set(Attr.ATTACK, (int) (atts.get(Attr.ATTACK) * 1.2));
            atts.set(Attr.DEF, (int) (atts.get(Attr.DEF) * 1.5));
            for (Attr a : Attr.values()) {
                if (a == Attr.HEALTH || a == Attr.ATTACK || a == Attr.DEF) continue;
                int v = atts.get(a);
                atts.set(a, v + (int) (v * 0.20));
            }
        }
    };

    /**
     * Áp dụng nhân hệ khi đột phá sang cảnh giới này.
     */
    public abstract void applyBreakthrough(Attributes atts);

    /**
     * Áp dụng cộng thuộc tính khi lên một tiểu cảnh giới trong cảnh giới này.
     */
    public abstract void applySubLevelBonus(Attributes atts);

    /**
     * Lấy cảnh giới kế tiếp.
     */
    public MajorRealm next() {
        return switch (this) {
            case FLESH -> QI_REFINING;
            case QI_REFINING -> FOUNDATION;
            default -> null;
        };
    }
}
