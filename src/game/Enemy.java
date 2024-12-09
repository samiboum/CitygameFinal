package game;

import city.cs.engine.*;
import org.jbox2d.common.Vec2;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Objects;

public class Enemy extends Walker{
    private String state = "idle";
    private String animPlaying = "";
    private boolean facingLeft = false;
    private boolean animPlayingLeft = false;
    private float attackCounter;
    private boolean dead;
    private int hp;
    private Vec2 deathpoint;
    private BulletTrace storedTrace;
    private final World world;
    private float hitTime;

    private static final Hashtable<String, Integer> animationFrameCounts = new Hashtable<>() {
        {
            //frame count
            put("idle",1);
            put("hit", 5);
        }
    };
    private static final Hashtable<String, Integer> animationFps = new Hashtable<>() {
        {
            //fps
            put("idle",12);
            put("hit",24);
        }
    };

    private static final Hashtable<String, Boolean> animationLooped = new Hashtable<>() {
        {
            //looped
            put("idle",true);
            put("hit",false);
        }
    };

    public Enemy(World w, Vec2 location, int hp) {
        super(w);
        this.setPosition(location);
        this.setGravityScale(25f);
        this.hp = hp;

        EnemyController.AddEnemy(this);

        world = w;

        float halfWidth = 1f;
        float halfHeight = 1.75f;
        SolidFixture fixture = new SolidFixture(this, new BoxShape(halfWidth, halfHeight));
        new Sensor(this, new BoxShape(halfWidth+0.2f, halfHeight + 0.2f));

        fixture.setFriction(1000f);

        TraceInit();
    }

    public void TraceInit() {
        float DELAY = 2f;
        float SHOTSPEED = 10f;
        float HANGTIME = 0.5f;
        //create shot
        storedTrace = new BulletTrace(world, this, PlayerController.getPlayerInstance(), DELAY, SHOTSPEED, HANGTIME);
    }

    public void enemyStep(float deltaTime) {
        this.hitTime += deltaTime;
        animationStep();
        velocityStep();
        if (dead) return;
        if (Objects.equals(state, "hit") && hitTime > 2) {
            state = "idle";
            attackCounter = 2.5f;
        }
        if (hitTime < 2) { return;}
        attackStep(deltaTime);
        antiMovementStep();
    }

    private void animationStep() {
        String animationSuffix = "EnemyOne";
        float animSizeMultiplier = 4.5f;
        if (animPlayingLeft != facingLeft) {
            AnimationController.playAnimation(this, animationSuffix +state,animationFrameCounts.get(state),animationFps.get(state), animSizeMultiplier, facingLeft, animationLooped.get(state));
            animPlaying = state;
            animPlayingLeft = facingLeft;
            return;
        }
        if (Objects.equals(state, animPlaying)) {return;}
        animPlaying = state;
        AnimationController.playAnimation(this, animationSuffix +state,animationFrameCounts.get(state),animationFps.get(state), animSizeMultiplier, facingLeft, animationLooped.get(state));
    }

    private void velocityStep() {
        if (dead || Objects.equals(state, "hit")) {
            this.setPosition(deathpoint);
        }
    }

    private void attackStep(float deltaTime) {
        Walker player = PlayerController.getPlayerInstance();
        Vec2 pos = this.getPosition();
        Vec2 subPos = player.getPosition().sub(pos);

        float attackRange = 35f;
        if (subPos.length() > attackRange) {return;}
        attackCounter+=deltaTime;
        if (attackCounter >= 2.5f) {
            attackCounter = 0;

            AttackActivate();

        }
        facingLeft = !(player.getPosition().x > pos.x);
    }

    public void AttackActivate() {
        storedTrace.ActivateTrace();
    }

    private void antiMovementStep() {
        //so lame, but the engine is incomplete so i don't get collision filtering.
        for (Body body1 : this.getBodiesInContact()) {
            if (body1 == PlayerController.getPlayerInstance()) {
                this.setLinearVelocity(new Vec2(0,-100));
            }
        }
    }

    public void Hit(float momentum) {
        if (dead) return;
        if (Objects.equals(state, "hit")) return;
        if (momentum < 3) return;

        state = "hit";

        Player plr = PlayerController.getPlayerInstance();
        facingLeft = !(plr.getPosition().x > this.getPosition().x);
        deathpoint = this.getPosition().add(new Vec2(0, 2));

        hp -= 1;
        if (hp <= 0) {
            dead = true;

            storedTrace.Destroy();
            Helper.purgeFixtures(this);
            Die();
        } else {
            hitTime = 0;
            DisableTraces();
        }

        try {
            SoundHandler.PlaySound("hitEnemy");
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void DisableTraces() {
        storedTrace.Disable();
    }
    public void Die() {

    }

    public Walker getBody() {
        return this;
    }

    public void Destroy() {
        this.destroy();
    }

}
