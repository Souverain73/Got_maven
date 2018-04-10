package got.server.serverStates.base;

import com.esotericsoftware.kryonet.Connection;
import got.interfaces.IPauseable;
import got.model.ChangeAction;
import got.model.Game;
import got.model.Player;
import got.network.Packages;
import got.server.GameServer;
import got.server.PlayerManager;
import got.server.serverStates.StateMachine;

import java.util.Arrays;

/**
 * Created by Souverain73 on 25.11.2016.
 */
public abstract class StepByStepState implements ServerState, IPauseable{

    protected boolean playersReady[];
    protected StateMachine stm;
    protected Player currentPlayer;
    private Class<? extends ServerState> nextStateClass;

    @Override
    public String getName() {
        return "StepByStepState;";
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

        //get first player on throne track
        currentPlayer = PlayerManager.instance().getPlayerByFraction(
                Game.instance().getTrack(Game.THRONE_TRACK).getFirst());
        //Правильно было бы проверить, может ли игрок совершить ход.
        //Но в текущей архитектуе сервер самостоятельно не может этого сделать,
        //поэтому отдадим это на сторону клиента.
        GameServer.getServer().sendToAllTCP(new Packages.PlayerTurn(currentPlayer.id));
    }

    @Override
    public void exit() {

    }

    @Override
    public void recieve(Connection c, Object pkg) {
        GameServer.PlayerConnection connection = ((GameServer.PlayerConnection)c);
        Player player = connection.player;
        if (pkg instanceof Packages.Ready){
            //Если о готовности сообщает не текущий игрок, игнорируем сообщение.
            if (player.id != currentPlayer.id) return;
            handleReady(currentPlayer, ((Packages.Ready) pkg).ready);
        }
    }

    protected void handleReady(Player player, boolean ready) {
        //если свойство ready = true, значит игрок совершил ход
        //если false, значит возможных ходов для него больше нет

        if (!ready){
            playersReady[player.id] = true;
        }
        //проверяем, если все игроки готовы, значит никто больше не может совершить ход, значит можно переходить к следующей фазе.
        if (isAllPlayersReady()){
            onReadyToChangeState();
            return;
        }
        //передаем управление следующему игроку.
        nextTurn();
    }

    protected void onReadyToChangeState(){
        if (nextStateClass == null){
            stm.changeState(null, ChangeAction.REMOVE);
            return;
        }

        try {
            stm.changeState(nextStateClass.newInstance(), ChangeAction.SET);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Can't instantiate next State from CLASS object");
            System.exit(0);
        }
    }

    private boolean isAllPlayersReady() {
        for(boolean ready: playersReady){
            if (!ready) return false;
        }
        return true;
    }

    protected void nextTurn(){
        do {
            currentPlayer = PlayerManager.instance().getPlayerByFraction(
                    Game.instance().getTrack(Game.THRONE_TRACK).getNext(currentPlayer.getFraction()));
        }while (playersReady[currentPlayer.id]);
        GameServer.getServer().sendToAllTCP(new Packages.PlayerTurn(currentPlayer.id));
    }
    
    protected void setNextState(Class<? extends ServerState> nextStateClass){
        this.nextStateClass = nextStateClass;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public String toString() {
        return "StepByStepState " + Arrays.toString(playersReady);
    }
}
