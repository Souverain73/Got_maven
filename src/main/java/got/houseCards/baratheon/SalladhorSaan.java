package got.houseCards.baratheon;

import got.GameClient;
import got.gameObjects.battleDeck.BattleCardObject;
import got.gameObjects.battleDeck.BattleDeckObject;
import got.houseCards.ActiveHouseCard;
import got.model.Fraction;
import got.model.Unit;

/**
 * Created by Souverain73 on 24.01.2017.
 */
public class SalladhorSaan extends ActiveHouseCard {
    @Override
    public void onPlace(Fraction fraction) {
        super.onPlace(fraction);
        SalladhorEffect salladhorEffect = new SalladhorEffect();
        BattleDeckObject BDO = GameClient.shared.battleDeck;
        if (!BDO.isSupported(placerFraction)) {
            //поддержки не было, карта не работает
            return;
        }
        BDO.getAttackers().forEach(card->{
            if (card.getFraction() != placerFraction){
                card.addEffect(salladhorEffect);
            }
        });

        BDO.getDefenders().forEach(card->{
            if (card.getFraction() != placerFraction){
                card.addEffect(salladhorEffect);
            }
        });
    }

    private class SalladhorEffect implements BattleCardObject.UnitEffect{

        @Override
        public int getAffectedPower(int power) {
            return 0;
        }

        @Override
        public boolean isAffected(Unit unit) {
            return unit == Unit.SHIP;
        }

        @Override
        public int getPriority() {
            return 10;
        }
    }
}
