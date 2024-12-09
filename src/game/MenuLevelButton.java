package game;

import javax.swing.*;
import java.awt.*;
import java.awt.image.ImageObserver;

public class MenuLevelButton extends MenuButton{
    private final String stageName;
    private final MenuFrame frame;
    private final Rectangle bounds;
    private final boolean Visible;
    private final ImageIcon icon;
    private final ImageObserver imageObserver = (img, infoflags, x, y, width, height) -> false;

    public MenuLevelButton(int x, int y, int width, int height, MenuFrame frame, String stagename) {
        super(x, y, width, height, frame);
        this.stageName = stagename;
        this.frame = frame;
        this.bounds = new Rectangle(x,y,width,height);

        this.Visible = true;

        icon = new ImageIcon("data/"+stageName+"Icon.png");
    }

    @Override
    public void onClick() {
        super.onClick();

        frame.getWorld().setMapName(stageName);
        //check for cutscene
        if (DialogueHandler.checkPreCutscene(stageName)) {
            DialogueHandler.BeginDialogue("PreCutscene"+stageName);
        } else {
            frame.getWorld().ResetLevel();
        }
        frame.setActive(false);
    }

    public void drawButton(Graphics2D g) {
        if (!Visible) {return;}
        g.drawRect(bounds.x,bounds.y, bounds.width, bounds.height);
        g.drawImage(icon.getImage(), bounds.x, bounds.y, imageObserver);
    }
}
