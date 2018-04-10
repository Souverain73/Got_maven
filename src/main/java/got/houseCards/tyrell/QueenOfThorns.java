package got.houseCards.tyrell;

import got.GameClient;
import got.ModalState;
import got.gameObjects.MapPartObject;
import got.gameObjects.battleDeck.BattleDeckObject;
import got.gameStates.StateMachine;
import got.gameStates.modals.SelectRegionModal;
import got.gameStates.modals.WaitingModal;
import got.houseCards.ActiveHouseCard;
import got.model.Fraction;
import got.network.Packages;

import java.util.List;
import java.util.stream.Collectors;

import static got.server.PlayerManager.getSelf;
import static got.utils.UI.logAction;

/**
 * Created by Souverain73 on 25.01.2017.
 */
public class QueenOfThorns extends ActiveHouseCard {

    private BattleDeckObject BDO;

    @Override
    public void onPlace(Fraction fraction) {
        super.onPlace(fraction);
        BDO = GameClient.shared.battleDeck;
        (new ModalState(new RemoveEnemyActionModal(), true, true)).run();
    }

    class RemoveEnemyActionModal extends WaitingModal{
        @Override
        public void enter(StateMachine stm) {
            super.enter(stm);
            if(getSelf().getFraction() != placerFraction)
                return;
            List<MapPartObject> regions = BDO.getDefenderRegion().getNeighbors().stream()
                    .filter(
                            region->region.getFraction() == enemyFraction &&
                                    region != BDO.getAttackerRegion() &&
                                    region.getAction() != null
                    ).collect(Collectors.toList());
            if (!regions.isEmpty()){
                GameClient.instance().setTooltipText("Выберите какой приказ убрать");
                MapPartObject selectedRegion = SelectRegionModal.selectFrom(regions);
                GameClient.instance().send(new Packages.SetAction(selectedRegion.getID(), null));
                resumeModal();
            }else{
                logAction("Нет приказов, которые можно убрать");
                resumeModal();
            }
        }

        @Override
        protected void onResume() {
        }
    }
}
