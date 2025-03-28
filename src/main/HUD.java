package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import main.GamePanel.InputAction;

public class HUD implements IStatusMessageListener, IInputListener {
	GamePanel gp;
	Graphics2D g2;
	Graphics g;
	public Font arial16;
	public Font arial20;
	public String lcText = "";
	public String lcText1 = "";
	public String lcText2 = "";
	public String lcText3 = "";
	public String lcText4 = "";
	public String promptText = "PRESS E";
	public String lcTextLine3, lcText5, lcText6;
	public boolean showInfotext = false;
	public int maxHealth = 100;
	public int health = 100;
	public int stamina = 100;
	public String healthText;  
	private String statusMessageText = "";
	public int killCount =0;
	public String killCountString = ""; 
	public String gemCountString = "";
	public int gemCount = 0;
	
	
	//equipped item
	public static final String INVENTORY_EQ_FRAME = "/images/InvHudSingle.png";
	public static final String INVENTORY_EQ_ITEMS = "/images/inventoryItem.png";
	private static final int ITEM_EQ_OFFSET_X = 10;
	private static final int ITEM_EQ_OFFSET_Y = 10;
	private static final int ITEM_EQ_FRAME_SIZE = 70;
	private static final int ITEM_EQ_ITEM_IMAGE_SIZE = 50;
	private static final int ITEM_EQ_ITEM_IMAGE_OFFSET = 10;
	private static final int ITEM_EQ_FRAME_ALPHA = 200;
	final int TOOLBAR_SLOTS = 10;
	int selectedSlot = 0;
	int selectedBoxX = 0;
	int selectedBoxY = 0;
	private boolean[] movesRequested=new boolean[4];
	Color selectedBoxColor = new Color(222,222,0,222);
	Color selectedBoxColorBG = new Color(222,222,50,122);
	Stroke borderStroke = new BasicStroke(2);
	
	private static int itemEqBrcOffsetY = 10;
	private static int itemEqBrcOffsetX = 0;
	private static int itemEqScreenY = 10;
	private static int itemEqScreenX = 10;
	private static int[] toolbarBoxOffsetsX;
	private static int[][] inventoryKindAmount;
	public static int itemEq = -1;
	public static boolean showToolbar = false;
	public boolean toggleToolbar = false;
	public static boolean showEquippedItemFrame = true;
	
	// text boxes
	private boolean showDialog = false;
	private boolean showPrompt = false;
	public Delay showActionPromptDelay = new Delay();
	public boolean showNamePlate = true;
	public RasterString speakerString;
	private final int RUN_STRNG_TEXT_OFFSET_X = 60;
	private final int RUN_STRNG_TEXT_OFFSET_Y = 50;
	
	
	// nameplate
	
	

	public static final String NAMEPLATE_SPRITE = "/images/nameplate.png";
	private final int NAMEPLATE_HEIGHT = 25;
	private final int NAMEPLATE_WIDTH = 100;
	private final int NAMEPLATE_TEXT_OFFSET = 5;
	private int nameplateX = 50;
	private int nameplateY = 50;
	
	//dialog boxes
	int dialogTextBoxPositionH ,dialogTextBoxPositionW , dialogTextBoxPositionX , dialogTextBoxPositionY;
	int toolTipTextBoxPositionH,toolTipTextBoxPositionW,toolTipTextBoxPositionX,toolTipTextBoxPositionY ;
	
	
	TextBox dialogTextBox, promptTextBox;
	Position dialogTextBoxPosition, toolTipTextBoxPosition;
	RasterString runString;
	BufferedImage[] images;
	BufferedImage[] itemImages;
	// @formatter:off
	/*
	 *Inventory items:
	 *
	 *0 health
	 *1 seeds
	 *2 machete
	 *3 hoe
	 *4 watering can
	 *5 bear trap
	 *6 pickaxe
	 *7 bomb 
	 
	 * 
	 * 
	 */
	// @formatter:on
	int alpha = 127;
	
