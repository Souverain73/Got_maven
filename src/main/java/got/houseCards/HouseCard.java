package got.houseCards;


import got.graphics.Texture;
import got.model.Fraction;
import got.utils.LoaderParams;

/**
 * Created by Souverain73 on 09.01.2017.
 */
public interface HouseCard {
    void init(LoaderParams params);

    /**
     * Событие вызываемое при выборе соответствующей карты
     */
    void onPlace(Fraction fraction);

    /**
     * Событие вызываемое если игрок, выложивший эту карту победил
     */
    void onWin();

    /**
     * Событие вызываемое, если игрок выложивший эту карту проиграл
     */
    void onLoose();

    /**
     * Событие вызываемое при завершении боя
     */
    void onBattleEnd();

    String getTitle();
    int getPower();
    int getSwords();
    int getTowers();
    Texture getTexture();

    int getID();
}
