package got.houseCards;

/**
 * Created by Souverain73 on 11.01.2017.
 */
public class ActiveHouseCard extends CommonHouseCard {
    protected int bonusPower;
    protected int bonusSwords;
    protected int bonusTowers;
    @Override
    public int getPower() {
        return super.getPower() + bonusPower;
    }

    @Override
    public int getTowers() {
        return super.getTowers() + bonusTowers;
    }

    @Override
    public int getSwords() {
        return super.getSwords() + bonusSwords;
    }
}