	Color smBackground = new Color(100, 100, 100, alpha);
	Color smBorder = new Color(50, 50, 50, alpha);
	Color healthBarColor = new Color(50, 200, 50);

	Color clear = new Color(50, 200, 50,0);
	
	private static boolean mboxTextVisible ;
	private static int mboxTextVisibleTimeout =60;
	private static String mboxTextString1,
	mboxTextString2,mboxTextString3,mboxTextString4,mboxTextString5;
	
	public HUD(GamePanel gp) {
		if(gp==null) System.out.println("HUD ctor received null reference 1") ;
		if(g2==null) System.out.println("HUD ctor received null reference 2") ;
		this.gp = gp;
		
		int runStringX = gp.WIDTH - RUN_STRNG_TEXT_OFFSET_X;
		int runStringY = RUN_STRNG_TEXT_OFFSET_Y;
		runString = new RasterString(gp, "RUN", runStringX, runStringY);
		runString.visible=true;
		arial16 = new Font("Arial",Font.PLAIN,16);
		arial20 = new Font("Arial",Font.BOLD,20);
		//mboxTextVisible=false;
		itemEqBrcOffsetY = GamePanel.HEIGHT -ITEM_EQ_OFFSET_Y -ITEM_EQ_FRAME_SIZE; // y position of equipped item frame
		itemEqBrcOffsetX = ITEM_EQ_OFFSET_X; 
		itemEqScreenY = itemEqBrcOffsetY + ITEM_EQ_ITEM_IMAGE_OFFSET;
		itemEqScreenX = itemEqBrcOffsetX + ITEM_EQ_ITEM_IMAGE_OFFSET;
		initDialogTextBox();
		initElementPositions();
		speakerString=RasterString.RasterStringBGC(gp, "player", nameplateX+NAMEPLATE_TEXT_OFFSET, nameplateY+NAMEPLATE_TEXT_OFFSET, clear);
		initImages();
	}
	
	private void initElementPositions() {
		nameplateY = dialogTextBoxPositionY - NAMEPLATE_HEIGHT;
		nameplateX = dialogTextBoxPositionX;
		
	}

