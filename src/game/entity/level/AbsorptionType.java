package game.entity.level;

// Mức độ thể chất hấp thụ quyết định giới hạn dùng đan dược
public enum AbsorptionType {
    NONE(10),           // Không có thể chất hấp thụ
    ABSORB(20),         // Thể chất hấp thụ
    ABSORB_ONE(30),     // Hấp thụ 1
    ABSORB_PERFECT(40); // Hấp thụ viên mãn

    // Giới hạn số viên đan dược có thể dùng mỗi đại cảnh giới
    private final int limit;

    AbsorptionType(int limit){
        this.limit = limit;
    }

    // Trả về giới hạn
    public int limit(){ return limit; }
}
