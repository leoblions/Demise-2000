package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;

import main.GamePanel.GameState;

public class HUDStore implements IClickableElement{
	/*
	 * 
	 * Works similarly to inventory.  Sell screen shows player inventory with sell button replacing delete.
	 * Buy mode only has buy button.
	 * Draw a 3x10 grid containing item image, name, quantity, and a button to use and drop the item
	 * 0: image 
	 * 1: name
	 * 2: quantity
	 * 3: use
	 * 4: drop
	 */
	private static final String INVENTORY_EQ_FRAME = "/images/InvHudSingle.png";
	private static final String INV_BUTTON_SS = "/images/buttons1.png";
	private static final String INVENTORY_EQ_ITEMS = "/images/inventoryItem.png";
	private static final String CELL_E = "/images/menuCellE.png";
	private static final String CELL_C = "/images/menuCellC.png";
	private static final String BAR_BORDER = "/images/barBorder.png";
	private static final String NAMEPLATE_SPRITE = "/images/nameplateC.png";
	private static final String BUY_MODE_TITLE = " Store: Buy Items";
	private static final String SELL_MODE_TITLE = " Store: Sell Items";
	private String menuTitle = "";
	private static int titleX, titleY;
	private static final int ITEM_EQ_OFFSET_X = 15;
	private static final int ITEM_EQ_OFFSET_Y = 15;
	private static final int ITEM_EQ_FRAME_SIZE = 70;
	private final int ITEM_ICON_SIZE = 40;
	private final int CELL_SIZE = 55;
	private final int  INVENTORY_COLLECTION_ID = 0;
	private final int STORE_COLLECTION_ID = 1;
	
	private BufferedImage rsBuy, rsSell;
	private BufferedImage buttonImageTalk, buttonImageSellItems, buttonImageBuyItems;
	
	public final int DEFAULT_PRICE = 10;
	public final int DEFAULT_MARKUP = 1;

	private final int TEXT_CORNER_OFFSET = 20;
	private static final int CELL_CONTENT_OFFSET = 10;
	private static final int ITEM_EQ_FRAME_ALPHA = 200;
	private static final int BLANK_ITEM_ID = -1;
	private final int MENU_SLOTS_X = 5;
	private final int MENU_SLOTS_Y = 10;
	
	private boolean inventoryDisplayedLastTick = false;
	public int amountToTransact = 1;
	public StoreMode storeMode = StoreMode.BUY;
	
	int screenX,screenY, cellUnitSize, width, height;
	int titleScreenY;
	int cellHeight, cellHeightShort; 
	int[] cellWidth ;

	int[][] cellXValues ;
	//pages
	int inventoryCurrentPage = 1;
	int inventorymaxPage = 1;
	private static final String PAGE_STRING_TEMPLATE = "Page %d of %d";
	private static final String MONEY_STRING_TEMPLATE = "Cash $%d";
	private static String pageString,moneyString;
	int invButtonX,invButtonY,pageControlButtonSize,pageControlButtonY,pageControlButtonX1,pageControlButtonX2,pageControlButtonX3;
	Rectangle[]  selectButtonRect,deleteButtonRect;
	Button[] pageButtons; // 
	Button[] buyButtons;
	Button[] sellButtons, infoButtons;
	int rowStart,rowEnd,rowsToDisplay;
	int deleteXOffset, selectXOffset;
	
	// mouse data
	boolean playerClicked = false;
	int[] mouseClickData = new int[3];

	int[][] cellYValues ;
	int selectedSlot = 0;
	int selectedBoxX = 0;
	int selectedBoxY = 0;
	
	private static int[] toolbarBoxOffsetsX;
	private static int[][] inventoryKindAmount;
	public static int itemEq = BLANK_ITEM_ID;
	public static boolean showInventoryScreen = false;
	public boolean toggleActivate = false;
	public static boolean showEquippedItemFrame = true;
	private int pageStringX, pageStringY;
	private BufferedImage[] images;
	private BufferedImage[] itemImages;
	private BufferedImage[] inventoryButtonImages;
	private BufferedImage gridbackgroundImage;
	Color selectedBoxColor = new Color(222, 222, 0, 222);
	Color selectedBoxColorBG = new Color(150, 150, 150, 122);
	Color selectedBoxColorBGSolid = new Color(70, 70, 70, 254);
	Color selectedBoxColorFG = new Color(200, 200, 200, 254);

