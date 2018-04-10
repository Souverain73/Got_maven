package got.gameObjects.interfaceControls;

import got.gameStates.GameState;
import got.graphics.GraphicModule;
import got.graphics.Texture;
import org.joml.Vector3f;

/**
 * Created by Souverain73 on 22.11.2016.
 */
public class ToggleImageButton extends ImageButton{
    private boolean toggled;

    public ToggleImageButton(String textName, int x, int y, int w, int h, Object param) {
        super(textName, x, y, w, h, param);
    }

    public ToggleImageButton(Texture tex, int x, int y, int w, int h, Object param) {
        super(tex, x, y, w, h, param);
    }


    @Override
    public void draw(GameState st) {
        if (toggled){
            setMultiply(new Vector3f(3f, 3f, 3f));
        }

        super.draw(st);

        GraphicModule.instance().resetEffect();
    }

    @Override
    protected void click(GameState st) {
        toggled = !toggled;
        super.click(st);
    }

    public boolean isToggled() {
        return toggled;
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
    }

    private void setMultiply(Vector3f multiply){
        GraphicModule.instance().getEffect().Multiply(multiply);
    }
}
