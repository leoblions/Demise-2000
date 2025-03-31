package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class HUDInventory {
	/*
	 * Draw a 3x10 grid containing item image, name, quantity, and a button to use and drop the item
	 * 0: image 
	 * 1: name
	 * 2: quantity
	 * 3: use
	 * 4: drop
	 */
	public static final String INVENTORY_EQ_FRAME = "/images/InvHudSingle.png";
	public static final String INVENTORY_EQ_ITEMS = "/images/inventoryItem.png";
	public static final String BAR_BORDER = "/images/barBorder.png";
	public static final String NAMEPLATE_SPRITE = "/images/nameplate.png";
	private static final int ITEM_EQ_OFFSET_X = 10;
	private static final int ITEM_EQ_OFFSET_Y = 10;
	private static final int ITEM_EQ_FRAME_SIZE = 70;
	private final int ITEM_ICON_SIZE = 40;
	private static final int ITEM_EQ_ITEM_IMAGE_SIZE = 50;
	private static final int CELL_CONTENT_OFFSET = 10;
	private static final int ITEM_EQ_FRAME_ALPHA = 200;
	private static final int BLANK_ITEM_ID = -1;
	final int MENU_SLOTS_X = 5;
	final int MENU_SLOTS_Y = 10;
	
	int screenX,screenY, width, height;
	int cellHeight; 
	int[] cellWidth ;

	int[][] cellXValues ;

	int[][] cellYValues ;
	int selectedSlot = 0;
	int selectedBoxX = 0;
	int selectedBoxY = 0;
	
	//private static int itemEqBrcOffsetY = 10;
	//private static int itemEqBrcOffsetX = 0;
	//private static int itemEqScreenY = 10;
	//private static int itemEqScreenX = 10;
	private static int[] toolbarBoxOffsetsX;
	private static int[][] inventoryKindAmount;
	public static int itemEq = BLANK_ITEM_ID;
	public static boolean showToolbar = false;
	public boolean toggleActivate = false;
	public static boolean showEquippedItemFrame = true;
	private BufferedImage[] images;
	private BufferedImage[] itemImages;
	private BufferedImage gridbackgroundImage;
	Color selectedBoxColor = new Color(222, 222, 0, 222);
	Color selectedBoxColorBG = new Color(150, 150, 150, 122);
	Color selectedBoxColorFG = new Color(222, 222, 150, 122);
	Stroke borderStroke = new BasicStroke(4);
	
	
	GamePanel gp;

	public HUDInventory(GamePanel gp) {
		this.gp = gp;
		screenX = GamePanel.WIDTH / 10;
		screenY = screenX;
		width = GamePanel.WIDTH - (screenX*2);
		height = GamePanel.HEIGHT - (screenY*2);
		cellHeight = height / 10;
		cellWidth = new int[5];
		cellWidth[0] = cellHeight;
		cellWidth[1] = 5 * cellHeight;
		cellWidth[2] = (2 * cellHeight);
		cellWidth[3] = cellHeight;
		cellWidth[4] = cellHeight;
		cellXValues = new int[MENU_SLOTS_Y][MENU_SLOTS_X];
		cellYValues = new int[MENU_SLOTS_Y][MENU_SLOTS_X];
		createGridBackground();
		initImages();

	}

	private void initImages() {
		try {
			if (gp.item != null && gp.item.itemImages != null) {

				this.itemImages = gp.item.itemImages;
			} else {
				this.itemImages = Item.getImages();
			}
			// frames
			this.images = new BufferedImage[5];
			this.images[0] = ImageIO.read(getClass().getResourceAsStream(INVENTORY_EQ_FRAME));
			this.images[0] = Utils.imageSetAlpha(this.images[0], ITEM_EQ_FRAME_ALPHA);
			this.images[1] = makeInventoryToolbarBackground();
			this.images[2] = ImageIO.read(getClass().getResourceAsStream(NAMEPLATE_SPRITE));
			this.images[3] = ImageIO.read(getClass().getResourceAsStream(BAR_BORDER));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void createGridBackground() {
		gridbackgroundImage= new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
		Graphics gfx = gridbackgroundImage.getGraphics();
		gfx.setColor(selectedBoxColorBG);
		gfx.fillRect(0, 0, width, height);
		((Graphics2D) gfx).setStroke(borderStroke);
		gfx.setColor(selectedBoxColorFG);
		int currY = 0;
		for(int y = 0;y<MENU_SLOTS_Y;y++) {
			int currX = 0;
			
			for (int x=0;x<MENU_SLOTS_X;x++) {
				
				int cellX = currX ;
				int cellY = currY;
				int width = cellWidth[x];
				int height = cellHeight;
				gfx.drawRect(cellX, cellY, width, height);
				cellYValues[y][x] = cellY + screenY;
				cellXValues[y][x] = cellX + screenX;
				currX += width;
				
				
			}
			currY+=cellHeight;
		}
	}

	private BufferedImage makeInventoryToolbarBackground() {

		BufferedImage toolbar = null;
		toolbarBoxOffsetsX = new int[MENU_SLOTS_X];
		try {

			BufferedImage raw = ImageIO.read(getClass().getResourceAsStream(INVENTORY_EQ_FRAME));
			toolbar = new BufferedImage(raw.getWidth() * MENU_SLOTS_X, raw.getHeight(), raw.getType());
			int w = raw.getWidth();
			int h = raw.getHeight();
			Graphics2D tbGraphics = (Graphics2D) toolbar.getGraphics();

			int xOffset = 0;
			for (int i = 0; i < MENU_SLOTS_X; i++) {
				tbGraphics.drawImage(raw, xOffset, 0, w, h, null);
				toolbarBoxOffsetsX[i] = xOffset ;
				xOffset += w;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return toolbar;
	}

	public void drawMenuItemSprites() {

		int spriteAmount = 10;
		if (inventoryKindAmount.length < 10) {
			spriteAmount = inventoryKindAmount.length;
		}
		int currX = screenX;
		int currY = screenY;
		for (int yRow = 0; yRow < spriteAmount; yRow++) {
			int imageID = inventoryKindAmount[yRow][0];
			GamePanel.g2.drawImage(itemImages[imageID], currX, currY, ITEM_ICON_SIZE, ITEM_ICON_SIZE, null);
			currY += cellHeight;
		}
	}
	
	public void drawMenuItemNames() {

		int spriteAmount = 10;
		if (inventoryKindAmount.length < 10) {
			spriteAmount = inventoryKindAmount.length;
		}
		int currX = screenX + cellWidth[0] + CELL_CONTENT_OFFSET;
		int currY = screenY+ CELL_CONTENT_OFFSET;
		for (int yRow = 0; yRow < spriteAmount; yRow++) {
			int imageID = inventoryKindAmount[yRow][0];
			//GamePanel.g2.drawImage(itemImages[imageID], currX, currY, ITEM_ICON_SIZE, ITEM_ICON_SIZE, null);
			String itemname = gp.inventory.itemNames[imageID];
			GamePanel.g2.drawString(itemname, currX, currY);
			currY += cellHeight;
		}
	}

	public void handleMenuInput(boolean[] movesRequested) {
		boolean moved = (itemEq == BLANK_ITEM_ID);

		if (showToolbar) {
			if (movesRequested[2]) {
				this.selectedSlot -= 1;
				moved = true;
			} else if (movesRequested[3]) {
				this.selectedSlot += 1;
				moved = true;
			}
			selectedSlot = Utils.clamp(0, MENU_SLOTS_X, selectedSlot);
			if (moved) {
				try {
					itemEq = inventoryKindAmount[selectedSlot][0];
					gp.inventory.selectItem(itemEq);
					System.out.println("inv item " + gp.inventory.selectItem(itemEq));
					gp.inventory.selectProjectileType();
				} catch (ArrayIndexOutOfBoundsException e) {
				
				}
			}

		}
		movesRequested[0] = false;
		movesRequested[1] = false;
		movesRequested[2] = false;
		movesRequested[3] = false;
	}

	public void draw() {
		int tbWidth = ITEM_EQ_FRAME_SIZE;
		int tbHeight = ITEM_EQ_FRAME_SIZE;

		if (showEquippedItemFrame) {
			int backgroundImage = 0;
			if (showToolbar) {

				
				

				GamePanel.g2.drawImage(gridbackgroundImage, screenX, screenY, width, height, null);

				drawMenuItemSprites();

				drawMenuItemNames();

			} else {
				

			}

		}
	}

	public void update() {
		if (toggleActivate) {
			toggleActivate = false;
			showToolbar = !showToolbar;
			inventoryKindAmount = gp.inventory.queryKindAndAmount();
			System.out.println("Show toolbar " + showToolbar);

		}

		if (showToolbar) {
			selectedBoxX = ITEM_EQ_OFFSET_X + (selectedSlot * ITEM_EQ_FRAME_SIZE);
			selectedBoxY = gp.getHeight() - ITEM_EQ_OFFSET_Y - ITEM_EQ_FRAME_SIZE;
		}

	}

}
