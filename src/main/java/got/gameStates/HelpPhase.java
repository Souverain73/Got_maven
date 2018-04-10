package got.gameStates;

import com.esotericsoftware.kryonet.Connection;
import got.GameClient;
import got.ModalState;
import got.gameObjects.ImageObject;
import got.gameObjects.battleDeck.BattleDeckObject;
import got.gameObjects.interfaceControls.ImageButton;
import got.gameStates.modals.CustomModalState;
import got.graphics.DrawSpace;
import got.model.Player;
import got.network.Packages;
import got.server.PlayerManager;
import org.joml.Vector2f;

import static got.utils.UI.logAction;
import static got.utils.UI.tooltipWait;

/**
 * Created by Souverain73 on 28.11.2016.
 */
public class HelpPhase extends StepByStepGameState {

    private BattleDeckObject bdo;

    enum BattleSide{
        SIDE_ATTACKER(Packages.Help.SIDE_ATTACKER),
        SIDE_DEFENDER(Packages.Help.SIDE_DEFENDER),
        SIDE_NONE(Packages.Help.SIDE_NONE);

        private int id;
        BattleSide(int id){
            this.id = id;
        }

        public int getId(){
            return id;
        }
    }

    private final String name = "HelpPhase";

    @Override
    public int getID() {
        return StateID.HELP_PHASE;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void enter(StateMachine stm) {
        super.enter(stm);
        GameClient.shared.gameMap.disableAllRegions();
    }

    @Override
    protected void onSelfTurn() {
        if(bdo.getDefenderRegion().canHelp(PlayerManager.getSelf().getFraction())){

            int battlePointsToHelp = bdo.getDefenderRegion().getBattlePowerForHelpers(
                    PlayerManager.getSelf().getFraction()
            );

            BattleSide side;
            if (bdo.isBattleMember(PlayerManager.getSelf().getFraction())) {
                if (bdo.isAttacker(PlayerManager.getSelf().getFraction())) {
                    side = BattleSide.SIDE_ATTACKER;
                } else {
                    side = BattleSide.SIDE_DEFENDER;
                }
            } else {
                //TODO: отображать количество очков в диалоге выбора.
                GameClient.instance().setTooltipText("help.selectSide");
                side = showSelectSideDialogAndGetResult();
            }

            GameClient.instance().send(new Packages.Help(side.getId()));
        }else {
            endTurn(false);
        }
    }

    @Override
    protected void onEnemyTurn(Player player) {
        tooltipWait(player);
    }

    @Override
    public void recieve(Connection connection, Object pkg) {
        super.recieve(connection, pkg);
        if (pkg instanceof Packages.InitBattle) {
            Packages.InitBattle msg = (Packages.InitBattle) pkg;
            logAction("help.initBattleFromTo", msg.from, msg.to);
            bdo = new BattleDeckObject(
                    GameClient.shared.gameMap.getRegionByID(msg.from),
                    GameClient.shared.gameMap.getRegionByID(msg.to)
            );
            GameClient.shared.battleDeck = bdo;
        }

        if (pkg instanceof Packages.PlayerHelp) {
            Packages.PlayerHelp msg = (Packages.PlayerHelp) pkg;
            Player player = PlayerManager.instance().getPlayer(msg.player);
            if (msg.side == Packages.Help.SIDE_NONE) {
                GameClient.instance().logMessage("help.playerHelpNobody", player.getNickname());
            } else {
                if (msg.side == BattleSide.SIDE_ATTACKER.getId()) {
                    GameClient.instance().logMessage("help.playerHelpAttacker", player.getNickname());
                    bdo.addAttackerHelper(player);
                } else if (msg.side == BattleSide.SIDE_DEFENDER.getId()) {
                    GameClient.instance().logMessage("help.playerHelpDefender", player.getNickname());
                    bdo.addDefenderHelper(player);
                }
            }
        }
    }

    private BattleSide showSelectSideDialogAndGetResult(){
        CustomModalState<BattleSide> cms = new CustomModalState<>(BattleSide.SIDE_NONE);

        ImageObject bg = new ImageObject("DialogBG.png",  300, 150).setPos(490, 285)
                            .setSpace(DrawSpace.SCREEN);

        bg.addChild(new ImageButton("buttons/helpAttacker.png", 0,0, 100, 150, null)
                        .setSpace(DrawSpace.SCREEN)
                        .setCallback((sender, param)->cms.setResultAndClose(BattleSide.SIDE_ATTACKER))
        );

        bg.addChild(new ImageButton("buttons/helpDefender.png", 100,0, 100, 150, null)
                .setSpace(DrawSpace.SCREEN)
                .setCallback((sender, param)->cms.setResultAndClose(BattleSide.SIDE_DEFENDER))
        );

        bg.addChild(new ImageButton("buttons/helpNone.png", 200,0, 100, 150, null)
                .setSpace(DrawSpace.SCREEN)
                .setCallback((sender, param)->cms.setResultAndClose(BattleSide.SIDE_NONE))
        );

        cms.addObject(bg);

        (new ModalState(cms)).run();

        return cms.getResult();
    }

    @Override
    public void exit() {

    }
}
