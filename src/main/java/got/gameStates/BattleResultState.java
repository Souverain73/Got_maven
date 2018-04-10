package got.gameStates;

import com.esotericsoftware.kryonet.Connection;
import got.Constants;
import got.GameClient;
import got.InputManager;
import got.ModalState;
import got.gameObjects.MapPartObject;
import got.gameObjects.TextObject;
import got.gameObjects.battleDeck.BattleDeckObject;
import got.gameObjects.battleDeck.BattleOverrides;
import got.gameStates.modals.SelectRegionModal;
import got.gameStates.modals.SelectUnitDialogState;
import got.gameStates.modals.SelectUnitsDialogState;
import got.graphics.DrawSpace;
import got.graphics.text.FontTrueType;
import got.houseCards.HouseCard;
import got.houseCards.HouseCardsLoader;
import got.model.*;
import got.network.Packages;
import got.server.PlayerManager;
import got.translation.Translator;
import got.utils.Timers;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.List;
import java.util.stream.Collectors;

import static got.server.PlayerManager.getSelf;
import static got.utils.UI.logAction;
import static got.utils.UI.logSystem;

/**
 * Created by Souverain73 on 15.12.2016.
 */
public class BattleResultState extends AbstractGameState{
    private BattleDeckObject BDO;

    @Override
    public String getName() {
        return "Battle result state";
    }

    @Override
    public void enter(StateMachine stm) {
        super.enter(stm);
        GameClient.shared.gameMap.disableAllRegions();
    }

    @Override
    public void recieve(Connection connection, Object pkg) {
        if (pkg instanceof Packages.BattleResult) {
            onBattleResult((Packages.BattleResult) pkg);
        }else if (pkg instanceof Packages.EndBattle) {
            Packages.EndBattle end = (Packages.EndBattle) pkg;
            endBattle();
        }else if (pkg instanceof Packages.PlayerChangeUnits) {
            Packages.PlayerChangeUnits changeUnits = (Packages.PlayerChangeUnits) pkg;
            MapPartObject region = GameClient.shared.gameMap.getRegionByID(changeUnits.region);
            Player player = PlayerManager.instance().getPlayer(changeUnits.player);
            GameClient.instance().logMessage("common.playerChangeUnits", player.getNickname(), region.getName());
            region.setUnits(changeUnits.units);
        }else if (pkg instanceof Packages.PlayerMove) {
            Packages.PlayerMove msg = (Packages.PlayerMove) pkg;
            moveAllUnits(msg.from, msg.to);
            GameClient.instance().logMessage("common.playerMoveUnits",
                    PlayerManager.instance().getPlayer(msg.player),
                    GameClient.shared.gameMap.getRegionByID(msg.from),
                    GameClient.shared.gameMap.getRegionByID(msg.to));
        }else if (pkg instanceof Packages.GetBattleResult) {
            if (GameClient.shared.battleDeck.isBattleMember(getSelf().getFraction())) {
                GameClient.instance().send(new Packages.PlayerDamage(
                        GameClient.shared.battleDeck.attackersPower,
                        GameClient.shared.battleDeck.defendersPower
                ));
            }
        }else if (pkg instanceof Packages.PlayerKillAllUnitsAtRegion) {
            Packages.PlayerKillAllUnitsAtRegion msg = (Packages.PlayerKillAllUnitsAtRegion) pkg;
            GameClient.shared.gameMap.getRegionByID(msg.regionID).removeAllUnits();
        }else if (pkg instanceof Packages.MoveAttackerToAttackRegion) {
            if (!GameClient.shared.battleDeck.overrides.noMoveAttacker) {
                moveAllUnits(GameClient.shared.battleDeck.getAttackerRegion(), GameClient.shared.battleDeck.getDefenderRegion());
                GameClient.instance().logMessage("battle.winnerMove", GameClient.shared.battleDeck.getDefenderRegion());
            }
        }else if (pkg instanceof Packages.PlayerSetAction) {
            Packages.PlayerSetAction msg = (Packages.PlayerSetAction) pkg;
            MapPartObject region = GameClient.shared.gameMap.getRegionByID(msg.region);
            if (msg.action == null) {
                GameClient.instance().logMessage("common.playerRemoveAction" + region.getName());
                region.setAction(null);
            } else {
                GameClient.instance().logMessage("common.playerSetAction" + region.getName());
                region.setAction(msg.action);
            }
        }else if (pkg instanceof Packages.PlayerSetOverrides) {
            Packages.PlayerSetOverrides msg = (Packages.PlayerSetOverrides) pkg;
            Player pl = PlayerManager.instance().getPlayer(msg.player);

            GameClient.instance().logMessage("battle.playerSetOverrides", pl.getNickname());
            GameClient.shared.battleDeck.setOverrides(msg.overrides);
        }else if (pkg instanceof Packages.PlayerRemoveHouseCard) {
            Packages.PlayerRemoveHouseCard msg = (Packages.PlayerRemoveHouseCard) pkg;
            Player sourcePlayer = PlayerManager.instance().getPlayer(msg.source);
            Player targetPlayer = PlayerManager.instance().getPlayer(msg.target);
            HouseCard cardToRemove = HouseCardsLoader.instance().getCardById(msg.houseCardID);

            GameClient.instance().logMessage("battle.playerRemoveHouseCard", sourcePlayer.getNickname(), targetPlayer.getNickname(), cardToRemove);
            targetPlayer.getDeck().useCard(cardToRemove);
        }
    }

