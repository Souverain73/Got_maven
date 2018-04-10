package got.houseCards.baratheon;

import got.GameClient;
import got.ModalState;
import got.gameStates.StateMachine;
import got.gameStates.modals.CustomModalState;
import got.gameStates.modals.Dialogs;
import got.gameStates.modals.WaitingModal;
import got.houseCards.ActiveHouseCard;
import got.houseCards.HouseCard;
import got.model.Player;
import got.network.Packages;
import got.server.PlayerManager;

import static got.gameStates.modals.Dialogs.createSelectHouseCardDialog;
import static got.server.PlayerManager.getSelf;

/**
 * Created by Souverain73 on 24.01.2017.
 */
public class Patchface extends ActiveHouseCard {

    private Player enemyPlayer;

    @Override
    public void onBattleEnd() {
        super.onBattleEnd();
        enemyPlayer = PlayerManager.instance().getPlayerByFraction(enemyFraction);
        (new ModalState(new RemoveEnemyCard(), true, true)).run();;
    }

    class RemoveEnemyCard extends WaitingModal{
        @Override
        public void enter(StateMachine stm) {
            super.enter(stm);
            if (placerFraction == getSelf().getFraction()){
                GameClient.instance().setTooltipText("Выберите какую карту противника убрать");
                CustomModalState<HouseCard> shcd = Dialogs.createSelectHouseCardDialog(enemyPlayer.getDeck());
                (new ModalState(shcd)).run();
                HouseCard selectedCard = shcd.getResult();
                GameClient.instance().send(new Packages.RemoveHouseCard(enemyPlayer.id, selectedCard.getID()));
                resumeModal();
            }
        }

        @Override
        protected void onResume() {

        }
    }
}
