package got.gameStates.modals;

import got.GameClient;
import got.InputManager;
import got.ModalState;
import got.gameObjects.MapPartObject;
import got.gameStates.AbstractGameState;
import got.gameStates.StateMachine;
import got.interfaces.IClickListener;

import java.util.List;

/**
 * Created by Souverain73 on 23.01.2017.
 */
public class SelectRegionModal extends AbstractGameState implements IClickListener{

    public static MapPartObject selectFrom(List<MapPartObject> regions){
        SelectRegionModal srm = new SelectRegionModal(regions);
        (new ModalState(srm, true, true)).run();
        return srm.getResult();
    }

    List<MapPartObject> regonsToSelect;
    MapPartObject result;

    private  SelectRegionModal(List<MapPartObject> regions){
        this.regonsToSelect = regions;
    }

    @Override
    public void enter(StateMachine stm) {
        super.enter(stm);
        GameClient.shared.gameMap.disableAllRegions();
        regonsToSelect.forEach(r->r.setEnabled(true));
    }

    @Override
    public void click(InputManager.ClickEvent event) {
        if (event.getTarget() instanceof MapPartObject) {
            result = (MapPartObject) event.getTarget();
            close();
        }
    }

    public MapPartObject getResult() {
        return result;
    }

    private void close() {
        GameClient.instance().closeModal();
    }
}
