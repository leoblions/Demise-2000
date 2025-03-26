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

public class Barrier implements IEditableComponent {
	private static final String DATA_FILE_PREFIX = "barrier";
	private static final String DATA_FILE_SUFFIX = ".csv";
	public final int ITEM_SCALE_PX = 25;
	public final int ITEM_TLC_OFFSET = GamePanel.TILE_SIZE_PX / 2;
	public final float ITEM_DRAWSIZE_FACTOR = 0.5f;
	public boolean drawbarrier = true;
	public final int ITEM_DEFAULT_W = 50;
	public final int ITEM_DEFAULT_H = 50;
	private final String SPRITE_SHEET_URL = "/images/barrierDefault.png";
	public final int BLANK_ITEM_TYPE = -1;
	public final int RUBBLE = 3;
	int gridW = 1;
	int gridH = 1;

	BufferedImage[] bufferedImages;
	GamePanel gp;
	int[][] barrierGrid; // store type of barrier, not type of tile
	// can be -1 for no barrier
	//or 0+ for a barrier
	Random random;
	CullRegion crg;
	ArrayList<BarrierRecord> barrierRecords;
	Rectangle testRectangle;
	private boolean modified;
	// @formatter:off
	/*
	
	/*
	 * grid x,   grid y,   grid width,  grid height,  kind, id
	 * 
	 * kinds:
	 * 0 fragile wall
	 * 1 gate
	 * 2 cobweb
	 * 3 laser h
	 * 4 laser v
	 */
	// @formatter:on

	Barrier(GamePanel gp) {
		this.gp = gp;
		//drawbarrier = false;;
		// this.barrierGrid = new int[gp.MAP_TILES_Y][gp.MAP_TILES_X];
		barrierGrid = Utils.initBlankGrid(gp.MAP_TILES_Y, gp.MAP_TILES_X, BLANK_ITEM_TYPE);
		crg = new CullRegion(gp, 15);
		barrierRecords = new ArrayList<>();
		testRectangle = new Rectangle(0, 0, ITEM_DEFAULT_W, ITEM_DEFAULT_H);

		try {
			initImages();
		} catch (IOException e) {
			e.printStackTrace();
		}

		gp.editor.addComponent(this);
	}

	public int getUIDForbarrierGridCoords(int gridX, int gridY) {
		for (BarrierRecord wr : barrierRecords) {
			if (gridX == wr.gridX() && gridY == wr.gridY()) {
				return wr.UID();
			}
		}
		return -1;

	}

	public void toggleBarrier(int pgX, int pgY) {
		//System.out.println("Barrier touched " + item);
		//gp.hud.showActionPromptDelay.setDelay(60);
		int pgXC = clamp(0,GamePanel.MAP_TILES_X,pgX);
		int pgYC = clamp(0,GamePanel.MAP_TILES_Y,pgY);
		//boolean tileState = gp.tileManager.queryTileForBarrier(pgXC,pgYC);
		boolean tileState = (barrierGrid[pgYC][pgXC]>=0)?true:false;
		gp.tileManager.swapTileForBarrier(pgXC,pgYC,!tileState);

		gp.sound.clipPlayFlags[2] = true;

	}

	public void playerAttackBarrierMelee() {
		
		int pgX = gp.player.tileForward[0]  ;
		int pgY = gp.player.tileForward[1]  ;
		int fwX = gp.player.tileForward[0]  * GamePanel.TILE_SIZE_PX;
		int fwY = gp.player.tileForward[1] * GamePanel.TILE_SIZE_PX ;
		int kind ;
		try {
			kind = barrierGrid[pgY][pgX];
			if (kind != BLANK_ITEM_TYPE) {
				System.out.println("Player melee barrier "+kind);
				int UID = getUIDForbarrierGridCoords(pgY, pgX);
				//toggleBarrier(kind, UID);
				if(kind>=0) {
					 barrierGrid[pgY][pgX]=BLANK_ITEM_TYPE; // for displaying barrier sprite
					 gp.tileManager.revertTileMarkedAsBarrier(pgX,pgY);
					 gp.particle.addParticle(pgX, fwY, RUBBLE);
					 return;
				}
				
			}
			kind = barrierGrid[pgY+1][pgX];
			if (kind != BLANK_ITEM_TYPE) {
				System.out.println("Player melee barrier "+kind);
				int UID = getUIDForbarrierGridCoords(pgY+1, pgX);
				//toggleBarrier(kind, UID);
				if(kind>=0) {
					 barrierGrid[pgY+1][pgX]=BLANK_ITEM_TYPE;
					 gp.particle.addParticle(fwX, fwY+GamePanel.TILE_SIZE_PX, RUBBLE);
					 gp.tileManager.revertTileMarkedAsBarrier(pgX,pgY);
					 toggleBarrier(kind, UID);
					 return;
				}
				
			}
		}catch(ArrayIndexOutOfBoundsException e) {
		}
	}

