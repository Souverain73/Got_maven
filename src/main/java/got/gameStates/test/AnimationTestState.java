package got.gameStates.test;

import got.animations.Animator;
import got.animations.Easings;
import got.gameObjects.AbstractGameObject;
import got.gameObjects.AnimatedObject;
import got.gameObjects.ContainerObject;
import got.gameObjects.ImageObject;
import got.gameObjects.gui.NumberSelectorObject;
import got.gameObjects.interfaceControls.ImageButton;
import got.gameStates.AbstractGameState;
import got.gameStates.StateMachine;
import got.graphics.DrawSpace;
import org.joml.Vector2f;

import static com.esotericsoftware.jsonbeans.JsonValue.ValueType.object;

/**
 * Created by Souverain73 on 28.03.2017.
 */
public class AnimationTestState extends AbstractGameState {
    @Override
    public void enter(StateMachine stm) {
        super.enter(stm);
        AbstractGameObject<?> obj;
        ContainerObject cnt = new ContainerObject(){{
            addChild(new ImageObject("buttons/plus.png", 100, 100).setSpace(DrawSpace.SCREEN));
            addChild(new ImageObject("buttons/plus.png", 100, 100).setSpace(DrawSpace.SCREEN).setPos(100,0));
        }}.setPos(new Vector2f(0,200)).setSpace(DrawSpace.SCREEN);
        addObject(cnt);
        AnimatedObject ao = new AnimatedObject(cnt);
        addObject(new ImageButton("buttons/plus.png", 0, 0, 100, 100, null).setSpace(DrawSpace.SCREEN)
            .setCallback((sender, params)->{
                ao.scale(2.0f, 1000);
            }));
        addObject(new ImageButton("buttons/minus.png", 100, 0, 100, 100, null).setSpace(DrawSpace.SCREEN)
            .setCallback((sender, params)->{
                ao.scale(1.0f, 1000);
            }));
        addObject(new NumberSelectorObject(0, 15, 5).setPos(300,300).setSpace(DrawSpace.SCREEN));
    }
}
