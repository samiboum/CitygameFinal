package game;

import city.cs.engine.*;

public class DialogueProperty {

    public DialogueProperty(Tile t) {
        Sensor sensor = t.getSensor();
        sensor.addSensorListener(new SensorListener() {
            @Override
            public void beginContact(SensorEvent sensorEvent) {
                if (sensorEvent.getContactBody() instanceof  Player) {
                    //here would be the activation
                    PlayerController.SetActiveDialogue(t.getName());
                }
            }

            @Override
            public void endContact(SensorEvent sensorEvent) {
                PlayerController.SetActiveDialogue("");
            }
        });
    }
}
