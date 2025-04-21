package main;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;


//import main.Tile.TileType;

public class Plant implements IEditableComponent{
	private static final String DATA_FILE_PREFIX = "decor";
	private static final String DATA_FILE_SUFFIX = ".csv";
	private static final String SPRITE_SHEET_URL = "/images/plantA.png";
	//private static final String SPRITE_TREE_URL = "/images/decorTree100.png";
	//private static final String SPRITE_COMMON2_URL = "/images/decorCommercial.png";
	private static int[] sizeArray;
	GamePanel gp;
	BufferedImage[] bufferedImages;
	Random random;
	public int[][] plantGrid;
	public final int maxPlantOnScreen = 200;

	public final int TREE_DECOR_SIZE = 100;
	public final int COMMON_DECOR_SIZE = 50;
	public final int defaultPlantSizePx = 50;
	public final int defaultPlantSizePxX = 25;
	public final int defaultPlantSizePxY = 25;
	public final int minTilesDrawX = 16;
	public final int minTilesDrawY = 12;
	public final int RANDOM_ITEM_DENSITY = 50;
	public final int MINIMUM_RANDOM_GRIDX = 300;
	public final int Y_CUTOFF_OFFSET = 40;
	//
	private final int SEEDKIND = 1;
	private final int TILLEDKIND = 0;
	//public Dictionary<PlantType, Integer> kindMap;
	int drawableRange;
	public final int WALL_TILE_TYPE = 1;
	public final int BLANK_ASSET_TYPE = -1;
	private boolean modified = false;
	private int xstart, xend, ystart, yend, yCutoff;

