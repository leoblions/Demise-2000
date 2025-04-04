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
	private final String WIDGET_IMG_A = "/images/widgetA.png";
	private final String WIDGET_IMG_B = "/images/widgetB.png";
	public final int BLANK_ITEM_TYPE = -1;
	public final int LEAF_PARTICLE = 0;
	public final int ROCK_PARTICLE = 3;

	public final int WOOD_PARTICLE = 4;
	public final int PARTICLE_OFFSET_Y = 55;
	public final int STICK1 = 16;
	public final int STICK2 = 17;
	public final int PLANT1 = 18;
	public final int PLANT2 = 19;
	public final int STUMP1 = 20;
	public final int STUMP2 = 21;
	public final int ROCK1 = 22;
	public final int ROCK2 = 23;
	public final int BARREL1 = 4;
	public final int BARREL2 = 5;

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
	 * 8 = Chest 1 closed
	 * 9 = Chest 1 open
	 * 10 = Chest 2 closed
	 * 11 = Chest 2 open
	 * 12 = desk with phone
	 * 13 = keg
	 * 14 = sign
	 * 15 = hand pump
	 * 16 = log 1
	 * 17 = log 2
	 * 18 = plant 1
	 * 19 = plant 2
	 * 20 = stump 1
	 * 21 = stump 2
	 * 22 = rock 1
	 * 23 = rock 2
	 * 24 = bed
	 * 25 = work bench
	 * 26 = plant 3
	 * 27 = box
	 * 28 = box 2
	 * 29 = boiler
	 * 30 = sign 2
	 * 31 = grille
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

		gp.addComponent(this);
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
		// System.out.println("Widget touched " + item);
		gp.hud.showActionPromptDelay.setDelay(60);

		gp.sound.clipPlayFlags[2] = true;

	}

	public void playerAttackWidgetMelee() {

		int pgX = gp.player.tileForward[0];
		int pgY = gp.player.tileForward[1];
		int fwX = gp.player.tileForward[0] * GamePanel.TILE_SIZE_PX;
		int fwY = gp.player.tileForward[1] * GamePanel.TILE_SIZE_PX;
		int kindB, kindT;
		try {
			kindB = widgetGrid[pgY][pgX];
			kindT = widgetGrid[pgY + 1][pgX];
			meleeWidgetAddParticle(pgX, pgY);
			meleeWidgetAddParticle(pgX, pgY + 1);
		} catch (ArrayIndexOutOfBoundsException e) {
		}
	}

	public void playerAttackWidgetMelee_0() {

		int pgX = gp.player.tileForward[0];
		int pgY = gp.player.tileForward[1];
		int fwX = gp.player.tileForward[0] * GamePanel.TILE_SIZE_PX;
		int fwY = gp.player.tileForward[1] * GamePanel.TILE_SIZE_PX;
		int kindB, kindT;
		try {
			kindB = widgetGrid[pgY][pgX];
			kindT = widgetGrid[pgY + 1][pgX];
			if (kindB != BLANK_ITEM_TYPE) {
				System.out.println("Player melee widget " + kindB);
				int UID = getUIDForWidgetGridCoords(pgY, pgX);
				// toggleWidget(kind, UID);
				if (kindB == PLANT1 || kindB == PLANT2) {
					widgetGrid[pgY][pgX] = -1;
					gp.particle.addParticle(fwX, fwY, LEAF_PARTICLE);
				}

			}
			if (kindT != BLANK_ITEM_TYPE) {
				System.out.println("Player melee widget " + kindT);
				int UID = getUIDForWidgetGridCoords(pgY + 1, pgX);
				// toggleWidget(kind, UID);
				if (kindT == PLANT1 || kindT == PLANT2) {
					widgetGrid[pgY + 1][pgX] = -1;
					gp.particle.addParticle(fwX, fwY + GamePanel.TILE_SIZE_PX, LEAF_PARTICLE);
				}

			}
		} catch (ArrayIndexOutOfBoundsException e) {
		}
	}

	public void meleeWidgetAddParticle(int gridX, int gridY) {

		int fwX = gp.player.tileForward[0] * GamePanel.TILE_SIZE_PX;
		int fwY = gp.player.tileForward[1] * GamePanel.TILE_SIZE_PX;
		int wX = gridX * GamePanel.TILE_SIZE_PX;
		int wY = gridY * GamePanel.TILE_SIZE_PX;
		try {
			int kind = widgetGrid[gridY][gridX];
			if (kind != BLANK_ITEM_TYPE) {
				System.out.println("Player melee widget " + kind);
				int UID = getUIDForWidgetGridCoords(gridY , gridX);
				// toggleWidget(kind, UID);

				switch (kind) {
				case PLANT1, PLANT2:
					gp.particle.addParticle(wX, wY, LEAF_PARTICLE);

					widgetGrid[gridY][gridX] = -1;
					break;
				case ROCK1, ROCK2:
					gp.particle.addParticle(wX, wY, ROCK_PARTICLE);
					gp.inventory.addItem(gp.inventory.STONE, 1);
					widgetGrid[gridY][gridX] = -1;
					break;
				case STICK1, STICK2:
					gp.particle.addParticle(wX, wY, WOOD_PARTICLE);
				gp.inventory.addItem(gp.inventory.WOOD, 2);
					widgetGrid[gridY][gridX] = -1;
					break;
				case STUMP1, STUMP2:
					gp.particle.addParticle(wX, wY, WOOD_PARTICLE);
					gp.inventory.addItem(gp.inventory.WOOD, 2);
					widgetGrid[gridY][gridX] = -1;
					break;
				case BARREL1:
					gp.particle.addParticle(wX, wY, ROCK_PARTICLE);
					gp.inventory.addItem(gp.inventory.WOOD, 1);
					gp.inventory.addItem(gp.inventory.IRON, 1);
					widgetGrid[gridY][gridX] = BARREL2;
					break;
				default:
					break;

				}

			}
		} catch (ArrayIndexOutOfBoundsException e) {
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
		BufferedImage[] widgetA = new Utils().spriteSheetCutter(WIDGET_IMG_A, 4, 4, 50, 50);
		BufferedImage[] widgetB = new Utils().spriteSheetCutter(WIDGET_IMG_B, 4, 4, 50, 50);
		this.bufferedImages = Utils.appendArray(widgetA, widgetB);

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
	public void initBlank( ) {
		//this.barrierRecords = new ArrayList<>();

		widgetGrid = Utils.initBlankGrid(gp.MAP_TILES_Y, gp.MAP_TILES_X, BLANK_ITEM_TYPE);

		//this.entityList = outerList;
		//this.decorGrid = Utils.initBlankGrid(gp.MAP_TILES_Y, gp.MAP_TILES_X, BLANK_DECOR_TYPE);
		//barrierGrid = Utils.initBlankGrid(GamePanel.MAP_TILES_Y, GamePanel.MAP_TILES_X, BLANK_ITEM_TYPE);

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