	public void update() {
		crg.update();
		try {
			itemsTouchedByPlayer();
		} catch (Exception e) {

		}


	}

	private void initImages() throws IOException {
		this.bufferedImages = new Utils().spriteSheetCutter(SPRITE_SHEET_URL, 4, 4, 50, 50);

		// resize scale images

	}

	public void scaleImages(BufferedImage[] bufferedImages) {
		Image tmp_image;
		BufferedImage tmp_bimage;
		for (int i = 0; i < bufferedImages.length; i++) {
			if (bufferedImages[i] == null)
				continue;
			tmp_image = bufferedImages[i].getScaledInstance(ITEM_SCALE_PX, ITEM_SCALE_PX, Image.SCALE_SMOOTH);
			tmp_bimage = new BufferedImage(ITEM_SCALE_PX, ITEM_SCALE_PX, BufferedImage.TYPE_INT_ARGB);
			tmp_bimage.getGraphics().drawImage(tmp_image, 0, 0, null);
			bufferedImages[i] = tmp_bimage;
		}
	}

	public int clamp(int min, int max, int test) {
		if (test > max) {
			return max;
		} else if (test < min) {
			return min;
		} else {
			return test;
		}
	}

	/**
	 * adds items that collide with player to a list
	 */
	public void itemsTouchedByPlayer() {

		int pgX = gp.player.worldX / GamePanel.TILE_SIZE_PX;
		int pgY = gp.player.worldY / GamePanel.TILE_SIZE_PX;
		int kind = barrierGrid[pgY][pgX];
		if (kind != BLANK_ITEM_TYPE) {
			int UID = getUIDForbarrierGridCoords(pgY, pgX);
			toggleBarrier(pgX,pgY);
		}

	}

	/**
	 * draws the items on screen, also adds onscreen items to a list
	 */
	public void draw() {
		if(!drawbarrier) {
			return;
		}
		int[] visible = gp.visibleArea;
		int TopLeftCornerX = gp.wpScreenLocX;
		int TopLeftCornerY = gp.wpScreenLocY;
		int maxy = barrierGrid.length;
		int maxx = barrierGrid[0].length;
		int startx = clamp(0, maxx, visible[0] - 50);
		int starty = clamp(0, maxy, visible[1] - 50);
		int endx = clamp(0, maxx, visible[2] + 50);
		int endy = clamp(0, maxy, visible[3] + 50);
		int screenX, screenY;
		int tmp;

		for (int y = starty; y < endy; y++) {

			for (int x = startx; x < endx; x++) {
				if (barrierGrid[y][x] != -1) {

					int kind = barrierGrid[y][x];
					int worldX = x * GamePanel.TILE_SIZE_PX;
					int worldY = y * GamePanel.TILE_SIZE_PX;
					screenX = worldX - TopLeftCornerX;
					screenY = worldY - TopLeftCornerY;

					gp.g2.drawImage(bufferedImages[kind], screenX, screenY, ITEM_DEFAULT_W, ITEM_DEFAULT_H, null);
				}

			}

		}
	}

	public void addRecordOrReplace(int tileGridX, int tileGridY, int kind) {
		if (barrierRecords == null) {
			barrierRecords = new ArrayList<Barrier.BarrierRecord>();
		}
		for (int i = 0; i < barrierRecords.size(); i++) {
			BarrierRecord wr = barrierRecords.get(i);
			if (tileGridX == wr.gridX() && tileGridY == wr.gridY()) {
				int UID = wr.UID();
				barrierRecords.set(i, new BarrierRecord(tileGridX, tileGridY, gridW,gridH, kind, UID));
				return;
			}
		}
		int UID = getNewUIDFromRecords();
		barrierRecords.add(new BarrierRecord(tileGridX, tileGridY, gridW,gridH, kind, UID));
		// mark tile grid as non walkable -1
		markTileGridCellAsBarrier(tileGridX,   tileGridY,true);

	}
	
