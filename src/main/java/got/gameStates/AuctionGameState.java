package got.gameStates;

import com.esotericsoftware.kryonet.Connection;
import got.Constants;
import got.GameClient;
import got.ModalState;
import got.gameObjects.AbstractGameObject;
import got.gameObjects.ImageObject;
import got.gameObjects.TextObject;
import got.gameObjects.gui.NumberSelectorObject;
import got.gameObjects.interfaceControls.ImageButton;
import got.gameStates.modals.CustomModalState;
import got.graphics.Colors;
import got.graphics.DrawSpace;
import got.graphics.GraphicModule;
import got.graphics.text.Font;
import got.graphics.text.FontTrueType;
import got.model.Fraction;
import got.model.Game;
import got.model.Player;
import got.network.Packages;
import got.server.PlayerManager;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static sun.misc.PostVMInitHook.run;

/**
 * Created by Souverain73 on 20.04.2017.
 */
public class AuctionGameState extends ParallelGameState{
    NumberSelectorObject nso;
    private Bet[] bets;
    private AuctionResultObject auctionResultObject;

    private class Bet{
        public Player player;
        public int value;

        public Bet(Player player, int value) {
            this.player = player;
            this.value = value;
        }
    }
    @Override
    public void enter(StateMachine stm) {
        super.enter(stm);
        CustomModalState<?> cms = new CustomModalState<>(null, false);
        GameClient.instance().setTooltipText("auction.selectCount");
        cms.addObject(nso = new NumberSelectorObject(0, PlayerManager.getSelf().getMoney(), 0){
            @Override
            protected void onSelect() {
                super.onSelect();
                setEnabled(false);
                GameClient.instance().send(new Packages.Bet(getValue()));
                cms.close();
            }
        }.setPos((Constants.SCREEN_WIDTH-100)/2, 300)
                .setSpace(DrawSpace.SCREEN));

        (new ModalState(cms, true, true)).run();
        GameClient.instance().setTooltipText("common.waitForOtherPlayers");
    }

    @Override
    public void recieve(Connection connection, Object pkg) {
        super.recieve(connection, pkg);
        if (pkg instanceof Packages.BetsList) {
            Packages.BetsList msg = (Packages.BetsList) pkg;
            bets = new Bet[msg.bets.length];
            for (int i = 0; i < msg.bets.length; i++) {
                int bet = msg.bets[i];
                bets[i] = new Bet(PlayerManager.instance().getPlayer(i), bet);
            }
            Arrays.sort(bets, (o1, o2) -> o2.value - o1.value);

            auctionResultObject = createAuctionResultObject(bets);
            addObject(auctionResultObject);
            addObject(createBetsListObject(bets));
            if (PlayerManager.getSelf().getFraction() == Game.instance().getTrack(Game.THRONE_TRACK).getFirst()) {
                auctionResultObject.resolvePositions();
                GameClient.instance().send(new Packages.AuctionResult(auctionResultObject.getResult()));
            }
        }

        if (pkg instanceof Packages.PlayerResolvePosition) {
            Packages.PlayerResolvePosition msg = (Packages.PlayerResolvePosition) pkg;
            if (PlayerManager.getSelf().getFraction() != Game.instance().getTrack(Game.THRONE_TRACK).getFirst())
                auctionResultObject.resolvePosition(msg.position, msg.fraction);
        }
    }

    private AuctionResultObject createAuctionResultObject(Bet[] _bets){
        return new AuctionResultObject(_bets).setSpace(DrawSpace.SCREEN).setPos(400,200);
    }

    private AbstractGameObject createBetsListObject(Bet[] bets){
        return new BetsListObject(bets).setSpace(DrawSpace.SCREEN).setPos(400,100);
    }

    private class BetsListObject extends AbstractGameObject<BetsListObject>{
        @Override protected BetsListObject getThis() {return this;}
        public BetsListObject(Bet[] bets){
            Font textFont = new FontTrueType("Trajan", 50, Colors.BLUE.asVector3());
            for (int i = 0; i < bets.length; i++) {
                Bet bet = bets[i];
                addChild(new ImageObject(bet.player.getFraction().getBackTexture(), 50, 50).setPos(i*50,0));
                addChild(new TextObject(textFont, String.valueOf(bet.value)).setPos(i*50, 50));
            }
        }
    }

    private class AuctionResultObject extends AbstractGameObject<AuctionResultObject>{
        @Override protected AuctionResultObject getThis() {return this;}
        private class Position{
            List<Fraction> candidates  = new ArrayList<>();
            public Position() {
            }
            public Position(Fraction fraction){
                candidates.add(fraction);
            }
            public void addCandidate(Fraction fraction){
                candidates.add(fraction);
            }
            public void removeCandidate(Fraction fraction){
                candidates.remove(fraction);
            }
            public boolean isDefined(){
                return candidates.size() <= 1;
            }

            public List<Fraction> getCandidates(){
                return candidates;
            }

            public Fraction getFraction(){
                if (isDefined()) return candidates.get(0);
                else return Fraction.UNKNOWN;
            }
        }

        List<Position> positions = new ArrayList<>();
        public AuctionResultObject(Bet[] bets){
            Position lastPosition= new Position();
            int lastBet = -1;
            for (int i=0; i<bets.length; i++){
                Bet bet = bets[i];
                if (bet.value!=lastBet){
                    lastPosition = new Position(bet.player.getFraction());
                    lastBet = bet.value;
                }else{
                    lastPosition.addCandidate(bet.player.getFraction());
                }
                positions.add(lastPosition);
            }
        }

        @Override
        public void draw(GameState state) {
            super.draw(state);
            int i=0;
            GraphicModule.instance().setDrawSpace(this.space);
            Vector2f cp = getPos();
            for(Position p : positions){
                p.getFraction().getBackTexture().draw(cp.x+50*i, cp.y, 50, 50);
                i++;
            }
        }

        public void resolvePositions(){
            for (int i = 0; i < positions.size(); i++) {
                Position p = positions.get(i);
                if (!p.isDefined()){
                    Fraction selected = selectCandidate(i, p.candidates);
                    auctionResultObject.resolvePosition(i, selected);
                    GameClient.instance().send(new Packages.ResolvePosition(i, selected));
                }
            }
        }

        private void resolvePosition(int i, Fraction selected) {
            Position p = positions.get(i);
            positions.set(i, new Position(selected));
            p.removeCandidate(selected);
        }

        private Fraction selectCandidate(int i, List<Fraction> candidates) {
            CustomModalState<Fraction> cms = new CustomModalState<>(null, false);
            int x=300;
            int y=400;

            cms.addObject(new TextObject("Select fraction for " + i+1 + " position")
                    .setPos(x, y)
                    .setSpace(DrawSpace.SCREEN));

            y+=50;
            for (Fraction f : candidates){
                cms.addObject(new ImageButton(f.getBackTexture(), x, y, 50, 50, null)
                        .setCallback((gameObject, o) -> cms.setResultAndClose(f))
                        .setSpace(DrawSpace.SCREEN));
                x+=50;
            }

            (new ModalState(cms)).run();
            return cms.getResult();
        }

        public Fraction[] getResult(){
            return positions.stream().map(Position::getFraction).toArray(Fraction[]::new);
        }
    }
}
