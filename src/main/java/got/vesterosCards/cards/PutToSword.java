package got.vesterosCards.cards;

import got.GameClient;
import got.ModalState;
import got.model.Action;
import got.model.Game;
import got.network.Packages;
import got.server.GameServer;
import got.server.PlayerManager;
import got.server.serverStates.StateMachine;
import got.vesterosCards.CommonVesterosCard;
import got.vesterosCards.states.OptionSelectorState;

/**
 * Created by Souverain73 on 12.04.2017.
 */
public class PutToSword extends CommonVesterosCard {
    public PutToSword(String textureName, String internalName, String title) {
        super(textureName, internalName, title);
    }

    @Override
    public void onOpenClient() {
        if (PlayerManager.getSelf().getFraction() == Game.instance().getTrack(Game.SWORD_TRACK).getFirst()) {
            OptionSelectorState oss;
            (new ModalState(oss = new OptionSelectorState(3), true, true)).run();
            GameClient.instance().send(new Packages.SelectItem(oss.getResult()));
        }else{
            super.onOpenClient();
        }
    }

    @Override
    public void onOpenServer(StateMachine stm, openParams param) {
        if (param.selection == 3) {
            super.onOpenServer(stm, param);
            return;
        }else if (param.selection == 1){
            GameServer.getServer().sendToAllTCP(new Packages.SetRestrictedActions(new Action[]{Action.DEFEND, Action.DEFENDPLUS}));
        }else if (param.selection == 2){
            GameServer.getServer().sendToAllTCP(new Packages.SetRestrictedActions(new Action[]{Action.MOVEPLUS}));
        }
        super.onOpenServer(stm, param);
    }
}
