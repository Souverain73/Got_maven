package got.gameStates;

import got.interfaces.INetworkListener;

public interface GameState extends INetworkListener{
	String getName();
	int getID();
	void enter(StateMachine stm);
	void exit();
	void draw();
	void update();
	void tick();
}
