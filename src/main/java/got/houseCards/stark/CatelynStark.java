package got.houseCards.stark;

import got.GameClient;
import got.houseCards.ActiveHouseCard;
import got.model.Action;
import got.model.Fraction;

/**
 * Created by Souverain73 on 11.01.2017.
 */
public class CatelynStark extends ActiveHouseCard {
    @Override
    public void onPlace(Fraction fraction) {
        super.onPlace(fraction);
        if (GameClient.shared.battleDeck.isAttacker(fraction)) return;
        Action action = GameClient.shared.battleDeck.getDefenderRegion().getAction();
        if (action == Action.DEFEND || action == Action.DEFENDPLUS){
            bonusPower = action.getPower();
        }
    }
}
