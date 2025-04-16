package main;

import java.util.HashMap;
import java.util.Map;

import main.GamePanel.GameState;
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
	private int defaultFlag = 0;
	private final int ENTITY_DEFAULT_FLAG = 0;
	private final int ENTITY_MISSING_FLAG = -1;
	private final int CHAINID_MISSING_FLAG = -1;
	private final int ACTIVATE_PROMPT_DEBOUNCE = 100;
	public HashMap<Integer,Integer> entityFlags; // entity kind -> int flag
	public HashMap<Integer,int[]> entityConversationChains; 
	private final Integer[] VENDOR_NPC = {13,14};
	
	/*
	 * zoneActions
	 * 0 mapID   1 zoneID  2 actionID
	 */
	private final int DEF_ZONE_ACTIONS_AMOUNT = 10;
	public int[][] zoneActions;
	
	
	public Brain(GamePanel gp) {
		this.gp=gp;
		this.bflags=new HashMap<String, Boolean>();
		this.entityFlags = new HashMap<Integer,Integer>(); // perform an action when player touches or activates entity
		this.entityConversationChains = new HashMap<Integer,int[]>(); // perform an action when player touches or activates entity
		
		
		zoneActions = new int[DEF_ZONE_ACTIONS_AMOUNT][]; // perform an action when player touches or activates zone
		
		initStartingFlags();
		initConversationChainData();
		
		// thread for processing flags asynchronously
		brainThread = new Thread(this);
		brainThread.start();
		
	}
	
	private void initStartingFlags() {
		// perform an action when player touches or activates zone
		
		zoneActions[0] = new int[] {0,0,0};
		zoneActions[1] = new int[] {0,1,1};
		zoneActions[2] = new int[] {0,2,2};
		zoneActions[3] = new int[] {0,3,3};
		
		zoneActions[4] = new int[] {1,0,4};
		
		//
		
	}
	/*
	 * Types: 
	 * 0 Meatberry 
	 * 1 Bat 
	 * 2 Centipede 
	 * 3 Spider 
	 * 4 Maggot 
	 * 5 Earwig 
	 * 6 Groundhog 
	 * 7 Zombie 
	 * 8 Mercenary
	 * 9 Mercenary Leader 
	 * 
	 * 10 Rick 
	 * 11 Lilly 
	 * 12 Rodney 
	 * 13 Nicole
	 * 14 Ed
	 * 15 Melissa
	 * 16 John
	 * 17 Terry
	 * 18 Sue
	 * 19 Jason
	 * 20 Mary
	 */
	private void initConversationChainData() {
		//assign arrays of chainIDs to a hashmap
		// entityKind -> array of chainIDs
		int[] conversationChains;
		int kind;
		// each NPC has a list of conversation chains to be selected with their entity flag
		
		// 10 Rick
		conversationChains = new int[] {0};
		kind = 10;
		this.entityConversationChains.put(kind,conversationChains);
		
		// 13 Nicole
		kind=13;
		conversationChains = new int[] {1};
		this.entityConversationChains.put(kind,conversationChains);
	}
	
	public boolean getFlag(String key) {
		return bflags.getOrDefault(key, BFLAG_DEFAULT_VAULE);
	}
	
	public void setFlag(String key, boolean val) {
		  bflags.put(key  ,val);
	}
	
	public int getEntityFlag(String key) {
		
		return entityFlags.getOrDefault(key, defaultFlag);
	}
	
	public void setEntityFlag(int entityID, int value) {
		entityFlags.put(entityID  ,value);
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
		// helper game loop thread for processing flags asynchronously
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
		// get an entity flag, externally tracks a state about the entity
		// lookup a conversation chain based on that flag
		
		int entFlag = this.entityFlags.getOrDefault(entType,ENTITY_MISSING_FLAG);
		if (entFlag==ENTITY_MISSING_FLAG) {
			// if entity is not in hashmap, set their flag value to 0
			entityFlags.put(entType,ENTITY_DEFAULT_FLAG) ;
			entFlag = ENTITY_DEFAULT_FLAG;
		}
		int[] chainArray =  this.entityConversationChains.getOrDefault(entType, null);
		if (entFlag >= chainArray.length) {
			System.out.println("Brain: getConversationChainForEntityType: entFlag has no associated conversation chain");
		}else {

			return chainArray[entFlag];
		}
		return CHAINID_MISSING_FLAG;
	}

	public void playerActivateNPC(Entity entity, boolean pressecActivate) {
		// when player activates NPC, determine which conversation to start
		if (pressecActivate) {

			System.out.printf("Brain: player touch the NPC: %d  %d \n ",entity.kind,entity.UID);
			int chainID = getConversationChainForEntityType(entity.kind);
			if(chainID==CHAINID_MISSING_FLAG) {
				System.out.println("Brain: Player activated NPC, but no valid conversation chain found");
			}
			gp.conversation.setSpeakerNPC(entity.kind);
			gp.conversation.startConversation(chainID);
			
		}else {
			;
		}
		gp.hud.showActionPromptDelay.setDelay(ACTIVATE_PROMPT_DEBOUNCE);
		
	}
	
	public void endConversationNPC(int kind) {
		// action to be taken at end of conversation
		System.out.printf("Brain: player end conversation NPC: %d   \n ",kind);
		if (Utils.contains(VENDOR_NPC,kind)) {
			gp.gameState = GameState.STORE;
		}
		
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
