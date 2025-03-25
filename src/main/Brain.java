package main;

import java.util.HashMap;
import java.util.Map;
/*
 * In separate thread, this class stores a hashmap of flags, and regularly checks if certain ones are present
 * and performs given actions on them
 */

public class Brain implements Runnable{
	GamePanel gp;
	Thread brainThread;
	private HashMap<String,Boolean>bflags;
	public static final boolean BFLAG_DEFAULT_VAULE = false;
	public static final int BRAIN_TICK_RATE = 10;
	public Brain(GamePanel gp) {
		this.gp=gp;
		this.bflags=new HashMap<String, Boolean>();
		brainThread = new Thread(this);
		brainThread.start();
		
	}
	
	public boolean getFlag(String key) {
		return bflags.getOrDefault(key, BFLAG_DEFAULT_VAULE);
	}
	
	public void setFlag(String key, boolean val) {
		  bflags.put(key  ,val);
	}
	
	public void draw() {
		
		
		}
	
	public void update() {
		checkAndPerformFlagActions();
		
	}

	private void checkAndPerformFlagActions() {
		for(Map.Entry<String,Boolean> entry : this.bflags.entrySet()) {
			String key = entry.getKey();
			Boolean value = entry.getValue();
		}
		
	}

	@Override
	public void run() {
		// game loop code here
		long lastTime = System.currentTimeMillis();
		long thisTime = System.currentTimeMillis();
		long deltaTime=0;
		long overshoot =0;
		long msPerFrame = 1000 / BRAIN_TICK_RATE;
		
		while(brainThread!=null) {
			thisTime= System.currentTimeMillis();
			deltaTime = thisTime-lastTime;
			overshoot = deltaTime - msPerFrame;
			
			if (deltaTime>=msPerFrame-overshoot) {
				lastTime = thisTime;
				deltaTime=0;
				update(); 
				
				
			}
			//repaint();
		}
		
	}
	
	public int getConversationChainForEntityType(int entType) {
		// TODO: temporary function, implement properly later
		switch(entType) {
		case 10:
			return 0;
		case 11:
			return 1;
		case 12:
			return 0;
		case 13:
			return 1;
		case 14:
			return 0;
		case 15:
			return 1;
		default:
			return 0;
		}
	}

	public void playerActivateNPC(Entity entity, boolean pressecActivate) {
		if (pressecActivate) {

			System.out.printf("Brain: player touch the NPC: %d  %d \n ",entity.kind,entity.UID);
			int chainID = getConversationChainForEntityType(entity.kind);
			gp.conversation.startConversation(chainID);
		}else {
			//System.out.printf("BrainsdfggggC: %d  %d \n ",entity.kind,entity.UID);
		}
		gp.hud.showActionPromptDelay.setDelay(60);
		
	}
	
	

}
