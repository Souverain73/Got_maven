package got.vesterosCards;

import got.Constants;
import got.GameClient;
import got.InputManager;
import got.ModalState;
import got.gameObjects.interfaceControls.ImageButton;
import got.gameStates.modals.CustomModalState;
import got.gameStates.modals.Dialogs;
import got.graphics.DrawSpace;
import got.graphics.Texture;
import got.graphics.TextureManager;
import got.model.ChangeAction;
import got.server.serverStates.StateMachine;
import got.wildlings.Wildlings;

/**
 * Created by Souverain73 on 13.02.2017.
 */
public class CommonVesterosCard implements VesterosCard {
    private static String TEXTURE_BASE = "Vesteros Cards/";
    private Texture texture = null;
    private String textureName;
    private String internalName;
    private String title;
    private boolean wildlings;

    public CommonVesterosCard(String textureName, String internalName, String title) {
        this(textureName, internalName, title, false);
    }

    public CommonVesterosCard(String textureName, String internalName, String title, boolean wildlings) {
        this.textureName = textureName;
        this.internalName = internalName;
        this.title = title;
        this.wildlings = wildlings;
    }

    public CommonVesterosCard wildlings(){
        wildlings = true;
        return this;
    }

    @Override
    public void onOpenClient() {
    }

    @Override
    public void onOpenServer(StateMachine stm, openParams param) {
        stm.changeState(null, ChangeAction.REMOVE);
    }

    public String getInternalName() {
        return internalName;
    }

    public String getTitle(){
        return title;
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

    @Override
    public boolean hasWildlings() {
        return wildlings;
    }
}
