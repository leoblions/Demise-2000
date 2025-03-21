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

public class Widget implements IEditableComponent {
	private static final String DATA_FILE_PREFIX = "widget";
	private static final String DATA_FILE_SUFFIX = ".csv";
	public final int ITEM_SCALE_PX = 25;
	public final int ITEM_TLC_OFFSET = GamePanel.TILE_SIZE_PX / 2;
	public final float ITEM_DRAWSIZE_FACTOR = 0.5f;

	public final int ITEM_DEFAULT_W = 50;
	public final int ITEM_DEFAULT_H = 50;
	private final String SPRITE_SHEET_URL = "/images/widgetDefault.png";
	public final int BLANK_ITEM_TYPE = -1;
	public final int LEAF_PARTICLE = 0;
	public final int PLANT_WIDGET = 10;

	BufferedImage[] bufferedImages;
	GamePanel gp;
	int[][] widgetGrid;
	Random random;
	CullRegion crg;
	ArrayList<WidgetRecord> widgetRecords;
	Rectangle testRectangle;
	private boolean modified;
	// @formatter:off
	/*
	 * 
	 * Widgets are interactive immovable objects
	 * 0 = Door-Barrier
	 * 1 = Door-Warp
	 * 2 = Gate-Barrier
	 * 3 = Gate-Warp
	 * 4 = Barrel 1
	 * 5 = Barrel 2
	 * 6 = Switch 1
	 * 7 = Switch 2
	 * 8 = Chest 1
	 * 9 = Chest 2
	 * 10 = plant
	 * 11 = 
	 * 12
	 * 13
	 * 14
	 * 15
	 * 
	 * 
	 */
	// @formatter:on

	Widget(GamePanel gp) {
		this.gp = gp;
		;
		// this.widgetGrid = new int[gp.MAP_TILES_Y][gp.MAP_TILES_X];
		widgetGrid = Utils.initBlankGrid(gp.MAP_TILES_Y, gp.MAP_TILES_X, BLANK_ITEM_TYPE);
		crg = new CullRegion(gp, 15);
		widgetRecords = new ArrayList<>();
		testRectangle = new Rectangle(0, 0, ITEM_DEFAULT_W, ITEM_DEFAULT_H);

		try {
			initImages();
		} catch (IOException e) {
			e.printStackTrace();
		}

		gp.editor.addComponent(this);
	}

	public int getUIDForWidgetGridCoords(int gridX, int gridY) {
		for (WidgetRecord wr : widgetRecords) {
			if (gridX == wr.gridX() && gridY == wr.gridY()) {
				return wr.UID();
			}
		}
		return -1;

	}

	public void toggleWidget(int item, int UID) {
		//System.out.println("Widget touched " + item);
		gp.hud.showActionPromptDelay.setDelay(60);

		gp.sound.clipPlayFlags[2] = true;

	}

