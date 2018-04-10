package got.server.serverStates;

import com.esotericsoftware.kryonet.Connection;

import got.gameStates.StateID;
import got.model.Player;
import got.network.Packages;
import got.server.GameServer;
import got.server.GameServer.PlayerConnection;
import got.server.serverStates.base.StepByStepState;

public class FirePhaseState extends StepByStepState {
	private static final String name = "FirePhase";

	public FirePhaseState(){
		super.setNextState(MovePhaseState.class);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getID() {
		return StateID.FIRE_PHASE;
	}

	@Override
	public void enter(StateMachine stm) {
		super.enter(stm);
	}

	@Override
	public void exit() {

	}

	@Override
	public void recieve(Connection c, Object pkg) {
		super.recieve(c, pkg);
		PlayerConnection connection = (PlayerConnection)c;
		Player player = connection.player;

		if (pkg instanceof Packages.Act){
			Packages.Act msg = ((Packages.Act)pkg);
			GameServer.getServer().sendToAllTCP(new Packages.PlayerAct(msg.from, msg.to));
		}
		
	}
}
