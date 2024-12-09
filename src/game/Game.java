package game;

import city.cs.engine.*;
import city.cs.engine.StepListener;
import org.jbox2d.common.Vec2;

import javax.swing.JFrame;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


/**
 * Your main game entry point
 */
public class Game {
    private final GameView userView;

    /** Initialise a new Game. */
    public Game() {

        //1. make an empty game world
        GameWorld world = new GameWorld();
        world.getSimulationSettings().setTargetFrameRate(30);
        world.addStepListener(new StepListener() {
            @Override
            public void preStep(StepEvent stepEvent) {
                //universal step listener for function calling
                PlayerController.Step(stepEvent);
                AnimationController.AnimationStep(stepEvent);
                EnemyController.EnemyStep(stepEvent);
                BulletHandler.bulletStep(stepEvent);
                BulletTraceController.traceStep(stepEvent);
                if (PlayerController.getPlayerInstance() != null ) {
                    Vec2 playerPos = PlayerController.getPlayerInstance().getPosition();

                    Vec2 cameraPos = new Vec2(playerPos.x, playerPos.y + 3.5f);
                    if (cameraPos.y < 30) {
                        cameraPos.y = 30;
                    }
                    userView.setCentre(cameraPos);
                }
            }

            @Override
            public void postStep(StepEvent stepEvent) {

            }
        });

        //3. make a view to look into the game world
        userView = new GameView(world, 700, 700);
        userView.setZoom(15);

        CreateFrame();


        //optional: uncomment this to make a debugging view
         //JFrame debugView = new DebugViewer(world, 500, 500);

        // start our game world simulation!
        world.start();

        PlayerController.setWorld(world);
        PlayerController.setView(userView);

    }

    private void CreateFrame() {
        final JFrame frame = new JFrame("City Game");
        frame.add(userView);

        // enable the frame to quit the application
        // when the x button is pressed
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationByPlatform(true);
        // don't let the frame be resized
        frame.setResizable(false);
        // size the frame to fit the world view
        frame.pack();
        // finally, make the frame visible
        frame.setVisible(true);

        //controls
        frame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent event) {ControlHandler.KeyPressed(event);}

            public void keyReleased(KeyEvent event) {
                ControlHandler.KeyReleased(event);
            }
        });
    }


    /** Run the game. */
    public static void main(String[] args) {

        new Game();

    }

}
