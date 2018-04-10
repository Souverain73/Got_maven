package got.gameStates.modals;

import java.util.List;

import got.InputManager;
import got.gameStates.StateMachine;
import org.joml.Vector2f;

import com.esotericsoftware.kryonet.Connection;

import got.Constants;
import got.GameClient;
import got.ModalState;
import got.gameObjects.AbstractGameObject;
import got.gameObjects.GameObject;
import got.gameObjects.interfaceControls.ImageButton;
import got.gameObjects.ImageObject;
import got.gameObjects.UnitObject;
import got.gameStates.AbstractGameState;
import got.graphics.TextureManager;
import got.interfaces.IClickListener;
import got.model.Unit;

public class HireMenuState extends  AbstractGameState implements IClickListener{
	private static final String name = "HireMenu";
	private int hirePoints;
	private List<UnitObject> units = null;
	private AbstractGameObject plusButton = null;
	private ImageButton [] buttons;
	private Vector2f pos;
	private boolean sea;
	private boolean hired = false;

	@Override
	public void enter(StateMachine stm) {
		GameClient.instance().setTooltipText("hire.hireUnits");
	}

	public HireMenuState(List<UnitObject> units, Vector2f pos, int hirePoints, boolean sea) {
		this.pos = pos;
		this.sea = sea;
		this.hirePoints = hirePoints;
		this.units = units;
		this.buttons = new ImageButton[4];
		float x = pos.x;
		float y = pos.y;
		
		ImageObject bg = new ImageObject(TextureManager.instance().loadTexture("unitsMenuBg.png"),
			220, 60).setPos((int)x, (int)y);
		addObject(bg);
			
		x = pos.x + 6;
		y = pos.y + 5;
		
		int step = (int)(Constants.UNIT_SIZE + Constants.UNIT_STEP);
		int unitsCount = units.size();
		for (int i=0; i<4; i++){
			int cx = (int)(x+step*i);
			ImageButton btn = new ImageButton(TextureManager.instance().loadTexture("blank.png"), 
					cx, (int)y, 
					(int)Constants.UNIT_SIZE,
					(int)Constants.UNIT_SIZE,
					i)
					.setCallback(this::unitClickCallback);
			
			btn.setVisible(false);		
			addObject(btn);
			buttons[i] = btn;
		}
		if (unitsCount<4){
			int cx = (int)(x+step*3);
			ImageButton btn = new ImageButton(TextureManager.instance().loadTexture("buttons/plus.png"),
					cx, (int)y, 
					(int)Constants.UNIT_SIZE,
					(int)Constants.UNIT_SIZE,
					null)
					.setCallback(this::plusButtonCallback);
			plusButton = btn;
			addObject(btn);
		}
		updateButtons();
	}
	
	private void updateButtons(){
		int unitsCount = units.size();
		for (int i=0; i<4; i++){
			if (i>=unitsCount){
				buttons[i].setVisible(false);
			}else{
				buttons[i].setTexture(units.get(i).getTexture());
				buttons[i].setVisible(true);
			}
		}
	}
	
	
	private void unitClickCallback(GameObject sender, Object param){
		int i = (Integer)param;
		UnitObject unit = units.get(i);
		if (unit.isUpgradeable() && hirePoints>0){
			hideObjects();
			Unit[] upUnits = unit.getPossibleUpgrades();
			SelectUnitDialogState ust = new SelectUnitDialogState(upUnits, pos);
			
			(new ModalState(ust)).run();
			
			if (ust.result!=null){
				Unit newUnit = (Unit)ust.result;
				units.set(i, new UnitObject(newUnit));
				hired = true;
				hirePoints--;
			}
			
			showObjects();
			updateButtons();
		}
	}
	
	private void plusButtonCallback(GameObject sender, Object param){
		hideObjects();
		SelectUnitDialogState ust = new SelectUnitDialogState(Unit.getUnitsByCondition(unit->{
			if (unit.getCost()<=hirePoints){
				if (sea && unit==Unit.SHIP){
					return true;
				}else if(!sea && unit!=Unit.SHIP){
					return true;
				}
			}
			return false;
		}),	pos);
		
		(new ModalState(ust)).run();
		if (ust.result!=null){
			Unit unit = (Unit)ust.result;
			hirePoints-=unit.getCost();
			hired = true;
			units.add(new UnitObject(unit));
		}
		showObjects();
		updateButtons();
	}
	
	public void close(){
		GameClient.instance().closeModal();
	}
	
	@Override
	public String getName() {
		return name;
	}

	

	
	private void hideObjects(){
		gameObjects.forEach(obj->obj.setVisible(false));
	}
	
	private void showObjects(){
		gameObjects.forEach(obj->obj.setVisible(true));
	}

	@Override
	public void update() {
		if (hirePoints == 0) close();
		if (plusButton != null && (hirePoints<=0 || units.size()>=4)){
			removeObject(plusButton);
			plusButton.finish();
			plusButton = null;
		}
		super.update();
	}

	/**
	 * @return hirePoints - количество оставшихся очков для набора юнитов
	 */
	public int getHirePoints() {
		return hirePoints;
	}

	@Override
	public void recieve(Connection connection, Object pkg) {
	}

	@Override
	public int getID() {
		return -1;
	}

	public boolean isHired() {
		return hired;
	}


	@Override
	public void click(InputManager.ClickEvent event) {
		GameObject sender = event.getTarget();
		if (sender == null){
			close();
		}
	}
	
}
