package got.wildlings.states;

import com.esotericsoftware.kryonet.Connection;
import got.GameClient;
import got.InputManager;
import got.ModalState;
import got.gameObjects.MapPartObject;
import got.gameStates.ParallelGameState;
import got.gameStates.StateID;
import got.gameStates.modals.SelectUnitsDialogState;
import got.interfaces.IClickListener;
import got.model.ChangeAction;
import got.model.Player;
import got.model.Unit;
import got.network.Packages;
import got.server.GameServer;
import got.server.PlayerManager;
import got.server.serverStates.StateMachine;
import got.server.serverStates.base.ParallelState;
import org.joml.Vector2f;

/**
 * Created by КизиловМЮ on 04.07.2017.
 */
public class HordeLoose {
    public static class ClientState extends ParallelGameState implements IClickListener{
        Packages.WildlingsData data;
        private boolean looser;
        private boolean inCastle = true;
        private int unitsLeft;
        @Override
        public void enter(got.gameStates.StateMachine stm) {
            super.enter(stm);
            data = (Packages.WildlingsData) stm.getParam(StateMachine.WILDLINGS_DATA_PARAM);
            looser = PlayerManager.getSelf().getFraction() == data.actor;
            enableActiveRegions();
        }

        private void enableActiveRegions(){
            if (!looser) {
                unitsLeft = 1;
                GameClient.shared.gameMap.enableByCondition(r ->
                        (r.getFraction() == PlayerManager.getSelf().getFraction()) && r.getUnitsCount() > 0
                );
            } else{
                unitsLeft = 2;
                //Регионы с замком или крепостью и 2 или более юнитами
                GameClient.shared.gameMap.enableByCondition(r->
                    r.getFraction() == PlayerManager.getSelf().getFraction()
                            && r.getBuildingLevel() > 0
                            && r.getUnitsCount() >= 2
                );
                //если таких нет, то все регионы с юнитами
                if (GameClient.shared.gameMap.getEnabledRegions().size() == 0){
                    inCastle = false;
                    GameClient.shared.gameMap.enableByCondition(r->
                        (r.getFraction() == PlayerManager.getSelf().getFraction()) && r.getUnitsCount() > 0
                    );
                }
            }
            //Если некого убить, автоматически проставляется готовность.
            if (GameClient.shared.gameMap.getEnabledRegions().size() == 0){
                setReady(true);
            }
        }


        @Override
        public void click(InputManager.ClickEvent event) {
            if (event.getTarget() instanceof MapPartObject) {
                MapPartObject region = (MapPartObject) event.getTarget();
                if (looser && inCastle){
                    SelectUnitsDialogState suds = new SelectUnitsDialogState(region.getUnits(), 2, 2);
                    (new ModalState(suds)).run();
                    if (suds.isOk()) {
                        Unit[] unitsToKill = suds.getSelectedUnits();
                        region.removeUnits(unitsToKill);
                        GameClient.instance().send(new Packages.ChangeUnits(region.getID(), region.getUnits()));
                        endTurn();
                    }
                }else{
                    SelectUnitsDialogState suds = new SelectUnitsDialogState(region.getUnits(), 1, unitsLeft);
                    (new ModalState(suds)).run();
                    if (suds.isOk()) {
                        Unit[] unitsToKill = suds.getSelectedUnits();
                        region.removeUnits(unitsToKill);
                        unitsLeft -= unitsToKill.length;
                        GameClient.instance().send(new Packages.ChangeUnits(region.getID(), region.getUnits()));
                        if (region.getUnitsCount() == 0){
                            region.setEnabled(false);
                        }

                        if (unitsLeft == 0 || GameClient.shared.gameMap.getEnabledRegions().isEmpty()) {
                            endTurn();
                        }

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
    }

    public static class ServerState extends ParallelState{
        @Override
        public int getID() {
            return StateID.WILDLINGS_HORDE_LOOSE;
        }

        @Override
        public void enter(StateMachine stm) {
            super.enter(stm);
        }

        @Override
        protected void onReadyToChangeState() {
            stm.changeState(null, ChangeAction.REMOVE);
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
    }
}