	private void initImages() {
		try {
			if(gp.item!=null && gp.item.itemImages!=null) {

				this.itemImages = gp.item.itemImages;
			}else {
				this.itemImages = Item.getImages();
			}
			//frames
			this.images = new BufferedImage[5];
			this.images[0] = ImageIO.read(getClass().getResourceAsStream(INVENTORY_EQ_FRAME));
			this.images[0] = Utils.imageSetAlpha(this.images[0],ITEM_EQ_FRAME_ALPHA);
			this.images[1] = makeInventoryToolbarBackground();
			this.images[2] = ImageIO.read(getClass().getResourceAsStream(NAMEPLATE_SPRITE));
			
			//this.itemImages = new Utils().spriteSheetCutter(INVENTORY_EQ_ITEMS, 4, 4, ITEM_EQ_ITEM_IMAGE_SIZE, ITEM_EQ_ITEM_IMAGE_SIZE);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private BufferedImage makeInventoryToolbarBackground() {
		
		BufferedImage toolbar = null;
		toolbarBoxOffsetsX = new int[TOOLBAR_SLOTS];
		try {

			BufferedImage raw = ImageIO.read(getClass().getResourceAsStream(INVENTORY_EQ_FRAME));
			toolbar = new BufferedImage(raw.getWidth()*TOOLBAR_SLOTS,raw.getHeight(),raw.getType());
			int w = raw.getWidth();
			int h = raw.getHeight();
			Graphics2D tbGraphics = (Graphics2D) toolbar.getGraphics();

			int xOffset = 0;
			for(int i = 0; i< TOOLBAR_SLOTS;i++) {
				tbGraphics.drawImage(raw,xOffset,0,w,h,null);
				toolbarBoxOffsetsX[i] = xOffset + itemEqBrcOffsetX;
				xOffset += w;
			}
			
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return toolbar;
	}
	
	
	
	private void initDialogTextBox() {
		int HEIGHT  = GamePanel.HEIGHT;
		int WIDTH = GamePanel.WIDTH;
		dialogTextBoxPositionH = HEIGHT / 10;
		dialogTextBoxPositionW = WIDTH/2;
		dialogTextBoxPositionX = (WIDTH/2) - (dialogTextBoxPositionW/2);
		dialogTextBoxPositionY = (HEIGHT) - (dialogTextBoxPositionH*2);
		dialogTextBoxPosition = new Position(this.gp,
				dialogTextBoxPositionX,
				dialogTextBoxPositionY,
				dialogTextBoxPositionW,
				dialogTextBoxPositionH);
		this.dialogTextBox = new TextBox(this.gp, dialogTextBoxPosition);
		//this.dialogTextBox.backgroundColor =new Color(255, 200, 200, 150);
		
		toolTipTextBoxPositionH = HEIGHT / 20;
		toolTipTextBoxPositionW = WIDTH/6;
		toolTipTextBoxPositionX = (WIDTH) - (toolTipTextBoxPositionW) - (toolTipTextBoxPositionH);;
		toolTipTextBoxPositionY = (HEIGHT) - (toolTipTextBoxPositionH*2);
		toolTipTextBoxPosition = new Position(this.gp,
				toolTipTextBoxPositionX,
				toolTipTextBoxPositionY,
				toolTipTextBoxPositionW,
				toolTipTextBoxPositionH);
		this.promptTextBox = new TextBox(this.gp, toolTipTextBoxPosition);
		//this.promptTextBox.backgroundColor =new Color(255, 200, 200, 150);
		this.promptTextBox.setTextContent(promptText);
	}
	
	
	public void toggleShowInfoText() {
		this.showInfotext = !this.showInfotext;
	}
	
	public void setShowDialogBox(boolean showIt) {
		showDialog = showIt;
	}
	
	public void draw() {
		if(gp.g2==null)return;
		try {
			leftCornerText();
			centerBottomMessageBox() ;
			healthBar();
			staminaBar();
			killCount();
			gemCount();
			drawStatusMessage();
			if(gp.player.getRun())runString.draw();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}

		if (showDialog) {
			dialogTextBox.draw();
			if(showNamePlate) {
				gp.g2.drawImage(images[2],nameplateX,nameplateY,NAMEPLATE_WIDTH,NAMEPLATE_HEIGHT,null);
				speakerString.draw();
			}
		}
		if(showPrompt) {
			promptTextBox.draw();
		}

		drawInventoryToolbar();
		
		
		// by default hide the text boxes unless another class needs them.
		
		//showPrompt=false; //press e
		//showDialog=false;
		
	}
	
	public void drawInventoryToolbar() {
		int tbWidth = ITEM_EQ_FRAME_SIZE;
		int tbHeight = ITEM_EQ_FRAME_SIZE;
		
		if(showEquippedItemFrame) {
			int backgroundImage = 0;
			if(showToolbar) {
				
				backgroundImage = 1;
				tbWidth = ITEM_EQ_FRAME_SIZE*10;
				gp.g2.drawImage(images[backgroundImage],itemEqBrcOffsetX,itemEqBrcOffsetY,
						tbWidth,tbHeight,null);
				gp.g2.setStroke(borderStroke);
				gp.g2.setColor(selectedBoxColorBG);
				gp.g2.fillRect(selectedBoxX, selectedBoxY, ITEM_EQ_FRAME_SIZE, ITEM_EQ_FRAME_SIZE);

				gp.g2.setColor(selectedBoxColor);
				gp.g2.drawRect(selectedBoxX, selectedBoxY, ITEM_EQ_FRAME_SIZE, ITEM_EQ_FRAME_SIZE);
				drawInventoryToolbarItemSprites();
				
			}else {
				gp.g2.drawImage(images[backgroundImage],itemEqBrcOffsetX,itemEqBrcOffsetY,
						tbWidth,tbHeight,null);
				if(itemEq>0) {

					gp.g2.drawImage(itemImages[itemEq],itemEqScreenX,itemEqScreenY,
							ITEM_EQ_ITEM_IMAGE_SIZE,ITEM_EQ_ITEM_IMAGE_SIZE,null);
				}
				
			}
			
			
		}
	}
	
	public void drawInventoryToolbarItemSprites() {
		
		int spriteAmount = 10;
		if (inventoryKindAmount.length<10) {
			spriteAmount=inventoryKindAmount.length;
		}
		int currX = itemEqScreenX;
		int currY = itemEqScreenY;
		for (int i = 0; i< spriteAmount;i++) {
			int imageID = inventoryKindAmount[i][0];
			gp.g2.drawImage(itemImages[imageID],currX,currY,
					ITEM_EQ_ITEM_IMAGE_SIZE,ITEM_EQ_ITEM_IMAGE_SIZE,null);
			currX +=ITEM_EQ_FRAME_SIZE;
		}
	}
	
	public void update() {
		
		if(this.g2==null) {
			this.g2 = gp.g2;
			
		}
		
		// check if player pressed to show inv toolbar
		if(toggleToolbar) {
			toggleToolbar = false;
			showToolbar =! showToolbar;
			inventoryKindAmount = gp.inventory.queryKindAndAmount();
			System.out.println("Show toolbar "+showToolbar);
			
		}
		
		if (showToolbar) {
			selectedBoxX = ITEM_EQ_OFFSET_X + (selectedSlot*ITEM_EQ_FRAME_SIZE);
			selectedBoxY = gp.getHeight() -  ITEM_EQ_OFFSET_Y - ITEM_EQ_FRAME_SIZE;
		}
		
		//lcText = "wX: "+ gp.player.worldX+" wY: "+ gp.player.worldY ;
		gp.clamp(1, 100, health);
		float green = (float) ((float)health/maxHealth * 255.0);
		this.health = gp.player.health;
		int red = (int) (400-green);
		red=gp.clamp(0, 254, red);
		green=gp.clamp(0, 254, (int)green);
		healthBarColor = new Color(red, (int)green, 100);
		
		
		healthText = "Health: "+health;
		lcText3 = Integer.toString(gp.player.worldX / gp.TILE_SIZE_PX);
		lcText4 = Integer.toString(gp.player.worldY / gp.TILE_SIZE_PX);
		//if (gp.enemy!=null)lcText5 = Integer.toString(gp.enemy.screenX);
		//if (gp.enemy!=null)lcText6 = Integer.toString(gp.enemy.screenY);
		statusMessageChangeTimeout--;

		dialogTextBox.update();
		promptTextBox.update();
		
		showPrompt = !showActionPromptDelay.delayExpired();
		showActionPromptDelay.reduce();
		handleMenuInput() ;
		
		
	}
	
	private void handleMenuInput() {
		if (showToolbar) {
			if (this.movesRequested[2]) {
				this.selectedSlot -=1;
			}else if(this.movesRequested[3]) {
				this.selectedSlot +=1;
			}
			selectedSlot = Utils.clamp(0,TOOLBAR_SLOTS,selectedSlot);
			try {
				itemEq = inventoryKindAmount[selectedSlot][0];
			}catch(ArrayIndexOutOfBoundsException e) {}
		}
		this.movesRequested[0]=false;
		this.movesRequested[1]=false;
		this.movesRequested[2]=false;
		this.movesRequested[3]=false;
	}
	
	//info text
	public void killCount() {
		if (this.killCount>0) {
			this.killCountString = "Kills "+ Integer.toString(killCount);
			gp.g2.setColor(Color.white);
			gp.g2.setFont(arial20);
			int xmax = gp.WIDTH;
			int textX = (xmax /20)*18 ;
			int textY = (int)(gp.HEIGHT /30)*4 ;
			gp.g2.drawString(killCountString, textX,	textY);
			
			
		}
	}
	
	public void gemCount() {
		if (this.gemCount>0) {
			this.gemCountString = "Gems "+ String.format("%05d",gemCount);
			gp.g2.setColor(Color.white);
			gp.g2.setFont(arial20);
			int xmax = gp.WIDTH;
			int textX = (xmax /20)*15 ;
			int textY = (int)(gp.HEIGHT /30)*6 ;
			gp.g2.drawString(gemCountString, textX,	textY);
			
			
		}
	}
	
	public void leftCornerText() {
		if (this.showInfotext==false)return;
		gp.g2.setColor(Color.yellow);
		int textX = 50;
		int textY = 75;
		int textY2 = 100;
		int textY3 = 125;
		lcText = "wX: "+ gp.player.worldX+" wY: "+ gp.player.worldY ;
		lcText2 = "TileX: "+ lcText3+" TileY: "+ lcText4;
		lcTextLine3 = "Esx: "+ lcText5+" Esy: "+ lcText6;
		String str = lcText;
		gp.g2.setFont(arial20);
		gp.g2.drawString(str, textX,	textY);
		gp.g2.drawString(lcText2, textX,	textY2);
		gp.g2.drawString(lcTextLine3, textX,	textY3);
		
	
		
	}
	public void healthBar() {
		int boxWidth = gp.WIDTH /5;
		float percHealth = (float)health/(float)maxHealth;
		int innerWidth =  (int) (boxWidth*percHealth);
		int boxHeight = gp.HEIGHT /26;
		int boxX = (gp.WIDTH /20) ;
		int boxY = (int)(gp.HEIGHT /30) ;
		gp.g2.setColor(smBorder);
		gp.g2.fillRect(boxX, boxY, boxWidth, boxHeight);
		gp.g2.setColor(healthBarColor);
		gp.g2.fillRect(boxX, boxY, innerWidth, boxHeight);
		
	}
	
	public void staminaBar() {
		int boxWidth = gp.WIDTH /5;
		float percHealth = (float)stamina/(float)maxHealth;
		int innerWidth =  (int) (boxWidth*percHealth);
		int boxHeight = gp.HEIGHT /26;
		int boxX = gp.WIDTH-(gp.WIDTH /20) -boxWidth ;
		int boxY = (int)(gp.HEIGHT /30) ;
		gp.g2.setColor(Color.gray);
		gp.g2.fillRect(boxX, boxY, boxWidth, boxHeight);
		gp.g2.setColor(Color.yellow);
		gp.g2.fillRect(boxX, boxY, innerWidth, boxHeight);
		
	}
	
	
	
	public int statusMessageChangeTimeout = 0; //decrement in update function
	public void setStatusMessage(String statusMessage) {
		//change the status message, with timeout to block outside functions 
		// from changing it too rapidly
		if (statusMessageChangeTimeout == 0)
		this.statusMessageText = statusMessage;
		statusMessageChangeTimeout = 60;
	}
	
	public void startDisplayStatusMessage( int delay) {
		if (statusMessageTimeout  <0)statusMessageTimeout=0;
		if (delay > 0) {
			statusMessageTimeout = delay;
			
		}
	}
	
	public int statusMessageTimeout = 0;
	private void drawStatusMessage(){
		// renders status message, not to be called by outside function
		if(statusMessageTimeout>0) {
			int boxWidth = gp.WIDTH /5;
			float percHealth = (float)stamina/(float)maxHealth;
			int innerWidth =  (int) (boxWidth*percHealth);
			int boxHeight = gp.HEIGHT /26;
			int boxX = gp.WIDTH-(gp.WIDTH /20) -boxWidth ;
			int boxY = gp.HEIGHT-(int)(gp.HEIGHT /30) -(boxHeight*2) ;
			gp.g2.setColor(Color.gray);
			gp.g2.fillRect(boxX, boxY, boxWidth, boxHeight);
			gp.g2.setColor(Color.black);
			gp.g2.fillRect(boxX, boxY, innerWidth, boxHeight);
			int textWidth = g2.getFontMetrics().stringWidth(statusMessageText);
			gp.g2.setColor(Color.white);
			gp.g2.drawString(this.statusMessageText, boxX+boxWidth/2 - textWidth/2,	boxY+boxHeight/2);
			statusMessageTimeout -=1;
		}
		
		
	}
	
	
	
	public void mboxText(String s) {
		//center lower message box
//		this.mboxTextVisible = !mboxTextVisible;
//		
//		this.mboxTextString1 = s;
//		this.mboxTextString2 = ("up_blocked "+gp.enemy.up_blocked);
//		this.mboxTextString3 = ("down_blocked "+gp.enemy.down_blocked);
//		this.mboxTextString4 = ("left_blocked "+gp.enemy.left_blocked);
//		this.mboxTextString5 = ("right_blocked "+gp.enemy.right_blocked);
		System.out.println("mboxText");
	}
	
	public void centerBottomMessageBox() {
		
		if (mboxTextVisible) {
			//System.out.println("test");
			int boxWidth = 300;
			int boxHeight = 100;
			int boxX = (gp.WIDTH /2) - (boxWidth/2);
			int boxY = (int)(gp.HEIGHT *0.75) - (boxHeight/2);
			int strokeW = 2;
			gp.g2.setStroke(new BasicStroke(strokeW));
			gp.g2.setColor(smBorder);
			gp.g2.drawRect(boxX, boxY, boxWidth, boxHeight);
			gp.g2.setColor(smBackground);
			gp.g2.fillRect(boxX, boxY, boxWidth, boxHeight);
			gp.g2.setColor(Color.white);
			gp.g2.drawString(mboxTextString1, boxX+strokeW*2,	boxY+(strokeW*8));
			gp.g2.drawString(mboxTextString2, boxX+strokeW*2,	boxY+(strokeW*16));
			gp.g2.drawString(mboxTextString3, boxX+strokeW*2,	boxY+(strokeW*24));
			gp.g2.drawString(mboxTextString4, boxX+strokeW*2,	boxY+(strokeW*32));
			gp.g2.drawString(mboxTextString5, boxX+strokeW*2,	boxY+(strokeW*40));
			
		}else {
			//System.out.println("no mboxText");
		}

		
		
	}
	@Override
	public void triggerAction(String eventname) {
	
		if (eventname.contentEquals("status_show")) {
			startDisplayStatusMessage(60);
		}else if (eventname.contentEquals("door")) {
			this.statusMessageText = "Door";
		}
			startDisplayStatusMessage(60);
		
	}
	public void newStatusMessage(String message) {
		this.statusMessageText = message;
		startDisplayStatusMessage(60);
	}

	public void toolbarModeToggle() {
		toggleToolbar=true;
		gp.player.frozen =! gp.player.frozen;
		
	}

	@Override
	public void inputListenerAction(InputAction action) {

		switch (action) {
		case UP:
			this.movesRequested[0] = true;
			break;
		case DOWN:
			this.movesRequested[1] = true;
			break;
		case LEFT:
			this.movesRequested[2] = true;
			break;
		case RIGHT:
			this.movesRequested[3] = true;
			break;
		case UPSTOP:
			// System.out.println("up");
			this.movesRequested[0] = false;
			break;
		case DOWNSTOP:
			this.movesRequested[1] = false;
			break;
		case LEFTSTOP:
			this.movesRequested[2] = false;
			break;
		case RIGHTSTOP:
			this.movesRequested[3] = false;
			break;
		case FIRE:
			break;
		case ACTION:
			gp.inventory.selectItem(itemEq);
			System.out.println("select item "+itemEq);
			break;

		default:
			break;
		}

	}

}
