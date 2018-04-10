package got;

import org.lwjgl.glfw.GLFW;

/**
 * This class creates Game object and handles main loop
 * 
 * @author Souverain73
 */
public class GOT {

	private static long pWindow;
	
	public static void main(String[] args) {
		pWindow = GameClient.instance().init();
		
		while(!GLFW.glfwWindowShouldClose(pWindow)){
			GameClient.instance().executeWorks();
			GameClient.instance().updateInput();
			GameClient.instance().updateAnimations();
			GameClient.instance().updateLogic();
			GameClient.instance().updateGraphics();
			GameClient.instance().tick();
		}

		GameClient.instance().finish();
		System.exit(0);
	}

}
