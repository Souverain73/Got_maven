package got.gameStates.test;

import got.InputManager;
import got.gameObjects.DebugMapPart;
import got.gameObjects.GameMapObject;
import got.gameObjects.MapPartObject;
import got.gameObjects.interfaceControls.ImageButton;
import got.gameStates.AbstractGameState;
import got.gameStates.StateMachine;
import got.graphics.DrawSpace;
import got.interfaces.IClickListener;
import got.utils.LoaderParams;

/**
 * Created by Souverain73 on 13.02.2017.
 */
public class MapViewState extends AbstractGameState implements IClickListener{
    private static final String name = "MainState";
    private static final String MAP_FILE = "data/map.xml";

    @Override
    public void enter(StateMachine stm) {
        super.enter(stm);
        GameMapObject map = new GameMapObject();
        map.Debug();
        map.init(new LoaderParams(new String[]{"filename", MAP_FILE}));

        addObject(map);

        addObject(new ImageButton("buttons/power.png", 0, 0, 100, 50, null).setSpace(DrawSpace.SCREEN).setCallback((sender, params)->{
            map.forEachRegion(region->{
                DebugMapPart dbr = (DebugMapPart) region;
                dbr.toggleAction();
            });
        }));

        addObject(new ImageButton("buttons/units.png", 0, 50, 100, 50, null).setSpace(DrawSpace.SCREEN).setCallback((sender, params)->{
            map.forEachRegion(region->{
                DebugMapPart dbr = (DebugMapPart) region;
                dbr.toggleUnits();
            });
        }));

        addObject(new ImageButton("buttons/plus.png", 0, 100, 50, 50, null).setSpace(DrawSpace.SCREEN).setCallback((sender, params)->{
            map.forEachRegion(region->{
                DebugMapPart dbr = (DebugMapPart) region;
                dbr.toggleToken();
            });
        }));
    }

    @Override
    public void click(InputManager.ClickEvent event) {
        if (event.getTarget() instanceof MapPartObject) {
            DebugMapPart region = (DebugMapPart) event.getTarget();
            System.out.println(region.toString());
        }
    }
}
