package game;


import game.simple.JSONObject;
import game.simple.parser.JSONParser;
import game.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ScoreWriter {
    public static void WriteScore(String MapName) {
        try {
            //get json
            Object obj;
            try {
                obj = new JSONParser().parse(new FileReader("data/scoreData.json"));
            } catch (IOException | ParseException e) {
                throw new RuntimeException(e);
            }

            JSONObject jo = (JSONObject) obj;
            jo.putIfAbsent(MapName, -1d);

            double score = (double) jo.get(MapName);
            if (score > Time.getScoreTime() || score == -1d) {
                jo.put(MapName, Time.getScoreTime());
            }

            FileWriter fileWriter = new FileWriter("data/scoreData.json");
            fileWriter.write(jo.toJSONString());
            fileWriter.close();
        } catch (IOException e){
            System.out.println("Error occurred");
        }
    }

    public static double GetScore(String MapName) {
        //get json
        Object obj;
        try {
            obj = new JSONParser().parse(new FileReader("data/scoreData.json"));
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

        JSONObject jo = (JSONObject) obj;

        if (jo.get(MapName) == null) { return -1; }

        return (double) jo.get(MapName);
    }
}
