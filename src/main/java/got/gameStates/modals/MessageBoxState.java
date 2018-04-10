package got.gameStates.modals;

import got.GameClient;
import got.InputManager;
import got.gameObjects.GameObject;
import got.gameStates.AbstractGameState;
import got.graphics.DrawSpace;
import got.graphics.text.FontBitmap;
import got.graphics.GraphicModule;
import got.graphics.text.Text;
import got.interfaces.IClickListener;

public class MessageBoxState extends AbstractGameState implements IClickListener{
	//TODO create BG and Buttons.
	private static FontBitmap msgFont = new FontBitmap("test");
	private Text text;

	private float x, y;
	
	public MessageBoxState(String text, int x, int y) {
		this.text = Text.newInstance(text, msgFont);
		this.x = x;
		this.y = y;
	}
	
	@Override
	public String getName() {
		return "MessageBox";
	}

	@Override
	public void draw() {
		super.draw();
		GraphicModule.instance().setDrawSpace(DrawSpace.SCREEN);
		text.draw(x, y, 1, 1);
	}

	@Override
	public void update() {
		super.update();
	}
		
	private void close(){
		GameClient.instance().closeModal();
	}

	@Override
	public int getID() {
		return -1;
	}


	@Override
	public void click(InputManager.ClickEvent event) {
		GameObject sender = event.getTarget();
		if (sender == null){
			close();
		}
	}

}
