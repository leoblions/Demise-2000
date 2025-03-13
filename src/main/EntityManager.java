package main;

import java.util.ArrayList;
import java.util.LinkedList;

public class EntityManager implements IEditableComponent{
	public static final int NEW_ENTITY_DEFAULT_UID = 0;
	private static final String DATA_FILE_PREFIX = "entity";
	private static final String DATA_FILE_SUFFIX = ".csv";
	GamePanel gp;
	ArrayList<EntityRecord>entityRecords; //stores entity init data only
	ArrayList<Entity>entityList; //stores refs to Entity objects
	private boolean modified=false;
	
	public EntityManager(GamePanel gp) {
		this.gp=gp;
		entityRecords = new ArrayList<>();
		entityList = new ArrayList<>();
		//this.addEntity(5, 5, 0, 0);
		gp.editor.addComponent(this);
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
		for (int i = 0; i < entityList.size(); i++) {
			 Entity entity =entityList.get(i) ;
				if (null!= entity) {
					entity.update();
					
				} 
				
			}
		
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
	
	

}

record EntityRecord(int startGX, int startGY, int kind, int UID) {}
//only used to save and load entity init data
