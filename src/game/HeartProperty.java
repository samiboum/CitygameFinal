package game;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

public class HeartProperty {
    public HeartProperty(Tile t) {
        t.addCollisionListener(collisionEvent -> {
            if (collisionEvent.getOtherBody() instanceof Player) {
                PlayerController.GainHealth(1);
                t.destroy();
                try {
                    SoundHandler.PlaySound("heart");
                } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
