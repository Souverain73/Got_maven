package got.gameStates;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.esotericsoftware.kryonet.Connection;

import got.GameClient;
import got.gameObjects.AbstractGameObject;
import got.gameObjects.GameObject;
import got.interfaces.IPauseable;
import got.network.Packages;
import got.utils.UI;
import static got.network.Packages.Ready;

/**
 * @author Souverain73
 *	This class implements base for all GameStates
 */
public abstract class AbstractGameState implements GameState, IPauseable {
	protected List<AbstractGameObject> gameObjects = new ArrayList<>();
	protected StateMachine stm;
	/* 
	 * If you want log all recieved packages use super.recieve(), otherwise skip it
	 */
	@Override
	public void recieve(Connection connection, Object pkg) {
		UI.systemMessage(pkg.toString());
	}

	@Override
	public String getName() {
		return this.getClass().getName();
	}

	@Override
	public void enter(StateMachine stm) {
		GameClient.instance().send(new Packages.StateReady());
		this.stm = stm;
	}

	@Override
	public void exit() {
		gameObjects.forEach(o->o.finish());
	}

	@Override
	public void draw() {
		gameObjects.forEach(obj->obj.draw(this));
	}

	@Override
	public void update() {
		gameObjects.forEach(obj->obj.update(this));
	}
	
	protected void addObject(AbstractGameObject obj){
		gameObjects.add(obj);
	}

	protected void removeObject(AbstractGameObject obj){
		gameObjects.remove(obj);
		obj.finish();
	}

	@Override
	public int getID() {
		return 0;
	}
	
	@Override
	public void tick(){
		gameObjects.forEach(obj->obj.tick());
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {
		GameClient.instance().send(new Packages.StateReady());
	}
}
