package game;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundHandler {
    private static AudioInputStream inputStream;

    private static Clip bgclip;

    public static void PlaySound(String filename) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        inputStream = AudioSystem.getAudioInputStream(new File("data/Sounds/" + filename+".wav"));

        Clip clip = AudioSystem.getClip();

        clip.open(inputStream);
        FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        control.setValue((control.getMaximum()-control.getMinimum() * 0.75f) + control.getMinimum());
        clip.start();
    }

    public static void PlayMusic(String filename) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        inputStream = AudioSystem.getAudioInputStream(new File("data/Sounds/"+filename+".mid"));

        if (bgclip != null) {
            bgclip.stop();
        }

        bgclip = AudioSystem.getClip();

        bgclip.open(inputStream);
        bgclip.loop(Clip.LOOP_CONTINUOUSLY);
        FloatControl control = (FloatControl) bgclip.getControl(FloatControl.Type.MASTER_GAIN);
        control.setValue((control.getMaximum()-control.getMinimum() * 0.65f) + control.getMinimum());
        bgclip.start();
    }

    public static void StopMusic() {
        bgclip.stop();
    }
}
