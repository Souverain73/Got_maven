package got.houseCards.greyjoy;

import got.GameClient;
import got.ModalState;
import got.gameStates.StateMachine;
import got.gameStates.modals.CustomModalState;
import got.gameStates.modals.Dialogs;
import got.gameStates.modals.WaitingModal;
import got.houseCards.ActiveHouseCard;
import got.houseCards.HouseCard;
import got.network.Packages;
import got.server.PlayerManager;
import got.translation.Translator;

/**
 * Created by Souverain73 on 25.01.2017.
 */
public class AeronDamphair extends ActiveHouseCard {

    class ReselectCardModal extends WaitingModal{

        @Override
        public void enter(StateMachine stm) {
            super.enter(stm);
            if (PlayerManager.getSelf().getFraction() != placerFraction) return;

            Dialogs.DialogResult res = Dialogs.showConfirmDialog(Translator.tt("AeronСhangeCard"));
            if (res == Dialogs.DialogResult.OK){
                CustomModalState<HouseCard> shcd = Dialogs.createSelectHouseCardDialog(PlayerManager.getSelf().getDeck());
                (new ModalState(shcd)).run();
                HouseCard selectedCard = shcd.getResult();
                GameClient.instance().send(new Packages.SelectHouseCard(selectedCard.getID()));
            }

            resumeModal();
        }

        @Override
        protected void onResume() {

        }
    }
}