	public Plant(GamePanel gp) {
		this.gp = gp;

		//plantGrid = new int[GamePanel.MAP_TILES_Y][GamePanel.MAP_TILES_X];
		this.plantGrid = (new Utils()).initBlankGridD(GamePanel.MAP_TILES_Y, GamePanel.MAP_TILES_X, BLANK_ASSET_TYPE);
		//BufferedImage[] decorImages = new BufferedImage[10];
		try {
			initPlantImages();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		gp.addComponent(this);
		sizeArray = new int[bufferedImages.length];
		for (int i = 0; i< sizeArray.length;i++) {
			sizeArray[i] = defaultPlantSizePx;
		}
		//sizes to render sprites by kind
		for(int i = 0;i< 8;i++) {
			sizeArray[i]=COMMON_DECOR_SIZE;
			
		}
		for(int i = 8;i< 9;i++) {
			sizeArray[i]= TREE_DECOR_SIZE;
			
		}
		

	}
	
	public int[][] copyGrid(int[][] original){
		int rows = original.length;
		if( rows==0) return null;
		int cols = original[0].length;
		int[][] output = new int[rows][cols];
		for(int r = 0;r<rows;r++) {
			for(int c= 0;c<cols;c++) {
				output[r][c] = original[r][c];
			}
		}
		return output;
	}
	
	public int[][] copyPlantData(){
		int rows = plantGrid.length;
		if( rows==0) return null;
		int cols = plantGrid[0].length;
		int[][] output = new int[rows][cols];
		for(int r = 0;r<rows;r++) {
			for(int c= 0;c<cols;c++) {
				output[r][c] = plantGrid[r][c];
			}
		}
		return output;
	}

	
	private void initPlantImages() throws IOException {
		BufferedImage[] commonPlant = new Utils().spriteSheetCutter(SPRITE_SHEET_URL, 4, 2, 50, 50);
		BufferedImage[] bigPlant = new Utils().spriteSheetCutter(SPRITE_SHEET_URL, 2, 2, 100, 100);
		//BufferedImage[] treePlant = new Utils().spriteSheetCutter(SPRITE_TREE_URL, 2, 2, 100, 100);
		//BufferedImage[] common2Plant = new Utils().spriteSheetCutter(SPRITE_COMMON2_URL, 4, 4, 50, 50);
		this.bufferedImages = Utils.appendArray(commonPlant,  Arrays.copyOfRange(bigPlant, 2, 4));
		//this.bufferedImages = Utils.appendArray(bufferedImages, common2Plant);
		//this.bufferedImages = commonPlant;
	

	}


	public int clamp(int minval, int maxval, int test) {
		if (test < minval)
			return minval;
		if (test > maxval)
			return maxval;
		return test;
	}

	/**
	 * xmin, ymin, xmax, ymax visible on decor gred
	 * 
	 * @return
	 */
	public int[] gridRange() {
		int[] range = new int[4];
		range[0] = GamePanel.wpScreenLocX / GamePanel.TILE_SIZE_PX;
		range[1] = GamePanel.wpScreenLocY / GamePanel.TILE_SIZE_PX;
		range[2] = (GamePanel.WIDTH + GamePanel.wpScreenLocX) / GamePanel.TILE_SIZE_PX;
		range[3] = (GamePanel.HEIGHT + GamePanel.wpScreenLocY) / GamePanel.TILE_SIZE_PX;
		return range;
	}
	
	private void updateDrawRange() {
		int[] drawableRange = gridRange();

		// sprite culling distances
		xstart = drawableRange[0] - 4;
		ystart = drawableRange[1] - 4;
		xend = drawableRange[2] + 4;
		yend = drawableRange[3] + 4;

		if (ystart < 0)
			ystart = 0;
		if (xstart < 0)
			xstart = 0;
		if (xend > GamePanel.MAP_TILES_X)
			xend = GamePanel.MAP_TILES_X;
		if (yend > GamePanel.MAP_TILES_Y)
			yend = GamePanel.MAP_TILES_Y;
		
	}
	
	public void draw() {
		drawTop();
	}

	public void drawTop() {
		// render tiles above yCutoff first, then render Actors, then render lower Plant Sprites on top

		int screenX, screenY;
		xend=clamp(0, GamePanel.MAP_TILES_X, xend);
		yend=clamp(0, GamePanel.MAP_TILES_Y, yend);
		xstart=clamp(0, GamePanel.MAP_TILES_X, xstart);
		ystart=clamp(0, GamePanel.MAP_TILES_Y, ystart);
		yCutoff = (gp.player.worldY+Y_CUTOFF_OFFSET)/GamePanel.TILE_SIZE_PX;

		yCutoff=clamp(0, GamePanel.MAP_TILES_Y, yCutoff);

		for (int x = xstart; x < xend; x++) {
			for (int y = ystart; y < yCutoff; y++) {
				int kind = plantGrid[y][x];
				//System.err.println(kind);
				if (kind != BLANK_ASSET_TYPE) {
					// System.out.println("tree");
					int worldX = x * GamePanel.TILE_SIZE_PX;
					int worldY = y * GamePanel.TILE_SIZE_PX;
					screenX = worldX - GamePanel.wpScreenLocX;
					screenY = worldY - GamePanel.wpScreenLocY;
					int size = sizeArray[kind];
					 
					GamePanel.g2.drawImage(bufferedImages[kind], screenX, screenY, size,
							size, null);

				}

			}
		}

	}
	
	public void drawLower() {

		// render tiles above yCutoff first, then render Actors, then render lower Plant Sprites on top

		int screenX, screenY;
		xend=clamp(0, GamePanel.MAP_TILES_X, xend);
		yend=clamp(0, GamePanel.MAP_TILES_Y, yend);
		int kind;
		

		for (int x = xstart; x < xend; x++) {
			for (int y = yCutoff; y < yend; y++) {
				try {
					kind = plantGrid[y][x];
				}catch(ArrayIndexOutOfBoundsException e) {
					kind = BLANK_ASSET_TYPE;
				}
				
				if (kind != BLANK_ASSET_TYPE) {
					int worldX = x * GamePanel.TILE_SIZE_PX;
					int worldY = y * GamePanel.TILE_SIZE_PX;
					screenX = worldX - GamePanel.wpScreenLocX;
					screenY = worldY - GamePanel.wpScreenLocY;
					int size = sizeArray[kind];
					 
					GamePanel.g2.drawImage(bufferedImages[kind], screenX, screenY, size,
							size, null);

				}

			}
		}

	}

	





	public void update() {
		 updateDrawRange();
	}





	public boolean visibleOnScreen(int worldX, int worldY) {
		int buffer = 100;
		int swx = GamePanel.wpScreenLocX;
		int swy = GamePanel.wpScreenLocY;
		if (worldX > swx - buffer && worldY > swy - buffer && worldX < swx + buffer + GamePanel.WIDTH
				&& worldX < swy + buffer + GamePanel.HEIGHT) {
			return true;
		} else {
			return false;
		}

	}



	@Override
	public boolean validateAssetID(int testAssetID) {
		int maximum = this.maxAssetID();
		int actualAssetID = testAssetID;
		if (testAssetID > maximum) {
			testAssetID = 0;
		}else if(testAssetID <  0) {
			testAssetID = maximum;
		}else {
			actualAssetID = testAssetID;
		}
		try {
			BufferedImage asset = this.bufferedImages[actualAssetID];
		}catch(Exception e) {
			return false;
		}
		
		
		return true;
	}

	@Override
	public void paintAsset(int gridX, int gridY, int kind) {
		modified=true;
		if(gp.editor.delete) {
			kind = -1;
		}
		try {
			this.plantGrid[gridY][gridX] = kind;
			
			System.err.println(kind);
		}
		catch(Exception e){
			e.printStackTrace();
		} 
	
	}

	@Override
	public int maxAssetID() {
		
		return this.bufferedImages.length;
	}

	
	@Override
	public String getDataFilename() {
		return Utils.getLevelresourceFilename(this.gp.level, DATA_FILE_PREFIX, DATA_FILE_SUFFIX);
	}

	@Override
	public EditMode getEditMode() {
		return EditMode.PLANT;
	}

	@Override
	public int[][] getGridData() {
		// TODO Auto-generated method stub
		return this.plantGrid;
	}

	@Override
	public void setGridData(int[][] data) {
		if( data.equals(data))return;
		if (null!=data) {
			this.plantGrid = data;
		}
		
		
	}
	
	@Override
	public void initBlank( ) {
		//this.barrierRecords = new ArrayList<>();
		this.plantGrid = Utils.initBlankGrid(GamePanel.MAP_TILES_Y, GamePanel.MAP_TILES_X, BLANK_ASSET_TYPE);
		//barrierGrid = Utils.initBlankGrid(GamePanel.MAP_TILES_Y, GamePanel.MAP_TILES_X, BLANK_ITEM_TYPE);

	}



	@Override
	public boolean isModified() {
		if (modified) {
			modified=false;
			return true;
		}
		return false;
	}




	public void plantSeed() {
		modified=true;
		
		try {
			if(this.plantGrid[gp.player.tileForward[1]][gp.player.tileForward[0]] == TILLEDKIND) {

				this.plantGrid[gp.player.tileForward[1]][gp.player.tileForward[0]] = SEEDKIND;
			}
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	public void hoeGround() {
		modified=true;
		
		try {
			int gx = gp.player.tileForward[0];
			int gy = gp.player.tileForward[1];
			int tile = gp.tileManager.getTileYX(gy, gx);
			// tile should be grass
			// not contain other plant
			// not contain widget
			if( (tile==2||tile==1) &&
					( plantGrid[gx][gy] == BLANK_ASSET_TYPE)&&
					gp.widget.getWidgetGridXY(gx, gy)==gp.widget.BLANK_ITEM_TYPE){

				this.plantGrid[gp.player.tileForward[1]][gp.player.tileForward[0]] = TILLEDKIND;
			}
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
