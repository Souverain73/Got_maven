package got.gameObjects.gui;

import got.animations.Animator;
import got.animations.Easings;
import got.gameObjects.AbstractGameObject;
import got.gameObjects.ImageObject;
import got.gameObjects.interfaceControls.TransparentButton;
import got.gameStates.GameState;
import got.graphics.Effect;
import got.graphics.GraphicModule;
import got.graphics.Texture;
import got.graphics.TextureManager;
import got.model.Fraction;
import got.model.SuplyTrack;
import org.joml.Vector2f;

import java.util.List;

/**
 * Created by Souverain73 on 13.04.2017.
 */
public class SuplyTrackObject extends AbstractGameObject<SuplyTrackObject> {
    private SuplyTrack track;
    private ImageObject bg;
    private Vector2f token_start = new Vector2f(50, 65);
    private Texture iconTexture;
    private boolean expanded;

    @Override
    protected SuplyTrackObject getThis() {
        return this;
    }

    public SuplyTrackObject(SuplyTrack track){
        this.track = track;
        this.h = 240;
        this.w = 420;

        addChild(bg = new ImageObject("tracks/SUPLY_BG.png", (int)w, (int)h));
        addChild(new TransparentButton(0,0,65, (int)h, null).setCallback((gameObject, o) -> toggle()));

        iconTexture = TextureManager.instance().loadTexture("tracks/SUPLY_ICON.png");
    }

    private void toggle(){
        if (!expanded){
            Animator.animateVector2f(this.pos, new Vector2f(-w, 480), 1000, this::setPos)
                    .setEasing(Easings.OUT_CUBIC);
            expanded = true;
        }else{

            Animator.animateVector2f(this.pos, new Vector2f(-65, 480), 1000, this::setPos)
                    .setEasing(Easings.OUT_CUBIC);
            expanded = false;
        }
    }

    @Override
    public void draw(GameState state) {
        super.draw(state);
        if (!isVisible()) return;

        GraphicModule.instance().setDrawSpace(space);
        List<List<Fraction>> data = track.getData();
        for (int i = 0; i < 7; i++){
             List<Fraction> slot = data.get(i);
            drawSlot(i, slot);
        }

        GraphicModule.instance().resetEffect();
    }

    private void drawSlot(int num, List<Fraction> data) {
        Vector2f cp = getAbsolutePos();
        int x = (int) (token_start.x + 46 * num);
        int y = (int) token_start.y;
        for (Fraction f : data){
            GraphicModule.instance().setEffect(new Effect().Multiply(f.getMultiplyColor()));
            iconTexture.draw(cp.x+x, cp.y+y, 40, 8);
            y+=8;
        }
    }
}
