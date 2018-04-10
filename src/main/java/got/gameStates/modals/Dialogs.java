package got.gameStates.modals;

import got.Constants;
import got.ModalState;
import got.gameObjects.ContainerObject;
import got.gameObjects.HouseCardsListObject;
import got.gameObjects.ImageObject;
import got.gameObjects.TextBoxObject;
import got.gameObjects.interfaceControls.ImageButton;
import got.gameStates.StateMachine;
import got.graphics.DrawSpace;
import got.houseCards.Deck;
import got.houseCards.HouseCard;

import org.joml.Vector2f;

import java.util.List;

import static got.server.PlayerManager.getSelf;

/**
 * Created by Souverain73 on 24.11.2016.
 */
public class Dialogs {
    public enum DialogResult{
        OK,
        CANCEL
    }

    public static class Dialog extends CustomModalState<DialogResult>{
        public Dialog(DialogResult defaultResult) {
            super(defaultResult);
        }
    }

    private static Dialog createConfirmDialog(Vector2f pos, String message){
        Dialog cms = new Dialog(DialogResult.CANCEL);

        ImageObject bg = new ImageObject("DialogBackground.png", 200, 100).setPos(pos).setSpace(DrawSpace.WORLD);

        TextBoxObject tbo = new TextBoxObject(180, 40).setText(message).setPos(10, 5);
        bg.addChild(tbo);

        bg.addChild(new ImageButton("buttons/yes.png", 0, 50, 100, 50, null)
                .setSpace(DrawSpace.SCREEN)
                .setCallback((sender, param)->{
                    cms.setResult(DialogResult.OK);
                    cms.close();
                }));
        System.out.println();
        bg.addChild(new ImageButton("buttons/no.png", 100, 50, 100, 50, null)
                .setSpace(DrawSpace.SCREEN)
                .setCallback((sender, param)->{
                    cms.setResult(DialogResult.CANCEL);
                    cms.close();
                }));

        cms.addObject(bg);
        return cms;
    }

    public static DialogResult showConfirmDialog(String message) {
        Dialog dlg = createConfirmDialog(new Vector2f((Constants.SCREEN_WIDTH - 200) / 2,(Constants.SCREEN_HEIGHT - 100) / 2), message);
        (new ModalState(dlg)).run();
        return dlg.getResult();
    }

    public static CustomModalState<HouseCard> createSelectHouseCardDialog(Deck deck){
        return createSelectHouseCardDialog(deck.getActiveCards());
    }


    public static CustomModalState<HouseCard> createSelectHouseCardDialog(List<HouseCard> cards){
        CustomModalState<HouseCard> cms = new CustomModalState<>(null, false);
        ImageButton selectButton = new ImageButton("buttons/select.png", 0,0,200,100,null);
        selectButton.setVisible(false);

        HouseCardsListObject hclo = new HouseCardsListObject(cards){
            @Override
            protected void onSelect() {
                selectButton.setVisible(true);
            }

            @Override
            protected void onUnSelect() {
                selectButton.setVisible(false);
            }
        }.setSpace(DrawSpace.SCREEN);

        hclo.setPos(
                new Vector2f((Constants.SCREEN_WIDTH - hclo.getW())/2, 250)
        );

        selectButton.setPos(new Vector2f((hclo.getW()-200) / 2, 155*2.5f))
                .setCallback((sender, param)->cms.setResultAndClose(hclo.getSelectedCard()));

        cms.addObject(hclo);
        hclo.addChild(selectButton);
        return cms;
    }

}
