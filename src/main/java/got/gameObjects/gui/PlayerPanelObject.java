package got.gameObjects.gui;

import got.gameObjects.AbstractGameObject;
import got.gameObjects.ImageObject;
import got.gameObjects.TextObject;
import got.gameStates.GameState;
import got.graphics.DrawSpace;
import got.model.Game;
import got.model.Player;
import got.wildlings.Wildlings;
import org.joml.Vector2f;

/**
 * Created by Souverain73 on 24.11.2016.
 */
public class PlayerPanelObject extends AbstractGameObject<PlayerPanelObject> {
    @Override protected PlayerPanelObject getThis() {return this;}

    TextObject fraction;
    TextObject money;
    TextObject tTurn;
    TextObject tWildlings;
    Player player;
    int currentTurn;

    public PlayerPanelObject(Player player ){
        this.player = player;
        this.currentTurn = Game.instance().getTurn();
        ImageObject bg = new ImageObject("Player Panel/bg.png", 304, 170);
        addChild(bg);

        fraction = new TextObject(player.getFraction().toString())
                .setPos(new Vector2f(10,10));
        addChild(fraction);

        addChild(money = new TextObject(String.valueOf(player.getMoney()))
                .setPos(new Vector2f(10, 30))
        );

        addChild(tTurn = new TextObject(String.valueOf(currentTurn))
                .setPos(new Vector2f(100, 10))
        );

        addChild(tWildlings = new TextObject("Одичалые: " + String.valueOf(Wildlings.instance().getLevel()))
                .setPos(new Vector2f(10, 50)));
    }

    @Override
    public void update(GameState state) {
        super.update(state);
        fraction.setText(player.getFraction().toString());
        money.setText(String.valueOf(player.getMoney()));
        tWildlings.setText("Одичалые: " + String.valueOf(Wildlings.instance().getLevel()));
        if (Game.instance().getTurn() != currentTurn){
            tTurn.setText(String.valueOf(Game.instance().getTurn()));
        }
    }
}
