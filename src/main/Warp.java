package main;

import java.awt.BasicStroke;

public class Warp {

	int[][]warpZones;
	GamePanel gp;
	final int ZONE_AMOUNT = 10;
	public Warp(GamePanel gp) {
		this.gp=gp;
		warpZones = new int[ZONE_AMOUNT][];
		// level, gx, gy
		warpZones[0]=new int[] {0,3,3};

		warpZones[1]=new int[] {1,3,3};

		warpZones[1]=new int[] {2,3,3};
		
		loadWarpsFromFile();
	}
	
	public void loadWarpsFromFile() {
		
	}
	
	public void warpToID(int warpID) {
		try {
			int[] locationData = warpZones[warpID];
			warpToLocation(locationData[0], locationData[1], locationData[2]);
		}catch(ArrayIndexOutOfBoundsException e) {
			System.out.printf("Warp can't warp to ID %d, out of array bounds\n",warpID);
		}catch(NullPointerException e) {

			System.out.printf("Warp can't warp to ID %d, data not initialized\n",warpID);
		}
	}
	
	public void warpToLocation(int level, int gridX, int gridY) {
		boolean gm = GamePanel.godMode ;
		GamePanel.godMode = true;
		gp.level = level;
		gp.loadComponentData();
		gp.conversation.loadDataFromFileCurrentRoom();
		gp.player.warpPlayer(gridX,gridY);
		GamePanel.godMode = gm;
	}
	
	

}