	public void playerAttackWidgetMelee() {
		
		int pgX = gp.player.tileForward[0]  ;
		int pgY = gp.player.tileForward[1]  ;
		int fwX = gp.player.tileForward[0]  * GamePanel.TILE_SIZE_PX;
		int fwY = gp.player.tileForward[1] * GamePanel.TILE_SIZE_PX ;
		int kind ;
		try {
			kind = widgetGrid[pgY][pgX];
			if (kind != BLANK_ITEM_TYPE) {
				System.out.println("Player melee widget "+kind);
				int UID = getUIDForWidgetGridCoords(pgY, pgX);
				//toggleWidget(kind, UID);
				if(kind==PLANT_WIDGET) {
					 widgetGrid[pgY][pgX]=-1;
					 gp.particle.addParticle(fwX, fwY, LEAF_PARTICLE);
				}
				
			}
			kind = widgetGrid[pgY+1][pgX];
			if (kind != BLANK_ITEM_TYPE) {
				System.out.println("Player melee widget "+kind);
				int UID = getUIDForWidgetGridCoords(pgY+1, pgX);
				//toggleWidget(kind, UID);
				if(kind==PLANT_WIDGET) {
					 widgetGrid[pgY+1][pgX]=-1;
					 gp.particle.addParticle(fwX, fwY+GamePanel.TILE_SIZE_PX, LEAF_PARTICLE);
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
		int kind = widgetGrid[pgY][pgX];
		if (kind != BLANK_ITEM_TYPE) {
			int UID = getUIDForWidgetGridCoords(pgY, pgX);
			toggleWidget(kind, UID);
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
		int startx = clamp(0, maxx, visible[0] - 50);
		int starty = clamp(0, maxy, visible[1] - 50);
		int endx = clamp(0, maxx, visible[2] + 50);
		int endy = clamp(0, maxy, visible[3] + 50);
		int screenX, screenY;
		int tmp;

		for (int y = starty; y < endy; y++) {

			for (int x = startx; x < endx; x++) {
				if (widgetGrid[y][x] != -1) {

					int kind = widgetGrid[y][x];
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
		if (widgetRecords == null) {
			widgetRecords = new ArrayList<Widget.WidgetRecord>();
		}
		for (int i = 0; i < widgetRecords.size(); i++) {
			WidgetRecord wr = widgetRecords.get(i);
			if (tileGridX == wr.gridX() && tileGridY == wr.gridY()) {
				int UID = wr.UID();
				widgetRecords.set(i, new WidgetRecord(tileGridX, tileGridY, kind, UID));
				return;
			}
		}
		int UID = getNewUIDFromRecords();
		widgetRecords.add(new WidgetRecord(tileGridX, tileGridY, kind, UID));

	}

	public int getNewUIDFromRecords() {

		boolean UIDfound = true;
		int testUID = 0;
		int maxPasses = 1000;
		while (testUID < maxPasses) {
			for (WidgetRecord wr : widgetRecords) {
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
			System.err.println("Widget::getNewUIDFromRecords failed to generate unique ID");
		}
		return testUID;

	}

	public void addItem(int tileGridX, int tileGridY, int kind) {
		modified = true;
		try {
			widgetGrid[tileGridY][tileGridX] = kind;

		} catch (Exception e) {

		}

	}

	public void initGridDataFromRecordsList() {
		widgetGrid = Utils.initBlankGrid(GamePanel.MAP_TILES_Y, GamePanel.MAP_TILES_X, BLANK_ITEM_TYPE);
		// this.widgetGrid = new int[GamePanel.MAP_TILES_Y][GamePanel.MAP_TILES_X];
		for (WidgetRecord wr : widgetRecords) {
			widgetGrid[wr.gridY()][wr.gridX()] = wr.kind();
		}

	}

	public void initRecordsListFrom2DA(int[][] data) {
		this.widgetRecords = new ArrayList<>();
		for (int i = 0; i < data.length; i++) {
			widgetRecords.add(new WidgetRecord(data[i][0], data[i][1], data[i][2], data[i][3]));
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
			this.widgetGrid[gridY][gridX] = kind;
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
		return EditMode.WIDGET;
	}

	@Override
	public int[][] getGridData() {
		// convert widget records to 2DA of their contents
		LinkedList<int[]> widgetRecordsAsArray = new LinkedList<>();
		for (WidgetRecord wr : widgetRecords) {
			int[] recordAsArray = new int[] { wr.gridX(), wr.gridY(), wr.kind(), wr.UID() };
			widgetRecordsAsArray.add(recordAsArray);
		}
		int recordAmount = widgetRecordsAsArray.size();
		int[][] outputDataArray = new int[recordAmount][];
		for (int i = 0; i < recordAmount; i++) {
			outputDataArray[i] = widgetRecordsAsArray.get(i);
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

	public record WidgetRecord(int gridX, int gridY, int kind, int UID) {
	}

}