	private final Color TEXT_COLOR = Color.white;
	Stroke stroke4 = new BasicStroke(4);
	Stroke stroke10 = new BasicStroke(10);
	
	public enum StoreMode {
		BUY, SELL
	}
	
	
	GamePanel gp;

	public HUDStore(GamePanel gp) {
		this.gp = gp;
		
		initLayoutDimensions();
		initImages();
		setButtonImages();
		createGridBackground();
		recalculateRows();
		setPageStringLocation();
		gp.clickableElements.add(this);

	}
	
	private void setButtonImages() {
		pageButtons[0].setImage(inventoryButtonImages[0]);
		pageButtons[1].setImage(inventoryButtonImages[1]);
		pageButtons[2].setImage(inventoryButtonImages[3]);
		
	}
	
private void setPageStringLocation() {
	pageStringX = pageControlButtonY-pageControlButtonSize*3;
	pageStringY = pageControlButtonX1+10;
}

	private void initLayoutDimensions() {
		screenX = GamePanel.WIDTH / 10;
		screenY = screenX;
		width = GamePanel.WIDTH - (screenX*2);
		height = GamePanel.HEIGHT - (screenY*2);
		cellUnitSize = height / 10;
		cellHeight = height / 10;
		cellHeightShort = cellHeight / 2;
		titleScreenY = screenY - (cellHeight / 2);
		cellWidth = new int[5];
		cellWidth[0] = cellHeight;
		cellWidth[1] = 5 * cellHeight;
		cellWidth[2] = (2 * cellHeight);
		cellWidth[3] = cellHeight;
		cellWidth[4] = cellHeight;
		selectXOffset = cellHeight * 8;
		deleteXOffset = cellHeight * 9;
		cellXValues = new int[MENU_SLOTS_Y][MENU_SLOTS_X];
		cellYValues = new int[MENU_SLOTS_Y][MENU_SLOTS_X];
		titleX = (GamePanel.WIDTH / 2) - 20;
		titleY = titleScreenY + 15;
		invButtonX = (cellUnitSize * 10) + screenX;
		invButtonY = (cellUnitSize * 9) + screenY;
		//page control buttons
		pageControlButtonSize = 40;
		pageControlButtonY = invButtonY + 4;
		pageControlButtonX1 = invButtonX + 4;
		pageControlButtonX2 = pageControlButtonX1 + pageControlButtonSize ;
		pageControlButtonX3 = pageControlButtonX2 + pageControlButtonSize ;
		
		pageButtons = new Button[5];
		sellButtons = new Button[10];
		buyButtons = new Button[10];
		infoButtons = new Button[10];
		// move page buttons
		pageButtons[0] = Button.InventoryButton(gp,pageControlButtonX1,pageControlButtonY,pageControlButtonSize,pageControlButtonSize).setID(0);
		pageButtons[1] = Button.InventoryButton(gp,pageControlButtonX2,pageControlButtonY,pageControlButtonSize,pageControlButtonSize).setID(1);
		pageButtons[2] = Button.InventoryButton(gp,pageControlButtonX3,pageControlButtonY,pageControlButtonSize,pageControlButtonSize).setID(2);
		//store buttons
		int pageControlButtonY2 = pageControlButtonY - pageControlButtonSize;
		int pageControlButtonY3 = pageControlButtonY - (2*pageControlButtonSize);
		pageButtons[3] = Button.InventoryButton(gp,pageControlButtonX1,pageControlButtonY2,pageControlButtonSize*3,pageControlButtonSize).setID(3);
		pageButtons[4] = Button.InventoryButton(gp,pageControlButtonX1,pageControlButtonY3,pageControlButtonSize*3,pageControlButtonSize).setID(4);
		// select and delete buttons
		for (int i = 0; i < MENU_SLOTS_Y;i++) {
			int buttonY = (i*cellUnitSize)+screenY;
			buyButtons[i]  = Button.InventoryButton(gp,screenX+selectXOffset, buttonY,cellUnitSize,cellUnitSize).setID(i);
			sellButtons[i] = Button.InventoryButton(gp,screenX+deleteXOffset, buttonY,cellUnitSize,cellUnitSize).setID(i);
			infoButtons[i] = Button.InventoryButton(gp,screenX+deleteXOffset, buttonY,cellUnitSize,cellUnitSize).setID(i);
		
		}
		
	}

