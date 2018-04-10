package got.gameObjects.gui;

import got.Constants;
import got.gameObjects.*;
import got.graphics.DrawSpace;
import got.graphics.text.FontTrueType;
import got.model.Game;
import got.server.PlayerManager;
import org.joml.Vector2f;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Souverain73 on 24.03.2017.
 */
public class GUIObject extends AbstractGameObject<GUIObject> {
    public static final String CURRENT_CARD_SHARED_OBJECT = "soCurrentCard";
    @Override protected GUIObject getThis() {return this;}

    private TextObject tooltipText;
    private GameLogObject glo;
    private ThronesPanel tp;
    private SuplyTrackObject sto;
    private ContainerObject upperRight;
    private ContainerObject sharedLayer;
    private Map<String, AbstractGameObject> sharedObjectsMap = new HashMap<>();

    public GUIObject(){
        upperRight = new ContainerObject().setPos(new Vector2f(Constants.SCREEN_WIDTH, 0));
        sharedLayer = new ContainerObject().setSpace(DrawSpace.SCREEN);
        addChild(sharedLayer);

        addChild(new PlayerPanelObject(PlayerManager.getSelf()));

        addChild(tooltipText = new TextObject(new FontTrueType("BKANT", 20),"Tooltip").setPos(new Vector2f(300,10)));

        addChild(glo = new GameLogObject(300, 100, 16).setPos(new Vector2f(0, Constants.SCREEN_HEIGHT-100)));

        upperRight.addChild(tp = new ThronesPanel().setPos(new Vector2f(-65, 0)));
        upperRight.addChild(sto = new SuplyTrackObject(Game.instance().getSuplyTrack()).setPos(new Vector2f(-65, 480)));

        addChild(upperRight);
        addChild(new FPSCounterObject().setPos(new Vector2f(0, 200)));

        setSpace(DrawSpace.SCREEN);
    }

    public ThroneTrackObject getThroneTrack() {
        return tp.getThroneTrack();
    }

    public void setTooltipText(String text){
        tooltipText.setText(text);
    }

    public void logMessage(String message){
        glo.addMessage(message);
    }

    public AbstractGameObject getSharedObject(String key){
        return sharedObjectsMap.get(key);
    }

    public void addSharedObject(String key, AbstractGameObject object){
        if (sharedObjectsMap.containsKey(key))
            removeSharedObject(key);
        sharedObjectsMap.put(key, object);
        sharedLayer.addChild(object);
    }

    public void removeSharedObject(String key){
        AbstractGameObject go = getSharedObject( key);
        if (go!=null){
            sharedObjectsMap.remove(go);
            sharedLayer.removeChild(go);
        }
    }

}
