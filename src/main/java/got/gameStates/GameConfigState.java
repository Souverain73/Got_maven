package got.gameStates;

import com.esotericsoftware.kryonet.Connection;
import got.GameClient;
import got.gameObjects.MapPartObject;
import got.model.Fraction;
import got.model.Game;
import got.network.Packages;
import got.utils.UI;

/**
 * Created by Souverain73 on 10.03.2017.
 */
public class GameConfigState extends AbstractGameState {
    @Override
    public void enter(StateMachine stm) {
        super.enter(stm);
        GameClient.shared.gameMap.forEachRegion(region->{
            region.setAction(null);
        });
    }

    @Override
    public void exit() {
        super.exit();
        for (Fraction f : Game.instance().getTrack(Game.THRONE_TRACK).getData()){
            Game.instance().getSuplyTrack().setPos(f, GameClient.shared.gameMap.getSuply(f));
        }
    }

    @Override
    public void recieve(Connection connection, Object pkg) {
        super.recieve(connection, pkg);

        if (pkg instanceof Packages.ChangeUnits) {
            Packages.ChangeUnits msg = (Packages.ChangeUnits) pkg;
            MapPartObject region = GameClient.shared.gameMap.getRegionByID(msg.region);
            region.setUnits(msg.units);
            UI.systemMessage("Server change units in region " + region.getName());
        }

        if (pkg instanceof Packages.ChangeRegionFraction) {
            Packages.ChangeRegionFraction msg = (Packages.ChangeRegionFraction) pkg;
            MapPartObject region = GameClient.shared.gameMap.getRegionByID(msg.region);
            region.setFraction(msg.fraction);
            UI.systemMessage("Server change fraction in region " + region.getName());
        }

        if (pkg instanceof Packages.SetTrack) {
            Packages.SetTrack msg = (Packages.SetTrack) pkg;
            Game.instance().setTrackData(msg.track, msg.data);
            UI.systemMessage("Server change track " + msg.track);
        }
    }

    @Override
    public int getID() {
        return StateID.GAME_CONFIG_STATE;
    }
}
