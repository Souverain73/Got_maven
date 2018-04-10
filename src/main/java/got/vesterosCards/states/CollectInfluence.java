package got.vesterosCards.states;

import com.esotericsoftware.kryonet.Connection;
import got.GameClient;
import got.gameObjects.MapPartObject;
import got.gameStates.StateID;
import got.gameStates.StepByStepGameState;
import got.model.ChangeAction;
import got.model.Player;
import got.network.Packages;
import got.server.GameServer;
import got.server.PlayerManager;
import got.server.serverStates.base.StepByStepState;

/**
 * Created by Souverain73 on 12.04.2017.
 */
public class CollectInfluence {
    public static class ServerState extends StepByStepState{
        @Override
        public int getID() {
            return StateID.VESTEROS_COLLECT_INFLUENCE;
        }

        @Override
        protected void onReadyToChangeState() {
            stm.changeState(null, ChangeAction.REMOVE);
        }

        @Override
        public void recieve(Connection c, Object pkg) {
            super.recieve(c, pkg);
            GameServer.PlayerConnection connection = (GameServer.PlayerConnection) c;
            Player player = connection.player;
            if (pkg instanceof Packages.CollectInfluence) {
                Packages.CollectInfluence msg = (Packages.CollectInfluence) pkg;
                GameServer.getServer().sendToAllTCP(new Packages.PlayerCollectInfluence(msg.region, msg.count, player.id));
            }
        }
    }


    public static class ClientState extends StepByStepGameState {
        @Override
        protected void onSelfTurn() {
            super.onSelfTurn();
            int money = GameClient.shared.gameMap.getRegions().stream().filter(r->
                    r.getFraction() == PlayerManager.getSelf().getFraction()
            ).mapToInt(MapPartObject::getInfluencePoints).sum();

            GameClient.instance().logMessage("common.collectInfluence", money);
            GameClient.instance().send(new Packages.CollectInfluence(0, money));
            endTurn(false);
        }

        @Override
        public void recieve(Connection connection, Object pkg) {
            super.recieve(connection, pkg);
            if (pkg instanceof Packages.PlayerCollectInfluence) {
                Packages.PlayerCollectInfluence msg = (Packages.PlayerCollectInfluence) pkg;
                Player pl = PlayerManager.instance().getPlayer(msg.player);
                pl.addMoney(msg.count);
                GameClient.instance().logMessage("common.playerCollectInfluence", pl.getNickname(), msg.count);
            }
        }
    }

}
