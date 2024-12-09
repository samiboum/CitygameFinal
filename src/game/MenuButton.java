package game;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MenuButton extends MouseAdapter {
    private final Rectangle bounds;
    private final MenuFrame frame;
    private boolean Visible;

    public MenuButton(int x, int y, int width, int height, MenuFrame frame) {
        this.bounds = new Rectangle(x,y,width,height);
        this.frame = frame;

        this.Visible = true;
    }

    public void drawButton(Graphics2D g) {
        if (!Visible) {return;}
        g.drawRect(bounds.x,bounds.y, bounds.width, bounds.height);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);

        Point point = e.getPoint();

        if (!this.frame.isActive()) { return;}

        if (point.x >= bounds.x && point.x <= bounds.x + bounds.width && point.y >= bounds.y && point.y <= bounds.y + bounds.height) {
            onClick();
        }

    }

    public void onClick() {

    }

    public void SetVisible(boolean val) {
        this.Visible = val;
    }

}
