package got.houseCards;

import got.houseCards.HouseCard;
import got.houseCards.HouseCardsLoader;
import got.model.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Souverain73 on 11.01.2017.
 */
public class Deck {
    private List<HouseCard> activeCards;
    private List<HouseCard> usedCards = new ArrayList<>();

    public List<HouseCard> getActiveCards(){
        return activeCards;
    }

    public List<HouseCard> getUsedCards(){
        return usedCards;
    }

    public Deck(Player player){
        activeCards = HouseCardsLoader.instance().getCardsForFraction(player.getFraction());
    }

    public void useCard(HouseCard card){
        if (!activeCards.contains(card)){
            throw new IllegalStateException("You can't use not active card");
        }
        activeCards.remove(card);
        usedCards.add(card);
    }

    public void rewindCard(HouseCard card){
        if (!usedCards.contains(card)){
            throw new IllegalStateException("You can't rewind not used card");
        }
        usedCards.remove(card);
        activeCards.add(card);
    }

    public void rewindAll(){
        activeCards.addAll(usedCards);
        usedCards.clear();
    }

    public void removeCard(HouseCard card){
        activeCards.remove(card);
        usedCards.remove(card);
    }
}
