package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import main.GamePanel.GameState;

public class HUDToolbar {
	// equipped item
	public static final String INVENTORY_EQ_FRAME = "/images/InvHudSingle.png";
	public static final String INVENTORY_EQ_ITEMS = "/images/inventoryItem.png";
	public static final String BAR_BORDER = "/images/barBorder.png";
	public static final String NAMEPLATE_SPRITE = "/images/nameplate.png";
	private static final int ITEM_EQ_OFFSET_X = 10;
	private static final int ITEM_EQ_OFFSET_Y = 10;
	private static final int ITEM_EQ_FRAME_SIZE = 70;
	private static final int ITEM_EQ_ITEM_IMAGE_SIZE = 50;
	private static final int ITEM_EQ_ITEM_IMAGE_OFFSET = 10;
	private static final int ITEM_EQ_FRAME_ALPHA = 200;
	private static final int BLANK_ITEM_ID = -1;
	final int TOOLBAR_SLOTS = 10;
	int selectedSlot = 0;
	int selectedBoxX = 0;
	int selectedBoxY = 0;
	private static int itemEqBrcOffsetY = 10;
	private static int itemEqBrcOffsetX = 0;
	private static int itemEqScreenY = 10;
	private static int itemEqScreenX = 10;
	private static int[] toolbarBoxOffsetsX;
	private static int[][] inventoryKindAmount;
	public static int itemEq = BLANK_ITEM_ID;
	public static boolean showToolbar = false;
	public boolean toggleActivate = false;
	public static boolean showEquippedItemFrame = true;
	private BufferedImage[] images;
	private BufferedImage[] itemImages;
	Color selectedBoxColor = new Color(222, 222, 0, 222);
	Color selectedBoxColorBG = new Color(222, 222, 50, 122);
	Stroke borderStroke = new BasicStroke(2);
	GamePanel gp;

	public HUDToolbar(GamePanel gp) {
		this.gp = gp;
		itemEqBrcOffsetY = GamePanel.HEIGHT - ITEM_EQ_OFFSET_Y - ITEM_EQ_FRAME_SIZE; // y position of equipped item
																						// frame
		itemEqBrcOffsetX = ITEM_EQ_OFFSET_X;
		itemEqScreenY = itemEqBrcOffsetY + ITEM_EQ_ITEM_IMAGE_OFFSET;
		itemEqScreenX = itemEqBrcOffsetX + ITEM_EQ_ITEM_IMAGE_OFFSET;
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

	private BufferedImage makeInventoryToolbarBackground() {

		BufferedImage toolbar = null;
		toolbarBoxOffsetsX = new int[TOOLBAR_SLOTS];
		try {

			BufferedImage raw = ImageIO.read(getClass().getResourceAsStream(INVENTORY_EQ_FRAME));
			toolbar = new BufferedImage(raw.getWidth() * TOOLBAR_SLOTS, raw.getHeight(), raw.getType());
			int w = raw.getWidth();
			int h = raw.getHeight();
			Graphics2D tbGraphics = (Graphics2D) toolbar.getGraphics();

			int xOffset = 0;
			for (int i = 0; i < TOOLBAR_SLOTS; i++) {
				tbGraphics.drawImage(raw, xOffset, 0, w, h, null);
				toolbarBoxOffsetsX[i] = xOffset + itemEqBrcOffsetX;
				xOffset += w;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return toolbar;
	}

	public void drawInventoryToolbarItemSprites() {

		int spriteAmount = 10;
		if (inventoryKindAmount.length < 10) {
			spriteAmount = inventoryKindAmount.length;
		}
		int currX = itemEqScreenX;
		int currY = itemEqScreenY;
		for (int i = 0; i < spriteAmount; i++) {
			int imageID = inventoryKindAmount[i][0];
			GamePanel.g2.drawImage(itemImages[imageID], currX, currY, ITEM_EQ_ITEM_IMAGE_SIZE, ITEM_EQ_ITEM_IMAGE_SIZE, null);
			currX += ITEM_EQ_FRAME_SIZE;
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
			selectedSlot = Utils.clamp(0, TOOLBAR_SLOTS, selectedSlot);
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

				backgroundImage = 1;
				tbWidth = ITEM_EQ_FRAME_SIZE * 10;
				GamePanel.g2.drawImage(images[backgroundImage], itemEqBrcOffsetX, itemEqBrcOffsetY, tbWidth, tbHeight, null);
				GamePanel.g2.setStroke(borderStroke);
				GamePanel.g2.setColor(selectedBoxColorBG);
				GamePanel.g2.fillRect(selectedBoxX, selectedBoxY, ITEM_EQ_FRAME_SIZE, ITEM_EQ_FRAME_SIZE);

				GamePanel.g2.setColor(selectedBoxColor);
				GamePanel.g2.drawRect(selectedBoxX, selectedBoxY, ITEM_EQ_FRAME_SIZE, ITEM_EQ_FRAME_SIZE);
				drawInventoryToolbarItemSprites();

			} else {
				GamePanel.g2.drawImage(images[backgroundImage], itemEqBrcOffsetX, itemEqBrcOffsetY, tbWidth, tbHeight, null);
				if (itemEq > 0) {

					GamePanel.g2.drawImage(itemImages[itemEq], itemEqScreenX, itemEqScreenY, ITEM_EQ_ITEM_IMAGE_SIZE,
							ITEM_EQ_ITEM_IMAGE_SIZE, null);
				}

			}

		}
	}

	public void update() {
		if (toggleActivate) {
			toggleActivate = false;
			showToolbar = !showToolbar;
			inventoryKindAmount = gp.inventory.queryKindAndAmount();
			System.out.println("Show toolbar " + showToolbar);
			if (!showToolbar)  {
				gp.gameState = GameState.PLAY;
			}
		}

		if (showToolbar) {
			gp.gameState=GameState.TOOLBAR;
			selectedBoxX = ITEM_EQ_OFFSET_X + (selectedSlot * ITEM_EQ_FRAME_SIZE);
			selectedBoxY = gp.getHeight() - ITEM_EQ_OFFSET_Y - ITEM_EQ_FRAME_SIZE;
		}

	}

}
