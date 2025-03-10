package main;

import java.awt.Color;

public class Shadow {
	
	public final int SHADOW_BLOCK_SIZE = 3;
	public final int RAY_SPLASH = 7;
	public final boolean SHADOWS_ENABLED = false;
	
	Color[] colors;
	//colors[0] = new Color(.9f,0f,0f,0f);
	ShadowTile[][] shadowTiles;
	GamePanel gp;
	
	double playerCenterDistance;
	
	public Shadow(GamePanel gp) {
		this.gp=gp;
		colors = new Color[5];
		colors[0] = new Color(.9f,.9f,.9f,.9f); //light
		colors[1] = new Color(.1f,.1f,.1f,1.0f);
		colors[3] = new Color(.9f,.9f,.9f,.0f); //dark
		colors[4] = new Color(.9f,.9f,.9f,.0f); //darkish
		initShadowGrid();
		
	}
	
	public class ShadowTile{
		int gridX, gridY, colorNo;

		public ShadowTile(int screenX, int screenY, int colorNo) {
			super();
			this.gridX = screenX;
			this.gridY = screenY;
			this.colorNo = colorNo;
		}
	}
	
	public void initShadowGrid() {
		
		int numBlocksX = GamePanel.WIDTH / SHADOW_BLOCK_SIZE;
		int numBlocksY = GamePanel.WIDTH / SHADOW_BLOCK_SIZE;
		shadowTiles = new ShadowTile[numBlocksY+1][numBlocksX+1];
		for(int y = 0;y<shadowTiles.length;y++) {
			for(int x = 0;x<shadowTiles[0].length;x++) {
				shadowTiles[y][x] = new ShadowTile(
						x,
						y,
						0);
			}
		}
	}
	
	public void setTransparent(int screenX, int screenY) {
		int tileX = screenX / SHADOW_BLOCK_SIZE;
		int tileY = screenY / SHADOW_BLOCK_SIZE;
		shadowTiles[tileX][tileY].colorNo=3;
		
	}
	public void setTransparentW(int worldX, int worldY) {
		
		int tileX = (worldX-gp.wpScreenLocX) / SHADOW_BLOCK_SIZE;
		int tileY = (worldY-gp.wpScreenLocY) / SHADOW_BLOCK_SIZE;
		for (int y = tileY-RAY_SPLASH; y< tileY+RAY_SPLASH; y++) {
			for (int x = tileX-RAY_SPLASH; x< tileX+RAY_SPLASH; x++) {
				try {
					shadowTiles[y][x].colorNo=3;
				}catch(Exception e) {}
			}
		}

		
	}
public void setShadowW(int worldX, int worldY) {
		
		int tileX = (worldX-gp.wpScreenLocX) / SHADOW_BLOCK_SIZE;
		int tileY = (worldY-gp.wpScreenLocY) / SHADOW_BLOCK_SIZE;
		shadowTiles[tileY][tileX].colorNo=1;
		
	}
	
	public void draw(){
		
		if (GamePanel.drawShadows==false) {
			//System.out.println("draw shadows disabled");
			return;
		}
		
		for(int y = 0;y<shadowTiles.length;y++) {
			for(int x = 0;x<shadowTiles[0].length;x++) {
				int tmp = shadowTiles[y][x].colorNo;
				gp.g2.setColor(colors[tmp]);
				gp.g2.fillRect(
						x*SHADOW_BLOCK_SIZE,
						y*SHADOW_BLOCK_SIZE, 
						SHADOW_BLOCK_SIZE, 
						SHADOW_BLOCK_SIZE
						);
				shadowTiles[y][x].colorNo=1;
				
			}
		}
	}
	
	public void update() {
		
	}

}
