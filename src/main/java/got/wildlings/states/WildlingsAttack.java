package got.wildlings.states;

import com.esotericsoftware.kryonet.Connection;
import got.Constants;
import got.GameClient;
import got.ModalState;
import got.animations.Animator;
import got.gameObjects.AnimatedObject;
import got.gameObjects.ImageObject;
import got.gameObjects.gui.GUIObject;
import got.gameObjects.interfaceControls.ImageButton;
import got.gameStates.ParallelGameState;
import got.gameStates.StateID;
import got.gameStates.modals.CustomModalState;
import got.gameStates.modals.Dialogs;
import got.graphics.DrawSpace;
import got.houseCards.HouseCard;
import got.houseCards.HouseCardsLoader;
import got.model.Fraction;
import got.model.Player;
import got.network.Packages;
import got.server.GameServer;
import got.server.PlayerManager;
import got.server.serverStates.AuctionState;
import got.server.serverStates.StateMachine;
import got.server.serverStates.base.ParallelState;
import got.utils.Timers;
import got.utils.UI;
import got.wildlings.Wildlings;
import got.wildlings.WildlingsCard;
import org.joml.Vector2f;

import java.util.Arrays;

/**
 * Created by Souverain73 on 20.04.2017.
 */
public class WildlingsAttack {
    public static class ClientState extends ParallelGameState{
        @Override
        public String getName() {
            return "WildlingsAttack phase";
        }

        ImageObject cardImage;
        @Override
        public void recieve(Connection connection, Object pkg) {
            if (pkg instanceof Packages.WildlingsData) {
                stm.saveParam(StateMachine.WILDLINGS_DATA_PARAM, pkg);
                Packages.WildlingsData msg = (Packages.WildlingsData) pkg;
                WildlingsCard card = Wildlings.instance().getCard(msg.card);

                GameClient.shared.gui.addSharedObject(GUIObject.CURRENT_CARD_SHARED_OBJECT, cardImage = new ImageObject(card.getTexture(), 225, 350).setSpace(DrawSpace.SCREEN).setPos(590, -350));

                Animator.animateVector2f(cardImage.getAbsolutePos(), new Vector2f(590, 50), 1000, cardImage::setPos);

                Timers.getTimer(1000, ()->{
                    GameClient.instance().registerTask(()->{
                        showReadyModal();

                        AnimatedObject aoCard = new AnimatedObject(cardImage);
                        aoCard.move(new Vector2f(310, 50), 1000);
                        aoCard.resize(new Vector2f(165, 255), 1000);

                        Timers.getTimer(1000, ()->GameClient.instance().registerTask(()->{
                            card.onOpenClient(msg);
                            GameClient.instance().sendReady(true);
                        })).start(true);
                    });
                }).start(true);
            }

            if (pkg instanceof Packages.PlayerSelectHouseCard) {
                Packages.PlayerSelectHouseCard msg = (Packages.PlayerSelectHouseCard) pkg;
                Player player = PlayerManager.instance().getPlayer(msg.player);
                HouseCard card = HouseCardsLoader.instance().getCardById(msg.card);
                player.getDeck().rewindCard(card);
                GameClient.instance().logMessage("houseCards.playerRewindCard", player.getFraction(), card.getTitle());
            }

            if (pkg instanceof Packages.PlayerRemoveHouseCard) {
                Packages.PlayerRemoveHouseCard msg = (Packages.PlayerRemoveHouseCard) pkg;
                Player player = PlayerManager.instance().getPlayer(msg.source);
                HouseCard card = HouseCardsLoader.instance().getCardById(msg.houseCardID);
                player.getDeck().useCard(card);
                GameClient.instance().logMessage("houseCards.playerDropCard", player.getFraction(), card.getTitle());
            }
        }

        @Override
        public int getID() {
            return StateID.WILDLINGS_ATTACK;
        }

        @Override
        public void exit() {
            Wildlings.instance().resetLevel();
            super.exit();
        }

        private void showReadyModal(){
            (new ModalState(new CustomModalState<Dialogs.DialogResult>(Dialogs.DialogResult.CANCEL, false){
                @Override
                public void enter(got.gameStates.StateMachine stm) {
                    addObject(new ImageButton("buttons/ready.png", Constants.SCREEN_WIDTH / 2 - 100, 250, 150, 75, null)
                            .setSpace(DrawSpace.SCREEN)
                            .setCallback((gameObject, o) -> close())
                    );
                }
            })).run();
        }
    }

    public static class ServerState extends ParallelState{
        private Packages.WildlingsData data;
        private int[] bets;
        private Fraction[] results;
        private WildlingsCard card;
        private int level;

        @Override
        public String getName() {
            return "WildlingsAttack phase";
        }

        public ServerState(WildlingsCard card, int level) {
            this.card = card;
            this.level = level;
        }

        @Override
        public int getID() {
            return StateID.WILDLINGS_ATTACK;
        }

        @Override
        public void enter(StateMachine stm) {
            super.enter(stm);
            bets = (int[]) stm.getParam(AuctionState.AUCTION_BETS_PARAM);
            results = (Fraction[]) stm.getParam(AuctionState.AUCTION_RESULTS_PARAM);
            Fraction maxBet = results[0];
            Fraction minBet = results[results.length - 1];

            if (Arrays.stream(bets).sum() >= level) {
                data = new Packages.WildlingsData(card.getID(), maxBet, true, bets[0]);
            }else{
                data = new Packages.WildlingsData(card.getID(), minBet, false, bets[0]);
            }
            UI.systemMessage("Wildlings attack: data" + data);
            GameServer.getServer().sendToAllTCP(data);
            stm.saveParam(StateMachine.WILDLINGS_DATA_PARAM, data);
        }

        @Override
        protected void onReadyToChangeState() {
            card.onOpenServer(stm, data);
        }

        @Override
        public void recieve(Connection connection, Object pkg) {
            super.recieve(connection, pkg);
            GameServer.PlayerConnection c = (GameServer.PlayerConnection) connection;
            Player player = c.player;

            if (pkg instanceof Packages.SelectHouseCard) {
                Packages.SelectHouseCard msg = (Packages.SelectHouseCard) pkg;
                GameServer.getServer().sendToAllTCP(new Packages.PlayerSelectHouseCard(player.id, msg.card));
            }

            if (pkg instanceof Packages.RemoveHouseCard) {
                Packages.RemoveHouseCard msg = (Packages.RemoveHouseCard) pkg;
                GameServer.getServer().sendToAllTCP(new Packages.PlayerRemoveHouseCard(player.id, player.id, msg.houseCardID));
            }
        }
    }
}