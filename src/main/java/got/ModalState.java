package got;

import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;

import got.gameObjects.DebugPanel;
import got.gameStates.GameState;
import got.gameStates.StateMachine;
import got.graphics.GraphicModule;

/**
 * This class handles modal states like unit select, action select, etc
 * It stops main loop and handle the same loop with itself
 * @author Souverain73
 *
 */
public class ModalState implements Runnable{
	private boolean running;
	private GameState state;
	private boolean updateMain;
	private boolean drawMain;
	
	public ModalState(GameState st){
		this(st, false, true);
	}
	
	public ModalState(GameState st, boolean updateMain, boolean drawMain) {
		state = st;
		this.updateMain = updateMain;
		this.drawMain = drawMain;
	}
	
	public GameState getGameState(){
		return state;
	}
	
	@Override
	public void run() {
		GameClient.instance().registerModalState(this);
		StateMachine stm = GameClient.instance().getStateMachine();
		
		state.enter(null);
		running = true;
		
		while(running && GameClient.instance().isRunning()){
			GameClient.instance().executeWorks();
			GameClient.instance().updateInput();
			GameClient.instance().updateAnimations();

			//update logic
			if (updateMain){
				stm.update();
				GameClient.instance().executeTasks();
			}
			state.update();

			//update graphic
			GraphicModule.instance().clear();

			if (drawMain){
				stm.draw();
			}
			state.draw();

			stm.tick();
			state.tick();
			
			glfwSwapBuffers(GameClient.instance().pWindow);
		}
		
		state.exit();
	}
	
	public void close(){
		running = false;
	}
}
