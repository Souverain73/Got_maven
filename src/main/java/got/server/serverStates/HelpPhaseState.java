package got.server.serverStates;

import com.esotericsoftware.kryonet.Connection;
import got.gameStates.StateID;
import got.model.Player;
import got.network.Packages;
import got.server.GameServer;
import got.server.serverStates.base.StepByStepState;

/**
 * Created by Souverain73 on 28.11.2016.
 */
public class HelpPhaseState extends StepByStepState {
    private static final String name = "HelpState";

    @Override
    public String getName() {
        return name;
    }

    public HelpPhaseState(){
        setNextState(SelectHouseCardPhaseState.class);
    }


    @Override
    public int getID() {
        return StateID.HELP_PHASE;
    }

    @Override
    public void enter(StateMachine stm) {
        GameServer.getServer().sendToAllTCP(new Packages.InitBattle(GameServer.shared.attackerRegionID,
                GameServer.shared.defenderRegionID));
        super.enter(stm);
    }

    @Override
    public void recieve(Connection c, Object pkg) {
        super.recieve(c, pkg);
        GameServer.PlayerConnection connection = ((GameServer.PlayerConnection)c);
        Player player = connection.player;

        if (pkg instanceof Packages.Help) {
            Packages.Help help = (Packages.Help) pkg;
            GameServer.getServer().sendToAllTCP(new Packages.PlayerHelp(player.id, help.side));
            handleReady(player, false);
        }
    }
}
