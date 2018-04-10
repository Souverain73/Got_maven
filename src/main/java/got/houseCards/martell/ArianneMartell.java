package got.houseCards.martell;

import got.GameClient;
import got.gameObjects.battleDeck.BattleOverrides;
import got.houseCards.ActiveHouseCard;
import got.network.Packages;
import got.server.PlayerManager;

/**
 * Created by Souverain73 on 25.01.2017.
 */
public class ArianneMartell extends ActiveHouseCard {
    @Override
    public void onLoose() {
        super.onLoose();
        if (PlayerManager.getSelf().getFraction() != placerFraction) return;

        GameClient.instance().send(new Packages.SetOverrides(BattleOverrides.noMoveAttacker()));
    }
}
