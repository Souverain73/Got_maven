package got.wildlings.cards;

import got.GameClient;
import got.ModalState;
import got.gameStates.modals.CustomModalState;
import got.gameStates.modals.Dialogs;
import got.houseCards.Deck;
import got.houseCards.HouseCard;
import got.network.Packages;
import got.server.PlayerManager;
import got.wildlings.CommonWildlingsCard;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by КизиловМЮ on 03.07.2017.
 */
public class Gathering extends CommonWildlingsCard {
    public Gathering(String textureName, String internalName, String title) {
        super(textureName, internalName, title);
    }

    @Override
    protected void other(Packages.WildlingsData data) {
        if (PlayerManager.getSelf().getDeck().getActiveCards().size() > 1){
            CustomModalState<HouseCard> cms = Dialogs.createSelectHouseCardDialog(PlayerManager.getSelf().getDeck());
            (new ModalState(cms)).run();
            HouseCard card = cms.getResult();
            PlayerManager.getSelf().getDeck().useCard(card);
            GameClient.instance().logMessage("wildlings.dropCard", card.getTitle());
        }
    }

    @Override
    protected void looser(Packages.WildlingsData data) {
        Deck deck = PlayerManager.getSelf().getDeck();
        List<HouseCard> activeCards = PlayerManager.getSelf().getDeck().getActiveCards();
        int max = activeCards.stream().mapToInt(HouseCard::getPower).max().orElse(0);
        List<HouseCard> cardsToRemove = activeCards.stream().filter(c->c.getPower()==max).collect(Collectors.toList());
        for (HouseCard c : cardsToRemove){
            deck.useCard(c);
            GameClient.instance().logMessage("wildlings.dropCard", c.getTitle());
        }
        if (deck.getActiveCards().size() == 0){
            deck.rewindAll();
            GameClient.instance().logMessage("common.rewindAllCards");
        }
    }

    @Override
    protected void winner(Packages.WildlingsData data) {
        PlayerManager.getSelf().getDeck().rewindAll();
        GameClient.instance().logMessage("common.rewindAllCards");
    }
}
