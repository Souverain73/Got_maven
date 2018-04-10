package got.vesterosCards.states;

import com.esotericsoftware.kryonet.Connection;
import got.GameClient;
import got.InputManager;
import got.ModalState;
import got.gameObjects.MapPartObject;
import got.gameStates.StateID;
import got.gameStates.StepByStepGameState;
import got.gameStates.modals.SelectUnitsDialogState;
import got.interfaces.IClickListener;
import got.model.*;
import got.network.Packages;
import got.server.GameServer;
import got.server.PlayerManager;
import got.server.serverStates.base.StepByStepState;
import got.utils.Utils;
import org.joml.Vector2f;

/**
 * Created by Souverain73 on 12.04.2017.
 */
public class CollectSuply {

    public static class ClientState extends StepByStepGameState implements IClickListener {
        @Override
        protected void onSelfTurn() {
            super.onSelfTurn();
            int suply = GameClient.shared.gameMap.getSuply(PlayerManager.getSelf().getFraction());
            GameClient.instance().send(new Packages.ChangeSuply(suply));
        }

        @Override
        public void recieve(Connection connection, Object pkg) {
            super.recieve(connection, pkg);
            if (pkg instanceof Packages.PlayerChangeSuply) {
                Packages.PlayerChangeSuply msg = (Packages.PlayerChangeSuply) pkg;

                Game.instance().getSuplyTrack().setPos(msg.fraction, msg.level);
                if (msg.fraction == PlayerManager.getSelf().getFraction()){
                    if (!checkArmyLimit()) {
                        GameClient.instance().setTooltipText("collectUnits.needKill");
                        GameClient.shared.gameMap.setEnabledByCondition (r->r.getFraction() == PlayerManager.getSelf().getFraction() && r.getUnitsCount() > 1);
                        addObject(Utils.getReadyButton(null).setCallback((gameObject, o) -> {
                            if (checkArmyLimit()) {
                                endTurn(false);
                            }
                        }));
                    }else {
                        endTurn(false);
                    }
                }
            }
            if (pkg instanceof Packages.PlayerChangeUnits) {
                Packages.PlayerChangeUnits msg = (Packages.PlayerChangeUnits) pkg;
                MapPartObject region = GameClient.shared.gameMap.getRegionByID(msg.region);
                region.setUnits(msg.units);
            }
        }

        private boolean checkArmyLimit(){
            Fraction playerFraction = PlayerManager.getSelf().getFraction();
            return (Game.instance().getSuplyTrack().canHaveArmies(playerFraction,
                    GameClient.shared.gameMap.getArmySizesForFraction(playerFraction)));
        }

        @Override
        public void click(InputManager.ClickEvent event) {
            if (event.getTarget() instanceof MapPartObject) {
                MapPartObject region = (MapPartObject) event.getTarget();
                SelectUnitsDialogState suds = new SelectUnitsDialogState(region.getUnits());
                (new ModalState(suds)).run();
                Unit[] unitsToKill = suds.getSelectedUnits();
                if (unitsToKill.length > 0) {
                    region.removeUnits(unitsToKill);
                    GameClient.instance().send(new Packages.ChangeUnits(region.getID(), region.getUnits()));
                }
            }
        }
    }


    public static class ServerState extends StepByStepState {
        @Override
        public void recieve(Connection connection, Object pkg) {
            super.recieve(connection, pkg);
            GameServer.PlayerConnection c = (GameServer.PlayerConnection) connection;
            Player player = c.player;
            if (pkg instanceof Packages.ChangeSuply) {
                Packages.ChangeSuply msg = (Packages.ChangeSuply) pkg;
                Game.instance().getSuplyTrack().setPos(player.getFraction(), msg.level);
                GameServer.getServer().sendToAllTCP(new Packages.PlayerChangeSuply(player.getFraction(), msg.level));
            }

            if (pkg instanceof Packages.ChangeUnits) {
                Packages.ChangeUnits msg = (Packages.ChangeUnits) pkg;
                GameServer.getServer().sendToAllTCP(new Packages.PlayerChangeUnits(player.id, msg.region, msg.units));
            }
        }

        @Override
        protected void onReadyToChangeState() {
            stm.changeState(null, ChangeAction.REMOVE);
        }

        @Override
        public int getID() {
            return StateID.WESTEROS_COLLECT_SUPLY;
        }
    }

}
