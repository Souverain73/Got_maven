package got.wildlings.cards;

import got.GameClient;
import got.ModalState;
import got.gameStates.modals.CustomModalState;
import got.gameStates.modals.Dialogs;
import got.houseCards.Deck;
import got.houseCards.HouseCard;
import got.model.ChangeAction;
import got.network.Packages;
import got.server.PlayerManager;
import got.server.serverStates.StateMachine;
import got.wildlings.CommonWildlingsCard;
import got.wildlings.states.RidersLoose;

/**
 * Created by КизиловМЮ on 06.07.2017.
 */
public class Riders extends CommonWildlingsCard {
    public Riders(String textureName, String internalName, String title) {
        super(textureName, internalName, title);
    }

    @Override
    protected void winner(Packages.WildlingsData data) {
        Deck deck = PlayerManager.getSelf().getDeck();
        if (deck.getUsedCards().size() == 0) return;

        CustomModalState<HouseCard> shcd = Dialogs.createSelectHouseCardDialog(deck.getUsedCards());
        (new ModalState(shcd)).run();
        HouseCard selectedCard = shcd.getResult();
        GameClient.instance().send(new Packages.SelectHouseCard(selectedCard.getID()));
    }

    @Override
    public void onOpenServer(StateMachine stm, Packages.WildlingsData data) {
        if (!data.victory){
            stm.changeState(new RidersLoose.ServerState(), ChangeAction.SET);
        }else{
            super.onOpenServer(stm, data);
        }
    }
}
