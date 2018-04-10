package got.model;

import got.graphics.Texture;
import got.graphics.TextureManager;

/**
 * Created by Souverain73 on 22.12.2016.
 */
public class PowerToken {
    private static Texture texture = null;
    private static String textureName = "power_token.png";

    public static Texture getTexture() {
        if (texture == null){
            texture = TextureManager.instance().loadTexture(textureName);
        }
        return texture;
    }
}
