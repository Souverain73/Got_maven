package got.server.serverStates;

import com.esotericsoftware.kryonet.Connection;
import got.server.serverStates.base.ServerState;

/**
 * Created by Souverain73 on 13.01.2017.
 * This state used for house cards custom events and can be used between standart game states and can replace some parts of game.
 * Server realisation does actually nothing, all custom actions defined in House cards classes.
 */
public class HouseCardActiveState implements ServerState {
    @Override
    public String getName() {
        return null;
    }

    @Override
    public int getID() {
        return 0;
    }

    @Override
    public void enter(StateMachine stm) {

    }

    @Override
    public void exit() {

    }

    @Override
    public void recieve(Connection connection, Object pkg) {

    }
}
