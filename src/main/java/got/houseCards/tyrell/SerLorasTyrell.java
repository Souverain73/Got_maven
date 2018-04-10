package got.houseCards.tyrell;

import got.GameClient;
import got.gameObjects.battleDeck.BattleDeckObject;
import got.houseCards.ActiveHouseCard;
import got.model.Action;
import got.model.Fraction;
import got.network.Packages;

import static got.server.PlayerManager.getSelf;

/**
 * Created by Souverain73 on 25.01.2017.
 */
public class SerLorasTyrell extends ActiveHouseCard {

    private BattleDeckObject BDO;
    private Action moveAction;
    private boolean win = false;

    @Override
    public void onPlace(Fraction fraction) {
        super.onPlace(fraction);
        BDO = GameClient.shared.battleDeck;

        if (BDO.isAttacker(placerFraction)){
            moveAction = BDO.getAttackerRegion().getAction();
        }
    }

    @Override
    public void onWin() {
        super.onWin();
        win = true;
    }

    @Override
    public void onBattleEnd() {
        super.onBattleEnd();
        if (getSelf().getFraction() != placerFraction)
            return;

        if (win && BDO.isAttacker(placerFraction)){
            GameClient.instance().send(new Packages.SetAction(BDO.getDefenderRegion().getID(), moveAction));
        }
    }
}
