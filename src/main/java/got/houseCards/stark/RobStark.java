package got.houseCards.stark;

import got.GameClient;
import got.ModalState;
import got.gameObjects.MapPartObject;
import got.gameObjects.battleDeck.BattleOverrides;
import got.gameStates.StateMachine;
import got.gameStates.modals.SelectRegionModal;
import got.gameStates.modals.WaitingModal;
import got.houseCards.ActiveHouseCard;
import got.model.Fraction;
import got.model.Game;
import got.network.Packages;

import java.util.List;
import java.util.stream.Collectors;

import static got.server.PlayerManager.getSelf;
import static got.utils.UI.logAction;

/**
 * Created by Souverain73 on 11.01.2017.
 */
public class RobStark extends ActiveHouseCard {
    @Override
    public void onWin() {
        super.onWin();
        (new ModalState(new SelectRegionToRetreatState(), true, true)).run();
    }

    class SelectRegionToRetreatState extends WaitingModal{
        int unitsToKill;
        @Override
        public void enter(StateMachine stm) {
            super.enter(stm);
            if (getSelf().getFraction() == placerFraction){
                List<MapPartObject> regionsToRetreat = getRegionsToRetreat();
                if (regionsToRetreat.size() == 0){
                    logAction("Противнику некуда отступать, все его войска будут убиты");
                    resumeModal();
                    return;
                }

                MapPartObject region = SelectRegionModal.selectFrom(regionsToRetreat);

                GameClient.instance().send(new Packages.SetOverrides(BattleOverrides.customRetreatRegionWithKills(region.getID(), unitsToKill)));
                resumeModal();
            }
        }

        private List<MapPartObject> getRegionsToRetreat() {
            MapPartObject battleRegion = GameClient.shared.battleDeck.getDefenderRegion();
            MapPartObject regionFrom = GameClient.shared.battleDeck.getPlayerRegion(enemyFraction);

            //todo: пофиксить баг с кораблями.
            List<MapPartObject> regionsToMove = battleRegion.getRegionsToMove(enemyFraction);
            List<MapPartObject> regionsToRetreat =
                    regionsToMove.stream().filter(r->{
                        //нельзя отступить в чужой регион
                        if (regionFrom.getFraction() != r.getFraction() && r.getFraction() != Fraction.NONE) return false;

                        return Game.instance().getSuplyTrack().canMove(regionFrom.getFraction(), regionFrom, r, regionFrom.getUnitsCount());
                    }).collect(Collectors.toList());

            if (GameClient.shared.battleDeck.isAttacker(enemyFraction)){
                regionsToRetreat.add(GameClient.shared.battleDeck.getAttackerRegion());
            }

            if (regionsToRetreat.size() > 0){
                return regionsToRetreat;
            }

            //Если нет регионов в которые можно пойти без убийства юнитов, ищем регион в который можно пойти убив минимум юнитов
            int i = GameClient.shared.battleDeck.getPlayerRegion(enemyFraction).getUnitsCount() - 1;
            for (; i>0; i--) {
                int unitsToMove = i;
                regionsToRetreat =
                        regionsToMove.stream().filter(r -> {
                            //нельзя отступить в чужой регион
                            if (regionFrom.getFraction() != r.getFraction() && r.getFraction() != Fraction.NONE)
                                return false;

                            return Game.instance().getSuplyTrack().canMove(regionFrom.getFraction(), regionFrom, r, unitsToMove);
                        }).collect(Collectors.toList());
                if (regionsToRetreat.size()!= 0){
                    unitsToKill = GameClient.shared.battleDeck.getPlayerRegion(enemyFraction).getUnitsCount() - i;
                    return regionsToRetreat;
                }
            }
            return regionsToRetreat;

        }

        @Override
        protected void onResume() {

        }
    }
}