	private void initImages() {
		try {
			if (gp.item != null && gp.item.itemImages != null) {

				this.itemImages = gp.item.itemImages;
			} else {
				this.itemImages = Item.getImages();
			}
			// frames
			this.images = new BufferedImage[6];
			this.images[0] = ImageIO.read(getClass().getResourceAsStream(INVENTORY_EQ_FRAME));
			this.images[0] = Utils.imageSetAlpha(this.images[0], ITEM_EQ_FRAME_ALPHA);
			this.images[1] = makeInventoryToolbarBackground();
			this.images[2] = ImageIO.read(getClass().getResourceAsStream(NAMEPLATE_SPRITE));
			this.images[3] = ImageIO.read(getClass().getResourceAsStream(BAR_BORDER));
			this.images[4] = ImageIO.read(getClass().getResourceAsStream(CELL_C));
			this.images[5] = ImageIO.read(getClass().getResourceAsStream(CELL_E));
			// inventory buttons
			inventoryButtonImages = (new Utils()).spriteSheetCutter(INV_BUTTON_SS, 4, 4, 50, 50);
			/*
			 * up
			 * down
			 * drop
			 * cancel
			 * private BufferedImage rsBuy, rsSell, rsSellItems, rsBuyItems, rsTalk, rsDown,rsUp;
			 */
			rsBuy = RasterString.getRasterStringAsSingleImage(new RasterString(gp,"BUY",0,0));
			rsSell = RasterString.getRasterStringAsSingleImage(new RasterString(gp,"SELL",0,0));
//			rsSellItems = RasterString.getRasterStringAsSingleImage(new RasterString(gp,"BUY ITEMS",0,0));
//			rsBuyItems = RasterString.getRasterStringAsSingleImage(new RasterString(gp,"SELL ITEMS",0,0));
//			rsTalk = RasterString.getRasterStringAsSingleImage(new RasterString(gp,"TALK",0,0));
//			rsUp = RasterString.getRasterStringAsSingleImage(new RasterString(gp,"UP",0,0));
//			rsDown = RasterString.getRasterStringAsSingleImage(new RasterString(gp,"DOWN",0,0));
			
			
			BufferedImage defImage = pageButtons[3].getImage();
			
			buttonImageBuyItems = stampTextOnImageCentered(defImage, "Buy Items", 3, 3);
			buttonImageSellItems = stampTextOnImageCentered(defImage, "Sell Items", 3, 3);
			buttonImageTalk = stampTextOnImageCentered(defImage, "Talk", 3, 3);
			pageButtons[2].setImage(buttonImageSellItems);
			pageButtons[3].setImage(buttonImageBuyItems);
			pageButtons[4].setImage(buttonImageTalk);
			
			
			
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void stampTextOnButton(Button button, String text, int offsetX, int offsetY) {
		BufferedImage buttonImage = button.getImage();
		int w = buttonImage.getWidth();
		int h = buttonImage.getHeight();
		BufferedImage output = new BufferedImage(w, h, buttonImage.getType());
		Graphics2D graphics = (Graphics2D) output.getGraphics();
		graphics.drawImage(buttonImage,0,0,w,h,null);
		BufferedImage rsText = RasterString.getRasterStringAsSingleImage(new RasterString(this.gp,text,0,0));
		graphics.drawImage(rsText,offsetX,offsetY,rsText.getWidth(),rsText.getHeight(),null);
		
		button.setImage(output);
	}
	
	public BufferedImage stampTextOnImage(BufferedImage orig, String text, int offsetX, int offsetY) {
		//BufferedImage buttonImage = button.getImage();
		int w = orig.getWidth();
		int h = orig.getHeight();
		BufferedImage output = new BufferedImage(w, h, orig.getType());
		Graphics2D graphics = (Graphics2D) output.getGraphics();
		graphics.drawImage(orig,0,0,w,h,null);
		BufferedImage rsText = RasterString.getRasterStringAsSingleImage(new RasterString(this.gp,text,0,0));
		graphics.drawImage(rsText,offsetX,offsetY,rsText.getWidth(),rsText.getHeight(),null);
		
		return output;
	}
	
	public BufferedImage stampTextOnImageCentered(BufferedImage orig, String text, int textScaleX ,int textScaleY) {
		//BufferedImage buttonImage = button.getImage();
		int w = orig.getWidth();
		int h = orig.getHeight();

		BufferedImage rsText = RasterString.getRasterStringAsSingleImage(new RasterString(this.gp,text,0,0));
		rsText.getScaledInstance(textScaleX, textScaleY, java.awt.Image.SCALE_DEFAULT);
		int xStart = (w/2) - (rsText.getWidth()/2);
		int yStart = (h/2) - (rsText.getHeight()/2);
		BufferedImage output = new BufferedImage(w, h, orig.getType());
		Graphics2D graphics = (Graphics2D) output.getGraphics();
		graphics.drawImage(orig,0,0,w,h,null);
		graphics.drawImage(rsText,xStart,yStart,rsText.getWidth(),rsText.getHeight(),null);
		
		return output;
	}
	
	private void createGridBackground() {
		gridbackgroundImage= new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
		Graphics gfx = gridbackgroundImage.getGraphics();
		gfx.setColor(selectedBoxColorBGSolid);
		gfx.fillRect(0, 0, width, height);
		((Graphics2D) gfx).setStroke(stroke10);
		gfx.setColor(selectedBoxColorFG);

		gfx.drawRect(0, 0, width, height);
		int currY = 0;
		BufferedImage img = this.images[5];
		int px = cellHeight * 10;
		int py = 0;
		gfx.drawRect( px, py, cellHeight*10, cellHeight*10);
		
		for(int y = 0;y<MENU_SLOTS_Y;y++) {
			int currX = 0;
			
			for (int x=0;x<MENU_SLOTS_X;x++) {

				 img = this.images[5];
				
				int cellX = currX ;
				int cellY = currY;
				int width = cellWidth[x];

				int height = cellHeight;
				if (width>height) {
					  img = this.images[4];
				}
				//gfx.drawRect(cellX, cellY, width, height);

				gfx.drawImage(img, cellX, cellY, width+5, height+5,null);

				//gfx.drawLine(cellX+width,cellY,cellX+width,cellY+height);
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
	
	public int getActiveCollection() {
		if(gp.gameState==GameState.STORE) {
			if(this.storeMode==StoreMode.BUY) {
				return STORE_COLLECTION_ID;
			}else {
				return INVENTORY_COLLECTION_ID;
			}
			
		}else {
			return INVENTORY_COLLECTION_ID;
		}
		//return INVENTORY_COLLECTION_ID;
	}

	public void drawMenuItemSprites() {

		if(null==inventoryKindAmount) {
			this.inventoryKindAmount = gp.inventory.queryKindAndAmount(getActiveCollection());
		}
		int items = MENU_SLOTS_Y;
		if (inventoryKindAmount.length < MENU_SLOTS_Y) {
			items = inventoryKindAmount.length;
		}
		int currX = screenX;
		int currY = screenY;
		int imageID;
		for (int row = rowStart; row < rowEnd; row++) {
			try {

				imageID = inventoryKindAmount[row][0];
			}catch(ArrayIndexOutOfBoundsException e) {
				break;
			}
			GamePanel.g2.drawImage(itemImages[imageID], currX, currY, ITEM_ICON_SIZE, ITEM_ICON_SIZE, null);
			currY += cellHeight;
		}
	}
	
	public void drawMenuItemStrings() {

		int items = MENU_SLOTS_Y;
		if (inventoryKindAmount.length < MENU_SLOTS_Y) {
			items = inventoryKindAmount.length;
		}
		int currXname = screenX + cellWidth[0] + CELL_CONTENT_OFFSET;

		int currXamount = currXname + cellWidth[1] + CELL_CONTENT_OFFSET;
		int currY = screenY+ CELL_CONTENT_OFFSET;
		
		GamePanel.g2.setColor(TEXT_COLOR);
		int imageID;
		for (int row = rowStart; row < rowEnd; row++) {
			try {

				 imageID = inventoryKindAmount[row][0];
			}catch(ArrayIndexOutOfBoundsException e) {
				break;
			}
			//GamePanel.g2.drawImage(itemImages[imageID], currX, currY, ITEM_ICON_SIZE, ITEM_ICON_SIZE, null);
			String itemname = gp.inventory.itemNames[imageID];
			int iAmount = gp.inventory.queryItemAmount(imageID,1);
			String sAmount = String.valueOf(iAmount);
			GamePanel.g2.drawString(itemname, TEXT_CORNER_OFFSET+currXname, TEXT_CORNER_OFFSET+ currY);
			GamePanel.g2.drawString(sAmount, TEXT_CORNER_OFFSET+currXamount, TEXT_CORNER_OFFSET+ currY);
			currY += cellHeight;
		}
	}
	
	public void recalculateRows() {
		if(inventoryKindAmount==null) {
			return;
		}
		 rowStart = (inventoryCurrentPage-1)*MENU_SLOTS_Y;
		 inventorymaxPage = (inventoryKindAmount.length / MENU_SLOTS_Y) +1;
		 rowEnd = rowStart + MENU_SLOTS_Y;
		 if(inventoryCurrentPage<inventorymaxPage) {
			 rowsToDisplay = MENU_SLOTS_Y;
		 }else if(inventoryKindAmount!=null){
			 rowsToDisplay = (inventoryKindAmount.length % MENU_SLOTS_Y);

			 //System.out.println("inventoryKindAmount.length " +inventoryKindAmount.length);
		 }else {
			 rowsToDisplay = MENU_SLOTS_Y;
		 }
		 //System.out.println("rowEnd "+rowEnd);

	}

	public void handleMenuInput(boolean[] movesRequested) {
		boolean moved = (itemEq == BLANK_ITEM_ID);

		if (showInventoryScreen) {
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
	
	public void drawPageControls() {
		/*
		 * 0 = down
		 * 1 = up
		 * 2 = close
		 * 3 = buy/sell
		 * 4 = talk
		 */
		GamePanel.g2.drawString(pageString, pageStringX, pageStringY);
		GamePanel.g2.drawString(moneyString, pageControlButtonX1+10, screenX + 20);
		for(Button button: pageButtons) {
			button.draw();
		}
//		pageButtons[0].draw();
//		pageButtons[1].draw();
//		pageButtons[2].draw();
//		GamePanel.g2.drawImage(this.inventoryButtonImages[0] , pageControlButtonX1, pageControlButtonY, pageControlButtonSize, pageControlButtonSize, null);
//		GamePanel.g2.drawImage(this.inventoryButtonImages[1] , pageControlButtonX2, pageControlButtonY, pageControlButtonSize, pageControlButtonSize, null);
//		GamePanel.g2.drawImage(this.inventoryButtonImages[3] , pageControlButtonX3, pageControlButtonY, pageControlButtonSize, pageControlButtonSize, null);
	}

	public void draw() {
		int tbWidth = ITEM_EQ_FRAME_SIZE;
		int tbHeight = ITEM_EQ_FRAME_SIZE;

		if (showEquippedItemFrame) {
			int backgroundImage = 0;
			if (gp.gameState==GameState.STORE) {

				
				

				GamePanel.g2.drawImage(gridbackgroundImage, screenX, screenY, width, height, null);
				//title bar
				GamePanel.g2.drawImage(this.images[2] , screenX, titleScreenY, width, cellHeightShort, null);
				GamePanel.g2.drawString(menuTitle, titleX, titleY);
				drawPageControls();
				drawMenuItemSprites();
				drawMenuItemStrings();
				drawBuyAndSellButtons();

			} else {
				

			}

		}
	}
	
	private void drawBuyAndSellButtons() {
		
		for (int i = 0; i < rowsToDisplay;i++) {
			int buttonY = (i*cellUnitSize)+screenY;
			int labelY = ((i+1)*cellUnitSize)+screenY-12;
			int labelH = 15;
			if(storeMode==StoreMode.BUY) {
				//col 1
				GamePanel.g2.drawImage(inventoryButtonImages[9],screenX+selectXOffset, buttonY,cellUnitSize,cellUnitSize,null);
				GamePanel.g2.drawImage(rsBuy,screenX+selectXOffset, labelY,cellUnitSize,labelH,null);
				//col 2
				GamePanel.g2.drawImage(inventoryButtonImages[10],screenX+deleteXOffset, buttonY,cellUnitSize,cellUnitSize,null);
			}else {
				//col 1
				GamePanel.g2.drawImage(inventoryButtonImages[9],screenX+selectXOffset, buttonY,cellUnitSize,cellUnitSize,null);
				GamePanel.g2.drawImage(rsSell,screenX+selectXOffset, labelY,cellUnitSize,labelH,null);
				//col 2
				GamePanel.g2.drawImage(inventoryButtonImages[10],screenX+deleteXOffset, buttonY,cellUnitSize,cellUnitSize,null);
			}
			
		}
		
	}

	private void changePage(int pageDelta) {
		inventoryCurrentPage += pageDelta;
		if(inventoryCurrentPage<1) {
			inventoryCurrentPage = 1;
		}
		if(inventoryCurrentPage>inventorymaxPage) {
			inventoryCurrentPage = inventorymaxPage;
		}
		
		recalculateRows();
	}
	
	private void handleClickData() {
		//System.out.println("player clicked inventory");
		Point clickPoint = new Point(mouseClickData[1],mouseClickData[2]);
		//page buttons
		for(var button: pageButtons) {
			if (button.contains(clickPoint)) {
				switch (button.id) {
				case 0:
					changePage(-1);
					break;
				case 1:
					changePage(1);
					break;
				case 2:
					showInventoryScreen = false;
					gp.gameState=GameState.PLAY;
					break;
				case 3:
					if(storeMode==StoreMode.BUY) {
						pageButtons[3].setImage(buttonImageBuyItems);
						storeMode = StoreMode.SELL;
						inventoryKindAmount = gp.inventory.queryKindAndAmount(0);
						menuTitle = SELL_MODE_TITLE;
					}else {
						pageButtons[3].setImage(buttonImageSellItems);
						storeMode = StoreMode.BUY;
						inventoryKindAmount = gp.inventory.queryKindAndAmount(1);
						menuTitle = BUY_MODE_TITLE;
					}

					recalculateRows();
					break;
				case 4:
					System.out.println("talk");
					gp.gameState=GameState.PLAY;
					gp.conversation.startConversation(gp.conversation.speakerNPC);
					gp.brain.talkWithShopkeeper = true;
				default:
					showInventoryScreen = false;
					gp.gameState=GameState.PLAY;
					break;
					
				}
				return;
			}
		}
		//buy-sell buttons
		int kind = -1;
		for(var button: sellButtons) {
			
			if (button.contains(clickPoint)) {
				
				kind = getItemTypeFromRowID(button.id);

				System.out.println("  info item kind: "+kind);
				return;
			}
		}
		//info buttons
		for(var button: buyButtons) {
			if (button.contains(clickPoint)) {
				kind = getItemTypeFromRowID(button.id);
				transactItem(kind);
				return;
			}
		}
		
		
	}
	
	private void transactItem(int kind) {
		System.out.println("buy/sell");
		
		int source,dest;
		if(storeMode==StoreMode.BUY) {
			source = 1;
			dest = 0;
		}else {
			source = 0;
			dest = 1;
		}
		gp.inventory.transactItem(source, dest, kind, amountToTransact);
		
		inventoryKindAmount = gp.inventory.queryKindAndAmount(getActiveCollection());
		recalculateRows();
	}
	
	private int getItemTypeFromRowID(int rowID) {
		
		int row = rowStart+rowID;
		int kind = -1;
			try {

				kind=inventoryKindAmount[row][0];
			}catch(ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		return kind;
		
	}
	
	private void handleClickData0() {
		System.out.println("player clicked inventory");
		Point clickPoint = new Point(mouseClickData[1],mouseClickData[2]);
		if (pageButtons[0].contains(clickPoint)) {
			changePage(-1);
		}else if(pageButtons[1].contains(clickPoint)) {
			changePage(1);
		}else if(pageButtons[2].contains(clickPoint)) {
			System.out.println("page Close");
			showInventoryScreen = false;
			gp.gameState=GameState.PLAY;
		}
	}

	public void update() {
		if(gp.gameState == GameState.STORE) {
			recalculateRows();
			if(!inventoryDisplayedLastTick) {
				if (storeMode==StoreMode.BUY) {
					this.menuTitle = BUY_MODE_TITLE;
					this.inventoryKindAmount = gp.inventory.queryKindAndAmount(1);
				}else {
					this.menuTitle = SELL_MODE_TITLE;
					this.inventoryKindAmount = gp.inventory.queryKindAndAmount(0);
				}
				
			}
			
			selectedBoxX = ITEM_EQ_OFFSET_X + (selectedSlot * ITEM_EQ_FRAME_SIZE);
			selectedBoxY = gp.getHeight() - ITEM_EQ_OFFSET_Y - ITEM_EQ_FRAME_SIZE;
			pageString = String.format(PAGE_STRING_TEMPLATE, inventoryCurrentPage,inventorymaxPage);
			moneyString = String.format(MONEY_STRING_TEMPLATE,  gp.player.money);
			if(playerClicked) {
				playerClicked=false;
				handleClickData();
			}
			
			inventoryDisplayedLastTick = true;
		}else {
			inventoryDisplayedLastTick = false;
		}
		

	}
	
	public void click(int kind, int mouseX, int mouseY) {
		if(gp.gameState==GameState.STORE) {
			playerClicked = true;
			mouseClickData[0]=kind;
			mouseClickData[1]=mouseX;
			mouseClickData[2]=mouseY;
		}
	}

}
