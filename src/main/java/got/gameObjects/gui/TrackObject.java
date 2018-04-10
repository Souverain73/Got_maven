package got.gameObjects.gui;

import got.gameObjects.AbstractGameObject;
import got.gameObjects.ImageObject;
import got.gameStates.GameState;
import got.graphics.*;
import got.model.Fraction;
import got.model.Track;
import org.joml.Vector2f;


/**
 * Created by Souverain73 on 22.03.2017.
 */
public class TrackObject extends AbstractGameObject<TrackObject> {
    @Override protected TrackObject getThis() { return this;}
    protected Track track;
    protected Texture blankTexture;

    public TrackObject(Track track){
        blankTexture = TextureManager.instance().loadTexture("fractions/back.png");
        this.w = 65;
        this.h = 480;
        this.track = track;
        setSpace(DrawSpace.SCREEN);

        ImageObject bg = new ImageObject("tracks/" + track.getName() + "_BG.png", 65, 420).setPos(0, 60);
        addChild(bg);

        AbstractGameObject<?> trackIcon = new ImageObject("tracks/" + track.getName() + "_ICON.png", 65, 60);
        addChild(trackIcon);
    }


    @Override
    public void draw(GameState state) {
        if (!isVisible()) return;
        super.draw(state);
        GraphicModule.instance().setDrawSpace(this.space);
        Vector2f cp = getAbsolutePos();
        Fraction[] data = track.getData();

        for (int i = 0; i < data.length; i++) {
            drawFraction(cp, i, data[i]);
        }
    }

    protected void drawFraction(Vector2f cp, int position, Fraction fraction){
        fraction.getBackTexture().draw(cp.x + 13 , cp.y + 82 + position * 65, 45, 45);
    }
}
