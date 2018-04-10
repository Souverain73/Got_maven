package got.houseCards.baratheon;

import got.GameClient;
import got.ModalState;
import got.gameObjects.MapPartObject;
import got.gameObjects.battleDeck.BattleCardObject;
import got.gameObjects.battleDeck.BattleDeckObject;
import got.gameStates.StateMachine;
import got.gameStates.modals.SelectRegionModal;
import got.gameStates.modals.WaitingModal;
import got.houseCards.ActiveHouseCard;
import got.model.Unit;
import got.network.Packages;

import java.util.List;
import java.util.stream.Collectors;

import static got.server.PlayerManager.getSelf;
import static got.utils.UI.logAction;

/**
 * Created by Souverain73 on 24.01.2017.
 */
public class RenlyBaratheon extends ActiveHouseCard {

    private BattleDeckObject BDO;

    @Override
    public void onWin() {
        super.onWin();
        BDO = GameClient.shared.battleDeck;
        (new ModalState(new UpgradeSoldierModal(), true, true)).run();
    }

    class UpgradeSoldierModal extends WaitingModal{
        @Override
        public void enter(StateMachine stm) {
            super.enter(stm);
            if (placerFraction != getSelf().getFraction()) {
                return;
            }

            //получаем регионы, в которых возможен апргейд
            List<BattleCardObject> cards;
            if (BDO.isAttacker(placerFraction)){
                cards = BDO.getAttackers();
            }else{
                cards = BDO.getDefenders();
            }
            List<MapPartObject> regions = cards.stream().filter(card->{
                if (card.getFraction() != placerFraction) return false;
                for (Unit unit : card.getUnits())
                    if (unit == Unit.SOLDIER) return true;
                return false;
            }).map(BattleCardObject::getRegion).collect(Collectors.toList());

            if (!regions.isEmpty()) {
                GameClient.instance().setTooltipText("Выберите регион, в котором улучшить пехотинца");
                MapPartObject selectedRegion = SelectRegionModal.selectFrom(regions);

                Unit[] units = selectedRegion.getUnits();
                for (int i = 0; i < units.length; i++) {
                    if (units[i] == Unit.SOLDIER){
                        units[i] = Unit.KNIGHT;
                        break;
                    }
                }

                GameClient.instance().send(new Packages.ChangeUnits(selectedRegion.getID(), units));
                resumeModal();
            }else{
                logAction("Нет регионов, в которых можно улучшить пехотинца");
                resumeModal();
            }
        }

        @Override
        protected void onResume() {

        }
    }
}
