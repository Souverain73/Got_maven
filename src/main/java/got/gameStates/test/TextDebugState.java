package got.gameStates.test;

import got.Constants;
import got.InputManager;
import got.gameObjects.ImageObject;
import got.gameObjects.TextBoxObject;
import got.gameObjects.TextObject;
import got.gameStates.AbstractGameState;
import got.gameStates.StateMachine;
import got.graphics.DrawSpace;
import got.graphics.text.FontBitmap;
import got.graphics.TextureManager;
import got.graphics.text.FontTrueType;
import got.interfaces.IClickListener;
import org.joml.Vector2f;

/**
 * Created by Souverain73 on 21.03.2017.
 */
public class TextDebugState extends AbstractGameState implements IClickListener{
    @Override
    public void enter(StateMachine stm) {
        super.enter(stm);
        TextObject to;
        addObject(new ImageObject(TextureManager.instance().loadTexture("backgroundMain.png"),
               Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT).setSpace(DrawSpace.SCREEN));
        addObject(new TextObject(new FontTrueType("trajan", 16), "True type small text test. Тест шрифтов для маленьких символов.").setSpace(DrawSpace.SCREEN).setPos(new Vector2f(100, 30)));
        addObject(new TextObject(new FontTrueType("trajan", 10), "True type small text test. Тест шрифтов для маленьких символов.").setSpace(DrawSpace.SCREEN).setPos(new Vector2f(100, 50)));
        addObject(new TextObject(new FontTrueType("trajan", 4), "True type small text test. Тест шрифтов для маленьких символов.").setSpace(DrawSpace.SCREEN).setPos(new Vector2f(100, 60)));

        TextBoxObject tbo = new TextBoxObject(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        tbo.setText("Hello world!!\nNew line;\nAlign center valign center").setAlign(TextBoxObject.Align.RIGHT).setVerticalAlign(TextBoxObject.VerticalAlign.TOP);
        addObject(tbo);


    }

    @Override
    public void click(InputManager.ClickEvent event) {

    }
}
