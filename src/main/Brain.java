package main;

import java.util.HashMap;
import java.util.Map;
/*
 * In separate thread, this class stores a hashmap of flags, and regularly checks if certain ones are present
 * and performs given actions on them
 */

public class Brain implements Runnable{
	/*
	 * Brain maintains lists of flags.
	 * Sets other flags when other combinations of flags are set
	 * Triggers events based on flags
	 */
	GamePanel gp;
	Thread brainThread;
	private HashMap<String,Boolean>bflags;
	public static final boolean BFLAG_DEFAULT_VAULE = false;
	public static final int BRAIN_TICK_RATE = 10;
	/*
	 * zoneActions
	 * 0 mapID   1 zoneID  2 actionID
	 */
	private final int DEF_ZONE_ACTIONS_AMOUNT = 10;
	public int[][] zoneActions;
	
	
	public Brain(GamePanel gp) {
		this.gp=gp;
		this.bflags=new HashMap<String, Boolean>();
		
		zoneActions = new int[DEF_ZONE_ACTIONS_AMOUNT][];
		zoneActions[0] = new int[] {0,0,0};
		zoneActions[1] = new int[] {0,1,1};
		zoneActions[2] = new int[] {0,2,2};
		zoneActions[3] = new int[] {0,3,3};
		
		zoneActions[4] = new int[] {1,0,4};
		
		// thread for processing flags asynchronously
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
	
	public void playerActivateZone(int kind, int UID) {
		/*
		 * zoneActions
		 * 0 mapID   1 zoneID  2 actionID
		 */
		for(int[] zoneAction:zoneActions) {
			if (null!=zoneAction && zoneAction[0]==gp.level && UID==zoneAction[1]) {
				performActionByID(zoneAction[2]);
			}
		}
		
	}

	private void performActionByID(int actionID) {
		switch(actionID) {
		case 0:
			gp.warp.warpToID(1);
			break;
		case 1:
			gp.warp.warpToID(1);
			break;
		case 2:
			gp.warp.warpToID(3);
			break;
		case 3:
			gp.warp.warpToID(4);
			break;
		case 4:
			gp.warp.warpToID(4);
			break;
		default:
			System.out.println("Brain: actionID not registered: "+actionID);
		}
		
	}
	
	

}