    private void moveAllUnits(int regionFromId, int regionToId) {
        MapPartObject regionFrom = GameClient.shared.gameMap.getRegionByID(regionFromId);
        MapPartObject regionTo = GameClient.shared.gameMap.getRegionByID(regionToId);

        moveAllUnits(regionFrom, regionTo);
    }

    private void moveAllUnits(MapPartObject regionFrom, MapPartObject regionTo){
        Unit[] units = regionFrom.getUnits();

        regionFrom.removeUnits(units);
        regionTo.addUnits(units);
        regionTo.setFraction(regionFrom.getFraction());
        if (!regionFrom.havePowerToket()){
            regionFrom.setFraction(Fraction.NONE);
        }
    }

    private void onBattleResult(Packages.BattleResult battleResult) {
        BDO = GameClient.shared.battleDeck;
        Player winner = PlayerManager.instance().getPlayer(battleResult.winnerID);
        Player looser = PlayerManager.instance().getPlayer(battleResult.looserID);
        GameClient.instance().logMessage("battle.battleEnd", winner.getNickname(), looser.getNickname());
        showWinnerBanner(winner);
        Timers.getTimer(2000, ()->GameClient.instance().registerTask(()->{
            BDO.setVisible(false);
            //Убираем приказы
            BDO.getAttackerRegion().setAction(null);

            int killUnitsCount = 0;
            if (BDO.isAttacker(PlayerManager.instance().getPlayer(battleResult.winnerID).getFraction())){
                //победил атакующий
                BDO.getDefenderRegion().setAction(null);
                BDO.getAttackerCard().onWin();
                BDO.getDefenderCard().onLoose();
                killUnitsCount = BDO.getAttackerCard().getSwords() - BDO.getDefenderCard().getTowers();
            }else{
                //победил защищавшийся
                BDO.getDefenderCard().onWin();
                BDO.getAttackerCard().onLoose();
                killUnitsCount = BDO.getDefenderCard().getSwords() - BDO.getAttackerCard().getTowers();
            }

            BattleOverrides overrides = BDO.overrides;
            if (overrides.customKillCount){
                killUnitsCount = overrides.unitsToKill;
            }

            GameClient.shared.gameMap.getRegionByID(battleResult.looserRegionID).killUnits();
            if (battleResult.looserID == getSelf().id){
                //Если ты проигравший
                MapPartObject playerRegion = BDO.getPlayerRegion(getSelf());
                killUnitsCount = Math.min(killUnitsCount, playerRegion.getUnitsCount());
                if (killUnitsCount>0) {
                    GameClient.instance().setTooltipText("retreat.killUnits", killUnitsCount);
                    Unit[] unitsToKill = null;
                    while(unitsToKill == null) {
                        unitsToKill = showKillUnitsDialog(playerRegion.getUnits(), killUnitsCount);
                    }
                    playerRegion.removeUnits(unitsToKill);
                    GameClient.instance().send(new Packages.ChangeUnits(playerRegion.getID(), playerRegion.getUnits()));
                }

                //Если ты оборонялся, ты можешь выбрать куда отступить
                RetreatOrKillUnits();
            }else{
            }
        })).start(true);
    }

    private void showWinnerBanner(Player winner) {
        TextObject to;
        addObject(to = new TextObject(new FontTrueType("GOTKG", 32, new Vector3f(0.8f, 0.2f, 0.2f)),
                String.format(Translator.tt("battle.winnerBanner"), winner.getFraction())).setSpace(DrawSpace.SCREEN));
        to.setPos(new Vector2f((Constants.SCREEN_WIDTH - to.getW())/2, 100));
    }

    private void endBattle() {
        logSystem("OnBattleEnd " + getSelf());
        BDO.getAttackerCard().onBattleEnd();
        BDO.getDefenderCard().onBattleEnd();
        GameClient.instance().sendReady(true);
    }

