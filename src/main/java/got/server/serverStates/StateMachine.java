package got.server.serverStates;

import java.util.HashMap;
import java.util.LinkedList;

import com.esotericsoftware.kryonet.Connection;

import got.interfaces.INetworkListener;
import got.interfaces.IPauseable;
import got.model.ChangeAction;
import got.server.serverStates.base.ServerState;

public class StateMachine implements INetworkListener{
	public static final String VESTEROS_PHASE_DATA= "vesterosPhaseData";
	public static final String WILDLINGS_DATA_PARAM = "wildlings_data";
    private HashMap<String, Object> params;

	private LinkedList<ServerState> _states;
	
	public StateMachine(){
		_states = new LinkedList<ServerState>();
		params = new HashMap<>();
	}

	public void changeState(ServerState nextState, ChangeAction action){
		if (action == ChangeAction.REMOVE){
			ServerState currentState = _states.poll();
			nextState = _states.peek();
			_states.push(currentState);
		}
		pushState(new ChangeState(nextState, action));
	}

	public void setState(ServerState state){
		if (!_states.isEmpty()){
			_states.poll().exit();
		}
		_states.push(state);
		state.enter(this);
	}

	public void pushState(ServerState state){
		if (getCurrentState() instanceof IPauseable){
			((IPauseable) getCurrentState()).pause();
		}
		_states.push(state);
		state.enter(this);
	}
	
	public void removeStateAndResume(){
		if (!_states.isEmpty()){
			_states.poll().exit();
			if (getCurrentState() instanceof IPauseable){
				((IPauseable) getCurrentState()).resume();
			}
		}
	}

	public void removeState(){
		if (!_states.isEmpty()) {
			_states.poll().exit();
		}
	}
	
	public ServerState getCurrentState(){
		return _states.peek();
	}
	
	public void recieve(Connection connection, Object pkg){
		ServerState st = getCurrentState();
		if (st!=null){
			st.recieve(connection, pkg);
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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (ServerState st : _states){
			sb.append(st.getName());
			if (_states.getLast() != st)
				sb.append(", ");
		}
		sb.append("]");

		return sb.toString();
	}
}