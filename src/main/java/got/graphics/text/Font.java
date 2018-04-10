package got.graphics.text;

import java.util.List;

/**
 * Created by Souverain73 on 14.04.2017.
 */
public interface Font {
    int getSize();
    Text newText(String text);
    void changeText(Text text, String newText);
    int getTextureID();
    int getStringWidth(String message);
    List<String> splitForWidth(String message, int w);
}
