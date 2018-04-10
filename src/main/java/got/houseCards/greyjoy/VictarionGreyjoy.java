package got.houseCards.greyjoy;

import got.GameClient;
import got.gameObjects.battleDeck.BattleCardObject;
import got.gameObjects.battleDeck.BattleDeckObject;
import got.houseCards.ActiveHouseCard;
import got.model.Fraction;
import got.model.Unit;

/**
 * Created by Souverain73 on 25.01.2017.
 */
public class VictarionGreyjoy extends ActiveHouseCard {
    @Override
    public void onPlace(Fraction fraction) {
        super.onPlace(fraction);
        VictarionEffect salladhorEffect = new VictarionEffect();
        BattleDeckObject BDO = GameClient.shared.battleDeck;

        if (!BDO.isAttacker(placerFraction)) return;

        BDO.getAttackers().forEach(card->{
            if (card.getFraction() != placerFraction){
                card.addEffect(salladhorEffect);
            }
        });
    }

    private class VictarionEffect implements BattleCardObject.UnitEffect{

        @Override
        public int getAffectedPower(int power) {
            return 2;
        }

        @Override
        public boolean isAffected(Unit unit) {
            return unit == Unit.SHIP;
        }

        @Override
        public int getPriority() {
            return 0;
        }
    }
}
