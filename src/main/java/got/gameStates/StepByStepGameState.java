package got.gameStates;

import com.esotericsoftware.kryonet.Connection;
import got.GameClient;
import got.gameObjects.gui.ThroneTrackObject;
import got.model.Game;
import got.model.Player;
import got.network.Packages;
import got.server.PlayerManager;

/**
 * Created by Souverain73 on 24.03.2017.
 */
public class StepByStepGameState extends AbstractGameState {

    @Override
    public void enter(StateMachine stm) {
        super.enter(stm);

        PlayerManager.instance().setCurrentPlayer(
                PlayerManager.instance().getPlayerByFraction(
                        Game.instance().getTrack(Game.THRONE_TRACK).getFirst())
        );

        GameClient.shared.gui.getThroneTrack().setHighlightMode(ThroneTrackObject.HighlightMode.HIGHLIGHT_CURRENT_PLAYER);
    }

    @Override
    public void exit() {
        super.exit();
        GameClient.shared.gui.getThroneTrack().setHighlightMode(ThroneTrackObject.HighlightMode.NO_HIGHLIGHT);
    }

    @Override
    public void recieve(Connection connection, Object pkg) {
        super.recieve(connection, pkg);
        if (pkg instanceof Packages.PlayerTurn) {
            Packages.PlayerTurn msg = (Packages.PlayerTurn) pkg;
            Player player = PlayerManager.instance().getPlayer(msg.playerID);
            PlayerManager.instance().setCurrentPlayer(player);
            if (player.id == PlayerManager.getSelf().id){
                onSelfTurn();
            }else{
                onEnemyTurn(player);
            }
        }
    }

    protected void onEnemyTurn(Player player) {
    }

    protected void onSelfTurn() {
    }

    protected void endTurn(boolean haveMoreTurns){
        GameClient.instance().sendReady(haveMoreTurns);
    }
}
