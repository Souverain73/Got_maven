package got.vesterosCards.cards;

import got.GameClient;
import got.ModalState;
import got.model.ChangeAction;
import got.model.Game;
import got.network.Packages;
import got.server.PlayerManager;
import got.server.serverStates.StateMachine;
import got.vesterosCards.CommonVesterosCard;
import got.vesterosCards.states.*;

/**
 * Created by Souverain73 on 03.05.2017.
 */
public class BlackWings extends CommonVesterosCard {
    public BlackWings(String textureName, String internalName, String title) {
        super(textureName, internalName, title);
    }

    @Override
    public void onOpenClient() {
        if (PlayerManager.getSelf().getFraction() == Game.instance().getTrack(Game.CROWN_TRACK).getFirst()) {
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
        }else if (param.selection == 1){
            stm.changeState(new BattleOfKings.ServerState(), ChangeAction.SET);
        }else if (param.selection == 2){
            stm.changeState(new CollectInfluence.ServerState(), ChangeAction.SET);
        }
    }
}
