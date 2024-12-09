package game;

import city.cs.engine.StepEvent;
import city.cs.engine.*;
import org.jbox2d.common.Vec2;

import java.util.Hashtable;
import java.util.Iterator;

public class AnimationController {
    private static final Hashtable<Body, Animation> playingAnimations = new Hashtable<>();
    private static final Hashtable<Body, Float> flashingAnimations = new Hashtable<>();
    private static float timeElapsed;

    public static void playAnimation(Body body, String animationName, int frames, int fps, float sizeMultiplier, boolean reversedAnim, boolean looped) {
        Animation anim = new Animation(animationName, fps, frames, sizeMultiplier, reversedAnim, looped);
        playingAnimations.put(body,anim);
    }

    public static void flashAnimation(Body body) {
        flashingAnimations.put(body, 0f);
    }

    /*/public static void stopAnimation(Body body) {
        playingAnimations.remove(body);
    }/*/

    public static void AnimationStep(StepEvent stepEvent) {
        float deltaTime = stepEvent.getStep();

        timeElapsed += deltaTime;

        for (Iterator<Body> it = playingAnimations.keys().asIterator(); it.hasNext(); ) {
            Body body = it.next();
            if (flashingAnimations.get(body) != null) {
                flashingAnimations.put(body, flashingAnimations.get(body) + (deltaTime));
                if (flashingAnimations.get(body) > 2) {
                    flashingAnimations.remove(body);
                }
            }
            Animation anim = playingAnimations.get(body);
            // every 1/fps we want this to happen
            // therefore, we want a last-updated time in the animation
            // if timeElapsed - lastUpdated >= 1/fps, do it
            if (timeElapsed - anim.getLastUpdated() >= ((float) 1 /anim.getFps())) {
                body.removeAllImages();
                anim.setLastUpdated(timeElapsed);
                if (flashingAnimations.get(body) != null) {
                    float counter = flashingAnimations.get(body);
                    while (counter > 1) {
                        counter -= 1;
                    }
                    if (counter < 0.5) {
                        new AttachedImage(body, new BodyImage("data/invisible.png"),1,0,new Vec2(0,0));
                        continue;
                    }
                }
                AttachedImage image = new AttachedImage(body, anim.getImage(true),1,0,new Vec2(0,0));
                if (anim.isReversedAnim()) {
                    image.flipHorizontal();
                }
            }
        }
    }

}
