package got.vesterosCards.states;

import com.esotericsoftware.kryonet.Connection;
import got.GameClient;
import got.InputManager;
import got.ModalState;
import got.gameObjects.GameObject;
import got.gameObjects.MapPartObject;
import got.gameStates.StateID;
import got.gameStates.StateMachine;
import got.gameStates.StepByStepGameState;
import got.gameStates.modals.HireMenuState;
import got.interfaces.IClickListener;
import got.model.ChangeAction;
import got.model.Fraction;
import got.model.Player;
import got.model.Unit;
import got.network.Packages;
import got.server.GameServer;
import got.server.PlayerManager;
import got.server.serverStates.base.StepByStepState;
import got.utils.Utils;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Souverain73 on 12.04.2017.
 */
public class CollectUnits {
    public static class ClientState extends StepByStepGameState implements IClickListener {
        private enum SubState {
            SELECT_SOURCE, SELECT_TARGET
        }

        private SubState state;
        private HashMap<String, Integer> hirePointsCache;

        private MapPartObject source; // Hire point source

        @Override
        public void enter(StateMachine stm) {
            super.enter(stm);
            state = SubState.SELECT_SOURCE;
            hirePointsCache = new HashMap<>();

            GameClient.shared.gameMap.disableAllRegions();
            addObject(Utils.getReadyButton(null)
                    .setCallback((sender, param)->
                        endTurn(false)
                    ));
        }

        @Override
        protected void onSelfTurn() {
            super.onSelfTurn();
            enableRegionsToHire();
            if (GameClient.shared.gameMap.getEnabledRegions().isEmpty()){
                endTurn(false);
            }
        }

        @Override
        protected void onEnemyTurn(Player player) {
            super.onEnemyTurn(player);
            GameClient.shared.gameMap.disableAllRegions();
        }

        @Override
        public void click(InputManager.ClickEvent event) {
            GameObject sender = event.getTarget();
            if (sender instanceof MapPartObject) {
                MapPartObject region = (MapPartObject) sender;

                if (state == SubState.SELECT_SOURCE) {
                    int hirePoints = getHirePoints(region);
                    if (hirePoints > 0) {
                        List<MapPartObject> regionsForHire = region.getRegionsForHire();
                        if (regionsForHire.size() == 1) {
                            hireUnits(region, region);
                        } else {
                            GameClient.shared.gameMap.disableAllRegions();
                            regionsForHire.forEach(obj -> obj.setEnabled(true));
                            state = SubState.SELECT_TARGET;
                            source = region;
                        }
                    }
                } else if (state == SubState.SELECT_TARGET) {
                    hireUnits(source, region);
                }
            }
        }

        private void hireUnits(MapPartObject source, MapPartObject target) {
            //todo:проверить по снабжению, сколько юнитов сюда влезет
            HireMenuState hms = new HireMenuState(target.getUnitObjects(), InputManager.instance().getMousePosWorld(),
                    getHirePoints(source), target.getType() == MapPartObject.RegionType.SEA);
            target.hideUnits();
            (new ModalState(hms)).run();
            target.showUnits();
            if (hms.isHired()) {
                Unit[] newUnits = target.getUnits();
                GameClient.instance().send(new Packages.ChangeUnits(
                        target.getID(), newUnits));
            }
            hirePointsCache.put(source.getName(), hms.getHirePoints());

            state = SubState.SELECT_SOURCE;
            enableRegionsToHire();

            if (GameClient.shared.gameMap.getEnabledRegions().isEmpty())
                endTurn(false);
        }

        private void enableRegionsToHire(){
            GameClient.shared.gameMap.forEachRegion((r)->{
                if (r.getFraction() == PlayerManager.getSelf().getFraction() && getHirePoints(r) > 0)
                    r.setEnabled(true);
                else
                    r.setEnabled(false);
            });
        }

        private int getHirePoints(MapPartObject region) {
            return hirePointsCache.getOrDefault(region.getName(), region.getHirePoints());
        }

        @Override
        public void exit() {
            GameClient.shared.gameMap.enableAllRegions();
            super.exit();
        }

        @Override
        public void update() {
            super.update();
        }

        public void recieve(Connection connection, Object pkg) {
            super.recieve(connection, pkg);
            if (pkg instanceof Packages.PlayerChangeUnits) {
                //изменяем состав войск в регионе
                Packages.PlayerChangeUnits msg = ((Packages.PlayerChangeUnits) pkg);

                MapPartObject region = GameClient.shared.gameMap.getRegionByID(msg.region);
                Player player = PlayerManager.instance().getPlayer(msg.player);
                region.setUnits(msg.units);
                if (region.getFraction() == Fraction.NONE) {
                    region.setFraction(player.getFraction());
                }
            }
        }
    }

    public static class ServerState extends StepByStepState {
        @Override
        public int getID() {
            return StateID.VESTEROS_COLLECT_UNITS;
        }

        @Override
        protected void onReadyToChangeState() {
            stm.changeState(null, ChangeAction.REMOVE);
        }

        @Override
        public void recieve(Connection c, Object pkg) {
            super.recieve(c, pkg);
            GameServer.PlayerConnection connection = (GameServer.PlayerConnection)c;
            Player player = connection.player;

            if (pkg instanceof Packages.ChangeUnits){
                Packages.ChangeUnits msg = ((Packages.ChangeUnits)pkg);
                GameServer.getServer().sendToAllTCP(new Packages.PlayerChangeUnits(
                        player.id, msg.region, msg.units
                ));
            }
        }
    }

}
