package got.houseCards.martell;

import got.GameClient;
import got.houseCards.ActiveHouseCard;
import got.model.Fraction;

/**
 * Created by Souverain73 on 25.01.2017.
 */
public class NymeriaSand extends ActiveHouseCard{
    @Override
    public void onPlace(Fraction fraction) {
        super.onPlace(fraction);
        if (GameClient.shared.battleDeck.isAttacker(placerFraction)){
            bonusSwords = 1;
        }else if(GameClient.shared.battleDeck.isDefender(placerFraction)){
            bonusTowers = 1;
        }
    }
}
