package got.server.serverStates;

import com.esotericsoftware.kryonet.Connection;
import got.gameStates.StateID;
import got.model.ChangeAction;
import got.server.GameServer;
import got.server.serverStates.base.ServerState;

/**
 * Created by Souverain73 on 09.03.2017.
 */
public class GameConfigState implements ServerState {
    private StateMachine stm;
    private boolean onDemand;

    public GameConfigState(boolean isOnDemand){
        onDemand = isOnDemand;
    }

    @Override
    public String getName() {
        return "GameConfig State";
    }

    @Override
    public int getID() {
        return StateID.GAME_CONFIG_STATE;
    }

    @Override
    public void enter(StateMachine stm) {
        this.stm = stm;
        if (!onDemand){
            executePreset();
            stm.changeState(new PlanningPhaseState(), ChangeAction.SET);
        }
    }

    @Override
    public void exit() {

    }

    @Override
    public void recieve(Connection connection, Object pkg) {

    }

    public void finish(){

    }

    public void executePreset(){
        GameServer.instance().execGameConfig();
    }
}
