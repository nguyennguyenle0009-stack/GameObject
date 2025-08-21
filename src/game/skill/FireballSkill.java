package game.skill;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import javax.imageio.ImageIO;

public class FireballSkill extends Skill {
    private static BufferedImage loadIcon() {
        try {
            return ImageIO.read(Objects.requireNonNull(FireballSkill.class.getResourceAsStream("/data/tile/aaa6.png")));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public FireballSkill() {
        super("Fireball", 1, loadIcon());
    }
}
