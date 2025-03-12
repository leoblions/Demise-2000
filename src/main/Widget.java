package main;


import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Random;

import javax.imageio.ImageIO;

public class Widget implements IEditableComponent{
	private static final String DATA_FILE_PREFIX = "widget";
	private static final String DATA_FILE_SUFFIX = ".csv";
	public final int ITEM_SCALE_PX = 25;
	public final int ITEM_TLC_OFFSET= GamePanel.TILE_SIZE_PX/2;
	public final float ITEM_DRAWSIZE_FACTOR = 0.5f;
	

	public final int ITEM_DEFAULT_W = 50;
	public final int ITEM_DEFAULT_H = 50;
	private final String SPRITE_SHEET_URL = "/images/itemA.png";
	public final int BLANK_ITEM_TYPE = -1;
	
	BufferedImage[] bufferedImages;
	GamePanel gp;
	int[][] widgetGrid;
	Random random;
	CullRegion crg;
	ArrayList<WidgetRecord> widgetRecords;
	Rectangle testRectangle;
	
	
	
	Widget(GamePanel gp) {
		this.gp=gp;;
		//this.widgetGrid = new int[gp.MAP_TILES_Y][gp.MAP_TILES_X];
		widgetGrid = Utils.initBlankGrid(gp.MAP_TILES_Y, gp.MAP_TILES_X, BLANK_ITEM_TYPE);
		crg = new CullRegion(gp, 15);
		widgetRecords = new ArrayList<>();
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
	
	
	
	public int getUIDForWidgetGridCoords(int gridX, int gridY) {
		for (WidgetRecord wr: widgetRecords) {
			if (gridX==wr.gridX()&&gridY==wr.gridY()) {
				return wr.UID();
			}
		}
		return -1;
	
		
	}
	
	
	
	public void toggleWidget(int item, int UID) {
		System.out.println("Picked up item "+ item);
		
		
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
		int kind = widgetGrid[pgY][pgX];
		if (kind!=BLANK_ITEM_TYPE) {
			widgetGrid[pgY][pgX] = BLANK_ITEM_TYPE;
			//System.out.println("Got item "+kind);
			int UID = getUIDForWidgetGridCoords(  pgY,   pgX);
			toggleWidget(kind,UID);
		}

		
		
		
		
				
	}
	/**
	 * draws the items on screen, also adds onscreen items to a list
	 */
	public void draw() {
		int[] visible = gp.visibleArea;
		int TopLeftCornerX = gp.wpScreenLocX;
		int TopLeftCornerY = gp.wpScreenLocY;
		int maxy = widgetGrid.length;
		int maxx = widgetGrid[0].length;
		int startx = clamp(0,maxx,visible[0]-50);
		int starty = clamp(0,maxy,visible[1]-50);
		int endx = clamp(0,maxx,visible[2]+50);
		int endy = clamp(0,maxy,visible[3]+50);
		int screenX,screenY;
		int tmp;
		
		
		
		for (int y = starty; y < endy; y++) {
			
			for (int x = startx; x < endx; x++) {
				if (widgetGrid[y][x]!=-1) {
					
					int kind=widgetGrid[y][x];
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
		try {
			widgetGrid[tileGridY][tileGridX] = kind;
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
			this.widgetGrid[gridY][gridX] = kind;
			
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
		return EditMode.WIDGET;
	}

	@Override
	public int[][] getGridData() {
		// convert widget records to 2DA of their contents
		LinkedList<int[]> widgetRecordsAsArray = new LinkedList<>();
		for(WidgetRecord wr: widgetRecords) {
			int[] recordAsArray = new int[] {wr.gridX(),wr.gridY(),wr.kind(),wr.UID()};
			widgetRecordsAsArray.add(recordAsArray);
		}
		int[][] outputDataArray = new int[widgetRecordsAsArray.size()][];
		return outputDataArray;
	}

	@Override
	public void setGridData(int[][] data) {
		if (null!=data) {
			this.widgetGrid = data;
		}
		
		
	}
	
	
	public record WidgetRecord(int gridX, int gridY, int kind, int UID) {}
		
		
	}


