package got.gameObjects;

import got.GameClient;
import got.gameStates.GameState;
import got.graphics.DrawSpace;
import got.graphics.text.FontBitmap;
import got.graphics.GraphicModule;
import got.graphics.text.Text;
import got.server.PlayerManager;

public class DebugPanel extends AbstractGameObject<DebugPanel>{
	private static DebugPanel _instance = null;
	private boolean drawn = false;
	private boolean updated = false;

	public static DebugPanel instance() {
		if (_instance == null) {
			_instance = new DebugPanel();
		}
		return _instance;
	}
	private FontBitmap dbgFont = new FontBitmap("test");
	private String statePrefix = "State:";
	private String fractionPrefix = "Fraction";
	private Text tCurState;
	private Text tCurFraction;
	
	public DebugPanel() {
		addChild(new FPSCounterObject());
		tCurState = Text.newInstance("", dbgFont);
		tCurFraction = Text.newInstance("", dbgFont);
	}
	
	@Override
	public void update(GameState state) {
		if (updated) return;
		super.update(state);
		if (state==null)
			tCurState.changeText(statePrefix + GameClient.instance().getStateMachine().getCurrentState().getName());
		else
			tCurState.changeText(statePrefix + state.getName());
		tCurFraction.changeText(PlayerManager.getSelf().getFraction().toString());
		updated = true;
	}
	
	@Override
	public void draw(GameState state) {
		if (drawn) return;
		super.draw(state);
		GraphicModule.instance().setDrawSpace(DrawSpace.SCREEN);
		tCurState.draw(10, 32, 1, 1);
		tCurFraction.draw(10, 64, 1, 1);
		drawn = true;
	}
	
	public void resetFlags(){
		updated = drawn = false;
	}

	@Override
	protected DebugPanel getThis() {
		return this;
	}
}
