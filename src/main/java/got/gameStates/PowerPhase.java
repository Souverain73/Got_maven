package got.gameStates;

import com.esotericsoftware.kryonet.Connection;

import got.GameClient;
import got.InputManager;
import got.ModalState;
import got.gameObjects.GameMapObject;
import got.gameObjects.GameObject;
import got.gameObjects.interfaceControls.ImageButton;
import got.gameObjects.ImageObject;
import got.gameObjects.MapPartObject;
import got.gameStates.modals.CustomModalState;
import got.gameStates.modals.HireMenuState;
import got.graphics.DrawSpace;
import got.interfaces.IClickListener;
import got.model.Action;
import got.model.Fraction;
import got.model.Player;
import got.model.Unit;
import got.gameObjects.MapPartObject.RegionType;
import got.network.Packages;
import got.server.PlayerManager;
import got.utils.UI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static got.utils.UI.tooltipWait;

class PowerPhase extends StepByStepGameState implements IClickListener {
    private enum SubState {
        SELECT_SOURCE, SELECT_TARGET
    }

    private SubState state;


    private boolean firstTurn;
    private Map<String, Integer> hirePointsCache;
    private MapPartObject sourceRegion = null;

    @Override
    public void enter(StateMachine stm) {
        super.enter(stm);
        firstTurn = true;
        hirePointsCache = new HashMap<>();
        state = SubState.SELECT_SOURCE;
        GameClient.shared.gameMap.disableAllRegions();
    }

    @Override
    public void click(InputManager.ClickEvent event) {
        GameObject sender = event.getTarget();
        if (sender instanceof MapPartObject) {
            MapPartObject region = (MapPartObject) sender;
            if (state == SubState.SELECT_SOURCE) {
                if (region.getAction() == Action.MONEYPLUS &&
                        region.getBuildingLevel() > 0) {
                    //Надо дать игроку выбор действия.
                    int action = -1;
                    //Если игрок уже начал нанимать юнитов в регионе, то у него нет выбора.
                    //Пусть набирает дальше
                    if (hirePointsCache.containsKey(region.getName())
                            && hirePointsCache.get(region.getName()) != region.getHirePoints()) {
                        action = 1;
                    } else {
                        GameClient.instance().setTooltipText("power.selectAction");
                        action = showSelectActionDialog();
                    }
                    if (action == -1) {
                        return;
                    } else if (action == 0) {
                        sendCollectMoney(region);
                        region.setEnabled(false);
                        if (GameClient.shared.gameMap.getEnabledRegions().isEmpty())
                            endTurn(false);
                    } else if (action == 1) {
                        //Если можем нанимать юнитов создаем меню для найма
                        //надо проверить, можем ли мы нанять в море, если да, то предоставить выбор.
                        {
                            List<MapPartObject> regionsForHire = region.getRegionsForHire();
                            if (regionsForHire.size() == 1) {
                                hireUnits(region, region);
                            } else {
                                GameClient.instance().setTooltipText("hire.selectTarget");
                                GameClient.shared.gameMap.disableAllRegions();
                                regionsForHire.forEach(obj -> obj.setEnabled(true));
                                state = SubState.SELECT_TARGET;
                                sourceRegion = region;
                            }
                        }
                    }

                }
            } else if (state == SubState.SELECT_TARGET) {
                hireUnits(sourceRegion, region);
            }
        }
    }

    private void hireUnits(MapPartObject source, MapPartObject target) {
        //todo:проверить по снабжению, сколько юнитов сюда влезет
        target.hideUnits();
        HireMenuState hms = new HireMenuState(
                target.getUnitObjects(), InputManager.instance().getMousePosWin(),
                getHirePoints(source), target.getType() == RegionType.SEA);
        (new ModalState(hms)).run();
        target.showUnits();

        if (hms.isHired()) {

            Unit[] newUnits = target.getUnits();
            GameClient.instance().send(new Packages.ChangeUnits(
                    target.getID(), newUnits));

            if (hms.getHirePoints() == 0) {
                //Если потратил все очки найма, надо отправить пакет об использовании действия.

                GameClient.instance().send(new Packages.Act(source.getID(), 0));
                GameClient.shared.gameMap.disableAllRegions();
                endTurn(true);
            } else {
                hirePointsCache.put(source.getName(), hms.getHirePoints());
                GameClient.shared.gameMap.disableAllRegions();
                sourceRegion.setEnabled(true);
            }
        }else {
            state = SubState.SELECT_SOURCE;
            sourceRegion = null;
        }
    }

