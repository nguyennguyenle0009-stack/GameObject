package game.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Các linh căn (Affinity) của nhân vật.
 */
public enum Affinity {
    FIRE("Hỏa"),
    WOOD("Mộc"),
    WATER("Thủy"),
    METAL("Kim"),
    EARTH("Thổ"),
    THUNDER("Lôi");

    private final String displayName;

    Affinity(String displayName) {
        this.displayName = displayName;
    }

    /**
     * @return tên hiển thị của linh căn
     */
    public String getDisplayName() {
        return displayName;
    }

    private static final int[] WEIGHTS = {20,20,20,20,20,5};

    /**
     * Random danh sách linh căn theo tỉ lệ quy định.
     * Người chơi có ít nhất 1 linh căn, 15% có 2, 10% có 3.
     */
    public static List<Affinity> randomAffinities() {
        Random r = new Random();
        int roll = r.nextInt(100);
        int count = 1;
        if (roll < 10) count = 3;
        else if (roll < 25) count = 2;
        List<Affinity> pool = new ArrayList<>(Arrays.asList(values()));
        List<Affinity> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Affinity a = weightedRandom(pool, r);
            result.add(a);
            pool.remove(a); // không trùng lặp
        }
        return result;
    }

    private static Affinity weightedRandom(List<Affinity> pool, Random r) {
        int total = pool.stream().mapToInt(a -> WEIGHTS[a.ordinal()]).sum();
        int n = r.nextInt(total);
        int acc = 0;
        for (Affinity a : pool) {
            acc += WEIGHTS[a.ordinal()];
            if (n < acc) return a;
        }
        return pool.get(0);
    }

    /**
     * Ghép nhiều linh căn thành chuỗi hiển thị.
     */
    public static String joinDisplay(List<Affinity> list) {
        return list.stream().map(Affinity::getDisplayName).collect(Collectors.joining(", "));
    }
}
