package got.translation;

import jdk.nashorn.internal.runtime.regexp.joni.Regex;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Created by Souverain73 on 30.01.2017.
 */
public class AbstractDictionary implements Dictionary {
    private Map<String, String> dict = new HashMap<>();

    @Override
    public String get(String key) {
        return dict.getOrDefault(key, "Translation not found for: " + key);
    }

    public AbstractDictionary loadFromFile(String fileName){
        String currentBlock = "";
        Pattern group = Pattern.compile("\\[(.*)\\]");
        Pattern data = Pattern.compile("(.*?)\\s*\"(.*)\"");


        try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("data/translations/" + fileName), "UTF8"))){//new FileReader("data/translations/" + fileName))){
            String line;
            while ((line = br.readLine()) != null){
                Matcher match = group.matcher(line);
                if (match.find()){
                    currentBlock = match.group(1);
                    continue;
                }
                match = data.matcher(line);
                if (match.find()){
                    String key = currentBlock + "." + match.group(1);
                    String text = match.group(2);
                    dict.put(key, text);
                    continue;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }
}
