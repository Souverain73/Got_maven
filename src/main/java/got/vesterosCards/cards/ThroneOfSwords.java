package got.vesterosCards.cards;

import got.GameClient;
import got.ModalState;
import got.model.ChangeAction;
import got.model.Game;
import got.network.Packages;
import got.server.PlayerManager;
import got.server.serverStates.StateMachine;
import got.vesterosCards.CommonVesterosCard;
import got.vesterosCards.states.CollectSuply;
import got.vesterosCards.states.OptionSelectorState;
import got.vesterosCards.states.CollectUnits;

/**
 * Created by Souverain73 on 12.04.2017.
 */
public class ThroneOfSwords extends CommonVesterosCard {
    public ThroneOfSwords(String textureName, String internalName, String title) {
        super(textureName, internalName, title);
    }

    @Override
    public void onOpenClient() {
        if (PlayerManager.getSelf().getFraction() == Game.instance().getTrack(Game.THRONE_TRACK).getFirst()) {
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
            stm.changeState(new CollectSuply.ServerState(), ChangeAction.SET);
        }else if (param.selection == 2){
            stm.changeState(new CollectUnits.ServerState(), ChangeAction.SET);
        }
    }

}
