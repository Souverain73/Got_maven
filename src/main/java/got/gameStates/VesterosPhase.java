package got.gameStates;

import com.esotericsoftware.kryonet.Connection;

import got.Constants;
import got.GameClient;
import got.gameObjects.AbstractGameObject;
import got.gameObjects.ImageObject;
import got.gameObjects.gui.GUIObject;
import got.graphics.DrawSpace;
import got.model.Game;
import got.network.Packages;
import got.vesterosCards.VesterosCard;
import got.vesterosCards.VesterosCards;
import got.wildlings.Wildlings;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class VesterosPhase extends AbstractGameState {
	private static final String name = "VesterosPhase";
	VesterosCard[] cards = new VesterosCard[3];
	AbstractGameObject<?>[] cardObjects = new AbstractGameObject[3];
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void enter(StateMachine stm) {
		super.enter(stm);
		Game.instance().nextTurn();
		GameClient.shared.restrictedActions = new ArrayList<>();
		GameClient.shared.gameMap.forEachRegion(region->{
			region.setAction(null);
			region.resurectUnits();
		});
	}

	@Override
	public void recieve(Connection connection, Object pkg) {
		if (pkg instanceof Packages.OpenCard) {
			Packages.OpenCard msg = (Packages.OpenCard) pkg;
			VesterosCard card = VesterosCards.getCard(msg.card);
			cards[msg.number] = card;
			GameClient.instance().logMessage("vesteros.cardOpen", msg.number+1, card.getTitle());

			addObject(cardObjects[msg.number] = new ImageObject(card.getTexture(), 285, 181).setSpace(DrawSpace.SCREEN).setPos( new Vector2f(330 + 305 * msg.number, 55)));
			if (card.hasWildlings()) Wildlings.instance().nextLevel();
		}

	}


	@Override
	public void pause() {
		super.pause();
		for(AbstractGameObject<?> co : cardObjects){
			if (co != null) co.setVisible(false);
		}
	}

	@Override
	public void resume() {
		super.resume();

		GameClient.shared.gui.removeSharedObject(GUIObject.CURRENT_CARD_SHARED_OBJECT);
		if (cardObjects[2] != null) return;

		for(AbstractGameObject<?> co : cardObjects){
			if (co != null) co.setVisible(true);
		}
	}
}