    @Override
    protected void onSelfTurn() {
        if (!enableRegionsWithCrown()) {
            //Если не был активирован ни один регион, значит текущий игрок не может совершить ход.
            //В таком случае необходлимо сообщить об этом серверу пакетом Ready.
            endTurn(false);
        } else {
            if (firstTurn) {
                firstTurn = false;
                firstTurn();
                endTurn(true);
            }
            GameClient.instance().setTooltipText("power.selectRegion");
        }
    }

    @Override
    protected void onEnemyTurn(Player player) {
        //Если чужой ход, отключаем все регионы, так как в чужой ход мы не можем совершать действий.
        GameClient.shared.gameMap.disableAllRegions();
        tooltipWait(player);
    }

    @Override
    public void recieve(Connection connection, Object pkg) {
        super.recieve(connection, pkg);
        if (pkg instanceof Packages.CollectInfluence) {
            //Собираем влияние(деньги)
            Packages.CollectInfluence msg = ((Packages.CollectInfluence) pkg);
            MapPartObject region = GameClient.shared.gameMap.getRegionByID(msg.region);

            if (region.getFraction() == PlayerManager.getSelf().getFraction()) {
                PlayerManager.getSelf().addMoney(region.getInfluencePoints() + 1);
            }
            region.setAction(null);
        }

        if (pkg instanceof Packages.PlayerChangeUnits) {
            //изменяем состав войск в регионе
            Packages.PlayerChangeUnits msg = ((Packages.PlayerChangeUnits) pkg);

            MapPartObject region = GameClient.shared.gameMap.getRegionByID(msg.region);
            Player player = PlayerManager.instance().getPlayer(msg.player);
            region.setUnits(msg.units);
            if (region.getFraction() == Fraction.NONE) {
                region.setFraction(player.getFraction());
            }
        }
        if (pkg instanceof Packages.PlayerAct) {
            Packages.PlayerAct msg = ((Packages.PlayerAct) pkg);
            MapPartObject region = GameClient.shared.gameMap.getRegionByID(msg.from);

            region.setAction(null);
        }
    }


    private boolean enableRegionsWithCrown() {
        Fraction selfFraction = PlayerManager.getSelf().getFraction();

        return GameClient.shared.gameMap.setEnabledByCondition(region ->
                region.getFraction() == selfFraction
                        && region.getAction() != null
                        && (region.getAction() == Action.MONEY ||
                        region.getAction() == Action.MONEYPLUS)
        ) > 0;
    }

    /**
     * В первый ход автоматически играются все приказы для которых не требуется решение игрока.
     */
    private void firstTurn() {
        for (MapPartObject region : GameClient.shared.gameMap.getEnabledRegions()) {
            if (region.getAction() == Action.MONEY) {
                sendCollectMoney(region);
                region.setEnabled(false);
            } else if (region.getAction() == Action.MONEYPLUS
                    && region.getBuildingLevel() == 0) {
                sendCollectMoney(region);
            }
        }
    }

    private int getHirePoints(MapPartObject region) {
        if (hirePointsCache.get(region.getName()) == null) {
            return region.getHirePoints();
        } else {
            return hirePointsCache.get(region.getName());
        }
    }

    private void sendCollectMoney(MapPartObject region) {
        GameClient.instance().send(new Packages.CollectInfluence(region.getID()));
    }

    private int showSelectActionDialog() {

        CustomModalState<Integer> cms = new CustomModalState<>(-1);

        ImageObject background = new ImageObject("selectActionBackground.png",
                430, 120).setPos(InputManager.instance().getMousePosWorld()).setSpace(DrawSpace.WORLD);
        background.addChild(new ImageButton("buttons/hire.png", 10, 10, 200, 100, null).setCallback((sender, param) -> {
            cms.setResult(1);
            GameClient.instance().closeModal();
        }).setSpace(DrawSpace.WORLD));
        background.addChild(new ImageButton("buttons/collectPower.png", 220, 10, 200, 100, null).setCallback((sender, param) -> {
            cms.setResult(0);
            GameClient.instance().closeModal();
        }).setSpace(DrawSpace.WORLD));
        cms.addObject(background);
        new ModalState(cms).run();

        return cms.getResult();
    }
}
