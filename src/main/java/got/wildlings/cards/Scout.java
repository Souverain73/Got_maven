package got.wildlings.cards;

import got.GameClient;
import got.network.Packages;
import got.server.PlayerManager;
import got.wildlings.CommonWildlingsCard;

/**
 * Created by КизиловМЮ on 30.06.2017.
 */
public class Scout extends CommonWildlingsCard {
    public Scout(String textureName, String internalName, String title) {
        super(textureName, internalName, title);
    }

    @Override
    protected void other(Packages.WildlingsData data) {
        int money = Math.max(PlayerManager.getSelf().getMoney(), 2);
        PlayerManager.getSelf().addMoney(money);
        GameClient.instance().logMessage("common.looseMoney", money);
    }

    @Override
    protected void looser(Packages.WildlingsData data) {
        PlayerManager.getSelf().addMoney(-2);
        GameClient.instance().logMessage("wildlings.looseAllMoney");
    }

    @Override
    protected void winner(Packages.WildlingsData data) {
        PlayerManager.getSelf().addMoney(data.maxBet);
        GameClient.instance().logMessage("wildlings.returnMoney", data.maxBet);
    }
}
