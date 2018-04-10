package got.houseCards.baratheon;

import got.houseCards.ActiveHouseCard;
import got.houseCards.HouseCard;
import got.houseCards.HouseCardsLoader;
import got.houseCards.Deck;
import got.model.Fraction;
import got.model.Player;
import got.server.PlayerManager;

/**
 * Created by Souverain73 on 24.01.2017.
 */
public class SerDawosSeaworth extends ActiveHouseCard {
    @Override
    public void onPlace(Fraction fraction) {
        super.onPlace(fraction);
        Player placer = PlayerManager.instance().getPlayerByFraction(placerFraction);
        Deck placerDeck = placer.getDeck();
        HouseCard stanis = HouseCardsLoader.instance().getCardByTitle("Stanis Baratheon");
        if (placerDeck.getUsedCards().contains(stanis)){
            bonusPower = 1;
            bonusSwords = 1;
        }
    }
}
