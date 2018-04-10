package got.houseCards.stark;

import got.houseCards.ActiveHouseCard;

import static got.utils.UI.logAction;

/**
 * Created by Souverain73 on 11.01.2017.
 */
public class RooseBolton extends ActiveHouseCard{
    @Override
    public void onLoose() {
        super.onLoose();
        owner().getDeck().rewindAll();
        logAction("houseCards.PlayerRewindAll", owner().getNickname());
    }
}
