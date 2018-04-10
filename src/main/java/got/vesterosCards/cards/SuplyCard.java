package got.vesterosCards.cards;

import got.model.ChangeAction;
import got.server.serverStates.StateMachine;
import got.vesterosCards.CommonVesterosCard;
import got.vesterosCards.states.CollectSuply;

/**
 * Created by Souverain73 on 12.04.2017.
 */
public class SuplyCard extends CommonVesterosCard {
    public SuplyCard(String textureName, String internalName, String title) {
        super(textureName, internalName, title);
    }

    @Override
    public void onOpenServer(StateMachine stm, openParams param) {
        super.onOpenServer(stm, param);
        stm.changeState(new CollectSuply.ServerState(), ChangeAction.SET);
    }
}
