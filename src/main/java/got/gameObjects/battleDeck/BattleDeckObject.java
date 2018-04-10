package got.gameObjects.battleDeck;

import got.Constants;
import got.GameClient;
import got.gameObjects.*;
import got.graphics.DrawSpace;
import got.graphics.text.FontTrueType;
import got.houseCards.HouseCard;
import got.model.*;
import got.server.PlayerManager;
import got.utils.UI;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Souverain73 on 25.11.2016.
 */

public class BattleDeckObject extends AbstractGameObject<BattleDeckObject> {
    @Override protected BattleDeckObject getThis() {return this;}

    private final FontTrueType powerFont = new FontTrueType("GOTKG", 72);

    private final ContainerObject attackerContainer;
    private final ContainerObject defenderContainer;

    private final List<BattleCardObject> attackers = new ArrayList<>();
    private final List<BattleCardObject> defenders = new ArrayList<>();

    private final ImageObject attackerHouseCardObject;
    private final ImageObject defenderHouseCardObject;

    private final TextObject attackersPowerText;
    private final TextObject defendersPowerText;

    private final MapPartObject attackerRegion;
    private final MapPartObject defenderRegion;

    private final Player attackerPlayer;
    private final Player defenderPlayer;

    private HouseCard attackerCard;
    private HouseCard defenderCard;

    private boolean attackersCardUsed = false;
    private boolean defendersCardUsed = false;

    public int attackersPower;
    public int defendersPower;

    public BattleOverrides overrides;

    public BattleDeckObject(MapPartObject attackerRegion, MapPartObject defenderRegion) {
        this.attackerRegion = attackerRegion;
        this.defenderRegion = defenderRegion;

        this.attackerPlayer = attackerRegion.getOwnerPlayer();
        this.defenderPlayer = defenderRegion.getOwnerPlayer();

        this.overrides = new BattleOverrides();

        ImageObject background = new ImageObject("BattleDeckBG.png",
                500, 100).setPos((Constants.SCREEN_WIDTH-500)/2, 80
        )
                .setSpace(DrawSpace.SCREEN);

        attackerContainer = new ContainerObject()
                .setPos(new Vector2f(0, 0))
                .setDim(new Vector2f(250, 100))
                .setSpace(DrawSpace.SCREEN);

        BattleCardObject attackerCard = new BattleCardObject(attackerRegion.getFraction(), attackerRegion)
                .setSpace(DrawSpace.SCREEN).setPos(new Vector2f(400, 0));
        attackers.add(attackerCard);
        attackerContainer.addChild(attackerCard);

        defenderContainer = new ContainerObject()
                .setPos(new Vector2f(250, 0))
                .setDim(new Vector2f(250, 100))
                .setSpace(DrawSpace.SCREEN);

        BattleCardObject defenderCard = new BattleCardObject(defenderRegion.getFraction(), defenderRegion)
                .setSpace(DrawSpace.SCREEN).setPos(new Vector2f(0, 0));
        defenders.add(defenderCard);
        defenderContainer.addChild(defenderCard);

        //House cards
        attackerHouseCardObject = new ImageObject(attackerPlayer.getFraction().getCoverTexture(),
                 230, 357).setPos(300,250).setSpace(DrawSpace.SCREEN);
        defenderHouseCardObject = new ImageObject(defenderPlayer.getFraction().getCoverTexture(),
                 230, 357).setPos(750, 250).setSpace(DrawSpace.SCREEN);

        addChild(attackerHouseCardObject);
        addChild(defenderHouseCardObject);

        attackersPowerText = new TextObject(powerFont, "").setSpace(DrawSpace.SCREEN).setPos(new Vector2f(440, 175));
        defendersPowerText = new TextObject(powerFont, "").setSpace(DrawSpace.SCREEN).setPos(new Vector2f(740, 175));

        addChild(attackersPowerText);
        addChild(defendersPowerText);

        addChild(background);
        background.addChild(attackerContainer);
        background.addChild(defenderContainer);
        setSpace(DrawSpace.SCREEN);
        updateState(true);
    }

    public void addAttackerHelper(Player player){
        addBattler(player, attackers, attackerContainer);
    }

    public void addDefenderHelper(Player player){
        addBattler(player, defenders, defenderContainer);
    }

    private void addBattler(Player player, List<BattleCardObject> set, ContainerObject container){

        List<MapPartObject> regionsForHelp = defenderRegion.getRegionsForHelp(player.getFraction());
        for (MapPartObject region : regionsForHelp){
            BattleCardObject cpc = new BattleCardObject(player.getFraction(), region);
            set.add(cpc);
            container.addChild(cpc);
            updateState(true);
        }
    }

