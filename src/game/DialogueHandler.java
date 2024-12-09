package game;

import game.simple.JSONArray;
import game.simple.JSONObject;
import game.simple.parser.JSONParser;
import game.simple.parser.ParseException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class DialogueHandler {
    private static boolean inDialogue;
    private static String currentDialogue;
    private static int currentLine = 0;
    private static String storedLine = "";
    private static String storedKey = "";

    public static boolean isInDialogue() {
        return inDialogue;
    }

    public static void HandleGUI(Graphics2D g) {
        if (!inDialogue) {return;}
        //dialogue box
        g.setPaint(new Color(14, 20, 59));
        g.fillRect(50,550, 600, 125);

        //text
        g.setPaint(new Color(255,255,255));
        g.setFont(new Font(Font.DIALOG_INPUT, Font.PLAIN, 20));
        g.drawString(storedKey, 60, 575);
        g.setFont(new Font(Font.DIALOG_INPUT, Font.PLAIN, 16));
        //g.drawString(storedLine, 60, 592);
        //okay wait i suddenly had a great idea
        //what if i measure the length of each word
        //and then apply stuff based on that
        //like this
        TextCycle(g,storedLine, new Font(Font.DIALOG_INPUT, Font.PLAIN, 16));
    }

    private static void TextCycle(Graphics2D g, String string, Font font) {
        String[] strings = string.split(" ");
        int offset = 0;
        int column = 0;
        for (String string1 : strings) {
            g.setFont(font);
            String effectiveString = string1.replaceAll("[*]", "");
            //check for italics
            if (string1.startsWith("*") && string1.endsWith("*")) {
                g.setFont(new Font(font.getFontName(), Font.ITALIC, font.getSize()));
            }

            //check for bold --probably better way to do this but eh
            if (string1.startsWith("**") && string1.endsWith("**")) {
                g.setFont(new Font(font.getFontName(), Font.BOLD, font.getSize()));
            }

            int length = g.getFontMetrics().stringWidth(effectiveString);
            int spacelength = g.getFontMetrics().stringWidth(" ");
            if (60 + offset + length > 600) {
                column+=1;
                offset = 0;
            }
            g.drawString(effectiveString, 60 + offset, 592 + (column * font.getSize()));
            offset+= length + spacelength;
        }
    }


    public static void ControlHandle(int Control) {
        if (!inDialogue) {return;}
        if (Control == KeyEvent.VK_Z) {
            GetNextLine();
        }
    }

    public static void BeginDialogue(String dialogue) {
        if (inDialogue) {return;}
        ControlHandler.SetEnabled(false);
        inDialogue = true;
        currentLine = 1;
        currentDialogue = dialogue;

        GetNextLine();
    }

    public static void EndDialogue() {
        inDialogue = false;
        ControlHandler.SetEnabled(true);

        //extra behavior
        if (currentDialogue.startsWith("PreCutscene")) {
            PlayerController.getWorld().ResetLevel();
        }
        if (currentDialogue.startsWith("PostCutscene")) {
            PlayerController.EndStage();
        }
    }

    public static boolean checkPreCutscene(String mapName) {
        Object obj;
        try {
            obj = new JSONParser().parse(new FileReader("data/Dialogue.json"));
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

        JSONObject jo = (JSONObject) obj;

        JSONObject currentD = (JSONObject) jo.get("PreCutscene" + mapName);

        return currentD != null;
    }

    private static void GetNextLine() {
        JSONObject dialogueJSON = GetJSON();

        if (dialogueJSON == null) { EndDialogue(); return; }

        try {
            SoundHandler.PlaySound("dialogue");
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            throw new RuntimeException(e);
        }

        int lineNumber = 1;
        for (int i = 1; i <= dialogueJSON.keySet().size(); i++) {
            String key = "Block"+i;
            JSONObject lines = (JSONObject) dialogueJSON.get(key);
            for (Object line : lines.values()) {
                JSONArray linelist = (JSONArray) line;
                for (Object atomicline : linelist) {
                    String finalline = (String) atomicline;
                    if (lineNumber == currentLine) {
                        currentLine++;
                        storedLine = finalline;
                        //weird, but works
                        storedKey = (String) lines.keySet().toArray()[0];
                        return;
                    }
                    lineNumber++;
                }
            }
        }
        EndDialogue();
    }

    private static JSONObject GetJSON() {
        Object obj;
        try {
            obj = new JSONParser().parse(new FileReader("data/Dialogue.json"));
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

        JSONObject jo = (JSONObject) obj;

        return (JSONObject) jo.get(currentDialogue);
    }
}
