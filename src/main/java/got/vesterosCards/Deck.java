package got.vesterosCards;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Souverain73 on 23.03.2017.
 */
public class Deck {
    private int deckSize = 0;
    private List<VesterosCard> cards = new ArrayList<>();

    protected void addCard(VesterosCard card){
        cards.add(0, card);
        deckSize++;
    }

    public void shuffle(){
        List<VesterosCard> result  = new ArrayList<>(deckSize);
        for (int i=deckSize; i>0; i--){
            int num = ThreadLocalRandom.current().nextInt(i);
            result.add(cards.get(num));
            cards.remove(num);
        }
        cards = result;
    }

    public VesterosCard getTopCard(){
        deckSize--;
        VesterosCard result = cards.get(deckSize);
        cards.remove(deckSize);
        addCard(result);
        return  result;
    }
}
