package main;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.LinkedList;

import main.GamePanel.InputAction;

public class EntityManager implements IEditableComponent,IInputListener{
	public static final int NEW_ENTITY_DEFAULT_UID = 0;
	public static final int ENTITY_ACTIVATE_DELAY_TICKS = 120;
	private static final String DATA_FILE_PREFIX = "entity";
	private static final String DATA_FILE_SUFFIX = ".csv";
	GamePanel gp;
	ArrayList<EntityRecord>entityRecords; //stores entity init data only
	ArrayList<Entity>entityList; //stores refs to Entity objects
	ArrayList<Entity>entityTouchedList;
	private boolean modified=false;
	public boolean playerTouchedActorSincelastTick = false;
	public boolean activateEntityFlag = false;
	public Delay entityActivateDalay;
	public Rectangle playerHitbox;
	public final int HITBOX_SIZE = 90;
	public final int HITBOX_OFFSET = -25;
	public boolean playerMelee = false;
	
	public EntityManager(GamePanel gp) {
		this.gp=gp;
		entityRecords = new ArrayList<>();
		entityTouchedList = new ArrayList<>();
		entityList = new ArrayList<>();
		//this.addEntity(5, 5, 0, 0);
		entityActivateDalay = new Delay();
		gp.editor.addComponent(this);
		gp.input.addListener(this);

		playerHitbox=new Rectangle();
	}
	
	public void playerAttackEntityMelee() {
		// set area where player struck
		int pgX = gp.player.tileForward[0]  ;
		int pgY = gp.player.tileForward[1]  ;
		
		int fwX = gp.player.tileForward[0]  * GamePanel.TILE_SIZE_PX;
		int fwY = gp.player.tileForward[1] * GamePanel.TILE_SIZE_PX ;
		int kind ;
		playerHitbox.x = fwX + HITBOX_OFFSET;
		playerHitbox.y = fwY + HITBOX_OFFSET;
		playerHitbox.width = HITBOX_SIZE;
		playerHitbox.height= HITBOX_SIZE;
		//System.out.println("playerAttackEntityMelee "+fwX+" "+fwY);
		playerMelee=true;
		
		
		
	}
	
	public void addEntity(int startGX, int startGY, int kind, int UID) {
		Entity entity = new Entity(this.gp,  startGX,   startGY,   kind,   UID);
		this.entityList.add(entity);
	}
	
	public void draw() {
		int[] visible = gp.visibleArea;
		

		
		for (int i = 0; i < entityList.size(); i++) {
			 Entity entity =entityList.get(i) ;
				if (null!= entity) {
					entity.draw();
					
				} 
				
			}
			
		
	}
	
	public void update() {
		playerTouchedActorSincelastTick = false;
		entityActivateDalay.reduce();
		for (int i = 0; i < entityList.size(); i++) {
			 Entity entity =entityList.get(i) ;
				if (null!= entity) {
					entity.update();
					
				} 
				
			}
		if(playerTouchedActorSincelastTick == false) {
			entityTouchedList.clear();
			activateEntityFlag = false;
		}
		playerMelee = false;
	}

	@Override
	public boolean validateAssetID(int testAssetID) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public int maxAssetID() {
		// TODO Auto-generated method stub
		return 10;
	}

	@Override
	public String getDataFilename() {
		return Utils.getLevelresourceFilename(this.gp.level, DATA_FILE_PREFIX, DATA_FILE_SUFFIX);
	}

	@Override
	public EditMode getEditMode() {
		// TODO Auto-generated method stub
		return EditMode.ENTITY;
	}

	@Override
	public int[][] getGridData() {
		// create data grid for saving to file
		int[][] dataGrid = getDataGridFromEntityRecordList();
		return dataGrid;
	}

	@Override
	public void setGridData(int[][] data) {
		// update records list
		convertDataGridToRecordsList(data);
		// init entity objects
		instantiateEntityObjectsFromRecordList();
		
	}

	@Override
	public void paintAsset(int gridX, int gridY, int kind) {
		// create entity object
		// check if coords in use
		// replace entity in coords if it does, append if does not
		modified=true;
		int UID = getNewUID();
		Entity entity = new Entity(this.gp,  gridX,   gridY,   kind, UID);
		EntityRecord eRecord = new EntityRecord(  gridX,   gridY,   kind,   UID);
		int matchingIndex = getIndexEntityRecordWithMatchingGridCoord(  gridX,   gridY);
		if(matchingIndex==-1) {
			entityRecords.add(eRecord);
			entityList.add(entity);
		}else {
			entityRecords.set(matchingIndex, eRecord);
			entityList.set(matchingIndex, entity);
		}
		
	}
	
	private boolean checkEntityRecordExistsAtGridCoord(int gridX, int gridY) {
		for(EntityRecord e : this.entityRecords) {
			if(e.startGX()==gridX&& e.startGY()==gridY) {
				return true;
			}
		}
		return false;
	}
	private int getIndexEntityRecordWithMatchingGridCoord(int gridX, int gridY) {
		int Length = this.entityRecords.size();
		for(int i = 0; i< Length; i++) {
			EntityRecord e = this.entityRecords.get(i);
			if(e.startGX()==gridX&& e.startGY()==gridY) {
				return i;
			}
		}
		//not found: return -1
		return -1;
	}
	
	private int getNewUID( ) {
		return NEW_ENTITY_DEFAULT_UID;
	}
	
	private int[][] getDataGridFromEntityRecordList(){
		LinkedList<int[]> outerList = new LinkedList<>();
		int length = 0;
		for(EntityRecord er: this.entityRecords) {
			if(null!=er) {
				int[] recordAsArray = new int[] {er.startGX(),er.startGY(),er.kind(),er.UID()};
				outerList.add(recordAsArray);
				length +=1;
			}
			
		}
		int[][] output = new  int[length][4];
		for(int i=0;i<length;i++) {
			output[i]=outerList.get(i);
		}
		
		return  output;
		
	}
	
	private void convertDataGridToRecordsList(int[][] dataGrid){
		ArrayList<EntityRecord> outerList = new ArrayList<>();
	 
		for(int[] arr: dataGrid) {
			if(null!=arr) {
				EntityRecord entrec = new EntityRecord(  arr[0],   arr[1],   arr[2],   arr[3]) ;
				outerList.add(entrec);
				 
			}
			
		}
		this.entityRecords = outerList;
		
	}
	
	private void instantiateEntityObjectsFromRecordList(){
		ArrayList<Entity> outerList = new ArrayList<>();
	 
		for(EntityRecord er: this.entityRecords) {
			if(null!=er) {
				Entity  entity = new Entity(gp, er.startGX(), er.startGY(), er.kind(), er.UID());
				outerList.add(entity);
			
			}
			
		}
		this.entityList = outerList;
		
	}

	@Override
	public boolean isModified() {
		if (modified) {
			modified=false;
			return true;
		}
		return false;
	}

	@Override
	public void inputListenerAction(InputAction action) {
		if (action!=null && action==InputAction.ACTION) {
			if (true) {
				activateEntityFlag=true;
				System.out.println("activate entity");
			}
			
			
		}
		
	}
	
	

}

record EntityRecord(int startGX, int startGY, int kind, int UID) {}
//only used to save and load entity init data
