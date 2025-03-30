package main;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;


//import main.Tile.TileType;

public class Decor implements IEditableComponent{
	private static final String DATA_FILE_PREFIX = "decor";
	private static final String DATA_FILE_SUFFIX = ".csv";
	private static final String SPRITE_SHEET_URL = "/images/decorCommon.png";
	private static final String SPRITE_TREE_URL = "/images/decorTree100.png";
	private static final String SPRITE_COMMON2_URL = "/images/decorCommercial.png";
	private static int[] sizeArray;
	GamePanel gp;
	BufferedImage[] bufferedImages;
	Random random;
	public int[][] decorGrid;
	public final int maxDecorOnScreen = 200;

	public final int TREE_DECOR_SIZE = 100;
	public final int COMMON_DECOR_SIZE = 50;
	public final int defaultDecorSizePx = 50;
	public final int defaultDecorSizePxX = 25;
	public final int defaultDecorSizePxY = 25;
	public final int minTilesDrawX = 16;
	public final int minTilesDrawY = 12;
	public final int RANDOM_ITEM_DENSITY = 50;
	public final int MINIMUM_RANDOM_GRIDX = 300;
	public final int Y_CUTOFF_OFFSET = 40;
	//public Dictionary<DecorType, Integer> kindMap;
	int drawableRange;
	public final int WALL_TILE_TYPE = 1;
	public final int BLANK_DECOR_TYPE = -1;
	private boolean modified = false;
	private int xstart, xend, ystart, yend, yCutoff;

