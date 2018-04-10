package got.model;

import got.graphics.Texture;
import got.graphics.TextureManager;

/**
 * Created by Souverain73 on 18.11.2016.
 */
public enum Action{
    FIRE(1, false, "fire.png"),
    FIREPLUS(1, true, "firePlus.png"),
    MONEY(1, false, "money.png"),
    MONEYPLUS(1, true, "moneyPlus.png"),
    MOVE(0, false, "move.png"),
    MOVEMINUS(-1, false, "moveMinus.png"),
    MOVEPLUS(+1, true, "movePlus.png"),
    DEFEND(1, false, "defend.png"),
    DEFENDPLUS(2, true, "defendPlus.png"),
    HELP(0, false, "help.png"),
    HELPPLUS(1, true, "helpPlus.png");

    private static final String TEXTURE_BASE = "actions/";
    private final int power;
    private final boolean special;
    private final String textureName;
    private Texture texture = null;

    public Texture getTexture() {
        if (texture==null) {
            texture = TextureManager.instance().loadTexture(TEXTURE_BASE+textureName);
        }
        return texture;
    }

    Action(int power, boolean special, String textureName){
        this.power = power;
        this.special = special;
        this.textureName = textureName;
    }

    public int getPower() {
        return power;
    }

    public boolean isSpecial() {
        return special;
    }
}
