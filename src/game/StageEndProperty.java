package game;

public class StageEndProperty {

    public StageEndProperty(Tile t) {
        t.addCollisionListener(collisionEvent -> {
            if (collisionEvent.getOtherBody() instanceof Player) {

                PlayerController.EndStage();
            }
        });
    }
}
