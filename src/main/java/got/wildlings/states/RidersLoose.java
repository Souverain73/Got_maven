package got.wildlings.states;

import com.esotericsoftware.kryonet.Connection;
import got.GameClient;
import got.InputManager;
import got.ModalState;
import got.gameObjects.MapPartObject;
import got.gameStates.ParallelGameState;
import got.gameStates.StateID;
import got.gameStates.StateMachine;
import got.gameStates.modals.SelectUnitsDialogState;
import got.interfaces.IClickListener;
import got.model.ChangeAction;
import got.model.Player;
import got.model.Unit;
import got.network.Packages;
import got.server.GameServer;
import got.server.PlayerManager;
import got.server.serverStates.base.ParallelState;

/**
 * Created by КизиловМЮ on 06.07.2017.
 */
public class RidersLoose {
    public static class ClientState extends ParallelGameState implements IClickListener{
        private int unitsToKill;
        private Packages.WildlingsData data;

        @Override
        public String getName() {
            return "RidersLoose";
        }

        @Override
        public void enter(StateMachine stm) {
            super.enter(stm);
            data = (Packages.WildlingsData) stm.getParam(StateMachine.WILDLINGS_DATA_PARAM);
            unitsToKill = PlayerManager.getSelf().getFraction() == data.actor ? 3 : 2;
            enableRegionsToKillUnits();
            if (GameClient.shared.gameMap.getEnabledRegions().isEmpty())
                endTurn();
        }

        private void enableRegionsToKillUnits(){
            GameClient.shared.gameMap.enableByCondition(r->
                r.getFraction() == PlayerManager.getSelf().getFraction()
                        &&r.getUnits().length > 0);
        }

        @Override
        public void click(InputManager.ClickEvent event) {
            if (event.getTarget() instanceof MapPartObject) {
                MapPartObject region = (MapPartObject) event.getTarget();
                SelectUnitsDialogState suds = new SelectUnitsDialogState(region.getUnits(),
                        0, Math.max(unitsToKill, region.getUnitsCount()));
                (new ModalState(suds)).run();
                if (suds.isOk()){
                    Unit[] units = suds.getSelectedUnits();
                    if (units.length == 0) return;
                    region.removeUnits(units);
                    GameClient.instance().send(new Packages.ChangeUnits(region.getID(), region.getUnits()));

                    unitsToKill -= units.length;
                    if (region.getUnitsCount() == 0){
                        region.setEnabled(false);
                    }

                    if (unitsToKill == 0 || GameClient.shared.gameMap.getEnabledRegions().isEmpty()){
                        endTurn();
                    }
                }
            }
        }

        private void endTurn(){
            GameClient.shared.gameMap.disableAllRegions();
            setReady(true);
        }

        @Override
        public void recieve(Connection connection, Object pkg) {
            super.recieve(connection, pkg);
            if (pkg instanceof Packages.PlayerChangeUnits) {
                Packages.PlayerChangeUnits msg = (Packages.PlayerChangeUnits) pkg;
                MapPartObject region = GameClient.shared.gameMap.getRegionByID(msg.region);
                region.setUnits(msg.units);
            }
        }

        @Override
        public int getID() {
            return StateID.WILDLINGS_RIDERS_LOOSE;
        }
    }

    public static class ServerState extends ParallelState{
        @Override
        public int getID() {
            return StateID.WILDLINGS_RIDERS_LOOSE;
        }

        @Override
        public void recieve(Connection connection, Object pkg) {
            super.recieve(connection, pkg);
            GameServer.PlayerConnection c = (GameServer.PlayerConnection) connection;
            Player player = c.player;

            if (pkg instanceof Packages.ChangeUnits) {
                Packages.ChangeUnits msg = (Packages.ChangeUnits) pkg;
                GameServer.getServer().sendToAllTCP(new Packages.PlayerChangeUnits(player.id, msg.region, msg.units));
            }
        }

        @Override
        protected void onReadyToChangeState() {
            stm.changeState(null, ChangeAction.REMOVE);
        }
    }
}
