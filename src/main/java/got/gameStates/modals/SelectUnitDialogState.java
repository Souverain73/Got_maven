package got.gameStates.modals;



import got.InputManager;
import got.graphics.DrawSpace;
import org.joml.Vector2f;

import got.Constants;
import got.GameClient;
import got.gameObjects.GameObject;
import got.gameObjects.interfaceControls.ImageButton;
import got.gameObjects.ImageObject;
import got.gameStates.AbstractGameState;
import got.graphics.TextureManager;
import got.interfaces.IClickListener;
import got.model.Unit;

public class SelectUnitDialogState extends AbstractGameState implements IClickListener{
	private final String name = "UnitSelect";
	public Object result;
	
	public SelectUnitDialogState(Unit[] units, Vector2f pos){
		float x = pos.x;
		float y = pos.y;
			ImageObject bg = new ImageObject(TextureManager.instance().loadTexture("unitsMenuBg.png"),
				220, 60).setPos(new Vector2f(x,y)).setSpace(DrawSpace.WORLD);
			addObject(bg);
		x = pos.x + 6;
		y = pos.y + 5;
		
		int unitsCount = units.length;
		for (int i=0; i<unitsCount; i++){
			ImageButton btn = new ImageButton(units[i].getTexture(), 
					(int)x, (int)y, 
					(int)Constants.UNIT_SIZE,
					(int)Constants.UNIT_SIZE,
					units[i]);
			btn.setCallback(this::clickCallback);
			addObject(btn);
			x+=Constants.UNIT_SIZE + Constants.UNIT_STEP;
		}
	}
	
	private void clickCallback(GameObject sender, Object param){
		result = param;
		close();
	}
	
	private void close(){
		GameClient.instance().closeModal();
	}
	
	@Override
	public String getName() {
		return name;
	}

	public Object getResult(){
		return result;
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
