package got.server;

import org.console.Console;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Souverain73 on 13.03.2017.
 */
public class GamePreset {
    List<String> initCommands;
    List<String> gameConfigCommands;
    GamePreset(String fileName){
        List<String> currentGroup = initCommands;
        initCommands = new ArrayList<>();
        gameConfigCommands = new ArrayList<>();
        Pattern groupPatern = Pattern.compile("\\[(.*)]");

        try {
            for (String line : Files.readAllLines(Paths.get("data/presets/" + fileName))){
                if (line.matches("^#.*")) continue;
                Matcher m = groupPatern.matcher(line);
                if(m.matches()){
                    String group = m.group(1);
                    switch (group.toLowerCase()){
                        case "init" : currentGroup = initCommands; break;
                        case "config" : currentGroup = gameConfigCommands; break;
                        default:
                            System.out.println("Группа " + group + " не найдена;");
                    }
                }else{
                    currentGroup.add(line);
                }
            }

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void executeInit(Console c){
        executeCommands(c, initCommands);
    }

    public void executeConfig(Console c){
        executeCommands(c, gameConfigCommands);
    }

    private void executeCommands(Console c, List<String> commands){
        for(String line : commands){
            String[] input = line.split("\\s");
            c.executeCommand(input);
        }
    }
}
