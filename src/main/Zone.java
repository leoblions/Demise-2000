package main;


import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class Zone implements IEditableComponent{
	private static final String DATA_FILE_PREFIX = "zone";
	private static final String DATA_FILE_SUFFIX = ".csv";
	public final int ITEM_SCALE_PX = 25;
	public final int ITEM_TLC_OFFSET= GamePanel.TILE_SIZE_PX/2;
	public final float ITEM_DRAWSIZE_FACTOR = 0.5f;
	

	public final int ITEM_DEFAULT_W = 50;
	public final int ITEM_DEFAULT_H = 50;
	public final int DEFAULT_GRID_W = 1;
	public final int DEFAULT_GRID_H = 1;
	private final String SPRITE_SHEET_URL = "/images/widgetDefault.png";
	public final int BLANK_ITEM_TYPE = -1;
	
	BufferedImage[] bufferedImages;
	GamePanel gp;
	int[][] zoneGrid;
	Random random;
	CullRegion crg;
	ArrayList<ZoneRecord> zoneRecords;
	Rectangle testRectangle;
	private boolean modified;
	Color highlightColor = new Color(100, 50, 100, 100);
	
	/*
	 * Zone fields:
	 * 
	 * 
	 */
	
	
	
	Zone(GamePanel gp) {
		this.gp=gp;;
		//this.zoneGrid = new int[gp.MAP_TILES_Y][gp.MAP_TILES_X];
		zoneGrid = Utils.initBlankGrid(gp.MAP_TILES_Y, gp.MAP_TILES_X, BLANK_ITEM_TYPE);
		crg = new CullRegion(gp, 15);
		zoneRecords = new ArrayList<>();
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
	
	
	
	public int getUIDForzoneGridCoords(int gridX, int gridY) {
		for (ZoneRecord zr: zoneRecords) {
			if (gridX==zr.gridX()&&gridY==zr.gridY()) {
				return zr.UID();
			}
		}
		return -1;
	
		
	}
	
	
	
	public void touchZoneAction(int item, int UID) {
		System.out.println("Player touching zone "+ item);
		
		
		gp.sound.clipPlayFlags[2]=true;
	
		
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
		int kind = zoneGrid[pgY][pgX];
		if (kind!=BLANK_ITEM_TYPE) {
			//zoneGrid[pgY][pgX] = BLANK_ITEM_TYPE;
			//System.out.println("Got item "+kind);
			int UID = getUIDForzoneGridCoords(  pgY,   pgX);
			touchZoneAction(kind,UID);
		}

		
		
		
		
				
	}
	/**
	 * draws the items on screen, also adds onscreen items to a list
	 */
	public void draw() {
		int[] visible = gp.visibleArea;
		int TopLeftCornerX = gp.wpScreenLocX;
		int TopLeftCornerY = gp.wpScreenLocY;
		int maxY = zoneGrid.length;
		int maxX = zoneGrid[0].length;
		int startX = clamp(0,maxX,visible[0]-25);
		int startY = clamp(0,maxY,visible[1]-25);
		int endX = clamp(0,maxX,visible[2]+25);
		int endY = clamp(0,maxY,visible[3]+25);
		int screenX,screenY;
		int tmp;
		int zoneAmount = zoneRecords.size();
		ZoneRecord zr;
		for (int i = 0; i < zoneAmount; i++) {
			zr = zoneRecords.get(i);
			int gridX = zr.gridX();
			int gridY = zr.gridY();
			if((startX <= gridX && gridX <= endX) && (startY <= gridY && gridY <= endY)) {
				int worldX = gridX * GamePanel.TILE_SIZE_PX;
				int worldY = gridY * GamePanel.TILE_SIZE_PX;
				int widthP = zr.width() * GamePanel.TILE_SIZE_PX;
				int heightP = zr.height() * GamePanel.TILE_SIZE_PX;
				
				screenX = worldX - TopLeftCornerX;
				screenY = worldY - TopLeftCornerY;
				
				gp.g2.fillRect(
						screenX,
						screenY,
						widthP,
						heightP
						);
				
			}
			
			
		}
		
		
	}
	public void addRecordOrReplace(int tileGridX, int tileGridY, int kind) {
		if (zoneRecords==null) {
			zoneRecords = new ArrayList<Zone.ZoneRecord>();
		}
		int girdW = DEFAULT_GRID_W;
		int gridH = DEFAULT_GRID_H;
		
		for(int i=0;i< zoneRecords.size() ;i++) {
			ZoneRecord zr = zoneRecords.get(i);
			if (tileGridX==zr.gridX()&&tileGridY==zr.gridY()) {
				int UID = zr.UID();
				zoneRecords.set(i,new ZoneRecord(tileGridX, tileGridY, girdW, gridH, kind, UID));
				return;
			}
		}
		int UID = getNewUIDFromRecords();
		
		zoneRecords.add(new ZoneRecord(tileGridX, tileGridY, girdW, gridH, kind, UID));
		 
		
	}
	
public int getNewUIDFromRecords( ) {
		
		boolean UIDfound = true;
		int testUID = 0;
		int maxPasses = 1000;
		while(  testUID < maxPasses) {
			for(ZoneRecord zr: zoneRecords) {
				if (zr.UID()==testUID) {
					UIDfound=false;
					
				}
			}
			if(UIDfound==true) {
				return testUID;
			}else {
				testUID +=1;
				UIDfound=true;
			}
		}
		if(testUID >= maxPasses) {
			System.err.println("Widget::getNewUIDFromRecords failed to generate unique ID");
		}
		return testUID;
		 
		
	}
	
	public void addItem(int tileGridX, int tileGridY, int kind) {
		modified=true;
		try {
			zoneGrid[tileGridY][tileGridX] = kind;
			
		}catch(Exception e) {
			
		}
		
		 
		
	}
	
	public void initGridDataFromRecordsList() {
		zoneGrid = Utils.initBlankGrid(GamePanel.MAP_TILES_Y, GamePanel.MAP_TILES_X, BLANK_ITEM_TYPE);
		//this.zoneGrid = new int[GamePanel.MAP_TILES_Y][GamePanel.MAP_TILES_X];
		for (ZoneRecord zr : zoneRecords) {
			zoneGrid[zr.gridY()][zr.gridX()]=zr.kind();
		}
		
		
	}
	
	public void printRecordsList() {
		zoneGrid = Utils.initBlankGrid(GamePanel.MAP_TILES_Y, GamePanel.MAP_TILES_X, BLANK_ITEM_TYPE);
		//this.zoneGrid = new int[GamePanel.MAP_TILES_Y][GamePanel.MAP_TILES_X];
		for (ZoneRecord zr : zoneRecords) {
			System.out.printf(" %d %d %d %d %d %d",zr.gridX(),zr.gridY,zr.width(),zr.height(),zr.kind(),zr.UID());
		}
		
		
	}
	
	public void initRecordsListFrom2DA(int[][] data) {
		this.zoneRecords=new ArrayList<>();
		for(int i = 0; i < data.length;i++) {
			zoneRecords.add(new ZoneRecord(
					data[i][0],
					data[i][1],
					data[i][2],
					data[i][3],
					data[i][4],
					data[i][5]
							));
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
		modified = true;
		try {
			this.zoneGrid[gridY][gridX] = kind;
			addRecordOrReplace(gridX, gridY, kind);
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
		return EditMode.ZONE;
	}

	@Override
	public int[][] getGridData() {
		// convert widget records to 2DA of their contents
		LinkedList<int[]> zoneRecordsAsArray = new LinkedList<>();
		for(ZoneRecord zr: zoneRecords) {
			int[] recordAsArray = new int[] {zr.gridX(),zr.gridY(),zr.width(),zr.height(),zr.kind(),zr.UID()};
			zoneRecordsAsArray.add(recordAsArray);
		}
		int recordAmount = zoneRecordsAsArray.size();
		int[][] outputDataArray = new int[recordAmount][];
		for (int i = 0; i< recordAmount; i++) {
			outputDataArray[i]=zoneRecordsAsArray.get(i);
		}
		return outputDataArray;
	}

	@Override
	public void setGridData(int[][] data) {
		if (null!=data) {
			
			try {
				initRecordsListFrom2DA(data);
				initGridDataFromRecordsList();
				//printRecordsList();
			}catch(Exception e) {
				e.printStackTrace();
			}
			System.out.println("Zone setGridData received ok data");
		}else {
			System.out.println("Zone setGridData received null data");
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
	
	
	public record ZoneRecord(int gridX, int gridY, int width, int height, int kind, int UID) {}
		
		
	}


