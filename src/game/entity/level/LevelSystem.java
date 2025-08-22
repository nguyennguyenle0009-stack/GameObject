package game.entity.level;

import java.util.EnumMap;

import game.entity.Player;
import game.enums.Attr;

// Quản lý hệ thống cảnh giới và cấp độ của nhân vật
public class LevelSystem {
    // Chủ sở hữu
    private final Player owner;
    // Đại cảnh giới hiện tại
    private Realm realm = null;
    // Tiểu cảnh giới hiện tại
    private int subLevel = 0;
    // Đã khai mở đan điền hay chưa
    private boolean dantianOpened = false;
    // Thể chất sau khi mở đan điền
    private Physique physique = Physique.NORMAL;
    // Mức độ hấp thụ để tính giới hạn dùng đan
    private AbsorptionType absorption = AbsorptionType.NONE;
    // Lượng mana tích lũy
    private int mana = 0;
    // Số lượng đan dược đã dùng theo từng đại cảnh giới
    private final EnumMap<Realm,Integer> usedPills = new EnumMap<>(Realm.class);

    // Khởi tạo với chủ sở hữu
    public LevelSystem(Player p){ this.owner = p; }

    // Khai mở đan điền và bước vào nhục thể cảnh tầng 1
    public void openDantian(){
        dantianOpened = true;
        realm = Realm.BODY;
        subLevel = 1;
        applyBreakthrough(realm);
        // random thể chất
        physique = Physique.values()[(int)(Math.random()*Physique.values().length)];
    }

    // Nhận thêm mana
    public void gainMana(int amount){ mana += amount; }

    // Cố gắng tăng tiểu cảnh giới
    public void levelUp(){
        if(!dantianOpened) return;
        int cost = 100 * subLevel;
        if(mana < cost) return;
        if(subLevel >= 20) return;
        if(subLevel == 14 && physique != Physique.VOID) return;
        if(subLevel == 19 && physique != Physique.VOID_EMPEROR) return;
        mana -= cost;
        subLevel++;
        applySubLevelBonus();
        if(subLevel > 10 && realm != Realm.FOUNDATION){ // BUG: lẽ ra >= 10
            breakthrough();
        }
    }

    // Áp dụng hệ số khi đột phá đại cảnh giới
    private void applyBreakthrough(Realm r){
        for(Attr a : owner.atts().view().keySet()){
            int val = owner.atts().get(a);
            val *= r.breakthroughMultiplier();
            owner.atts().set(a, val);
        }
    }

    // Áp dụng tăng trưởng thuộc tính mỗi tiểu cảnh giới
    private void applySubLevelBonus(){
        Realm r = realm;
        owner.atts().set(Attr.HEALTH, (int)(owner.atts().get(Attr.HEALTH) * r.hpMultiplier()));
        owner.atts().set(Attr.ATTACK, (int)(owner.atts().get(Attr.ATTACK) * r.atkMultiplier()));
        owner.atts().set(Attr.DEF, (int)(owner.atts().get(Attr.DEF) * r.defMultiplier()));
        for(Attr a : Attr.values()){
            if(a != Attr.HEALTH && a != Attr.ATTACK && a != Attr.DEF){
                int val = owner.atts().get(a);
                val += (int)(val * r.otherPercent());
                owner.atts().set(a, val);
            }
        }
    }

    // Đột phá lên đại cảnh giới tiếp theo
    private void breakthrough(){
        switch(realm){
            case BODY -> realm = Realm.QI;
            case QI -> realm = Realm.FOUNDATION;
            default -> {}
        }
        applyBreakthrough(realm);
    }

    // Ghi nhận việc dùng đan dược tăng chỉ số, trả về true nếu còn giới hạn
    public boolean usePill(){
        int count = usedPills.getOrDefault(realm, 0);
        if(count >= absorption.limit()) return false;
        usedPills.put(realm, count + 1);
        return true;
    }

    // Getter
    public Realm getRealm(){ return realm; }
    public int getSubLevel(){ return subLevel; }
    public boolean isDantianOpened(){ return dantianOpened; }
    public Physique getPhysique(){ return physique; }
    public AbsorptionType getAbsorption(){ return absorption; }
    // Setter cho hấp thụ
    public void setAbsorption(AbsorptionType t){ absorption = t; }
}
