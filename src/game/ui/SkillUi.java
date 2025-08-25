package game.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.List;

import game.main.GamePanel;
import game.entity.skill.CultivationTechnique;

/**
 * Bảng hiển thị danh sách công pháp đã học.
 */
public class SkillUi {
    private final GamePanel gp;
    private boolean visible = false;
    private Rectangle[] entries = new Rectangle[0];

    public SkillUi(GamePanel gp) {
        this.gp = gp;
    }

    public void draw(Graphics2D g2) {
        if (!visible) return;
        int x = gp.getTileSize() * 2;
        int y = gp.getTileSize();
        int w = gp.getTileSize() * 8;
        int h = gp.getTileSize() * 6;
        HUDUtils.drawSubWindow(g2, x, y, w, h, new Color(0,0,0,200), Color.WHITE);
        List<CultivationTechnique> skills = gp.getPlayer().getTechniques();
        entries = new Rectangle[skills.size()];
        for (int i = 0; i < skills.size(); i++) {
            int yy = y + 40 + i * 40;
            g2.setColor(Color.WHITE);
            g2.drawString(skills.get(i).getName(), x + 30, yy);
            entries[i] = new Rectangle(x + 20, yy - 25, w - 40, 30);
        }
    }

    public boolean handleMousePress(int mx, int my, int button) {
        if (!visible) return false;
        if (button == MouseEvent.BUTTON1) {
            for (int i = 0; i < entries.length; i++) {
                if (entries[i].contains(mx, my)) {
                    var list = gp.getPlayer().getTechniques();
                    if (i < list.size()) {
                        list.get(i).use(gp.getPlayer());
                        visible = false;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public void toggle() { visible = !visible; }
    public boolean isVisible() { return visible; }
}