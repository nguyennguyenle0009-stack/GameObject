package game.object.item;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Objects;

import javax.imageio.ImageIO;

import game.object.SuperObject;
import game.skill.Skill;

/**
 * Item that teaches a skill when picked up.
 */
public class OBJ_SkillBook extends SuperObject {
    private final Skill skill;

    public OBJ_SkillBook(Skill skill) {
        this.skill = skill;
        setName("SkillBook");
        try {
            BufferedImage img = ImageIO.read(Objects.requireNonNull(
                    getClass().getResourceAsStream("/data/tile/aa1.png")));
            setImage(img);
            setCollision(true);
            setCollisionArea(new Rectangle(8, 8, 32, 32));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Skill getSkill() {
        return skill;
    }
}

