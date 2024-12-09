package game;

import javax.swing.*;

public class GuiImage {
    private final String name;
    private ImageIcon icon;
    private final int x;
    private final int y;

    public GuiImage(String name, String iconName, int x, int y) {
        this.name = name;
        this.icon = new ImageIcon("data/"+iconName+".png");
        this.x = x;
        this.y = y;
    }

    public void setIcon(String iconName) {
        this.icon = new ImageIcon("data/"+iconName+".png");
    }

    public String getName() {
        return name;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public ImageIcon getIcon() {
        return icon;
    }
}
