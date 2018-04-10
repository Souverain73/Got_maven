package got.translation;

/**
 * Created by Souverain73 on 30.01.2017.
 */
public class Translator {
    private Dictionary dictionary;

    private static Translator instance;

    private Translator(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public static void init(String languageFileName){
        instance = new Translator(new AbstractDictionary().loadFromFile(languageFileName));
    }

    public static String tt(String text){
        if (instance == null){
            throw new IllegalStateException("Translator not initialized");
        }
        return instance.dictionary.get(text);
    }
}
