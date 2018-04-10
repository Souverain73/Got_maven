package got.gameObjects;

import got.graphics.DrawSpace;
import got.graphics.text.Font;
import got.graphics.text.FontTrueType;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by КизиловМЮ on 24.07.2017.
 */
public class TextBoxObject extends AbstractGameObject<TextBoxObject> {
    private VerticalAlign verticalAlign = VerticalAlign.TOP;
    private Align align = Align.LEFT;
    private final Font font;
    private final int SPACING = 2;
    private ContainerObject linesContainer;
    private List<TextObject> lines = new ArrayList<>();
    private int actualLines;
    private String text;

    @Override protected TextBoxObject getThis() {return this;}
    public enum VerticalAlign{TOP, CENTER, BOTTOM}
    public enum Align{LEFT, CENTER, RIGHT}

    public TextBoxObject(Font font, float w, float h){
        this.w = w;
        this.h = h;
        this.font = font;

        linesContainer = new ContainerObject();

        int linesCount = (int) (h / font.getSize());
        for (int i=0; i<linesCount; i++){
            TextObject to = new TextObject(font, "");
            to.setPos(new Vector2f(0, i*(font.getSize() + SPACING)));
            lines.add(to);
            linesContainer.addChild(to);
        }

        addChild(linesContainer);
        setSpace(DrawSpace.SCREEN);
    }

    public TextBoxObject(float w, float h){
        this(new FontTrueType("calibri", 16), w, h);
    }

    public TextBoxObject setVerticalAlign(VerticalAlign verticalAlign) {
        this.verticalAlign = verticalAlign;
        updatePlacing();
        return this;
    }

    public TextBoxObject setAlign(Align align) {
        this.align = align;
        updatePlacing();
        return this;
    }

    public TextBoxObject setText(String text) {
        this.text = text;
        List<String> textLines = font.splitForWidth(text, (int) w);
        actualLines = textLines.size();
        for (int i=0; i <lines.size(); i++){
            lines.get(i).setText(textLines.size()>i ? textLines.get(i) : "");
        }
        updatePlacing();
        return this;
    }

    private void updatePlacing(){
        for (TextObject to: lines){
            float lineWidth = to.getW();
            float x = 0;
            switch (align){
                case LEFT: x = 0; break;
                case RIGHT: x = this.w - lineWidth; break;
                case CENTER: x = (this.w - lineWidth) /2; break;
            }
            to.setPos(new Vector2f(x, to.getPos().y));
        }

        float linesHeight = (font.getSize() + SPACING) * actualLines;
        float y = 0;
        switch (verticalAlign){
            case TOP: y= 0; break;
            case BOTTOM: y = this.h - linesHeight; break;
            case CENTER: y = (this.h - linesHeight) / 2; break;
        }
        linesContainer.setPos(0, (int) y);
    }
}
