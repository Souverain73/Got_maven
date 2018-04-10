package got.gameObjects.gui;

import got.animations.Animator;
import got.animations.Easings;
import got.gameObjects.AbstractGameObject;
import got.gameObjects.ImageObject;
import got.gameObjects.interfaceControls.TransparentButton;
import got.graphics.DrawSpace;
import got.model.Game;
import org.joml.Vector2f;

/**
 * Created by Souverain73 on 10.04.2017.
 */
public class ThronesPanel extends AbstractGameObject<ThronesPanel> {
    public static final int TRACK_WIDTH = 65;
    @Override protected ThronesPanel getThis() {return this;}
    private ThroneTrackObject tt;
    private TrackObject ct;
    private TrackObject st;
    private boolean expanded = false;

    public ThronesPanel(){
        addChild(new ImageObject("tracks/PANEL_BG.png", TRACK_WIDTH*3, 480));
        tt = new ThroneTrackObject(Game.instance().getTrack(Game.THRONE_TRACK));
        tt.setSpace(DrawSpace.SCREEN).setPos(new Vector2f(0,0));
        addChild(tt);
        addChild(ct = new TrackObject(Game.instance().getTrack(Game.SWORD_TRACK))
                .setPos(new Vector2f(TRACK_WIDTH, 0)));
        addChild(st = new TrackObject(Game.instance().getTrack(Game.CROWN_TRACK))
                .setPos(new Vector2f(TRACK_WIDTH*2, 0)));
        addChild(new TransparentButton(0, 0, 65, 60, null)
        .setCallback((gameObject, o) -> toggle()));
    }

    private void toggle(){
        if (!expanded){
            Animator.animateVector2f(this.pos, new Vector2f(-TRACK_WIDTH*3, 0), 1000, this::setPos)
                    .setEasing(Easings.OUT_CUBIC);
            expanded = true;
        }else{

            Animator.animateVector2f(this.pos, new Vector2f(-TRACK_WIDTH, 0), 1000, this::setPos)
                    .setEasing(Easings.OUT_CUBIC);
            expanded = false;
        }
    }

    public ThroneTrackObject getThroneTrack(){
        return tt;
    }
}
