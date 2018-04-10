package got.gameObjects.gui;

import got.gameObjects.AbstractGameObject;
import got.gameObjects.ImageObject;
import got.gameObjects.TextObject;
import got.gameObjects.interfaceControls.ImageButton;
import got.gameObjects.interfaceControls.TransparentButton;
import got.graphics.Colors;
import got.graphics.text.FontTrueType;
import got.utils.Utils;

/**
 * Created by Souverain73 on 20.04.2017.
 */
public class NumberSelectorObject extends AbstractGameObject<NumberSelectorObject>{
    private final int from;
    private final int to;
    private boolean enabled;

    @Override protected NumberSelectorObject getThis() {return this;}
    private int currentValue;
    private TextObject toValue;
    protected ImageButton buttonObject;

    public NumberSelectorObject(int from, int to, int def){
        this.from = from;
        this.to = to;
        this.currentValue = def;
        this.enabled = true;
        addChild(new ImageObject("dialogBackground.png", 100, 50));
        addChild(toValue = new TextObject(new FontTrueType("GotKG", 50, Colors.BLACK.asVector3()), "").setPos(25, 0));
        addChild(new TransparentButton(0, 0, 100, 25, null).setCallback((gameObject, o) -> addValue(1)));
        addChild(new TransparentButton(0, 25, 100, 25, null).setCallback((gameObject, o) -> addValue(-1)));
        addChild(buttonObject = new ImageButton("buttons/select.png", 0, 50, 100, 50, null).setCallback((gameObject, o) -> onSelect()));
        updateValue();
    }

    protected void onSelect(){

    }

    private void addValue(int value){
        if (!enabled) return;
        currentValue = Utils.limitInt(currentValue += value, from, to);
        updateValue();
    }

    private void updateValue(){
        toValue.setText(String.format("%02d", currentValue));
    }

    protected void setEnabled(boolean enabled){
        this.enabled = enabled;
        buttonObject.setEnabled(enabled);
    }

    public int getValue(){
        return currentValue;
    }
}
