package got.gameStates.modals;

import got.GameClient;
import got.InputManager;
import got.gameObjects.AbstractGameObject;
import got.gameObjects.GameObject;
import got.gameStates.AbstractGameState;
import got.gameStates.StateMachine;
import got.interfaces.IClickListener;

/**
 * CustomModalState is a class for creating modal game states in runtime;<br>
 * Used for custom dialogs;<br>
 * For use this state create an instance and add GameObjects to it;<br>
 * @param <T> - type of result object;
 */

public class CustomModalState<T> extends AbstractGameState implements IClickListener{
	private T result;
	private boolean closeOnFreeClick = false;

	public CustomModalState(T defaultResult){
		super();
		this.result = defaultResult;
	}

	public CustomModalState(T defaultResult, boolean closeOnFreeClick){
		this(defaultResult);
		this.closeOnFreeClick = closeOnFreeClick;
	}

	@Override
	public void enter(StateMachine stm) {
	}

	@Override
	public void click(InputManager.ClickEvent event) {
		GameObject sender = event.getTarget();
		if (sender == null && closeOnFreeClick){
			close();
		}	
	}

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}

	public void addObject(AbstractGameObject gameObject){
		super.addObject(gameObject);
	}

	public void close() {
		GameClient.instance().closeModal();
	}

	public void setResultAndClose(T result){
		setResult(result);
		close();
	}
}