package got.vesterosCards.states;

import got.gameStates.ParallelGameState;
import got.gameStates.StateID;
import got.model.ChangeAction;
import got.model.Game;
import got.server.serverStates.AuctionState;
import got.server.serverStates.ChangeTrack;
import got.server.serverStates.StateMachine;
import got.server.serverStates.base.ParallelState;

/**
 * Created by Souverain73 on 03.05.2017.
 */
public class BattleOfKings {
    public static class ServerState extends ParallelState{
        int lastTrack = Game.THRONE_TRACK;
        @Override
        public int getID() {
            return StateID.WESTEROS_BATTLE_OF_KINGS;
        }

        @Override
        public void enter(StateMachine stm) {
            super.enter(stm);
            stm.changeState(new AuctionState(new ChangeTrack(Game.instance().getTrack(lastTrack))), ChangeAction.PUSH);
        }

        @Override
        protected void onReadyToChangeState() {

        }

        @Override
        public void resume() {
            super.resume();
            switch (lastTrack){
                case Game.THRONE_TRACK: lastTrack = Game.SWORD_TRACK; break;
                case Game.SWORD_TRACK: lastTrack = Game.CROWN_TRACK; break;
                case Game.CROWN_TRACK: stm.changeState(null, ChangeAction.REMOVE); return;
            }

            stm.changeState(new AuctionState(new ChangeTrack(Game.instance().getTrack(lastTrack))), ChangeAction.PUSH);
        }
    }

    public static class ClientState extends ParallelGameState {
        @Override
        public int getID() {
            return StateID.WESTEROS_BATTLE_OF_KINGS;
        }
    }
}
