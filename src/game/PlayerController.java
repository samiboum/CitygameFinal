package game;

import city.cs.engine.Body;
import city.cs.engine.CollisionEvent;
import city.cs.engine.StepEvent;
import org.jbox2d.common.Vec2;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class PlayerController {
    private static Player playerInstance;
    private static float lastJump = 1;
    private static float lastRoll = 2;
    private static boolean rollBuffer;
    private static boolean airBoostCharge;
    private static boolean doubleBoostCharge;
    private static int airBoostDirection;
    private static boolean dropKicking;
    private static float lastDropkick;
    private static boolean airBoostTurned;
    private static float yVector;
    private static int moveVector;
    private static float iFrameCounter;
    private static float momentum;
    private static final float momentumMax = 4.2f;
    private static float overmomentum;
    private static final float overmomentumMax = 2f;
    private static String playerState = "idle";
    private static boolean facingLeft;
    private static String currentMap;
    private static String ActiveDialogue = "";
    private static boolean animDirectionLeft;
    private static boolean fallImmunity = false;
    private static int health = 5;
    private static float lastBounce = 100;
    private static GameWorld world;
    private static GameView view;

    private static final Hashtable<String, Integer> animationFrameCounts = new Hashtable<>() {
        {
            //framecount
            put("idle",1);
            put("run", 6);
            put("jump",6);
            put("fall",5);
            put("doubleairboost",4);
            put("roll",4);
            put("dropkick",4);
        }
    };
    private static final Hashtable<String, Integer> animationFps = new Hashtable<>() {
        {
            //fps
            put("idle",12);
            put("run", 24);
            put("jump",100);
            put("fall",18);
            put("doubleairboost",24);
            put("roll",24);
            put("dropkick", 24);
        }
    };

    private static final Hashtable<String, Boolean> animationLooped = new Hashtable<>() {
        {
            //fps
            put("idle",true);
            put("run", true);
            put("jump",false);
            put("fall",true);
            put("doubleairboost", false);
            put("roll",false);
            put("dropkick", false);
        }
    };

    public static GameWorld getWorld() {
        return world;
    }

    public static void Step(StepEvent stepEvent) {
        if (playerInstance == null) {return;}

        float deltaTime = stepEvent.getStep();
        //apply times, maybe refactor later
        lastJump += deltaTime;
        lastRoll += deltaTime;
        iFrameCounter -= deltaTime;
        lastDropkick += deltaTime;
        lastBounce += deltaTime;
        AddOvermomentum(-deltaTime*0.5f);

        moveVector = movementStep(deltaTime);

        if (moveVector == -1 && !facingLeft) {
            facingLeft = true;
        }
        if (moveVector == 1 && facingLeft) {
            facingLeft = false;
        }

        if (moveVector == (airBoostDirection*-1)) {
            airBoostTurned = true;
        }

        collisionStep();

        momentumStep(deltaTime, moveVector);

        float xVector = velocityStep(moveVector);

        playerInstance.setLinearVelocity(new Vec2(xVector, yVector));
        playerInstance.setAngleDegrees(0);
        playerInstance.setAngularVelocity(0);

        stateUpdate(moveVector);

        if (playerInstance.getPosition().y < 0) {
            Die();
        }

        //add dropkick
    }

    private static int movementStep(float deltaTime) {
        //movement section
        int moveVector = 0;

        if (yVector > -500f && !isOnGround()) { //terminal velocity
            yVector -= (9.8f * deltaTime * 10);
        } else if (isOnGround()) {
            yVector = 0f; //gravity.
            airBoostCharge = true;
            doubleBoostCharge = true;
            dropKicking = false;
            if (lastRoll < 0.2 && rollBuffer) {
                rollBuffer = false;
                lastRoll = 0;
            }
        }
        if (lastJump < 0.1) {
            yVector = 31; //jump
            if (momentum < 1) {
                yVector *= 0.8f;
            }
        }
        if (lastBounce < 0.2) {
            yVector = 50;
        }

        if (ControlHandler.isKeyHeld(KeyEvent.VK_LEFT)) {
            moveVector = -1;
        }
        if (ControlHandler.isKeyHeld(KeyEvent.VK_RIGHT)) {
            moveVector = 1;
        }
        return moveVector;
    }

    private static void collisionStep() {
        //collision section
        List<Body> collidedBodies = playerInstance.getBodiesInContact();
        Hashtable<Body,CollisionEvent> activeCollisions = playerInstance.getActiveCollisions();
        for (Iterator<Body> it = activeCollisions.keys().asIterator(); it.hasNext(); ) {
            Body body = it.next();
            if (!collidedBodies.contains(body)) {
                activeCollisions.remove(body);
            }
        }
        playerInstance.setActiveCollisions(activeCollisions);
    }

    private static void momentumStep(float deltaTime, int moveVector) {
        //run calcs
        if (moveVector != 0) {
            AddMomentum(deltaTime);
        }
        if ((moveVector == 0 && isOnGround()) && Objects.equals(ActiveDialogue, "")) {
            AddMomentum(-deltaTime * 5f);
        }
    }

    private static float velocityStep(int moveVector) {
        float xVector = (10 * moveVector) + (4 * momentum * moveVector);
        if (xVector > 25) {
            xVector = 25;
        }
        if (xVector < -25) {
            xVector = -25;
        }
        return xVector;
    }

    private static void stateUpdate(int moveVector) {
        //set the state
        //not using a setter for this since it's a PlayerController class only
        String OldState = playerState;
        if (isOnGround()) {
            playerState = "idle";
            if (moveVector != 0) {
                playerState = "run";
            }
            if(lastRoll < 0.2) {
                playerState = "roll";
            }
        } else {
            if (lastJump < 0.5) {
                playerState = "jump";
                if (!airBoostCharge) {
                    playerState = "jump"; //airboost
                }
                if (!doubleBoostCharge) {
                    playerState = "doubleairboost";
                }
            } else {
                playerState = "fall";
            }
        }
        if (dropKicking && !isOnGround() && lastDropkick < 0.5) {
            playerState = "dropkick";
        }
        if (Objects.equals(OldState, playerState) && (animDirectionLeft == facingLeft)) {return;}
        //animation
        animDirectionLeft = facingLeft;
        float animSizeMultiplier = 4.5f;
        AnimationController.playAnimation(playerInstance,playerState, animationFrameCounts.get(playerState), animationFps.get(playerState), animSizeMultiplier, animDirectionLeft, animationLooped.get(playerState));

    }


    public static void setPlayerInstance(Player player) {
        playerInstance = player;
    }

    public static Player getPlayerInstance() {
        return playerInstance;
    }
    public static boolean isDropKicking() {
        return dropKicking;
    }

    public static boolean isOnGround() {
        for (CollisionEvent event : playerInstance.getActiveCollisions().values()) {
            //very freaky how there is a distinction between -0 and 0.
            if (Objects.equals(event.getNormal(), new Vec2(-0.0f, -1))) {
                return true;
            }
            if (Objects.equals(event.getNormal(), new Vec2(0.0f, -1))) {
                return true;
            }
        }
        return false;
    }

    public static void JumpStart() {
        if (isOnGround()) {
            lastJump = 0;
            try {
                SoundHandler.PlaySound("jump");
            } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        //airboost
        //if its above you, you cant do it.
        if (airBoostCharge && moveVector != 0) {
            for (Body body : playerInstance.getBodiesInContact()) {
                if (body.getPosition().y <= playerInstance.getPosition().y) {
                    lastJump = 0; //temporary
                    airBoostCharge = false;
                    airBoostTurned = false;
                    dropKicking = false;
                    airBoostDirection = moveVector;
                    AddOvermomentum(0.5f);
                    try {
                        SoundHandler.PlaySound("walljump");
                    } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                        throw new RuntimeException(e);
                    }
                    return;
                }
            }
        }

        //double boost
        //if you have turned around and come back and jumped within 0.5s of an airboost
        if (doubleBoostCharge && moveVector != 0 && airBoostTurned) {
            for (Body body : playerInstance.getBodiesInContact()) {
                if (body.getPosition().y <= playerInstance.getPosition().y) {
                    if (lastJump <= 0.5) {
                        lastJump = 0; //temporary
                        doubleBoostCharge = false;
                        dropKicking = false;
                        AddOvermomentum(0.9f);
                        try {
                            SoundHandler.PlaySound("wallboost");
                        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                            throw new RuntimeException(e);
                        }
                        return;
                    }
                }
            }
        }
    }

    public static void RollStart() {
        lastRoll = 0;
        rollBuffer = true;
        try {
            SoundHandler.PlaySound("roll");
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void Collision(CollisionEvent collisionEvent) {
        //hit ground fast
        if (Objects.equals(collisionEvent.getNormal(), new Vec2(-0.0f, -1)) || Objects.equals(collisionEvent.getNormal(), new Vec2(0.0f, -1))) { //if it's the ground
            if (fallImmunity) {
                fallImmunity = false;
                return;
            }
            if (yVector <= -55f && lastRoll > 0.4f) {
                ResetMomentum();
            }
        }
    }

    private static void DropKick() {
        if (!isOnGround() && !dropKicking && momentum >= 3) {
            dropKicking = true;
            lastDropkick = 0;
            try {
                SoundHandler.PlaySound("dropkick");
            } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void ControlHandle(int Control) {
        //I think im gonna be sick
        if (Control == KeyEvent.VK_Z) {
            JumpStart();
        }
        if (Control == KeyEvent.VK_C) {
            RollStart();
        }
        if (Control == KeyEvent.VK_X) {
            DropKick();
        }
        if (Control == KeyEvent.VK_UP) {
            BeginDialogue();
        }
    }

    public static float getMomentum() {
        return momentum;
    }

    public static float getMomentumMax() {
        return momentumMax;
    }

    public static float getOvermomentum() {
        return overmomentum;
    }

    public static float getOvermomentumMax() {
        return overmomentumMax;
    }

    public static void TakeDamage(int damage) {
        if (iFrameCounter <= 0) {
            health-= damage;
            iFrameCounter = 2;
            AddMomentum(-damage);
            AnimationController.flashAnimation(playerInstance);
            try {
                SoundHandler.PlaySound("hitHurt");
            } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (health <= 0) {
            Die();
        }
    }

    public static void GainHealth(int hp) {
        health += hp;
        if (health > 5) {
            health = 5;
        }
    }

    private static void Die() {
        world.ResetLevel();
        health = 5;
        momentum = 0;
    }

    public static int getHealth() {
        return health;
    }

    public static void setWorld(GameWorld w) {
        world = w;
    }

    public static void setView(GameView v) {
        view = v;
    }

    public static void Bounce() {
        lastBounce = 0;
        fallImmunity = true;
        airBoostCharge = true;
        doubleBoostCharge = true;
        dropKicking = false;
        try {
            SoundHandler.PlaySound("bounce");
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void AddMomentum(float amount) {
        if (overmomentum > 0) { return;}
        momentum += amount;

        momentum = Math.clamp(momentum,0,momentumMax);
    }

    public static void AddOvermomentum(float amount) {
        //only edit at max momentum
        if (momentum != momentumMax) {return;}
        overmomentum += amount;

        overmomentum = Math.clamp(overmomentum, 0, overmomentumMax);
    }

    public static void ResetMomentum() {
        momentum = 0;
        overmomentum = 0;
    }

    public static void setMap(String str) {
        currentMap = str;
    }

    public static String getMap() {
        return currentMap;
    }

    public static void SetActiveDialogue(String string) {
        ActiveDialogue = string;
    }

    public static void BeginDialogue() {
        if (!Objects.equals(ActiveDialogue, "")) {
            DialogueHandler.BeginDialogue(ActiveDialogue);
            //System.out.println(ActiveDialogue);
        }
    }

    public static String GetActiveDialogue() {
        return ActiveDialogue;
    }

    public static void EndStage() {
        view.getMenuHandler().EndStage();
        SoundHandler.StopMusic();
    }
 }
