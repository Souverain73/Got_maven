package got.gameStates.test;
import got.gameObjects.TextObject;
import got.gameStates.AbstractGameState;
import got.gameStates.StateMachine;
import got.graphics.DrawSpace;
import got.graphics.GraphicModule;
import got.graphics.Texture;
import got.graphics.TextureManager;
import got.graphics.text.FontTrueType;
import org.joml.Vector2f;

/**
 * Created by Souverain73 on 14.04.2017.
 */
public class TrueTypeTestState extends AbstractGameState{
    Texture t = TextureManager.instance().loadTexture("dummy.png");
    private FontTrueType font = new FontTrueType("trajan", 16.0f);

    @Override
    public void enter(StateMachine stm) {
        super.enter(stm);
        TextObject to = new TextObject(font, "TrueType test state: Quick brown fox jumps over the lazy dog;Съешь еше этих мягких французских булочек")
                .setPos(new Vector2f(100, 20))
                .setSpace(DrawSpace.SCREEN);
        addObject(to);

    }

    @Override
    public void draw() {
        GraphicModule.instance().setDrawSpace(DrawSpace.SCREEN);
        t.drawTextureById(0,100, 512, 512, font.getTextureID());
        super.draw();
    }
}
