package got.vesterosCards;

import got.model.Action;
import got.translation.Translator;
import got.vesterosCards.cards.*;

import java.util.HashMap;

/**
 * Created by Souverain73 on 30.03.2017.
 */
public class VesterosCards {
    static HashMap<Integer, VesterosCard> cards = new HashMap<>();
    static HashMap<String, VesterosCard> cardsByTitle = new HashMap<>();
    private static void add(VesterosCard card){
        cards.put(card.getID(), card);
        cardsByTitle.put(card.getInternalName(), card);
    }

    public static void init(){
        add(new RestrictedActionsCard("AutumnRains.png",   "AutumnRains",    Translator.tt("vesterosCard.AutumnRains"), new Action[]{Action.MOVEPLUS}).wildlings());
        add(new BattleOfKingsCard("BattleOfKings.png", "BattleOfKings",  Translator.tt("vesterosCard.BattleOfKings")));
        add(new BlackWings("BlackWings.png",    "BlackWings",     Translator.tt("vesterosCard.BlackWings")).wildlings());
        add(new CollectUnitsCard(  "CollectUnits.png",  "CollectUnits",   Translator.tt("vesterosCard.CollectUnits")));
        add(new RestrictedActionsCard("FeastForCrows.png", "FeastForCrows",  Translator.tt("vesterosCard.FeastForCrows"), new Action[]{Action.MONEY, Action.MONEYPLUS}).wildlings());
        add(new GameOfThrones("GameOfThrones.png", "GameOfThrones",  Translator.tt("vesterosCard.GameOfThrones")));
        add(new PutToSword("PutToSword.png",    "PutToSword",     Translator.tt("vesterosCard.PutToSword")));
        add(new RestrictedActionsCard("SeaOfStorms.png",   "SeaOfStorms",    Translator.tt("vesterosCard.SeaOfStorms"), new Action[]{Action.FIRE, Action.FIREPLUS}).wildlings());
        add(new RestrictedActionsCard("StormOfSwords.png", "StormOfSwords",  Translator.tt("vesterosCard.StormOfSwords"), new Action[]{Action.DEFEND, Action.DEFENDPLUS}).wildlings());
        add(new CommonVesterosCard("SummerTime1.png",   "SummerTime1",    Translator.tt("vesterosCard.SummerTime")).wildlings());
        add(new CommonVesterosCard("SummerTime2.png",   "SummerTime2",    Translator.tt("vesterosCard.SummerTime")).wildlings());
        add(new SuplyCard("Suply.png",         "Suply",          Translator.tt("vesterosCard.Suply")));
        add(new ThroneOfSwords("ThroneOfSwords.png","ThroneOfSwords", Translator.tt("vesterosCard.ThroneOfSwords")).wildlings());
        add(new RestrictedActionsCard("WebOfLie.png",      "WebOfLie",       Translator.tt("vesterosCard.WebOfLie"), new Action[]{Action.HELP, Action.HELPPLUS}).wildlings());
        add(new CommonVesterosCard("Wildlings.png",     "Wildlings",      Translator.tt("vesterosCard.Wildlings")));
        add(new CommonVesterosCard("WinterTime1.png",   "WinterTime1",    Translator.tt("vesterosCard.WinterTime")));
        add(new CommonVesterosCard("WinterTime2.png",   "WinterTime2",    Translator.tt("vesterosCard.WinterTime")));
    }

    public static VesterosCard getCard(int id){
        return cards.get(id);
    }

    public static VesterosCard getCardByName(String name){
        return cardsByTitle.get(name);
    }
}
