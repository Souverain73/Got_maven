package got.vesterosCards.cards;

import got.model.ChangeAction;
import got.server.serverStates.StateMachine;
import got.vesterosCards.CommonVesterosCard;
import got.vesterosCards.states.BattleOfKings;

/**
 * Created by Souverain73 on 03.05.2017.
 */
public class BattleOfKingsCard extends CommonVesterosCard {
    public BattleOfKingsCard(String textureName, String internalName, String title) {
        super(textureName, internalName, title);
    }

    @Override
    public void onOpenServer(StateMachine stm, openParams param) {
        super.onOpenServer(stm, param);
        stm.changeState(new BattleOfKings.ServerState(), ChangeAction.SET);
    }
}
