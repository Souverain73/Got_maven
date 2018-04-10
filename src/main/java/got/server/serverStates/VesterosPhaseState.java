package got.server.serverStates;

import com.esotericsoftware.kryonet.Connection;
import got.gameStates.StateID;
import got.interfaces.IPauseable;
import got.model.ChangeAction;
import got.model.Game;
import got.network.Packages;
import got.server.GameServer;
import got.server.serverStates.base.ServerState;
import got.utils.Timers;
import got.utils.UI;
import got.vesterosCards.VesterosCard;
import got.wildlings.Wildlings;

import java.util.stream.IntStream;

/**
 * Created by Souverain73 on 30.03.2017.
 */
public class VesterosPhaseState implements ServerState, IPauseable{
    private enum SubState{
        OPEN,
        PLAY
    }
    private StateMachine stm;

    class VesterosPhaseData {
        public VesterosCard[] cards = new VesterosCard[3];
        public int currentCard = -1;
        public SubState state = SubState.OPEN;
    }

    VesterosPhaseData data = new VesterosPhaseData();

    @Override
    public String getName() {
        return "Vesteros Phase";
    }

    @Override
    public int getID() {
        return StateID.VESTEROS_PHASE;
    }

    @Override
    public void enter(StateMachine stm) {
        this.stm = stm;
        Game.instance().nextTurn();
        UI.systemMessage("Entering Vesteros Phase");
        IntStream.range(0, 3).forEach(i->data.cards[i] = Game.instance().getVesterosDeck(i).getTopCard());

        stm.saveParam(stm.VESTEROS_PHASE_DATA, data);

        openNextCard();
    }

    @Override
    public void exit() {

    }

    @Override
    public void recieve(Connection connection, Object pkg) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {
        UI.systemMessage("Resume vesteros phase");
        if (data.state == SubState.OPEN){
            Timers.getTimer(1000, this::openNextCard).start(false);
        }else {
            if (data.currentCard < 2) {
                playNextCard();
            } else {
                stm.changeState(new PlanningPhaseState(), ChangeAction.SET);
            }
        }
    }

    private void openNextCard(){
        data.currentCard++;

        if (data.currentCard > 2){
            data.currentCard = -1;
            data.state = SubState.PLAY;
            playNextCard();
            return;
        }

        GameServer.getServer().sendToAllTCP(new Packages.OpenCard(data.currentCard, data.cards[data.currentCard].getID()));
        Timers.wait(1000);

        if (data.cards[data.currentCard].hasWildlings()) {
            Wildlings.instance().nextLevel();
        }

        if (Wildlings.instance().readyToAttack()){
            Wildlings.instance().attack(stm);
        }else{
            openNextCard();
        }
    }

    private void playNextCard(){
        data.currentCard++;
        stm.changeState(new PlayVesterosCardState(), ChangeAction.PUSH);
    }
}
