package got.gameObjects;

import got.graphics.*;
import got.graphics.text.Font;
import got.graphics.text.FontTrueType;
import got.graphics.text.Text;
import org.joml.Vector2f;

import static got.Constants.*;

import got.gameStates.GameState;
import got.graphics.text.FontBitmap;
import got.model.Player;

public class NetPlayersPanel extends AbstractGameObject<NetPlayersPanel> {
	private Player[] players;
	private NetPlayerPanel[] panels;
	
	@Override
	protected NetPlayersPanel getThis() {
		return this;
	}
	
	public NetPlayersPanel() {
		this(new Player[0]);
	}
	
	public NetPlayersPanel(Player[] list){
		setSpace(DrawSpace.SCREEN);
		players = new Player[MAX_PLAYERS];
		panels = new NetPlayerPanel[MAX_PLAYERS];
		
		//create visual pannels for all players;
		if (list!=null)
			addPlayers(list);
		
		addChild(new ImageObject(TextureManager.instance().loadTexture("nrs/panel.png"),
				200, 320).setSpace(DrawSpace.SCREEN));
	}
	
	public void addPlayers(Player[] list){
		for (int i=0; i<list.length; i++){
			addPlayer(list[i]);
		}
	}
	
	public void addPlayer(Player player){
		//skip if player is null and if player already exist
		if (player == null || players[player.id]!=null) return;
		
		//create panel for player
		NetPlayerPanel npp = new NetPlayerPanel(player.getNickname(), player.isReady());
		npp.setPos(new Vector2f(0, 10+player.id*50)); 
		panels[player.id] = npp;
		
		addChild(npp);
	}
	
	public void removePlayer(Player player){
		this.removePlayer(player.id);
	}
	
	public void removePlayer(int id){
		if (players[id]!=null){
			players[id] = null;
		}
		
		if (panels[id]!=null){
			removeChild(panels[id]);
			panels[id].finish();
			panels[id] = null;
		}
	}
	
	public void setPlayerReady(int id, boolean ready){
		if (panels[id]!=null){
			panels[id].setReady(ready);
		}
	}
	
	public class NetPlayerPanel extends AbstractGameObject<NetPlayerPanel> {
		Font font = new FontTrueType("BKANT");
		Text nick;
		Texture readyGemTex, notReadyGemTex;
		ImageObject readyImg;
		ImageObject bgImg;
		
		@Override
		protected NetPlayersPanel.NetPlayerPanel getThis() {
			return this;
		}
		
		public NetPlayerPanel(String nickname, boolean ready) {
			//init textures
			readyGemTex = TextureManager.instance().loadTexture("nrs/ready_gem.png");
			notReadyGemTex = TextureManager.instance().loadTexture("nrs/notready_gem.png");
			//create bakground image
			bgImg = new ImageObject(TextureManager.instance().loadTexture("nrs/panel.png"), 200, 50)
					.setSpace(DrawSpace.SCREEN);
			bgImg.setSpace(DrawSpace.SCREEN);
			addChild(bgImg);
			//create ready gem image
			readyImg = new ImageObject(notReadyGemTex, 50, 50).setPos(150, 0)
					.setSpace(DrawSpace.SCREEN);
			addChild(readyImg);
			
			nick = Text.newInstance(nickname, font);
			setReady(ready);
		}
		
		@Override
		public void draw(GameState state) {
			GraphicModule.instance().setDrawSpace(DrawSpace.SCREEN);
			super.draw(state);
			Vector2f cp = getAbsolutePos();
			nick.draw(cp.x, cp.y+5, 1.0f, 1.0f);
		}
		
		public void setReady(boolean ready){
			if (ready){
				readyImg.setTexture(readyGemTex);
			}else{
				readyImg.setTexture(notReadyGemTex);
			}
		}
	}
}
