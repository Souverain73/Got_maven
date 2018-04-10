package got.gameObjects.interfaceControls;

import got.gameStates.GameState;
import got.utils.Utils;
import org.joml.Vector2f;

/**
 * Created by Souverain73 on 11.04.2017.
 */
public class TransparentButton extends AbstractButtonObject<TransparentButton> {
    Object param;

    @Override
    protected TransparentButton getThis() {
        return this;
    }

    public TransparentButton(int x, int y, int w, int h, Object param){
        super(2);
        pos.x = x;
        pos.y = y;
        this.w = w;
        this.h = h;
        this.param = param;
    }

    @Override
    public boolean ifMouseIn(Vector2f mousePos) {
        return  (Utils.pointInRect(mousePos, getAbsolutePos(), getDim()));
    }

    @Override
    protected void click(GameState st) {
        if (callback != null){
            callback.accept(this, param);
        }
    }
}
