package got.houseCards.tyrell;

import got.GameClient;
import got.ModalState;
import got.gameObjects.MapPartObject;
import got.gameObjects.battleDeck.BattleCardObject;
import got.gameObjects.battleDeck.BattleDeckObject;
import got.gameStates.StateMachine;
import got.gameStates.modals.SelectRegionModal;
import got.gameStates.modals.WaitingModal;
import got.houseCards.ActiveHouseCard;
import got.model.Fraction;
import got.model.Unit;
import got.network.Packages;

import java.util.List;
import java.util.stream.Collectors;

import static got.server.PlayerManager.getSelf;
import static got.utils.UI.logAction;

/**
 * Created by Souverain73 on 25.01.2017.
 */
public class MaceTyrell extends ActiveHouseCard {

    private BattleDeckObject BDO;

    @Override
    public void onPlace(Fraction fraction) {
        super.onPlace(fraction);
        BDO = GameClient.shared.battleDeck;
        (new ModalState(new KillEnemySoldier(), true, true)).run();
    }

    class KillEnemySoldier extends WaitingModal{
        @Override
        public void enter(StateMachine stm) {
            super.enter(stm);
            if (placerFraction != getSelf().getFraction()) {
                return;
            }

            List<BattleCardObject> cards;
            if (BDO.isAttacker(placerFraction)){
                cards = BDO.getDefenders();
            }else{
                cards = BDO.getAttackers();
            }

            List<MapPartObject> regions = cards.stream().filter(card->{
                for (Unit unit : card.getUnits())
                    if (unit == Unit.SOLDIER) return true;
                return false;
            }).map(BattleCardObject::getRegion).collect(Collectors.toList());

            if (!regions.isEmpty()) {
                GameClient.instance().setTooltipText("Выберите регион, в котором убить пехотинца");
                MapPartObject selectedRegion = SelectRegionModal.selectFrom(regions);
                GameClient.instance().send(new Packages.KillUnit(selectedRegion.getID(), Unit.SOLDIER));
                resumeModal();
            }else{
                logAction("Нет пехотинцев, некого убивать");
                resumeModal();
            }
        }

        @Override
        protected void onResume() {

        }
    }
}