    private void updateState(boolean fullUpdate) {
        if (fullUpdate) {
            int xa = 200;
            int xd = 000;
            for (int i = 0; i < 4; i++) {
                if (i < attackers.size()) {
                    BattleCardObject attacker = attackers.get(i);
                    attacker.setPos(new Vector2f(xa, 0));
                }

                if (i < defenders.size()) {
                    BattleCardObject defender = defenders.get(i);
                    defender.setPos(new Vector2f(xd, 0));
                }

                xa -= 50;
                xd += 50;
            }
        }
        attackersPower = getAttackPower(attackers);
        if (attackerCard!=null) attackersPower += attackerCard.getPower();

        attackersPowerText.setText(String.valueOf(attackersPower));

        defendersPower = getDefendPower(defenders);
        if (defenderCard!=null) defendersPower += defenderCard.getPower();

        defendersPowerText.setText(String.valueOf(defendersPower));

        UI.logAction("Battle deck updated: " + attackersPower + " vs " + defendersPower);
    }

    private int getAttackPower(List<BattleCardObject> list) {
        return list.stream()
                .mapToInt(card->card.getBattlePower(BattleSide.ATTACKER, defenderRegion.getBuildingLevel()>0))
                .sum();
    }

    private int getDefendPower(List<BattleCardObject> list) {
        return list.stream()
                .mapToInt(card->card.getBattlePower(BattleSide.DEFENDER, defenderRegion.getBuildingLevel()>0))
                .sum();
    }

    public void placeCard(HouseCard card, Player player){
        if (isAttacker(player.getFraction())){
            if (attackerCard == null) {
                attackerCard = card;
            }else{
                player.getDeck().rewindCard(attackerCard);
                attackerCard = card;
                attackersCardUsed = false;
            }
            if (player.getFraction() == PlayerManager.getSelf().getFraction())
                attackerHouseCardObject.setTexture(card.getTexture());
        }
        if (isDefender(player.getFraction())){
            if (defenderCard == null) {
                defenderCard = card;
            }else{
                player.getDeck().rewindCard(defenderCard);
                defenderCard = card;
                defendersCardUsed = false;
            }
            if (player.getFraction() == PlayerManager.getSelf().getFraction())
                defenderHouseCardObject.setTexture(card.getTexture());
        }
        if (attackerCard != null && defenderCard != null){
            attackerHouseCardObject.setTexture(attackerCard.getTexture());
            defenderHouseCardObject.setTexture(defenderCard.getTexture());
            resetCardEffects();
            if (!defendersCardUsed) {
                defendersCardUsed = true;
                defenderCard.onPlace(defenderPlayer.getFraction());
            }
            if (!attackersCardUsed) {
                attackersCardUsed = true;
                attackerCard.onPlace(attackerPlayer.getFraction());
            }
            GameClient.instance().sendReady(true);
        }
        updateState(false);
    }

    private void resetCardEffects() {
        for(BattleCardObject card : attackers){
            card.resetEffect();
        }
        for(BattleCardObject card : defenders){
            card.resetEffect();
        }
    }

    public MapPartObject getAttackerRegion() {
        return attackerRegion;
    }

    public MapPartObject getDefenderRegion() {
        return defenderRegion;
    }

    public List<BattleCardObject> getAttackers() {
        return attackers;
    }

    public List<BattleCardObject> getDefenders() {
        return defenders;
    }

    public Player getAttackerPlayer() {
        return attackerPlayer;
    }

    public Player getDefenderPlayer() {
        return defenderPlayer;
    }

    public HouseCard getAttackerCard() {
        return attackerCard;
    }

    public HouseCard getDefenderCard() {
        return defenderCard;
    }

    public boolean isBattleMember(Fraction fraction){
        return  (isAttacker(fraction) || isDefender(fraction));
    }

    public boolean isDefender(Fraction fraction) {
        return fraction == defenderRegion.getFraction();
    }

    public boolean isAttacker(Fraction fraction){
        return fraction == attackerRegion.getFraction();
    }

    public MapPartObject getPlayerRegion(Player player) {
        return getPlayerRegion(player.getFraction());
    }

    public MapPartObject getPlayerRegion(Fraction playerFraction){
        if (playerFraction == defenderRegion.getFraction()) return defenderRegion;
        if (playerFraction == attackerRegion.getFraction()) return attackerRegion;
        throw new IllegalStateException("Игрок попыталя получить свой регион в бою, но он не был участником боя.");
    }

    public void setOverrides(BattleOverrides over){
        overrides = over;
    }

    public void onRegionStateChanged(MapPartObject region){
        BattleCardObject card = getCardForRegion(region);
        if (card == null)
            return;

        if (card != defenders.get(0) && region.getAction() == null){
            removeCard(card);
        }else {
            card.updateState();
        }
        updateState(true);
    }

    private void removeCard(BattleCardObject card) {
        attackers.remove(card);
        attackerContainer.removeChild(card);
        defenders.remove(card);
        defenderContainer.removeChild(card);
        card.finish();
    }

    public BattleCardObject getCardForRegion(MapPartObject region){
        for (BattleCardObject card : attackers){
            if (card.getRegion() == region) return card;
        }
        for (BattleCardObject card : defenders){
            if (card.getRegion() == region) return card;
        }
        return null;
    }

    public boolean isSupported(Fraction fraction) {
        if (isAttacker(fraction)){
            return attackers.size()>1;
        }else if (isDefender(fraction)){
            return defenders.size()>1;
        }
        return false;
    }
}
