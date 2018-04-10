package got.gameStates;

import com.esotericsoftware.kryonet.Connection;
import got.GameClient;
import got.ModalState;
import got.gameObjects.ContainerObject;
import got.gameObjects.MapPartObject;
import got.gameObjects.interfaceControls.ImageButton;
import got.gameStates.modals.CustomModalState;
import got.gameStates.modals.Dialogs;
import got.graphics.DrawSpace;
import got.houseCards.HouseCard;
import got.houseCards.HouseCardsLoader;
import got.model.Player;
import got.network.Packages;
import got.server.PlayerManager;
import org.joml.Vector2f;

import java.util.List;

import static got.utils.UI.logAction;


/**
 * Created by Souverain73 on 01.12.2016.
 */
public class SelectHouseCardPhase extends AbstractGameState {
    @Override
    public int getID() {
        return StateID.SELECT_HOUSE_CARD_PHASE;
    }

    @Override
    public String getName() {
        return "Select house card phase";
    }

    @Override
    public void enter(StateMachine stm) {
        super.enter(stm);
        if (GameClient.shared.battleDeck.isBattleMember(PlayerManager.getSelf().getFraction())){
            GameClient.instance().setTooltipText("selectHouseCard.selectCard");
            HouseCard selectedCard = showSelectHouseCardDialog();

            GameClient.instance().send(new Packages.SelectHouseCard(selectedCard.getID()));
        }
    }

    @Override
    public void recieve(Connection connection, Object pkg) {
        super.recieve(connection, pkg);
        if (pkg instanceof Packages.PlayerSelectHouseCard) {
            Packages.PlayerSelectHouseCard msg = (Packages.PlayerSelectHouseCard) pkg;
            Player player = PlayerManager.instance().getPlayer(msg.player);
            HouseCard card = HouseCardsLoader.instance().getCardById(msg.card);
            player.getDeck().useCard(card);
            logAction("selectHouseCard.playerSelectCard", player.getNickname(), card.getTitle());
            GameClient.shared.battleDeck.placeCard(card, player);
        }
        if (pkg instanceof Packages.PlayerKillUnit) {
            Packages.PlayerKillUnit msg = (Packages.PlayerKillUnit) pkg;
            Player player = PlayerManager.instance().getPlayer(msg.player);
            MapPartObject region = GameClient.shared.gameMap.getRegionByID(msg.region);
            logAction("common.playerKillUnitInRegion", player.getNickname(), msg.unit, region.getName());
            region.removeUnit(msg.unit);
            GameClient.shared.battleDeck.onRegionStateChanged(region);
        }
        if (pkg instanceof Packages.PlayerSetAction) {
            Packages.PlayerSetAction msg = (Packages.PlayerSetAction) pkg;
            MapPartObject region = GameClient.shared.gameMap.getRegionByID(msg.region);
            if (msg.action == null) {
                logAction("common.playerRemoveAction", region.getName());
                region.setAction(null);
                GameClient.shared.battleDeck.onRegionStateChanged(region);
            } else {
                logAction("common.playerSetAction", region.getName());
                region.setAction(msg.action);
                GameClient.shared.battleDeck.onRegionStateChanged(region);
            }
        }
    }

    private HouseCard showSelectHouseCardDialog() {
        CustomModalState<HouseCard> cms = Dialogs.createSelectHouseCardDialog(PlayerManager.getSelf().getDeck());

        (new ModalState(cms)).run();

        return cms.getResult();
    }
}
