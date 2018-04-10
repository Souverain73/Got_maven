package got.vesterosCards.states;

import got.Constants;
import got.gameObjects.AbstractGameObject;
import got.gameObjects.ContainerObject;
import got.gameObjects.GameObject;
import got.gameObjects.interfaceControls.ImageButton;
import got.gameStates.AbstractGameState;
import got.gameStates.StateMachine;
import got.gameStates.modals.CustomModalState;
import got.gameStates.modals.WaitingModal;
import got.graphics.DrawSpace;
import got.utils.UI;
import org.joml.Vector2f;

import java.util.function.BiConsumer;

/**
 * Created by Souverain73 on 12.04.2017.
 */
public class OptionSelectorState extends CustomModalState<Integer> {
    private int result;

    public OptionSelectorState(int defaultResult){
        super(defaultResult, false);
        this.result = defaultResult;
    }

    @Override
    public void enter(StateMachine stm) {
        super.enter(stm);
        BiConsumer<GameObject, Object> buttonCallback = (gameObject, object) -> setResultAndClose((Integer) object);

        ContainerObject cnt = new ContainerObject(){{
            addChild(new ImageButton("buttons/1.png", 0, 0, 50, 50, 1).setCallback(buttonCallback));
            addChild(new ImageButton("buttons/2.png", 50, 0, 50, 50, 2).setCallback(buttonCallback));
            addChild(new ImageButton("buttons/3.png", 100, 0, 50, 50, 3).setCallback(buttonCallback));
        }}.setSpace(DrawSpace.SCREEN)
                .setPos(new Vector2f((Constants.SCREEN_WIDTH - 150)/2, 245));

        addObject(cnt);
    }

    private void setResultAndClose(int result){
        this.result = result;
        close();
    }
}
