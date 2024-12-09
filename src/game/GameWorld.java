package game;

import city.cs.engine.*;
import game.simple.JSONArray;
import game.simple.JSONObject;
import game.simple.parser.JSONParser;
import game.simple.parser.ParseException;
import org.jbox2d.common.Vec2;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class GameWorld extends World{
    private String mapName = "Map1";
    public String getMapName() {
        return mapName;
    }

    public GameWorld() {
        super();
    }

    public void DestroyLevel() {
        ControlHandler.SetEnabled(false);
        for (DynamicBody body : this.getDynamicBodies()) {
            body.destroy();
        }
        for (StaticBody body : this.getStaticBodies()) {
            body.destroy();
        }
        EnemyController.RemoveAllEnemies();
        PlayerController.setPlayerInstance(null);
    }

    public void ResetLevel() {
        for (DynamicBody body : this.getDynamicBodies()) {
            if (body != PlayerController.getPlayerInstance()) {
                body.destroy();
            }
        }
        for (StaticBody body : this.getStaticBodies()) {
            body.destroy();
        }

        if (PlayerController.getPlayerInstance() == null ) {
            new Player(this);
        }
        ControlHandler.SetEnabled(true);
        EnemyController.RemoveAllEnemies();
        LoadWorld(mapName);
        PlayerController.getPlayerInstance().setPosition(new Vec2(5,45));
        try {
            SoundHandler.PlayMusic(mapName);
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            throw new RuntimeException(e);
        }

        Time.resetStageTime();
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
        PlayerController.setMap(mapName);
    }

    private void LoadWorld(String mapname) {
        //I'm using a tileset.

        //test
        Object obj;
        try {
            obj = new JSONParser().parse(new FileReader("data/"+mapname+".json"));
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

        JSONObject jo = (JSONObject) obj;

        JSONArray layers = (JSONArray) jo.get("layers");
        //get(1) is solids, get(2) is objects to interact with
        JSONObject object = (JSONObject) layers.get(1);
        JSONArray data = (JSONArray) object.get("data");

        Long mapWidth = (Long) jo.get("width");
        Long mapHeight = (Long) jo.get("height");

        //solids
        for (int i = 0; i < data.size(); i++) {
            Long longTile = (Long) data.get(i);
            int tile = Math.toIntExact(longTile);

            if (tile == 0 ) {
                continue;
            }

            //start at top right
            int rightmost = Math.toIntExact(mapWidth);
            int topmost = Math.toIntExact(mapHeight);

            int row = i / rightmost;
            int x = ((2*i) % (2*rightmost));
            int y = ((2 *topmost) - (2*row));


            //if the tile has one to the direct left, skip
            if (i != 0 && i % rightmost != 0) {
                Long LongLeftTile = (Long) data.get(i - 1);
                int LeftTile = Math.toIntExact(LongLeftTile);
                if (LeftTile != 0) {
                    continue;
                }
            }

            //create an extended tile for as many to the right as there are
            int tileCount = 0;
            ArrayList<Integer> tileNames = new ArrayList<>();
            for (int j = i; j < data.size(); j++) {
                if (j/rightmost != row) {
                    break;
                }
                Long LongJTile = (Long) data.get(j);
                int jTile = Math.toIntExact(LongJTile);
                if (jTile == 0) {
                    break;
                }
                tileCount++;
                tileNames.add(jTile);
            }

            CreateTile(tileNames, x, y, tileCount);

        }

        //interactibles
        object = (JSONObject) layers.get(2);
        data = (JSONArray) object.get("data");

        for (int i = 0; i < data.size(); i++) {
            Long longTile = (Long) data.get(i);
            int tile = Math.toIntExact(longTile);

            if (tile == 0 ) {
                continue;
            }

            //start at top right
            int rightmost = Math.toIntExact(mapWidth);
            int topmost = Math.toIntExact(mapHeight);

            int row = i / rightmost;
            int x = ((2*i) % (2*rightmost));
            int y = ((2 *topmost) - (2*row));

            CreateInteractibleTile(tile, x+1, y);

        }

        object = (JSONObject) layers.get(0);
        data = (JSONArray) object.get("data");
        //background
        for (int i = 0; i < data.size(); i++) {
            Long longTile = (Long) data.get(i);
            int tile = Math.toIntExact(longTile);

            if (tile == 0 ) {
                continue;
            }

            //start at top right
            int rightmost = Math.toIntExact(mapWidth);
            int topmost = Math.toIntExact(mapHeight);

            int row = i / rightmost;
            int x = ((2*i) % (2*rightmost));
            int y = ((2 *topmost) - (2*row));

            CreateBackgroundTile(tile, x+1, y);

        }

    }

    private void CreateTile(ArrayList<Integer> skin, int x, int y, int xMultiplier) {
        Shape shape = new BoxShape(1f * xMultiplier, 1f);
        Tile tile = new Tile(this, shape);
        tile.setName(String.valueOf(skin.get(0)));
        tile.setPosition(new Vec2(x+(xMultiplier), y));

        for (int i = 0; i < skin.size(); i++) {
            String name = String.valueOf(skin.get(i));
            BodyImage image = new BodyImage("data/Tiles/tile"+name+".png",2f);
            Vec2 offset = new Vec2((-xMultiplier+1) + (2f * i),0);
            new AttachedImage(tile,image,1f,0f,offset);
        }

    }

    private void CreateInteractibleTile(int skin, int x, int y) {
        Shape shape = new BoxShape(1f, 1f);
        Tile tile = new Tile(this, shape);
        tile.setName(String.valueOf(skin));
        tile.setPosition(new Vec2(x, y));

        // Interactible behaviours. its fine, theres not enough tiles for me to care about refactoring
        if (skin == 43) {
            tile.AddBounce();
        }
        if (skin == 44) {
            tile.AddDialogue();
        }
        if (skin == 45) {
            tile.AddStageEnd();
        }
        if (skin == 46) {
            tile.AddEnemy(1);
        }
        if (skin == 47) {
            tile.AddEnemy(2);
        }
        if (skin == 48) {
            tile.AddHeart();
        }
        if (skin == 49) {
            tile.AddEnemy(3);
        }

        BodyImage image = new BodyImage("data/Tiles/tile"+skin+".png",2f);
        new AttachedImage(tile,image, 1f, 0f, new Vec2(0,0));
    }

    private void CreateBackgroundTile(int skin, int x, int y) {
        Shape shape = new BoxShape(1f, 1f);
        StaticBody ground = new StaticBody(this);
        new GhostlyFixture(ground, shape);
        ground.setName(String.valueOf(skin));
        ground.setPosition(new Vec2(x, y));

        BodyImage image = new BodyImage("data/Tiles/tile"+skin+".png",2f);
        new AttachedImage(ground,image, 1f, 0f, new Vec2(0,0));
    }
}
