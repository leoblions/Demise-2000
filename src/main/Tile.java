package main;


public class Tile {
	
	public final int[] NOCLIP_TILES = {0};
	
	public int gridX, gridY, worldX, worldY, kind;
	public boolean collision; 

	public Tile(int gridX, int gridY, int kind) {
		// TODO Auto-generated constructor stub
		this.gridX=gridX;
		this.gridY=gridY;
		this. kind = kind;
		this.worldX = gridX* GamePanel.TILE_SIZE_PX;
		this.worldY = gridY* GamePanel.TILE_SIZE_PX;
		if (kind==1) {
			collision = true;
		}else {
			collision = false;
		}
		
		
		
		
		
		
		
	}
	
	public boolean collide() {
		if (kind==1) {
			return true;
		}else {
			return false;
		}
	}

}
