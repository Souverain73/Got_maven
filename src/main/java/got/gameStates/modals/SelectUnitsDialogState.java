package got.gameStates.modals;

import got.Constants;
import got.GameClient;
import got.InputManager;
import got.gameObjects.GameObject;
import got.gameObjects.interfaceControls.ImageButton;
import got.gameObjects.ImageObject;
import got.gameObjects.interfaceControls.ToggleImageButton;
import got.gameStates.AbstractGameState;
import got.gameStates.StateMachine;
import got.graphics.DrawSpace;
import got.graphics.TextureManager;
import got.interfaces.IClickListener;
import got.model.Unit;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Souverain73 on 22.11.2016.
 */
public class SelectUnitsDialogState extends AbstractGameState implements IClickListener{
    public static final int WIDTH = 220;
    public static final int HEIGHT = 115;
    List<ToggleImageButton> buttons = new ArrayList<>(4);
    Unit[] units;
    boolean ok = false;

    @Override
    public void enter(StateMachine stm) {
        this.stm = stm;
    }

    public SelectUnitsDialogState(Unit[] unitsToSelect){
        this(unitsToSelect, 0, 0);
    }

    public SelectUnitsDialogState(Unit[] unitsToSelect, int minUnits, int maxUnits){
        float posx = (Constants.SCREEN_WIDTH - WIDTH) /2;
        float posy = (Constants.SCREEN_HEIGHT - HEIGHT) /2;
        this.units = unitsToSelect;

        int maxUnitsToKill = Math.min(maxUnits, unitsToSelect.length);
        int minUnitsToKill = Math.max(0, minUnits);
        //background
        ImageObject bg = new ImageObject(TextureManager.instance().loadTexture("unitsMenuBg.png"),
                WIDTH, 60).setPos(new Vector2f(posx, posy)).setSpace(DrawSpace.SCREEN);
        addObject(bg);

        float x = posx + 6;
        float y = posy + 5;

        //buttons for select units
        for (int i = 0; i < unitsToSelect.length; i++) {
            Unit unit = unitsToSelect[i];

            ToggleImageButton btn = new ToggleImageButton(unit.getTexture(),
                    (int)x, (int)y,
                    (int) Constants.UNIT_SIZE,
                    (int)Constants.UNIT_SIZE,
                    unit);

            btn.setSpace(DrawSpace.SCREEN);
            addObject(btn);
            buttons.add(btn);
            x+=Constants.UNIT_SIZE + Constants.UNIT_STEP;
        }

        //action buttons: OK
        addObject(new ImageButton("buttons/select.png", (int)posx, (int)posy+60, 110, 55, null).setSpace(DrawSpace.SCREEN)
                .setCallback((sender, param)->{
                    int selectedCount = getSelectedUnits().length;
                    if (maxUnitsToKill != 0 && (selectedCount < minUnitsToKill || selectedCount > maxUnitsToKill)) return;
                    this.ok = true;
                    this.close();
                }));

        //action buttons: Cancel
        addObject(new ImageButton("buttons/cancel.png", (int)posx + 110, (int)posy+60, 110, 55, null).setSpace(DrawSpace.SCREEN)
                .setCallback((sender, param)->{
                    this.ok = false;
                    this.close();
                }));
    }

    public Unit[] getSelectedUnits(){
        List<Unit> result = new ArrayList<>(4);

        for (int i = 0; i < buttons.size(); i++) {
             if (buttons.get(i).isToggled()){
                 result.add(units[i]);
             }
        }

        return result.toArray(new Unit[0]);
    }

    public boolean isOk() {
        return ok;
    }


    @Override
    public void click(InputManager.ClickEvent event) {
        GameObject sender = event.getTarget();
        if (sender == null){
            close();
        }
    }

    private void close() {
        GameClient.instance().closeModal();
    }
}
