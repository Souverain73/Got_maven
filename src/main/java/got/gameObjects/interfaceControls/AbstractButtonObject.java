package got.gameObjects.interfaceControls;

import java.util.function.BiConsumer;
import java.util.function.Function;

import com.esotericsoftware.minlog.Log;
import got.GameClient;
import got.gameObjects.AbstractGameObject;
import got.gameObjects.GameObject;
import got.utils.UI;
import org.joml.Vector2f;

import got.InputManager;
import got.gameStates.GameState;
import got.interfaces.IClickListener;
import got.interfaces.IClickable;
import got.utils.LoaderParams;

/**
 * 
 * Base class for all buttons
 * <h1>Usage:</h1>
 * <p>
 * 	All buttons have callback function.<br>
 * 	Callback function called when button is pressed.
 * <h2>Default callback:</h2>
 * 	Default callback tries to use iClickListener interface of current state.
 * 	If current state is click listener then click() method will be called.
 * </p>
 * @author Souverain73
 *
 */
public abstract class AbstractButtonObject<T extends AbstractButtonObject<T>> extends AbstractGameObject<T>
																			  implements IClickable{
	protected enum State {DOWN, FREE, HOVER, DISABLED};
	protected BiConsumer<GameObject, Object> callback;
	protected State state;
	protected boolean wasClick;
	protected boolean mouseIn;
	protected int priority;

	public AbstractButtonObject(int priority) {
		super();
		wasClick = false;
		state = State.FREE;
		callback = null;
		this.priority = priority;
		InputManager.instance().registerClickable(this);
	}
	
	@Override
	public void finish() {
		InputManager.instance().removeClickable(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(LoaderParams params) {
		if (params.containsKey("callback")){
			callback = (BiConsumer<GameObject, Object>)params.get("callback");
		}
		return true;
	}

	public T setCallback(BiConsumer<GameObject, Object> cb){
		callback = cb;
		return getThis();
	}
	
	@Override
	public void update(GameState st) {
		super.update(st);

		if (state == State.DISABLED) return;
		
		if (state == State.FREE){
			if (mouseIn){
				mouseEnter();
				if (InputManager.instance().getMouseButtonState(InputManager.MOUSE_LEFT)==1){
					wasClick = true;
				}
			}
		}
		
		if (state == State.DOWN){
			if (!mouseIn){
				mouseOut();
				wasClick = false;
			}
			if (InputManager.instance().getMouseButtonState(InputManager.MOUSE_LEFT)==0){
				if (!wasClick){
					wasClick = true;
					click(st);
				}
				if (state!=State.DISABLED)
					state = State.HOVER;
			}
		}
		
		if (state == State.HOVER) {
			if (!mouseIn) {
				mouseOut();
			}
			if (InputManager.instance().getMouseButtonState(InputManager.MOUSE_LEFT) == 1) {
				state = State.DOWN;
			} else {
				wasClick = false;
			}
		}
	}
	
	protected void mouseEnter(){
		state = State.HOVER;
	}

	protected void mouseOut(){
		state = State.FREE;
	}
	
	protected void click(GameState st){
		if (callback != null){
			callback.accept(this, st);
		}
	}
	
	public T setEnabled(boolean enabled){
		if (enabled){
			state = State.FREE;
		}else{
			state = State.DISABLED;
		}

		return getThis();
	}
	
	//IClickable implementation
	public abstract boolean ifMouseIn(Vector2f mousePos);

	@Override
	public int getPriority() {
		return priority;
	}

	public T setPriority(int priority) {
		this.priority = priority;
		InputManager.instance().updatePriority();
		return getThis();
	}

	@Override
	public void setMouseIn(boolean mouseIn) {
		this.mouseIn = mouseIn;
	}

	@Override
	public boolean isActive() {
		return (state!=State.DISABLED && isVisible() && isUsed());
	}
	
	@Override
	public void setVisible(boolean visible) {
		if (this.visible == visible) return;
		this.visible = visible;
		if (visible == true){
			InputManager.instance().registerClickable(this);
		}else{
			InputManager.instance().removeClickable(this);
		}
	}



}