	public Decor(GamePanel gp) {
		this.gp = gp;

		//decorGrid = new int[gp.MAP_TILES_Y][gp.MAP_TILES_X];
		decorGrid = Utils.initBlankGrid(gp.MAP_TILES_Y, gp.MAP_TILES_X, BLANK_DECOR_TYPE);
		//BufferedImage[] decorImages = new BufferedImage[10];
		try {
			initDecorImages();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		
		drawWallShadow();
		gp.editor.addComponent(this);
		sizeArray = new int[bufferedImages.length];
		for (int i = 0; i< sizeArray.length;i++) {
			sizeArray[i] = defaultDecorSizePx;
		}
		//sizes to render sprites by kind
		for(int i = 0;i< 16;i++) {
			sizeArray[i]=COMMON_DECOR_SIZE;
			
		}
		for(int i = 16;i< 20;i++) {
			sizeArray[i]= TREE_DECOR_SIZE;
			
		}
		

	}
	
	

	public void randomPlaceDecor(int amount, int kind, int tileKind) {
		int itemsPlaced = 0;
		this.random = new Random();
		int tmp = 0;
		int height, width;
		boolean keepTrying = false;
		// loop through tileGrid
		// check if tile is colliding or open
		// use random number to decide place item or not
		do {
			for (int y = 1; y < GamePanel.MAP_TILES_Y; y++) {
				for (int x = 1; x < GamePanel.MAP_TILES_X; x++) {
					tmp = random.nextInt(RANDOM_ITEM_DENSITY);
//					if(gp.tileGrid[y][x].kind!=tileKind || tmp!=10 ||
//							(x <  MINIMUM_RANDOM_GRIDX &&
//							y < MINIMUM_RANDOM_GRIDX)
//							
//							) {
//						continue;
//					}
					if (gp.tileManager.getTileYX(y, x)  == tileKind && tmp == 10) {
						try {
							// get height and width of image
							height = bufferedImages[kind].getHeight();
							width = bufferedImages[kind].getWidth();
							// place new decor object on the matrix
							decorGrid[y][x] = kind;
						} catch (Exception e) {
							System.out.println(" randomPlaceDecor failed" + itemsPlaced);

						}
						itemsPlaced++;
					} else {

					}

					if (itemsPlaced >= amount)
						break;
				}
				if (itemsPlaced >= amount)
					break;
			}
		} while (keepTrying);

	}

	public void drawWallShadow() {
		int kind, aboveKind;
		for (int y = 0; y < GamePanel.MAP_TILES_Y - 1; y++) {
			for (int x = 0; x < GamePanel.MAP_TILES_X; x++) {
				// place shadows on wall tiles
				try {
					kind = gp.tileManager.getTileYX(y,x);
					aboveKind = gp.tileManager.getTileYX(y+1, x);
					boolean solid = Collision.tileKindIsSolid(kind);
					boolean solidAbove = Collision.tileKindIsSolid(aboveKind);
					if (true == solid && !solidAbove) {
						decorGrid[y][x] = kind;
					}
				} catch (Exception e) {
					// throw away index oob exceptions
				}

			}

		}

	}

	public void putDecorOnTileType(int kind, DecorType dtype) {
		// int kind;
		for (int y = 0; y < gp.MAP_TILES_Y - 1; y++) {
			for (int x = 0; x < gp.MAP_TILES_X; x++) {
				// place shadows on wall tiles
				kind = gp.tileManager.getTileYX(y, x) ;
				if (kind == WALL_TILE_TYPE && gp.tileManager.getTileYX(y+1, x)  != WALL_TILE_TYPE) {

					decorGrid[y][x] = kind;
				}

			}
		}

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
		range[0] = gp.wpScreenLocX / gp.TILE_SIZE_PX;
		range[1] = gp.wpScreenLocY / gp.TILE_SIZE_PX;
		range[2] = (gp.WIDTH + gp.wpScreenLocX) / gp.TILE_SIZE_PX;
		range[3] = (gp.HEIGHT + gp.wpScreenLocY) / gp.TILE_SIZE_PX;
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
		if (xend > gp.MAP_TILES_X)
			xend = gp.MAP_TILES_X;
		if (yend > gp.MAP_TILES_Y)
			yend = gp.MAP_TILES_Y;
		
	}

	public void draw() {
		// render tiles above yCutoff first, then render Actors, then render lower Decor Sprites on top

		int screenX, screenY;
		clamp(0, gp.MAP_TILES_X, xend);
		clamp(0, gp.MAP_TILES_Y, yend);
		yCutoff = (gp.player.worldY+Y_CUTOFF_OFFSET)/GamePanel.TILE_SIZE_PX;

		clamp(0, gp.MAP_TILES_Y, yCutoff);

		for (int x = xstart; x < xend; x++) {
			for (int y = ystart; y < yCutoff; y++) {
				int kind = decorGrid[y][x];
				//System.err.println(kind);
				if (kind != BLANK_DECOR_TYPE) {
					// System.out.println("tree");
					int worldX = x * GamePanel.TILE_SIZE_PX;
					int worldY = y * GamePanel.TILE_SIZE_PX;
					screenX = worldX - GamePanel.wpScreenLocX;
					screenY = worldY - GamePanel.wpScreenLocY;
					int size = sizeArray[kind];
					gp.g2.drawImage(bufferedImages[kind], screenX, screenY, size,
							size, null);

				}

			}
		}

	}
	
	public void drawLower() {

		// render tiles above yCutoff first, then render Actors, then render lower Decor Sprites on top

		int screenX, screenY;
		clamp(0, gp.MAP_TILES_X, xend);
		clamp(0, gp.MAP_TILES_Y, yend);
		int kind;
		

		for (int x = xstart; x < xend; x++) {
			for (int y = yCutoff; y < yend; y++) {
				try {
					kind = decorGrid[y][x];
				}catch(ArrayIndexOutOfBoundsException e) {
					kind = BLANK_DECOR_TYPE;
				}
				
				if (kind != BLANK_DECOR_TYPE) {
					int worldX = x * GamePanel.TILE_SIZE_PX;
					int worldY = y * GamePanel.TILE_SIZE_PX;
					screenX = worldX - GamePanel.wpScreenLocX;
					screenY = worldY - GamePanel.wpScreenLocY;
					int size = sizeArray[kind];
					gp.g2.drawImage(bufferedImages[kind], screenX, screenY, size,
							size, null);

				}

			}
		}

	}

	public void addTree(int tileX, int tileY) {
		;
		decorGrid[tileY][tileX] = DecorType.LOLLIPOP_TREE.ordinal();
	}

	public void drawRectOfDecor(

			int startx, int starty, int width, int height, int kind) {
		if (startx < 0)
			startx = 0;
		if (starty < 0)
			starty = 0;

		int endx = startx + width;
		int endy = starty + height;
		for (int x = startx; x < endx; x++) {
			for (int y = starty; y < endy; y++) {
				// addTree( x, y);
				
				decorGrid[y][x] = kind;
			}

		}

	}

	private void drawRectOfDecorStretch(int startx, int starty, int width, int height, int kind) {
		if (startx < 0)
			startx = 0;
		if (starty < 0)
			starty = 0;

		int endx = startx + width;
		int endy = starty + height;
		for (int x = startx; x < endx; x++) {
			for (int y = starty; y < endy; y++) {
				// addTree( x, y);
				
				decorGrid[y][x] = kind;
			}

		}

	}

	public void update() {
		 updateDrawRange();
	}

	public enum DecorType {
		LOLLIPOP_TREE, BARREL, CHAIR2, OLDPC, FOUNTAIN, TOMBSTONE, WALLSHADOW, BOX, TABLE, SKELETON, COBWEB, HOUSEPLANT
	}

	public class DecorItem {
		public int worldX, worldY, height, width;
		public DecorType dtype;

		DecorItem(int worldX, int worldY, int width, int height, DecorType dtype) {
			this.worldX = worldX;
			this.worldY = worldY;
			this.dtype = dtype;
			this.height = height;
			this.width = width;
		}

	}

	public boolean visibleOnScreen(int worldX, int worldY) {
		int buffer = 100;
		int swx = gp.wpScreenLocX;
		int swy = gp.wpScreenLocY;
		if (worldX > swx - buffer && worldY > swy - buffer && worldX < swx + buffer + gp.WIDTH
				&& worldX < swy + buffer + gp.HEIGHT) {
			return true;
		} else {
			return false;
		}

	}

	private void initDecorImages() throws IOException {
		BufferedImage[] commonDecor = new Utils().spriteSheetCutter(SPRITE_SHEET_URL, 4, 4, 50, 50);
		BufferedImage[] treeDecor = new Utils().spriteSheetCutter(SPRITE_TREE_URL, 2, 2, 100, 100);
		BufferedImage[] common2Decor = new Utils().spriteSheetCutter(SPRITE_COMMON2_URL, 4, 4, 50, 50);
		this.bufferedImages = Utils.appendArray(commonDecor, treeDecor);
		this.bufferedImages = Utils.appendArray(bufferedImages, common2Decor);

	

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
			this.decorGrid[gridY][gridX] = kind;
			
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
		return EditMode.DECOR;
	}

	@Override
	public int[][] getGridData() {
		// TODO Auto-generated method stub
		return this.decorGrid;
	}

	@Override
	public void setGridData(int[][] data) {
		if (null!=data) {
			this.decorGrid = data;
		}
		
		
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
