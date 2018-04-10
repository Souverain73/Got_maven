package got.houseCards.lanister;

import got.GameClient;
import got.gameObjects.battleDeck.BattleCardObject;
import got.houseCards.ActiveHouseCard;
import got.model.Fraction;
import got.model.Unit;

import java.util.Arrays;

/**
 * Created by Souverain73 on 11.01.2017.
 */

public class SerKevanLanister extends ActiveHouseCard {
    @Override
    public void onPlace(Fraction fraction) {
        super.onPlace(fraction);

        if(GameClient.shared.battleDeck.isAttacker(fraction)){
            GameClient.shared.battleDeck.getAttackers().forEach(card->{
                card.addEffect(new BattleCardObject.UnitEffect() {
                    @Override
                    public int getAffectedPower(int power) {
                        return 2;
                    }

                    @Override
                    public boolean isAffected(Unit unit) {
                        return unit==Unit.SOLDIER;
                    }

                    @Override
                    public int getPriority() {
                        return 0;
                    }
                });
            });
        }
    }
}
