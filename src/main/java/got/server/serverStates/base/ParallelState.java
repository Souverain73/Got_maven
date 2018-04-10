package got.server.serverStates.base;

import com.esotericsoftware.kryonet.Connection;
import got.interfaces.IPauseable;
import got.model.Player;
import got.network.Packages;
import got.server.GameServer;
import got.server.PlayerManager;
import got.server.serverStates.StateMachine;
import got.utils.UI;

import java.util.Arrays;

/**
 * Created by Souverain73 on 17.01.2017.
 */
public abstract class ParallelState implements ServerState, IPauseable {
    protected boolean playersReady[];
    protected StateMachine stm;
    private Class<? extends ServerState> nextStateClass;

    @Override
    public String getName() {
        return getClass().getName();
    }

    @Override
    public int getID() {
        return 0;
    }

    @Override
    public void enter(StateMachine stm) {
        this.stm = stm;
        //Если у игрока стоит признак готовности, значит он не может больше совершить ход
        //Когда все игроки будут готовы, необходимо осуществить переход к следующей фазе.

        //Все ID лежат в диапазоне [0..количество игроков).
        playersReady = new boolean[PlayerManager.instance().getPlayersCount()];
    }

    @Override
    public void exit() {

    }

    @Override
    public void recieve(Connection connection, Object pkg) {
        GameServer.PlayerConnection c = (GameServer.PlayerConnection) connection;
        Player player = c.player;
        if (pkg instanceof Packages.Ready) {
            Packages.Ready msg = (Packages.Ready) pkg;
            onReady(player, msg.ready);
        }
    }

    protected void onReady(Player player, boolean ready) {
        GameServer.getServer().sendToAllTCP(new Packages.PlayerReady(player.id, ready));
        UI.logSystem("player " + player.getNickname() + " set Ready");
        playersReady[player.id] = ready;
        if (isAllPlayersReady()){
            onReadyToChangeState();
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    protected void setNextState(Class<? extends ServerState> nextStateClass){
        this.nextStateClass = nextStateClass;
    }

    private boolean isAllPlayersReady() {
        for(boolean ready: playersReady){
            if (!ready) return false;
        }
        return true;
    }

    protected abstract void onReadyToChangeState();

    @Override
    public String toString() {
        return getName() + Arrays.toString(playersReady);
    }
}