	public void markTileGridCellAsBarrier(int gridX, int gridY, boolean mark) {
		gp.tileManager.swapTileForBarrier(gridX,gridY,mark);
	}

	public int getNewUIDFromRecords() {

		boolean UIDfound = true;
		int testUID = 0;
		int maxPasses = 1000;
		while (testUID < maxPasses) {
			for (BarrierRecord wr : barrierRecords) {
				if (wr.UID() == testUID) {
					UIDfound = false;

				}
			}
			if (UIDfound == true) {
				return testUID;
			} else {
				testUID += 1;
				UIDfound = true;
			}
		}
		if (testUID >= maxPasses) {
			System.err.println("Barrier::getNewUIDFromRecords failed to generate unique ID");
		}
		return testUID;

	}

	public void addItem(int tileGridX, int tileGridY, int kind) {
		modified = true;
		try {
			barrierGrid[tileGridY][tileGridX] = kind;

		} catch (Exception e) {

		}

	}

	public void initGridDataFromRecordsList() {
		barrierGrid = Utils.initBlankGrid(GamePanel.MAP_TILES_Y, GamePanel.MAP_TILES_X, BLANK_ITEM_TYPE);
		// this.barrierGrid = new int[GamePanel.MAP_TILES_Y][GamePanel.MAP_TILES_X];
		for (BarrierRecord wr : barrierRecords) {
			barrierGrid[wr.gridY()][wr.gridX()] = wr.kind();
			gp.tileManager.swapTileForBarrier(wr.gridX(), wr.gridY(), true );
		}

	}

	public void initRecordsListFrom2DA(int[][] data) {
		this.barrierRecords = new ArrayList<>();
		for (int i = 0; i < data.length; i++) {
			barrierRecords.add(new BarrierRecord(data[i][0], data[i][1], data[i][2], data[i][3], data[i][4], data[i][5]  ));
		}

	}

	@Override
	public boolean validateAssetID(int testAssetID) {
		int maximum = this.maxAssetID();
		int actualAssetID = testAssetID;
		if (testAssetID > maximum) {
			testAssetID = 0;
		} else if (testAssetID < 0) {
			testAssetID = maximum;
		} else {
			actualAssetID = testAssetID;
		}
		try {
			BufferedImage asset = this.bufferedImages[actualAssetID];
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	@Override
	public void paintAsset(int gridX, int gridY, int kind) {
		modified = true;
		try {
			this.barrierGrid[gridY][gridX] = kind;
			addRecordOrReplace(gridX, gridY, kind);
			System.err.println(kind);
		} catch (Exception e) {
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
		return EditMode.BARRIER;
	}

	@Override
	public int[][] getGridData() {
		// convert barrier records to 2DA of their contents
		LinkedList<int[]> barrierRecordsAsArray = new LinkedList<>();
		for (BarrierRecord wr : barrierRecords) {
			int[] recordAsArray = new int[] { wr.gridX(), wr.gridY(),  wr.gridW(), wr.gridH(), wr.kind(), wr.UID() };
			barrierRecordsAsArray.add(recordAsArray);
		}
		int recordAmount = barrierRecordsAsArray.size();
		int[][] outputDataArray = new int[recordAmount][];
		for (int i = 0; i < recordAmount; i++) {
			outputDataArray[i] = barrierRecordsAsArray.get(i);
		}
		return outputDataArray;
	}

	@Override
	public void setGridData(int[][] data) {
		if (null != data) {
			initRecordsListFrom2DA(data);
			initGridDataFromRecordsList();
		}

	}

	@Override
	public boolean isModified() {
		if (modified) {
			modified = false;
			return true;
		}
		return false;
	}

	public record BarrierRecord(int gridX, int gridY, int gridW, int gridH, int kind, int UID) {
	}
	
	
	

}
