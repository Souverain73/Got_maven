package got.server.serverStates;

import com.esotericsoftware.kryonet.Connection;

import got.gameStates.StateID;
import got.model.ChangeAction;
import got.server.serverStates.base.ServerState;

public class MainState implements ServerState {
	private static String name = "MainState";
	
	@Override
	public void recieve(Connection connection, Object pkg) {
		
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void enter(StateMachine stm) {
		stm.changeState(new GameConfigState(false), ChangeAction.SET);
	}

	@Override
	public void exit() {
		
	}

	@Override
	public int getID() {
		return StateID.MAIN_STATE;
	}

}
