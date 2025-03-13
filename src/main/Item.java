package main;


import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Random;

import javax.imageio.ImageIO;

public class Item implements IEditableComponent{
	private static final String DATA_FILE_PREFIX = "item";
	private static final String DATA_FILE_SUFFIX = ".csv";
	public final int ITEM_SCALE_PX = 25;
	public final int ITEM_TLC_OFFSET= GamePanel.TILE_SIZE_PX/2;
	public final float ITEM_DRAWSIZE_FACTOR = 0.5f;
	
	public final int MINIMUM_RANDOM_GRIDX = 10;
	public final int MINIMUM_RANDOM_GRIDY = 10;
	public final int RANDOM_ITEM_DENSITY = 50;
	public final int ITEM_DEFAULT_W = 50;
	public final int ITEM_DEFAULT_H = 50;
	private final String SPRITE_SHEET_URL = "/images/itemA.png";
	public final int BLANK_ITEM_TYPE = -1;
	private boolean modified = false;
	
	BufferedImage[] bufferedImages;
	GamePanel gp;
	int[][] itemGrid;
	Random random;
	CullRegion crg;
	
	Rectangle testRectangle;
	Item(GamePanel gp) {
		this.gp=gp;;
		//this.itemGrid = new int[gp.MAP_TILES_Y][gp.MAP_TILES_X];
		itemGrid = Utils.initBlankGrid(gp.MAP_TILES_Y, gp.MAP_TILES_X, BLANK_ITEM_TYPE);
		crg = new CullRegion(gp, 15);
		
		testRectangle = new Rectangle(
				0,
				0,
				ITEM_DEFAULT_W,
				ITEM_DEFAULT_H);
		
		try {
			initImages() ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		gp.editor.addComponent(this);
	}
	
	
	
	public void randomPlaceItem(int amount, int kind) {
		int itemsPlaced = 0;
		this.random = new Random();
		int tmp=0;
		// loop through tileGrid
		// check if tile is colliding or open
		// use random number to decide place item or not
		do {
			for (int y = 1; y< GamePanel.MAP_TILES_Y;y++) {
				for (int x = 1; x< GamePanel.MAP_TILES_X;x++) {
					tmp = random.nextInt(RANDOM_ITEM_DENSITY);
					if(gp.tileManager.getTileYX(x, y)==1 || tmp!=10 ||
							(x <  MINIMUM_RANDOM_GRIDX &&
							y < MINIMUM_RANDOM_GRIDX)
							
							) {
						continue;
					}
					try {
						itemGrid[y][x] = kind;
					}catch(Exception e) {
						
					}
					itemsPlaced++;
					if (itemsPlaced>=amount)break;
				}if (itemsPlaced>=amount)break;
			}
		}while(itemsPlaced<amount);
		
		
	}
	
	
	
	public void pickupItem(int item) {
		System.out.println("Picked up item "+ item);
		gp.inventory.addItem(item, 1);
		
		gp.sound.clipPlayFlags[2]=true;
	
		
	}
	
	 class ItemRecord{
		int gridX,gridY, kind;
		ItemRecord(int gridX, int gridY, int kind){
			this.kind=kind;
			this.gridX=gridX;
			this.gridY=gridY;
		}
	}
	
	
	public void update() {
		crg.update();
		try {
			itemsTouchedByPlayer();
		}catch(Exception e) {
			
		}
		
//		for (int item: itemsTouchedByPlayer) {
//			if(item!=null) {
//				
//			}
//			
//		}
		
	}
	
	private void initImages() throws IOException {
		this.bufferedImages = new Utils().spriteSheetCutter(SPRITE_SHEET_URL, 4, 4, 50, 50);
		
		
		
		//resize scale images
		
		
		
		
	}
	
	public void scaleImages(BufferedImage[] bufferedImages) {
		Image tmp_image;
		BufferedImage tmp_bimage;
		for(int i = 0; i< bufferedImages.length;i++) {
			if (bufferedImages[i]==null)continue;
			tmp_image = bufferedImages[i].getScaledInstance(ITEM_SCALE_PX,ITEM_SCALE_PX,Image.SCALE_SMOOTH);
			tmp_bimage = new BufferedImage(ITEM_SCALE_PX,ITEM_SCALE_PX,BufferedImage.TYPE_INT_ARGB);
			tmp_bimage.getGraphics().drawImage(tmp_image, 0, 0, null);
			bufferedImages[i] = tmp_bimage;
		}
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
	/**
	 * adds items that collide with player to a list
	 */
	public void itemsTouchedByPlayer() {
		
		Rectangle itemRect;
		
		Rectangle playerRect = gp.player.wpSolidArea;
		
		// check items n unculled area
		int pgX = gp.player.worldX / GamePanel.TILE_SIZE_PX;
		int pgY = gp.player.worldY/ GamePanel.TILE_SIZE_PX;
		int kind = itemGrid[pgY][pgX];
		if (kind!=BLANK_ITEM_TYPE) {
			itemGrid[pgY][pgX] = BLANK_ITEM_TYPE;
			//System.out.println("Got item "+kind);
			pickupItem(kind);
		}

		
		
		
		
				
	}
	/**
	 * draws the items on screen, also adds onscreen items to a list
	 */
	public void draw() {
		int[] visible = gp.visibleArea;
		int TopLeftCornerX = gp.wpScreenLocX;
		int TopLeftCornerY = gp.wpScreenLocY;
		int maxy = itemGrid.length;
		int maxx = itemGrid[0].length;
		int startx = clamp(0,maxx,visible[0]-50);
		int starty = clamp(0,maxy,visible[1]-50);
		int endx = clamp(0,maxx,visible[2]+50);
		int endy = clamp(0,maxy,visible[3]+50);
		int screenX,screenY;
		int tmp;
		
		
		
		for (int y = starty; y < endy; y++) {
			
			for (int x = startx; x < endx; x++) {
				if (itemGrid[y][x]!=-1) {
					
					int kind=itemGrid[y][x];
					int worldX = x * GamePanel.TILE_SIZE_PX;
					int worldY = y * GamePanel.TILE_SIZE_PX;
					screenX = worldX - TopLeftCornerX;
					screenY = worldY - TopLeftCornerY;
					
					gp.g2.drawImage(
							bufferedImages[kind],
							screenX,
							screenY,
							ITEM_DEFAULT_W,
							ITEM_DEFAULT_H,
							null);
				} 
				
			}
			
		}
	}
	
	public void addItem(int tileGridX, int tileGridY, int kind) {
		modified = true;
		try {
			itemGrid[tileGridY][tileGridX] = kind;
		}catch(Exception e) {
			
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
		try {
			this.itemGrid[gridY][gridX] = kind;
			
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
		return EditMode.ITEM;
	}

	@Override
	public int[][] getGridData() {
		// TODO Auto-generated method stub
		return this.itemGrid;
	}

	@Override
	public void setGridData(int[][] data) {
		if (null!=data) {
			this.itemGrid = data;
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


