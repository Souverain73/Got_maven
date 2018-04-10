package got.gameStates;

import got.Constants;
import got.GameClient;
import got.gameObjects.TextObject;
import got.gameObjects.interfaceControls.ImageButton;
import got.gameStates.test.AnimationTestState;
import got.gameStates.test.MapViewState;
import got.gameStates.test.TextDebugState;
import got.gameStates.test.TrueTypeTestState;
import got.graphics.Colors;
import got.graphics.DrawSpace;
import got.graphics.text.FontTrueType;
import org.joml.Vector2f;

public class MenuState extends AbstractGameState {
	private final String name = "MenuState";
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void enter(StateMachine stm) {		
		System.out.println("Entering "+name);
		//button play
		ImageButton btn = new ImageButton("buttons/play.png", 540, 260, 200, 100, null);
		btn.setCallback((sender, params)->{
			GameClient.instance().getStateMachine().setState(new NetworkRoomState());
		});
		btn.setSpace(DrawSpace.SCREEN);
		addObject(btn);
		//button exit
		btn = new ImageButton("buttons/exit.png", 540, 380, 200, 100, null);
		btn.setCallback((sender, params)->{
			GameClient.instance().exit();
		});
		btn.setSpace(DrawSpace.SCREEN);
		addObject(btn);


		FontTrueType titleFont = new FontTrueType("gotKG", 64, Colors.BLACK.asVector3());
		int titleWidth = titleFont.getStringWidth("Game Of Thrones");
		addObject(new TextObject(titleFont, "Game Of Thrones")
				.setSpace(DrawSpace.SCREEN)
		.setPos(new Vector2f((Constants.SCREEN_WIDTH-titleWidth)/2, 50)));

		addObject(new ImageButton("map/winterfell.png", 100, 50, 200, 140, null)
		.setSpace(DrawSpace.SCREEN)
		.setCallback((sender, params)->{
			GameClient.instance().getStateMachine().setState(new MapViewState());
		}));

		addObject(new ImageButton("buttons/plus.png", 100, 200, 100, 100, null)
		.setSpace(DrawSpace.SCREEN)
		.setCallback((sender, param)->
			GameClient.instance().getStateMachine().setState(new TextDebugState())
		));
		addObject(new ImageButton("buttons/minus.png", 100, 300, 100, 100, null)
				.setSpace(DrawSpace.SCREEN)
				.setCallback((sender, param)->
						GameClient.instance().getStateMachine().setState(new AnimationTestState())
				));
		addObject(new ImageButton("buttons/minus.png", 100, 400, 100, 100, null)
				.setSpace(DrawSpace.SCREEN)
				.setCallback((sender, param)->
						GameClient.instance().getStateMachine().setState(new TrueTypeTestState())
				));
	}

	@Override
	public void exit() {
		super.exit();
		System.out.println("Exit "+name);
	}	
}
