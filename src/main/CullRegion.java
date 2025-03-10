package main;

import java.awt.Rectangle;

public class CullRegion {
	Rectangle cullRect;
	int rangeFromPlayerGrid;
	int diam;
	GamePanel gp;
	public int startgx, startgy, endgx, endgy;

	public CullRegion(GamePanel gp, int rangeFromPlayerGrid) {
		this.gp = gp;
		this.rangeFromPlayerGrid = rangeFromPlayerGrid;
		cullRect = new Rectangle();
		cullRect.width = 2 * rangeFromPlayerGrid;
		cullRect.height = 2 * rangeFromPlayerGrid;
		diam = (2* rangeFromPlayerGrid);
	}
	
	public void update() {
		//int[] visible = getVisibleArea();
		int pgX = gp.player.worldX / GamePanel.TILE_SIZE_PX;
		int pgY = gp.player.worldY / GamePanel.TILE_SIZE_PX;
		cullRect.x = pgX - rangeFromPlayerGrid;
		cullRect.y = pgY - rangeFromPlayerGrid;
		startgx = cullRect.x;
		startgy = cullRect.y;
		int maxy = GamePanel.MAP_TILES_X-1;
		int maxx = GamePanel.MAP_TILES_X-1;
		int endgx = cullRect.x + diam;
		int endgy = cullRect.y + diam;
		
		// limit to real coordinates
		startgx = clamp(0,maxx,startgx);
		startgy = clamp(0,maxy,startgy );
		endgx = clamp(0,maxx,endgx);
		endgy = clamp(0,maxy,endgy);
		
	}
	
	
	public int clamp(int min, int max, int test) {
		if(test>max) {
			return max;
		}else if (test< min) {
			return min;
		}else {
			return test;
		}
	}

}