    private void retreatTo(MapPartObject regionToRetreat) {
        MapPartObject playerRegion = GameClient.shared.battleDeck.getPlayerRegion(getSelf());
        MapPartObject moveRegion = regionToRetreat;

        //Активным может быть регион не проходящий по снабжению, поэтому проверяем снабжение
        if (!Game.instance().getSuplyTrack().canMove(playerRegion.getFraction(), playerRegion, moveRegion, playerRegion.getUnitsCount())){
            //Считаем сколько юнитов надо убить что бы совершить этот ход
            int i = 0;
            while(i<4 && Game.instance().getSuplyTrack().canMove(playerRegion.getFraction(), playerRegion, moveRegion, i+1)){
                i++;
            }
            if (i==0){
                throw new IllegalStateException("Активен регион в который пользователь не может совершить ход");
            }
            int unitsToKillCount = playerRegion.getUnitsCount() - i;
            Unit[] unitsToKill = null;
            while (unitsToKill == null) {
                GameClient.instance().setTooltipText("retreat.killUnits", unitsToKillCount);
                GameClient.instance().logMessage("retreat.suplyKill", unitsToKillCount);
                unitsToKill = showKillUnitsDialog(playerRegion.getUnits(), unitsToKillCount);
            }
            playerRegion.removeUnits(unitsToKill);
            GameClient.instance().send(new Packages.ChangeUnits(playerRegion.getID(), playerRegion.getUnits()));
            GameClient.instance().send(new Packages.Move(playerRegion.getID(), moveRegion.getID(), playerRegion.getUnits()));
            GameClient.instance().send(new Packages.LooserReady());

        }else{
            GameClient.instance().send(new Packages.Move(playerRegion.getID(), moveRegion.getID(), playerRegion.getUnits()));
            GameClient.instance().send(new Packages.LooserReady());
        }
    }

    private void RetreatOrKillUnits() {
        GameClient.instance().logMessage("retreat.wait");
        if (BDO.isAttacker(getSelf().getFraction()) && !BDO.overrides.customRetreat){
            //Если ты атаковал по стандартным правилам ты не можешь выбирать регион для отступления и остаешься на месте
            GameClient.instance().send(new Packages.LooserReady());
            return;
        }

        //Если ты проиграл или утановлены другие правила отступления надо выбрать регион для отступления.
        if (BDO.overrides.customRetreat){
            //если регион отступления переопределен
            retreatTo(GameClient.shared.gameMap.getRegionByID(BDO.overrides.regionToRetreat));
            return;
        }
        //стандартные правила отступления
        GameClient.shared.gameMap.disableAllRegions();
        MapPartObject regionToRetreat = null;
        MapPartObject regionFrom = GameClient.shared.battleDeck.getDefenderRegion();
        List<MapPartObject> regionsToMove = regionFrom.getRegionsToMove();
        //Ищем регионы куда можно отступить не нарушая снабжения
        List<MapPartObject> regionsToRetreat =
                regionsToMove.stream().filter(r->{
                    //нельзя отступить в чужой регион
                    if (regionFrom.getFraction() != r.getFraction() && r.getFraction() != Fraction.NONE) return false;

                    return Game.instance().getSuplyTrack().canMove(regionFrom.getFraction(), regionFrom, r, regionFrom.getUnitsCount());
                }).collect(Collectors.toList());

        if (regionsToRetreat.size() > 0){
            GameClient.instance().setTooltipText("retreat.selectRegion");
            regionToRetreat = SelectRegionModal.selectFrom(regionsToRetreat);
            retreatTo(regionToRetreat);
            return;
        }

        //Если нет регионов в которые можно пойти без убийства юнитов, ищем регион в который можно пойти хотя бы 1-м юнитом
        regionsToRetreat =
                regionsToMove.stream().filter(r->{
                    //нельзя отступить в чужой регион
                    if (regionFrom.getFraction() != r.getFraction() && r.getFraction() != Fraction.NONE) return false;

                    return Game.instance().getSuplyTrack().canMove(regionFrom.getFraction(), regionFrom, r, 1);
                }).collect(Collectors.toList());

        if (regionsToRetreat.size() > 0){
            GameClient.instance().setTooltipText("retreat.selectRegion");
            regionToRetreat = SelectRegionModal.selectFrom(regionsToRetreat);
            retreatTo(regionToRetreat);
            return;
        }

        //Если нет регионов куда вообще можно пойти, убиваем всех юнитов.
        GameClient.instance().logMessage("retreat.noRegions");
        GameClient.instance().send(new Packages.KillAllUnitsAtRegion(regionFrom.getID()));
        //Сообщаем, что проигравций закончил отступление.
        GameClient.instance().send(new Packages.LooserReady());
    }

    private Unit[] showKillUnitsDialog(Unit[] units, int countToKill){
        SelectUnitsDialogState suds = new SelectUnitsDialogState(units, countToKill, countToKill);
        (new ModalState(suds)).run();

        if (suds.isOk()) {
            return suds.getSelectedUnits();
        } else return null;
    }

    @Override
    public void exit() {
        GameClient.shared.battleDeck.finish();
        GameClient.shared.battleDeck = null;
    }
}
