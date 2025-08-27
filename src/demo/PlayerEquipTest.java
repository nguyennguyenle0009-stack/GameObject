package demo;

import game.main.GamePanel;
import game.entity.Player;
import game.entity.item.EquipmentItem;
import game.entity.item.elixir.HealthPotion;
import game.enums.Attr;
import game.enums.EquipType;
import java.nio.file.*;

public class PlayerEquipTest {
    public static void main(String[] args) {
        Path save = Paths.get("player.Nguyeen_pro.20250826.txt");
        Path backup = Paths.get("player.Nguyeen_pro.20250826.txt.bak");
        try {
            if (Files.exists(save)) {
                Files.move(save, backup, StandardCopyOption.REPLACE_EXISTING);
            }

            GamePanel gp = new GamePanel();
            Player p = gp.getPlayer();

        // Giảm máu và pep rồi mặc trang bị
        p.atts().add(Attr.HEALTH, -30);
        p.atts().add(Attr.PEP, -20);
        int hp = p.atts().get(Attr.HEALTH);
        int pep = p.atts().get(Attr.PEP);
        int spirit = p.atts().get(Attr.SPIRIT);

        p.equip(new EquipmentItem("Mũ sắt", "+3 DEF", "", EquipType.HELMET));

        assert p.atts().get(Attr.HEALTH) == hp : "Equip should not restore health";
        assert p.atts().get(Attr.PEP) == pep : "Equip should not restore pep";
        assert p.atts().get(Attr.SPIRIT) == spirit : "Equip should not change spirit";

        // Sử dụng đan dược hồi máu rồi mặc trang bị để kiểm tra không bị mất máu
        p.atts().add(Attr.HEALTH, -20); // gây thêm sát thương
        HealthPotion pot = new HealthPotion(50, 1);
        pot.use(p);
        int healed = p.atts().get(Attr.HEALTH);
        p.equip(new EquipmentItem("Giày", "+3 DEF", "", EquipType.SHOES));
        assert p.atts().get(Attr.HEALTH) == healed : "Equip should keep healed health";

            System.out.println("All tests passed.");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (Files.exists(backup)) {
                    Files.move(backup, save, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (Exception ignored) {}
        }
    }
}
