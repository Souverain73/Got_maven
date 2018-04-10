package got.houseCards.baratheon;

import got.GameClient;
import got.houseCards.ActiveHouseCard;
import got.model.Fraction;
import got.model.Game;

/**
 * Created by Souverain73 on 24.01.2017.
 */
public class StanisBaratheon extends ActiveHouseCard {
    @Override
    public void onPlace(Fraction fraction) {
        super.onPlace(fraction);
        if (Game.instance().getTrack(Game.THRONE_TRACK).compare(placerFraction, enemyFraction) < 0){
            bonusPower += 1;
        }
    }
}
