package game;

import city.cs.engine.Fixture;
import org.jbox2d.common.Vec2;
import city.cs.engine.Body;

public class Helper {

    public static float getAngleBetweenVectors(Vec2 orig, Vec2 target) {
        float theta = (float) Math.atan2(target.y - orig.y, target.x - orig.x);
        theta+= (float) (Math.PI/2.0);
        return theta;
    }

    public static Vec2 getOffsetByAngleDistance(Vec2 origPos, float angle, float distance) {
        //you couldn't pay me to figure out why I had to multiply the y by -1.
        return origPos.add(new Vec2((float) (Math.sin(angle) * distance), (float) -(Math.cos(angle) * distance)));
    }

    public static void purgeFixtures(Body body) {
        for (Fixture f : body.getFixtureList()) {
            f.destroy();
        }
    }
}
