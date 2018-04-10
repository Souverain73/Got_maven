package got.houseCards.greyjoy;

import got.GameClient;
import got.houseCards.ActiveHouseCard;
import got.model.Fraction;

/**
 * Created by Souverain73 on 25.01.2017.
 */
public class AshaGreyjoy extends ActiveHouseCard {
    @Override
    public void onPlace(Fraction fraction) {
        super.onPlace(fraction);
        if (GameClient.shared.battleDeck.isSupported(placerFraction)) return;

        bonusTowers = 1;
        bonusSwords = 2;
    }
}
