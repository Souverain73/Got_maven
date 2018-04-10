package got.vesterosCards;

import got.graphics.Texture;
import got.server.serverStates.StateMachine;

/**
 * Created by Souverain73 on 13.02.2017.
 */
public interface VesterosCard {
    boolean hasWildlings();

    class openParams{
        public int selection;
    }
    /**
     * Событие происходящее при открытии карты
     */
    void onOpenClient();
    void onOpenServer(StateMachine stm, openParams param);
    String getTitle();
    String getInternalName();
    int getID();
    Texture getTexture();
}
