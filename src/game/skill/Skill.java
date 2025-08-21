package game.skill;

import java.awt.image.BufferedImage;

public class Skill {
    private final String name;
    private final int requiredStage;
    private final BufferedImage icon;

    public Skill(String name, int requiredStage, BufferedImage icon) {
        this.name = name;
        this.requiredStage = requiredStage;
        this.icon = icon;
    }

    public String getName() { return name; }
    public int getRequiredStage() { return requiredStage; }
    public BufferedImage getIcon() { return icon; }
}
