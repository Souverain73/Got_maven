package got.server.serverStates;

import got.gameStates.StateID;
import got.model.ChangeAction;
import got.model.Fraction;
import got.model.Track;
import got.network.Packages;
import got.server.GameServer;
import got.server.serverStates.base.ParallelState;

/**
 * Created by Souverain73 on 03.05.2017.
 */
public class ChangeTrack extends ParallelState {
    Track track;

    @Override
    public int getID() {
        return StateID.CHANGE_TRACK;
    }

    public ChangeTrack(Track track) {
        this.track = track;
    }

    @Override
    public void enter(StateMachine stm) {
        super.enter(stm);
        Fraction[] data = (Fraction[]) stm.getParam(AuctionState.AUCTION_RESULTS_PARAM);

        GameServer.getServer().sendToAllTCP(new Packages.SetTrack(track.getId(), data));
    }

    @Override
    protected void onReadyToChangeState() {
        stm.changeState(null, ChangeAction.REMOVE);
    }
}
