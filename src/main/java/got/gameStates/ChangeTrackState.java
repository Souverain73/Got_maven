package got.gameStates;

import com.esotericsoftware.kryonet.Connection;
import got.model.Game;
import got.network.Packages;

/**
 * Created by Souverain73 on 03.05.2017.
 */
public class ChangeTrackState extends ParallelGameState {
    @Override
    public int getID() {
        return StateID.CHANGE_TRACK;
    }

    @Override
    public void recieve(Connection connection, Object pkg) {
        super.recieve(connection, pkg);
        if (pkg instanceof Packages.SetTrack){
            Packages.SetTrack msg = (Packages.SetTrack) pkg;
            Game.instance().getTrack(msg.track).setData(msg.data);
            setReady(true);
        }
    }
}
