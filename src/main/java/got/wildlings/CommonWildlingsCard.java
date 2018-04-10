package got.wildlings;

import got.Constants;
import got.GameClient;
import got.ModalState;
import got.gameObjects.gui.GUIObject;
import got.gameObjects.interfaceControls.ImageButton;
import got.gameStates.modals.CustomModalState;
import got.gameStates.modals.Dialogs;
import got.graphics.DrawSpace;
import got.graphics.Texture;
import got.graphics.TextureManager;
import got.model.ChangeAction;
import got.model.NetworkSide;
import got.network.Packages;
import got.server.PlayerManager;
import got.server.serverStates.StateMachine;

/**
 * Created by Souverain73 on 20.04.2017.
 */
public class CommonWildlingsCard implements WildlingsCard {
    private static String TEXTURE_BASE = "Wildlings cards/";
    private Texture texture = null;
    private String textureName;
    private String internalName;
    private String title;


    public CommonWildlingsCard(String textureName, String internalName, String title) {
        this.textureName = textureName;
        this.internalName = internalName;
        this.title = title;
    }

    @Override
    public void onOpenClient(Packages.WildlingsData data) {
        if (data.victory){
            if (PlayerManager.getSelf().getFraction() == data.actor)
                winner(data);
        }else{
            if (PlayerManager.getSelf().getFraction() == data.actor){
                looser(data);
            }else{
                other(data);
            }
        }
    }

    protected void other(Packages.WildlingsData data){}

    protected void looser(Packages.WildlingsData data){}

    protected void winner(Packages.WildlingsData data){}

    @Override
    public void onOpenServer(StateMachine stm, Packages.WildlingsData data) {
        stm.changeState(null, ChangeAction.REMOVE);
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getInternalName() {
        return internalName;
    }

    @Override
    public int getID() {
        return internalName.hashCode();
    }

    @Override
    public Texture getTexture() {
        if (texture == null){
            texture = TextureManager.instance().loadTexture(TEXTURE_BASE + textureName);
        }
        return texture;
    }
}
