package game;

import city.cs.engine.World;
import org.jbox2d.common.Vec2;

public class EnemyTwo extends Enemy{

    private final World world;
    private BulletTrace storedTrace1;
    private BulletTrace storedTrace2;
    public EnemyTwo(World w, Vec2 location) {

        super(w, location, 5);
        this.world = w;
        // for ordering - super uses trace, trace uses world
        TraceInit2();
    }
    public void TraceInit2() {
        float DELAY = 1.3f;
        float SHOTSPEED = 10f;
        float HANGTIME = 0.3f;
        //create shot
        storedTrace1 = new BulletTrace(world, this, PlayerController.getPlayerInstance(), DELAY, SHOTSPEED, HANGTIME);
        storedTrace2 = new BulletTrace(world, this, PlayerController.getPlayerInstance(), DELAY, SHOTSPEED, HANGTIME + 0.3f);
    }

    public void AttackActivate() {
        storedTrace1.ActivateTrace();
        storedTrace2.ActivateTrace();
    }

    public void DisableTraces() {
        storedTrace1.Disable();
        storedTrace2.Disable();
    }

    public void Die() {
        PlayerController.EndStage();
    }


}
