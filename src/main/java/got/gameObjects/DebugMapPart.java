package got.gameObjects;

import got.Constants;
import got.InputManager;
import got.gameStates.GameState;

import got.graphics.Effect;
import got.graphics.GraphicModule;

import got.model.PowerToken;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Created by Souverain73 on 13.02.2017.
 */
public class DebugMapPart extends MapPartObject {
    public int debugState=0;
    private boolean showUnits;
    private boolean showAction;
    private boolean showToken;

    @Override
    protected void mouseEnter() {
        super.mouseEnter();
        debugState = 2;
    }

    @Override
    protected void mouseOut() {
        super.mouseOut();
        debugState = 0;
    }

    void addDebugForNeighbors(int level){
        for (MapPartObject reg : getNeighbors()){
            if (reg instanceof DebugMapPart) {
                DebugMapPart debugMap = (DebugMapPart) reg;
                debugMap.debugState = (debugMap.debugState + level) % 2;
            }
        }
    }

    @Override
    protected void click(GameState st) {
        super.click(st);
        addDebugForNeighbors(1);
        Vector2f clickPos = InputManager.instance().getMousePosWorld().sub(getPos());
        System.out.println(String.format("Click position: %1f %1f", clickPos.x, clickPos.y));
    }

    @Override
    public void draw(GameState st) {
        if (!isVisible()) return;
        if (texture == null){
            super.initTexture();
        }
        drawed = true;
        GraphicModule.instance().setDrawSpace(this.space);
        if (debugState == 0){

        }else if (debugState == 1){
            GraphicModule.instance().setEffect(
                    new Effect().Overlay(new Vector3f(0.0f, 0.2f, 0.2f))
            );
        }else if (debugState == 2){
            GraphicModule.instance().setEffect(
                    new Effect().Overlay(new Vector3f(0.0f, 0.2f, 0.0f))
            );
        }

        if (state == State.DISABLED){
            GraphicModule.instance().setEffect(
                    new Effect().Overlay(new Vector3f(-0.2f, -0.2f, -0.2f))
            );
        }

        Vector2f cp = getAbsolutePos();
        texture.draw(cp.x, cp.y, w, h, 0);
        GraphicModule.instance().resetEffect();

        float actImgSize = Constants.ACTION_IMAGE_SIZE;
        float halfActImgSize = Constants.ACTION_IMAGE_SIZE/2;
        float tokenImageSize = Constants.POWER_TOKEN_IMAGE_SIZE;
        if (action != null && showAction){
            action.getTexture().draw(cp.x + act_x - halfActImgSize, cp.y + act_y -halfActImgSize,
                actImgSize, actImgSize);
        }

        GraphicModule.instance().setEffect(
                new Effect().Multiply(fraction.getMultiplyColor())
        );
        if (units != null && showUnits){
            units.forEach(unit->unit.draw(st));
        }

        if (powerToken && showToken){
            PowerToken.getTexture().draw(cp.x + token_x, cp.y + token_y,
                    tokenImageSize, tokenImageSize);
        }
        GraphicModule.instance().resetEffect();
    }

    public void toggleUnits(){
        showUnits = !showUnits;
    }

    public void toggleAction(){
        showAction = !showAction;
    }

    public void toggleToken(){
        showToken = !showToken;
    }

    @Override
    public String toString() {
        return String.format("DMP{Name=%s; Building=%s; Resources=%s; Influence=%s;}", getName(),
                getBuildingLevel(), -1, getInfluencePoints());
    }
}
