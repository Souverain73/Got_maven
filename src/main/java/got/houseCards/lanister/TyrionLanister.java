package got.houseCards.lanister;

import com.esotericsoftware.kryonet.Connection;
import got.GameClient;
import got.ModalState;
import got.gameStates.StateMachine;
import got.gameStates.modals.CustomModalState;
import got.gameStates.modals.Dialogs;
import got.gameStates.modals.WaitingModal;
import got.houseCards.ActiveHouseCard;
import got.houseCards.HouseCard;
import got.model.Fraction;
import got.network.Packages;
import got.server.PlayerManager;
import got.translation.Translator;
import org.joml.Vector2f;

import static got.server.PlayerManager.getSelf;

/**
 * Created by Souverain73 on 11.01.2017.
 */
public class TyrionLanister extends ActiveHouseCard {
    @Override
    public void onPlace(Fraction fraction) {
        super.onPlace(fraction);

        (new ModalState(new ConfirmUseDialog(placerFraction, enemyFraction), true, true)).run();

    }

    private class ConfirmUseDialog extends WaitingModal{
        Fraction placerFraction;
        Fraction enemyFraction;

        public ConfirmUseDialog(Fraction placerFraction, Fraction enemyFraction) {
            this.placerFraction = placerFraction;
            this.enemyFraction = enemyFraction;
        }

        @Override
        public void enter(StateMachine stm) {
            if (getSelf().getFraction() == placerFraction){
                GameClient.instance().setTooltipText("common.Decide");
                Dialogs.DialogResult res = Dialogs.showConfirmDialog("houseCards.TyrionRemoveCard");
                if (res == Dialogs.DialogResult.OK){
                    GameClient.instance().send(new Packages.Confirm());
                }else{
                    resumeModal();
                }
            }else{
                GameClient.instance().setTooltipText("houseCards.TyrionWait");
            }
        }

        @Override
        public void recieve(Connection connection, Object pkg) {
            super.recieve(connection, pkg);
            if (pkg instanceof Packages.PlayerConfirm) {
                if (getSelf().getFraction() == enemyFraction) {

                    CustomModalState<HouseCard> cms = Dialogs.createSelectHouseCardDialog(PlayerManager.getSelf().getDeck());
                    (new ModalState(cms)).run();
                    HouseCard selectedCard = cms.getResult();
                    GameClient.instance().send(new Packages.SelectHouseCard(selectedCard.getID()));
                    resumeModal();
                }
            }
        }

        @Override
        protected void onResume() {
        }
    }
}
