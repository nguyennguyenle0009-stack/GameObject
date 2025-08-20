package game.enums;

public enum MajorRealm {
 LUYEN_KHI("Luyện khí", 9, 100),   // 9 tiểu cảnh giới, SpiritMax mặc định 100
 TRUC_CO  ("Trúc cơ",   9, 200),
 KIM_DAN  ("Kim đan",   9, 400),
 NGUYEN_ANH("Nguyên anh", 9, 800);
 // thêm tiếp...

 public final String display;
 public final int stages;       // số tiểu cảnh giới
 public final int spiritMax;    // dung lượng SPIRIT mặc định cho đại cảnh giới
 MajorRealm(String display, int stages, int spiritMax) {
     this.display = display;
     this.stages = stages;
     this.spiritMax = spiritMax;
 }
}
