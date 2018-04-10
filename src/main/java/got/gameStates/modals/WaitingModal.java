package got.gameStates.modals;

import com.esotericsoftware.kryonet.Connection;
import got.GameClient;
import got.gameStates.AbstractGameState;
import got.gameStates.GameState;
import got.gameStates.StateMachine;
import got.network.Packages;

/**
 * Created by Maksim on 17.01.2017.
 */
public abstract class WaitingModal extends AbstractGameState {
    StateMachine stm;
    @Override
    public String getName() {
        return "WaitingModalState";
    }

    @Override
    public int getID() {
        return 100;
    }

    @Override
    public void enter(StateMachine stm) {
        this.stm = stm;
        //send package wait
        GameClient.instance().send(new Packages.WaitForModal());
    }

    protected abstract void onResume();

    @Override
      public void recieve(Connection connection, Object pkg) {
        if (pkg instanceof Packages.PlayerResumeModal) {
            Packages.PlayerResumeModal resumeModal = (Packages.PlayerResumeModal) pkg;
            GameClient.instance().closeModal();
        }
    }

    public void resumeModal(){
        GameClient.instance().send(new Packages.ResumeModal());
    }
}
