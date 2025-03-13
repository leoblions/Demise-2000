package main;

import java.awt.Rectangle;


public class Collision {

	GamePanel gp;
	int BUFFER_ZONE = 2;
	double playerCenterDistance;
	
	public Collision(GamePanel gp) {
		this.gp=gp;
		
	}
	
	public static enum Direction4W{
		UP,DOWN,LEFT,RIGHT,NONE
	}
	
	public int tileAtWorldCoord(int wx, int wy) {
		return gp.tileManager.getTileYX( wy/GamePanel.TILE_SIZE_PX,wx/GamePanel.TILE_SIZE_PX);
		
	}
	
	public static boolean tileKindIsSolid(int kind) {
		if (kind>3) {
			return true;
		}else {
			return false;
		}
	}
	
	public boolean[] collideTilePlayer() {
		 
		boolean[] collisions = new boolean[] {false,false,false,false};
		Player p = gp.player;
		
		//up coll
		int tmp =  tileAtWorldCoord(p.worldX+(p.wpSolidArea.width/2),p.worldY-BUFFER_ZONE);
		if (tileKindIsSolid(tmp)) {
			System.out.println("collide up");
			collisions[0]=true;
		}
		//down coll
		tmp =  tileAtWorldCoord(p.worldX+(p.wpSolidArea.width/2),p.worldY+(p.wpSolidArea.height)+BUFFER_ZONE);
		if (tileKindIsSolid(tmp)) {
			System.out.println("collide dn");
			collisions[1]=true;
		}
		//left coll
		tmp =  tileAtWorldCoord(p.worldX -BUFFER_ZONE,p.worldY + p.wpSolidArea.height/2);
		if (tileKindIsSolid(tmp)) {
			System.out.println("collide lt");
			collisions[2]=true;
		}
		//right coll
		tmp =  tileAtWorldCoord(p.worldX + p.wpSolidArea.width +BUFFER_ZONE ,p.worldY + p.wpSolidArea.height/2);
		if (tileKindIsSolid(tmp)) {
			System.out.println("collide rt");
			collisions[3]=true;
			
		}
		return collisions;
		
	}
	/**
	 * returns array of bools, any of which will be true if the rectangles collide
	 * in that direction with a tile
	 * @param r
	 * @return
	 */
	public boolean[] collideTileRect(Rectangle r) {
		 
		boolean[] collisions = new boolean[] {false,false,false,false};
		//Player p = gp.player;
		
		//up coll
		int tmp =  tileAtWorldCoord(r.x+(r.width/2),r.y-BUFFER_ZONE);
		if (tileKindIsSolid(tmp)) {
			//System.out.println("collide up");
			collisions[0]=true;
		}
		//down coll
		tmp =  tileAtWorldCoord(r.x+(r.width/2),r.y+(r.height)+BUFFER_ZONE);
		if (tileKindIsSolid(tmp)) {
			//System.out.println("collide dn");
			collisions[1]=true;
		}
		//left coll
		tmp =  tileAtWorldCoord(r.x -BUFFER_ZONE ,r.y + r.height/2);
		if (tileKindIsSolid(tmp)) {
			//System.out.println("collide lt");
			collisions[2]=true;
		}
		//right coll
		tmp =  tileAtWorldCoord(r.x + r.width +BUFFER_ZONE ,r.y + r.height/2);
		if (tileKindIsSolid(tmp)) {
			//System.out.println("collide rt");
			collisions[3]=true;
			
		}
		return collisions;
		
	}
	
	public boolean collideTileRectDirection(Rectangle r, Direction4W direction) {
		 

		
		//up coll
		int tmp =  tileAtWorldCoord(r.x+(r.width/2),r.y-BUFFER_ZONE);
		if (tileKindIsSolid(tmp)&&direction==Direction4W.UP) {
			//System.out.println("collide up");
			return true;
		}
		//down coll
		tmp =  tileAtWorldCoord(r.x+(r.width/2),r.y+(r.height)+BUFFER_ZONE);
		if (tileKindIsSolid(tmp)&&direction==Direction4W.DOWN) {
			//System.out.println("collide dn");
			return true;
		}
		//left coll
		tmp =  tileAtWorldCoord(r.x -BUFFER_ZONE ,r.y + r.height/2);
		if (tileKindIsSolid(tmp)&&direction==Direction4W.LEFT) {
			//System.out.println("collide lt");
			return true;
		}
		//right coll
		tmp =  tileAtWorldCoord(r.x + r.width +BUFFER_ZONE ,r.y + r.height/2);
		if (tileKindIsSolid(tmp)&&direction==Direction4W.RIGHT) {
			//System.out.println("collide rt");
			return true;
			
		}
		return false;
		
	}
	
	public boolean collideTileRectAny(Rectangle r) {
		 
		//boolean[] collisions = new boolean[] {false,false,false,false};
		//Player p = gp.player;
		
		//up coll
		int tmp =  tileAtWorldCoord(r.x,r.y);
		if (tileKindIsSolid(tmp)) {
			System.out.println("collide up");
			return true; 
		}
		//down coll
		tmp =  tileAtWorldCoord(r.x,r.y);
		if (tileKindIsSolid(tmp)) {
			System.out.println("collide dn");
			return true; 
		}
		//left coll
		tmp =  tileAtWorldCoord(r.x  ,r.y );
		if (tileKindIsSolid(tmp)) {
			System.out.println("collide lt");
			return true; 
		}
		//right coll
		tmp =  tileAtWorldCoord(r.x   ,r.y );
		if (tileKindIsSolid(tmp)) {
			System.out.println("collide rt");
			return true; 
			
		}
		return false;
		
	}

}
