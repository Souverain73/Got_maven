package got.wildlings;

import got.graphics.Texture;
import got.model.NetworkSide;
import got.network.Packages;
import got.server.serverStates.StateMachine;

/**
 * Created by Souverain73 on 20.04.2017.
 */
public interface WildlingsCard {
    void onOpenClient(Packages.WildlingsData data);
    void onOpenServer(StateMachine stm, Packages.WildlingsData data);
    String getTitle();
    String getInternalName();
    int getID();
    Texture getTexture();
}
