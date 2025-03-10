package main;

import java.util.ArrayList;

public class EntityManager {
	GamePanel gp;
	ArrayList<EntityRecord>entityRecords;
	ArrayList<Entity>entityList;
	
	public EntityManager(GamePanel gp) {
		this.gp=gp;
		entityRecords = new ArrayList<>();
		entityList = new ArrayList<>();
		this.addEntity(5, 5, 0, 0);
		
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
	
	

}

record EntityRecord(int startGX, int startGY, int kind, int UID) {}
//only used to save and load entity init data
