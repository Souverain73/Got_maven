package got.server.serverStates;

import com.esotericsoftware.kryonet.Connection;
import got.gameStates.StateID;
import got.model.ChangeAction;
import got.model.Fraction;
import got.model.Player;
import got.network.Packages;
import got.server.GameServer;
import got.server.PlayerManager;
import got.server.serverStates.base.ParallelState;
import got.server.serverStates.base.ServerState;
import got.utils.Timers;

/**
 * Created by Souverain73 on 20.04.2017.
 */
public class AuctionState extends ParallelState {
    public static String AUCTION_RESULTS_PARAM = "AUCTION_RESULTS";
    public static String AUCTION_BETS_PARAM = "AUCTION_BETS";
    private int playersCount;
    private int[] bets;
    private Fraction[] results;
    private ServerState nextState;

    @Override
    public int getID() {
        return StateID.AUCTION_STATE;
    }

    public AuctionState(ServerState nextState){
        this.nextState = nextState;
    }

    @Override
    public void enter(StateMachine stm) {
        super.enter(stm);
        playersCount = PlayerManager.instance().getPlayersCount();
        bets = new int[playersCount];
        results = new Fraction[playersCount];
    }

    @Override
    protected void onReadyToChangeState() {
        GameServer.instance().getServer().sendToAllTCP(new Packages.BetsList(bets));
    }

    @Override
    public void recieve(Connection connection, Object pkg) {
        super.recieve(connection, pkg);
        GameServer.PlayerConnection c = (GameServer.PlayerConnection) connection;
        Player player = c.player;

        if (pkg instanceof Packages.Bet) {
            Packages.Bet msg = (Packages.Bet) pkg;
            bets[player.id] = msg.value;
            onReady(player, true);
        }

        if (pkg instanceof Packages.ResolvePosition) {
            Packages.ResolvePosition msg = (Packages.ResolvePosition) pkg;
            GameServer.getServer().sendToAllTCP(new Packages.PlayerResolvePosition(player.id, msg.position, msg.fraction));
        }

        if (pkg instanceof Packages.AuctionResult) {
            Packages.AuctionResult msg = (Packages.AuctionResult) pkg;
            stm.saveParam(AUCTION_RESULTS_PARAM, msg.result);
            stm.saveParam(AUCTION_BETS_PARAM, bets);
            Timers.wait(2000);
            stm.changeState(nextState, ChangeAction.SET);
        }
    }
}
