package got.houseCards.greyjoy;

import got.GameClient;
import got.gameObjects.MapPartObject;
import got.gameObjects.battleDeck.BattleDeckObject;
import got.houseCards.ActiveHouseCard;
import got.model.Fraction;

/**
 * Created by Souverain73 on 25.01.2017.
 */
public class TheonGreyjoy extends ActiveHouseCard {
    @Override
    public void onPlace(Fraction fraction) {
        super.onPlace(fraction);
        BattleDeckObject BDO = GameClient.shared.battleDeck;
        if (!BDO.isDefender(placerFraction)) return;

        MapPartObject region = BDO.getPlayerRegion(placerFraction);

        if (region.getBuildingLevel() == 0) return;

        bonusPower = 1;
        bonusSwords = 1;
    }
}
