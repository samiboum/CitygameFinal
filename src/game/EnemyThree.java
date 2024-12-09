package game;

import city.cs.engine.World;
import org.jbox2d.common.Vec2;

public class EnemyThree extends Enemy {
    private final World world;
    //traces for the one-two
    private BulletTrace storedTrace1;
    private BulletTrace storedTrace2;

    //traces for the barrage
    private BulletTrace storedTrace3;
    private BulletTrace storedTrace4;
    private BulletTrace storedTrace5;
    private BulletTrace storedTrace6;
    public EnemyThree(World w, Vec2 location) {
        super(w, location, 10);
        this.world = w;
        // for ordering - super uses trace, trace uses world
        TraceInit3();
    }

    public void TraceInit3() {
        float DELAY = 1.3f;
        float SHOTSPEED = 10f;
        float HANGTIME = 0.3f;
        //create shot
        storedTrace1 = new BulletTrace(world, this, PlayerController.getPlayerInstance(), DELAY, SHOTSPEED, HANGTIME);
        storedTrace2 = new BulletTrace(world, this, PlayerController.getPlayerInstance(), DELAY-0.3f, SHOTSPEED, HANGTIME);
        storedTrace3 = new BulletTrace(world, this, PlayerController.getPlayerInstance(), 0.85f, SHOTSPEED, 0);
        storedTrace4 = new BulletTrace(world, this, PlayerController.getPlayerInstance(), 0.85f, SHOTSPEED, 0.15f);
        storedTrace5 = new BulletTrace(world, this, PlayerController.getPlayerInstance(), 0.85f, SHOTSPEED, 0.3f);
        storedTrace6 = new BulletTrace(world, this, PlayerController.getPlayerInstance(), 0.85f, SHOTSPEED, 0.45f);
    }

    public void AttackActivate() {
        if (Math.random() > 0.5) {
            storedTrace1.ActivateTrace();
            storedTrace2.ActivateTrace();
        } else {
            storedTrace3.ActivateTrace();
            storedTrace4.ActivateTrace();
            storedTrace5.ActivateTrace();
            storedTrace6.ActivateTrace();
        }
    }

    public void DisableTraces() {
        storedTrace1.Disable();
        storedTrace2.Disable();
        storedTrace3.Disable();
        storedTrace4.Disable();
        storedTrace5.Disable();
        storedTrace6.Disable();
        //again, maybe i should have used a list, but the quantity is so low that it doesn't matter
    }

    public void Die() {
        //PlayerController.EndStage(true);
        DialogueHandler.BeginDialogue("PostCutsceneMap3");
    }


}
