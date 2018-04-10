package got.wildlings;

import got.model.ChangeAction;
import got.server.serverStates.AuctionState;
import got.server.serverStates.StateMachine;
import got.utils.Utils;
import got.wildlings.cards.Gathering;
import got.wildlings.cards.Horde;
import got.wildlings.cards.Riders;
import got.wildlings.cards.Scout;
import got.wildlings.states.WildlingsAttack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static got.translation.Translator.tt;

/**
 * Created by Souverain73 on 20.04.2017.
 */
public class Wildlings {
    int level = 0;
    static int maxLevel = 4;
    private static Wildlings _instance = new Wildlings();

    public static Wildlings instance(){
        return _instance;
    }

    private HashMap<Integer, WildlingsCard> cards = new HashMap<>();
    private HashMap<String, WildlingsCard> cardsByTitle = new HashMap<>();
    private List<WildlingsCard> deck = new ArrayList<>();

    private void add(WildlingsCard card){
        cards.put(card.getID(), card);
        cardsByTitle.put(card.getTitle(), card);
        deck.add(card);
    }

    private Wildlings(){
//        add(new CommonWildlingsCard("king.png",      "King",     tt("wildlings.king")        ));
//        add(new CommonWildlingsCard("robbers.png",   "Robbers",  tt("wildlings.robbers")     ));
        add(new CommonWildlingsCard("silence.png",   "Silence",  tt("wildlings.silence")     ));
//        add(new CommonWildlingsCard("killers.png",   "Killers",  tt("wildlings.killers")     ));
        add(new Riders("riders.png",    "Riders",   tt("wildlings.riders")      ));
//        add(new CommonWildlingsCard("squad.png",     "Squad",    tt("wildlings.squad")       ));
        add(new Horde("horde.png",     "Horde",    tt("wildlings.horde")       ));
        add(new Scout(              "scout.png",     "Scout",    tt("wildlings.scout")       ));
        add(new Gathering("gathering.png", "Gathering",tt("wildlings.gathering")   ));

        deck = Utils.shuffle(deck);
    }

    public WildlingsCard getTopCard(){
        return deck.get(0);
    }

    public void moveTopCardToEnd(){
        WildlingsCard top = getTopCard();
        deck.remove(top);
        deck.add(top);
    }

    public void nextCard(){
        deck.remove(0);
    }

    public int getLevel(){
        return level;
    }

    public void nextLevel(){
        level += 2;
    }

    public boolean readyToAttack(){
        return level >= maxLevel;
    }

    public void resetLevel(){
        level = 0;
    }

    public void attack(StateMachine stm){
        stm.changeState(new AuctionState(new WildlingsAttack.ServerState(getTopCard(), getLevel())), ChangeAction.PUSH);
        resetLevel();
        moveTopCardToEnd();
    }

    public WildlingsCard getCard(int id){
        return cards.get(id);
    }
}
