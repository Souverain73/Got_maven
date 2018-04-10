package got.gameStates;

import java.util.HashMap;
import java.util.LinkedList;

import com.esotericsoftware.kryonet.Connection;

import got.interfaces.INetworkListener;
import got.interfaces.IPauseable;
import got.model.ChangeAction;
import got.model.Game;
import got.server.GameServer;
import got.server.serverStates.base.ServerState;

public class StateMachine implements INetworkListener {
	public static final String WILDLINGS_DATA_PARAM = "wildlings_data";
	private LinkedList<GameState> _states;
	private HashMap<String, Object> params = new HashMap<>();

	
	public StateMachine(){
		_states = new LinkedList<>();
	}

	public void changeState(int stateID, ChangeAction method){
		if (method == ChangeAction.REMOVE){
			removeState();
			GameState st = getCurrentState();
			if (st instanceof IPauseable) {
				((IPauseable) st).resume();
			}
		}else if (method == ChangeAction.SET){
			setState(StateID.getGameStateByID(stateID));
		}else {
			GameState st = getCurrentState();
			if (st instanceof IPauseable) {
				((IPauseable) st).pause();
			}
			pushState(StateID.getGameStateByID(stateID));
		}
	}

	public void setState(GameState state){
		if (!_states.isEmpty()){
			_states.poll().exit();
		}
		_states.push(state);
		state.enter(this);
	}
	
	/**
	 * Push state in the states stack
	 * @param state
	 */
	public void pushState(GameState state){
		_states.push(state);
		state.enter(this);
	}
	
	/**
	 * Remove last state from states stack
	 */
	public void removeState(){
		if (!_states.isEmpty()){
			_states.poll().exit();
		}
	}
	
	public void update(){
		if (!_states.isEmpty())
			getCurrentState().update();
	}
	
	public void draw(){
		if (!_states.isEmpty())
			getCurrentState().draw();
	}
	
	public void tick(){
		if (!_states.isEmpty()){
			getCurrentState().tick();
		}
	}

	public void saveParam(String name, Object param){
		params.put(name, param);
	}

	public void removeParam(String name){
		params.remove(name);
	}

	public Object getParam(String name){
		return params.get(name);
	}

	public GameState getCurrentState(){
		return _states.peek();
	}
	
	public void recieve(Connection connection, Object pkg){
		GameState st = getCurrentState();
		if (st!=null){
			st.recieve(connection, pkg);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (GameState st : _states){
			sb.append(st);
			if (_states.getLast() != st)
				sb.append(", ");
		}
		sb.append("]");
		return sb.toString();
	}

}
