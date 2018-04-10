package got.server.serverStates;

import com.esotericsoftware.kryonet.Connection;
import got.gameStates.StateID;
import got.model.ChangeAction;
import got.model.Player;
import got.network.Packages;
import got.server.GameServer;
import got.server.serverStates.base.StepByStepState;

/**
 * Created by Souverain73 on 22.11.2016.
 */
public class MovePhaseState extends StepByStepState {

    public MovePhaseState(){
        super.setNextState(PowerPhaseState.class);
    }

    @Override
    public String getName() {
        return "MovePhaseState";
    }

    @Override
    public int getID() {
        return StateID.MOVE_PHASE;
    }

    @Override
    public void enter(StateMachine stm) {
        super.enter(stm);
    }

    @Override
    public void recieve(Connection c, Object pkg) {
        super.recieve(c, pkg);
        GameServer.PlayerConnection connection = (GameServer.PlayerConnection) c;
        Player player = connection.player;

        if (pkg instanceof Packages.Move){
            Packages.Move msg = (Packages.Move) pkg;
            GameServer.getServer().sendToAllTCP(new Packages.PlayerMove(player.id, msg.from, msg.to, msg.units));
        }

        if (pkg instanceof Packages.Attack) {
            Packages.Attack attack = (Packages.Attack) pkg;
            GameServer.shared.attackerRegionID = attack.from;
            GameServer.shared.defenderRegionID = attack.to;
            GameServer.shared.attackerID = attack.attackerId;
            GameServer.shared.defenderID = attack.defenderId;
            stm.changeState(new HelpPhaseState(), ChangeAction.PUSH);
        }

        if (pkg instanceof Packages.Act) {
            Packages.Act msg = (Packages.Act) pkg;
            GameServer.getServer().sendToAllTCP(new Packages.PlayerAct(msg.from, msg.to));
        }

        if (pkg instanceof Packages.PlacePowerToken) {
            Packages.PlacePowerToken msg = (Packages.PlacePowerToken) pkg;
            GameServer.getServer().sendToAllTCP(new Packages.PlayerPlacePowerToken(player.id, msg.regionId));
        }
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
        nextTurn();
    }
}
