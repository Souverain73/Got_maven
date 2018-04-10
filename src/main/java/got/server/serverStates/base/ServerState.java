package got.server.serverStates.base;

import got.interfaces.INetworkListener;
import got.server.serverStates.StateMachine;

public interface ServerState extends INetworkListener{
	String getName();
	int getID();
	void enter(StateMachine stm);
	void exit();
}
