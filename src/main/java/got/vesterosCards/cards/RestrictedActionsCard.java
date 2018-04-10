package got.vesterosCards.cards;

import got.model.Action;
import got.network.Packages;
import got.server.GameServer;
import got.server.serverStates.StateMachine;
import got.vesterosCards.CommonVesterosCard;

/**
 * Created by Souverain73 on 12.04.2017.
 */
public class RestrictedActionsCard extends CommonVesterosCard{
    private Action[] restrictedActions;

    public RestrictedActionsCard(String textureName, String internalName, String title, Action[] restrictedActions) {
        super(textureName, internalName, title);
        this.restrictedActions = restrictedActions;
    }

    @Override
    public void onOpenServer(StateMachine stm, openParams param) {
        GameServer.getServer().sendToAllTCP(new Packages.SetRestrictedActions(restrictedActions));
        super.onOpenServer(stm, param);
    }
}
