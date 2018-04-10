package got.gameObjects;

import got.gameStates.GameState;
import got.graphics.Colors;
import got.graphics.DrawSpace;
import got.graphics.GraphicModule;
import got.graphics.text.FontTrueType;
import got.graphics.text.Text;
import org.joml.Vector2f;

public class FPSCounterObject extends AbstractGameObject<FPSCounterObject>{
	@Override
	protected FPSCounterObject getThis() {
		return this;
	}

	private Text tFPS;
	
	private long counter;
	private long lastUpdate;
	
	public FPSCounterObject() {
		tFPS = Text.newInstance("0", new FontTrueType("calibri", 20));
		lastUpdate = System.currentTimeMillis();
	}
	
	@Override
	public void update(GameState state) {
		super.update(state);
		counter++;
		if (System.currentTimeMillis() - lastUpdate > 1000){
			tFPS.changeText("FPS:"+String.valueOf(counter));
			lastUpdate = System.currentTimeMillis();
			counter = 0;
		}
	}
	
	@Override
	public void draw(GameState state) {
		super.draw(state);
		if (!isVisible()) return;
		Vector2f cp = getAbsolutePos();
		GraphicModule.instance().setDrawSpace(DrawSpace.SCREEN);
		tFPS.draw(cp.x+10, cp.y+0, 1, 1);
	}
}
