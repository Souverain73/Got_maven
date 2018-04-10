package got.houseCards.stark;

import got.houseCards.ActiveHouseCard;
import got.model.Fraction;

/**
 * Created by Souverain73 on 11.01.2017.
 */
public class Blackfish extends ActiveHouseCard {
    @Override
    public void onLoose() {
        super.onLoose();
    }

    @Override
    public void onPlace(Fraction fraction) {
        //защита от потерь
        //todo: рассмотреть другие варианты реализации
        bonusTowers = 100;
    }
}
