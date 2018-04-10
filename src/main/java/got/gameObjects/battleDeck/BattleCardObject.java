package got.gameObjects.battleDeck;

import got.gameObjects.*;
import got.graphics.DrawSpace;
import got.model.Action;
import got.model.BattleSide;
import got.model.Fraction;
import got.model.Unit;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Souverain73 on 25.11.2016.
 */
public class BattleCardObject extends AbstractGameObject<BattleCardObject>{

    @Override protected BattleCardObject getThis() {return this;}

    private final int WIDTH = 50;
    private final int HEIGHT = 100;

    private static final int BASE_UNITS_X = 5;
    private static final int BASE_UNITS_Y = 55;
    public static final int  UNITS_SIZE = 15;
    public static final int UNITS_SPACING = 5;

    private final Fraction playerFraction;

    private AbstractGameObject view;
    private Unit[] units;
    private UnitObject[] unitObjects;
    private Action regionAction;
    private MapPartObject region;
    private List<UnitEffect> effects;

    public BattleCardObject(Fraction playerFraction, MapPartObject region){
        this.playerFraction = playerFraction;
        this.region = region;
        this.units = region.getUnits();
        this.regionAction = region.getAction();
        effects = null;
        addChild(view = createView());
        updateUnits();
    }

    private ContainerObject createView() {
        ContainerObject result = new ContainerObject();
        ///BG
        result.addChild(new ImageObject("PlayerCardBG.png", WIDTH, HEIGHT)
                .setSpace(DrawSpace.SCREEN));
        //Fraction icon
        result.addChild(new ImageObject(playerFraction.getBackTexture(), 40, 40).setPos(5, 5)
                .setSpace(DrawSpace.SCREEN));
        if (regionAction!=null) {
            result.addChild(new ImageObject(regionAction.getTexture(), 20, 20).setPos(25,25)
                    .setSpace(DrawSpace.SCREEN));
        }

        return result;
    }

    private void createUnitObjects(){
        unitObjects = new UnitObject[units.length];
        for (int i = 0; i < units.length; i++) {
            Unit unit = units[i];
            unitObjects[i] = new UnitObject(unit)
                            .setSpace(DrawSpace.SCREEN)
                            .setSize(UNITS_SIZE);
            view.addChild(unitObjects[i]);
        }
    }

    private void placeUnits(){
        int x = BASE_UNITS_X;
        int y = BASE_UNITS_Y;
        for (int i=0; i<units.length; i++){
            unitObjects[i].setPos(new Vector2f( x, y));
            x += UNITS_SIZE + UNITS_SPACING * 2;
            if (x > WIDTH /*ширина панельки*/){
                x-=WIDTH;
                y+= UNITS_SIZE + UNITS_SPACING*2;
            }
        }
    }

    private void updateUnits(){
        //remove old units
        if (unitObjects!=null) {
            for (UnitObject unitObject : unitObjects) {
                removeChild(unitObject);
                unitObject.finish();
            }
        }

        //create new units
        createUnitObjects();

        //place new units on card
        placeUnits();
    }

    /**
     * Don't use units to calculate battle power directly
     * @return
     */
    public Unit[] getUnits(){
        return units;
    }

    public int getBattlePower(BattleSide side, boolean isSiege){
        int res = 0;
        res += Arrays.stream(units)
                .filter(unit->(unit==Unit.SIEGE && isSiege && side==BattleSide.ATTACKER) || unit!=Unit.SIEGE)
                .mapToInt(unit->calcPower(unit)).sum();
        if (side == BattleSide.ATTACKER){
            if (regionAction == Action.MOVE ||
                    regionAction == Action.MOVEPLUS ||
                    regionAction == Action.MOVEMINUS){
                res += regionAction.getPower();
            }
        }

        if (side == BattleSide.DEFENDER){
            if (regionAction == Action.DEFEND ||
                    regionAction == Action.DEFENDPLUS){
                res += regionAction.getPower();
            }
        }

        return res;
    }

    public void addEffect(UnitEffect effect){
        if (effects == null){
            effects = new ArrayList<>();
        }
        effects.add(effect);
        effects.sort(Comparator.comparingInt(UnitEffect::getPriority));
    }

    public void resetEffect() {
        effects = null;
    }

    public int calcPower(Unit unit){
        int power = unit.getDamage();
        if (effects == null) return power;
        for (UnitEffect effect : effects){
            if (effect.isAffected(unit))
                power = effect.getAffectedPower(power);
        }
        return power;
    }

    public Fraction getFraction() {
        return playerFraction;
    }

    public void updateState() {
        units = region.getUnits();
        regionAction = region.getAction();
        view.finish();
        removeChild(view);
        addChild(view = createView());
        updateUnits();
    }

    public interface UnitEffect{
        int getAffectedPower(int power);
        boolean isAffected(Unit unit);
        int getPriority();
    }

    public MapPartObject getRegion() {
        return region;
    }
}
