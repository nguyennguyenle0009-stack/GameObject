package game.enums;

// thuộc tính
public enum Attr {
	HEALTH("Health"),// máu
	ATTACK("Attack"),// tấn công
	DEF("Def"),// phòng thủ
	PEP("Pep"),// năng lượng
	SPIRIT("Spirit"),// tinh thần // Kinh nghiệm
	STRENGTH("Strength"),// sức mạnh
	SOULD("Sould"),// linh hồn
	PHYSIQUE("Physique"),// thể chất
	AFFINITY("Affinity");// Hệ
	
	private final String display;
	Attr(String d) { this.display = d; }
	public String displayerName() { return display; }
}
