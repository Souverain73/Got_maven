package got.gameObjects.gui;

import got.Constants;
import got.InputManager;
import got.ModalState;
import got.gameObjects.AbstractGameObject;
import got.gameObjects.GameObject;
import got.gameObjects.HouseCardsListObject;
import got.gameObjects.interfaceControls.ImageButton;
import got.gameObjects.interfaceControls.TransparentButton;
import got.gameStates.modals.CustomModalState;
import got.graphics.DrawSpace;
import got.graphics.Effect;
import got.graphics.GraphicModule;
import got.houseCards.Deck;
import got.houseCards.HouseCard;
import got.model.Fraction;
import got.model.Track;
import got.server.PlayerManager;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.stream.IntStream;

import static sun.misc.PostVMInitHook.run;

/**
 * Created by Souverain73 on 24.03.2017.
 */
public class ThroneTrackObject extends TrackObject {
    public enum HighlightMode{
        HIGHLIGHT_CURRENT_PLAYER,
        HIGHLIGHT_READY,
        NO_HIGHLIGHT
    }

    private HighlightMode hm;

    public ThroneTrackObject(Track track) {
        super(track);
        hm = HighlightMode.NO_HIGHLIGHT;
        IntStream.range(0,6).forEach(i ->
        addChild(new TransparentButton(13, 82 + i * 65, 45, 45, i).setCallback(this::clickCallback)));
    }

    private void clickCallback(GameObject sender, Object param){
        int index = (int) param;
        Fraction[] fractions = track.getData();
        if (index > fractions.length) return;
        Fraction fraction = fractions[index];
        showHouseCards(PlayerManager.instance().getPlayerByFraction(fraction).getDeck());
    }

    public void setHighlightMode(HighlightMode hm) {
        if (hm == null) return;
        this.hm = hm;
    }

    @Override
    protected void drawFraction(Vector2f cp, int position, Fraction fraction) {
        if (hm == HighlightMode.HIGHLIGHT_READY){
            if (PlayerManager.instance().getPlayerByFraction(fraction).isReady()){
                GraphicModule.instance().setEffect(new Effect().Overlay(new Vector3f(0.0f, 0.3f, 0.0f)));
            }
        }else if(hm == HighlightMode.HIGHLIGHT_CURRENT_PLAYER){
            Fraction cf = PlayerManager.instance().getCurrentPlayer().getFraction();
            if (cf == fraction){
                GraphicModule.instance().setEffect(new Effect().Overlay(new Vector3f(0.0f, 0.3f, 0.0f)));
            }
        }
        super.drawFraction(cp, position, fraction);
        GraphicModule.instance().resetEffect();
    }

    private void showHouseCards(Deck deck){
        CustomModalState<HouseCard> cms = new CustomModalState<HouseCard>(null, true){
            @Override
            public void click(InputManager.ClickEvent event) {
                if (event.getTarget() == null || !(((AbstractGameObject)event.getTarget()).getParent() instanceof HouseCardsListObject)){
                    close();
                }
            }
        };
        HouseCardsListObject hcso = new HouseCardsListObject(deck).setSpace(DrawSpace.SCREEN);
        hcso.setPos(new Vector2f(
                (Constants.SCREEN_WIDTH - hcso.getW()) / 2, 200
        ));

        cms.addObject(hcso);
        (new ModalState(cms, true, true)).run();
    }
}
