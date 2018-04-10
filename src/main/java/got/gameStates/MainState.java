package got.gameStates;

import got.gameObjects.*;
import got.gameObjects.gui.GUIObject;
import got.network.Packages;
import org.joml.Vector2f;

import com.esotericsoftware.kryonet.Connection;

import got.Constants;
import got.GameClient;
import got.graphics.DrawSpace;
import got.graphics.TextureManager;
import got.network.Packages.ChangeState;
import got.utils.LoaderParams;

public class MainState extends AbstractGameState {
	private static final String name = "MainState";
	private static final String MAP_FILE = "data/map.xml";
	private StateMachine stm;
	private GameMapObject map;
	private ImageObject background;

	private GUIObject gui;


	@Override
	public String getName() {
		return name;
	}

	@Override
	public void enter(StateMachine extstm) {
		stm = new StateMachine();
		System.out.println("Entering "+name);

		background = new ImageObject(TextureManager.instance().loadTexture("backgroundMain.png"),
				Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
		background.setSpace(DrawSpace.SCREEN);

		map = new GameMapObject();
		map.init(new LoaderParams(new String[]{"filename", MAP_FILE}));

		GameClient.shared.gameMap = map;

		addObject(gui = new GUIObject());

		GameClient.shared.gui = gui;

		super.enter(extstm);
	}

	@Override
	public void exit() {
		background.finish();
		map.finish();
		super.exit();
		System.out.println("Exit "+name);
	}

	@Override
	public void draw() {
		background.draw(this);
		map.draw(stm.getCurrentState());

		if (GameClient.shared.battleDeck != null){
			GameClient.shared.battleDeck.draw(stm.getCurrentState());
		}

		stm.draw();
		super.draw();
	}

	@Override
	public void update() {
		background.update(this);
		map.update(stm.getCurrentState());

		if (GameClient.shared.battleDeck != null){
			GameClient.shared.battleDeck.update(stm.getCurrentState());
		}

		stm.update();
		super.update();
	}
	
	public void tick(){
		background.tick();
		map.tick();

		if (GameClient.shared.battleDeck != null){
			GameClient.shared.battleDeck.tick();
		}

		stm.tick();
		super.tick();
	}

	@Override
	public void recieve(Connection connection, Object pkg) {
		if (pkg instanceof ChangeState){
			Packages.ChangeState msg = (ChangeState) pkg;
			stm.changeState(msg.state, msg.action);
			return;
		}
		stm.recieve(connection, pkg);
	}

	@Override
	public int getID() {
		return StateID.MAIN_STATE;
	}
	
	public GameState getCurrentState(){
		return stm.getCurrentState();
	}

	@Override
	public String toString() {
		return "Main state:" + stm;
	}
}
