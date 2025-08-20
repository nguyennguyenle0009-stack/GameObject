package game.entity.item;

import game.entity.Player;
import game.enums.MajorRealm;

public class TechniqueBook extends Item {
    private final MajorRealm realm; // có thể là null => dùng cho "đại cảnh giới hiện tại"

    public TechniqueBook(String name,String decription, MajorRealm realm) {
        super(name, decription);
        this.realm = realm;
    }

    public void applyTo(Player p) {
        MajorRealm target = (realm != null) ? realm : p.cultivation().stage().realm;
        p.cultivation().learnTechnique(target);
    }
}