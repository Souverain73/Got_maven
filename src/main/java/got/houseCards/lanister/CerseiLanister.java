package got.houseCards.lanister;

import got.GameClient;
import got.InputManager;
import got.ModalState;
import got.gameObjects.MapPartObject;
import got.gameStates.StateMachine;
import got.gameStates.modals.WaitingModal;
import got.houseCards.ActiveHouseCard;
import got.interfaces.IClickListener;
import got.model.Fraction;
import got.network.Packages;

import static got.server.PlayerManager.getSelf;
import static got.utils.UI.logAction;
import static got.utils.UI.logSystem;

/**
 * Created by Souverain73 on 11.01.2017.
 */
public class CerseiLanister extends ActiveHouseCard {
    @Override
    public void onPlace(Fraction fraction) {
        super.onPlace(fraction);
    }

    @Override
    public void onWin() {
        super.onWin();
        (new ModalState(new RemoveActionState(placerFraction, enemyFraction), true, true)).run();
    }

    private class RemoveActionState extends WaitingModal implements IClickListener{
        private Fraction placerFraction;
        private Fraction enemyFraction;

        public RemoveActionState(Fraction placerFraction, Fraction enemyFraction) {
            this.placerFraction = placerFraction;
            this.enemyFraction = enemyFraction;
        }

        @Override
        public void enter(StateMachine stm) {
            super.enter(stm);
            if (placerFraction == getSelf().getFraction()) {
                GameClient.instance().setTooltipText("houseCards.CerseiSelect");
                enableRegionsWithActions(enemyFraction);
            } else {
                GameClient.instance().setTooltipText("houseCards.CerseiWait");
                GameClient.shared.gameMap.disableAllRegions();
            }
        }
        private void enableRegionsWithActions(Fraction fraction) {
            if (GameClient.shared.gameMap.setEnabledByCondition(reg->
                reg.getFraction() == fraction && reg.getAction() != null
            ) == 0){
                GameClient.instance().logMessage("houseCards.CerseiNothingToRemove");
                resumeModal();
            }
        }

        @Override
        public void click(InputManager.ClickEvent event) {
            if (event.getTarget() instanceof MapPartObject) {
                MapPartObject region = (MapPartObject) event.getTarget();
                GameClient.instance().send(new Packages.SetAction(region.getID(), null));
                GameClient.shared.gameMap.disableAllRegions();
                resumeModal();
            }
        }



        @Override
        protected void onResume() {
            logSystem("Waiting modal state resumed");
        }
    }
}
