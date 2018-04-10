package got.gameStates;

import com.esotericsoftware.kryonet.Connection;
import got.GameClient;
import got.gameObjects.gui.ThroneTrackObject;
import got.model.Player;
import got.network.Packages;
import got.server.PlayerManager;

/**
 * Created by Souverain73 on 24.03.2017.
 */
public class ParallelGameState extends AbstractGameState {
    @Override
    public void enter(StateMachine stm) {
        super.enter(stm);
        //Reset ready flag for all players
        for (Player pl: PlayerManager.instance().getPlayersList()){
            pl.setReady(false);
        }

        GameClient.shared.gui.getThroneTrack().setHighlightMode(ThroneTrackObject.HighlightMode.HIGHLIGHT_READY);
    }

    @Override
    public void exit() {
        super.exit();
        GameClient.shared.gui.getThroneTrack().setHighlightMode(ThroneTrackObject.HighlightMode.NO_HIGHLIGHT);
    }

    protected void toggleReady(){
        GameClient.instance().sendReady(!PlayerManager.getSelf().isReady());
    }
    protected void setReady(boolean ready){
        if (PlayerManager.getSelf().isReady() == ready) return;
        toggleReady();
    }
    protected void onReady(Player player){}
    protected void onUnready(Player player){}
    protected void onAllReady(){}

    @Override
    public void recieve(Connection connection, Object pkg) {
        super.recieve(connection, pkg);
        if (pkg instanceof Packages.PlayerReady){
            Packages.PlayerReady msg = (Packages.PlayerReady) pkg;
            Player player = PlayerManager.instance().getPlayer(msg.playerID);
            player.setReady(msg.ready);
            if (msg.ready){
                onReady(player);
            }else{
                onUnready(player);
            }
            if (PlayerManager.instance().isAllPlayersReady()){
                onAllReady();
            }
        }
    }
}
