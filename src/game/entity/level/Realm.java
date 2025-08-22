package game.entity.level;

// Các đại cảnh giới chính của nhân vật
public enum Realm {
    BODY("Nhục thể cảnh",1,1.2,1.2,2,0.10), // Nhục thể cảnh
    QI("Luyện khí kỳ",2,1.2,1.2,2,0.10),     // Luyện khí kỳ
    FOUNDATION("Trúc cơ",3,1.5,1.2,1.5,0.20); // Trúc cơ

    // Tên hiển thị của cảnh giới
    private final String displayName;
    // Hệ số nhân khi đột phá vào cảnh giới
    private final int breakthroughMultiplier;
    // Hệ số máu tăng mỗi tiểu cảnh giới
    private final double hpMultiplier;
    // Hệ số tấn công tăng mỗi tiểu cảnh giới
    private final double atkMultiplier;
    // Hệ số phòng thủ tăng mỗi tiểu cảnh giới
    private final double defMultiplier;
    // Phần trăm cộng thêm cho các thuộc tính còn lại
    private final double otherPercent;

    Realm(String name,int mul,double hp,double atk,double def,double other){
        this.displayName = name;
        this.breakthroughMultiplier = mul;
        this.hpMultiplier = hp;
        this.atkMultiplier = atk;
        this.defMultiplier = def;
        this.otherPercent = other;
    }

    // Trả về tên hiển thị
    public String display(){ return displayName; }
    // Trả về hệ số nhân khi đột phá
    public int breakthroughMultiplier(){ return breakthroughMultiplier; }
    // Trả về hệ số máu tăng mỗi tiểu cảnh giới
    public double hpMultiplier(){ return hpMultiplier; }
    // Trả về hệ số tấn công tăng mỗi tiểu cảnh giới
    public double atkMultiplier(){ return atkMultiplier; }
    // Trả về hệ số phòng thủ tăng mỗi tiểu cảnh giới
    public double defMultiplier(){ return defMultiplier; }
    // Trả về phần trăm cộng thêm cho các thuộc tính khác
    public double otherPercent(){ return otherPercent; }
}
