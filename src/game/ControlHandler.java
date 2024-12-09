package game;
import java.awt.event.*;
import java.util.ArrayList;

public class ControlHandler {
    private static boolean Enabled = true;
    private static final ArrayList<Integer> ActiveKeys = new ArrayList<>();

    public static void KeyPressed(KeyEvent event) {
        if (!ActiveKeys.contains(event.getKeyCode())) {
            ActiveKeys.add(event.getKeyCode());
            if (Enabled) {
                PlayerController.ControlHandle(event.getKeyCode());
            }
            DialogueHandler.ControlHandle(event.getKeyCode());
        }
    }

    public static void KeyReleased(KeyEvent event) {
        int index = ActiveKeys.indexOf(event.getKeyCode());
        if (index != -1) {
            ActiveKeys.remove(index);
        }
    }

    public static void SetEnabled(boolean val) {
        Enabled = val;
    }

    public static boolean isKeyHeld(int keycode) {
        if (!Enabled) {
            return false;
        }
        return ActiveKeys.contains(keycode);
    }
}
