package got.wildlings.cards;

import got.model.ChangeAction;
import got.network.Packages;
import got.server.serverStates.StateMachine;
import got.wildlings.CommonWildlingsCard;
import got.wildlings.states.HordeLoose;
import got.wildlings.states.HordeVictory;

/**
 * Created by КизиловМЮ on 04.07.2017.
 */
public class Horde extends CommonWildlingsCard {
    public Horde(String textureName, String internalName, String title) {
        super(textureName, internalName, title);
    }

    @Override
    public void onOpenServer(StateMachine stm, Packages.WildlingsData data) {
        if (data.victory){
            stm.changeState(new HordeVictory.ServerState(), ChangeAction.SET);
        }else{
            stm.changeState(new HordeLoose.ServerState(), ChangeAction.SET);
        }
    }
}
