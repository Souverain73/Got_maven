package got.server.serverStates;

import com.esotericsoftware.kryonet.Connection;
import got.gameStates.StateID;
import got.model.ChangeAction;
import got.model.Player;
import got.network.Packages;
import got.server.GameServer;
import got.server.PlayerManager;
import got.server.serverStates.base.ServerState;

/**
 * Created by Souverain73 on 29.11.2016.
 */
public class SelectHouseCardPhaseState implements ServerState {
    private StateMachine stm;
    public SelectHouseCardPhaseState(){

    }

    @Override
    public String getName() {
        return "Select house card state";
    }

    @Override
    public int getID() {
        return StateID.SELECT_HOUSE_CARD_PHASE;
    }

    @Override
    public void enter(StateMachine stm) {
       this.stm = stm;
        //Сбрасываем готовность игроков.
        for (Player pl : PlayerManager.instance().getPlayersList()){
            pl.setReady(false);
        }
    }

    @Override
    public void exit() {

    }

    @Override
    public void recieve(Connection connection, Object pkg) {
        GameServer.PlayerConnection c = (GameServer.PlayerConnection) connection;
        Player player = c.player;
        if (pkg instanceof Packages.SelectHouseCard) {
            Packages.SelectHouseCard msg = (Packages.SelectHouseCard) pkg;
            GameServer.getServer().sendToAllTCP(new Packages.PlayerSelectHouseCard(player.id, msg.card));
        }
        if (pkg instanceof Packages.Ready) {
            Packages.Ready msg = (Packages.Ready) pkg;
            player.setReady(msg.ready);
            //Все игроки будут готовы после выполнения метода onPlace для выбранных карт домов.
            if (PlayerManager.instance().isAllPlayersReady()){
                stm.changeState(new BattleResultState(), ChangeAction.SET);
            }
        }
        if (pkg instanceof Packages.KillUnit) {
            Packages.KillUnit msg = (Packages.KillUnit) pkg;
            GameServer.getServer().sendToAllTCP(new Packages.PlayerKillUnit(player.id, msg.region, msg.unit));
        }
        if (pkg instanceof Packages.SetAction) {
            Packages.SetAction msg = (Packages.SetAction) pkg;
            GameServer.getServer().sendToAllTCP(new Packages.PlayerSetAction(msg.region, msg.action));
        }
    }
}
