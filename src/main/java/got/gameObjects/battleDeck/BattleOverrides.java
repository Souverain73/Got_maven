package got.gameObjects.battleDeck;

/**
 * Created by Souverain73 on 23.01.2017.
 */
public class BattleOverrides {
    public boolean customKillCount;
    public int unitsToKill;

    public boolean customRetreat;
    public int regionToRetreat;

    public boolean noMoveAttacker;

    public static BattleOverrides customKillCount(int count){
        BattleOverrides bo = new BattleOverrides();
        bo.customKillCount = true;
        bo.unitsToKill = count;
        return bo;
    }

    public static BattleOverrides customRetreatRegion(int region){
        BattleOverrides bo = new BattleOverrides();
        bo.customRetreat = true;
        bo.regionToRetreat = region;
        return bo;
    }


    public static BattleOverrides customRetreatRegionWithKills(int region, int killsCount){
        BattleOverrides bo = new BattleOverrides();
        bo.customRetreat = true;
        bo.regionToRetreat = region;
        bo.customKillCount = true;
        bo.unitsToKill = killsCount;
        return bo;
    }

    public static BattleOverrides noMoveAttacker(){
        BattleOverrides bo = new BattleOverrides();
        bo.noMoveAttacker = true;
        return bo;
    }
}
