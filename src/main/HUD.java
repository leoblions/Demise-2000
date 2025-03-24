package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class HUD implements IStatusMessageListener {
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
	public static final String SINGLE_INVENTORY = "/images/invHudSingle.png";
	public static final int ITEM_EQ_OFFSET_X = 10;
	public static final int ITEM_EQ_OFFSET_Y = 10;
	public static final int ITEM_EQ_FRAME_SIZE = 70;
	public static int itemEqBrcOffsetY = 10;
	public static boolean showEquippedItemFrame = true;
	
	// text boxes
	private boolean showDialog = false;
	private boolean showPrompt = false;
	public Delay showActionPromptDelay = new Delay();
	
	TextBox dialogTextBox, promptTextBox;
	Position dialogTextBoxPosition, toolTipTextBoxPosition;
	RasterString runString;
	BufferedImage[] images;
	
	
	int alpha = 127;
	
	Color smBackground = new Color(100, 100, 100, alpha);
	Color smBorder = new Color(50, 50, 50, alpha);
	Color healthBarColor = new Color(50, 200, 50);
	
	private static boolean mboxTextVisible ;
	private static int mboxTextVisibleTimeout =60;
	private static String mboxTextString1,
	mboxTextString2,mboxTextString3,mboxTextString4,mboxTextString5;
	
	public HUD(GamePanel gp) {
		if(gp==null) System.out.println("HUD ctor received null reference 1") ;
		if(g2==null) System.out.println("HUD ctor received null reference 2") ;
		this.gp = gp;
		
		int runStringX = gp.WIDTH - 60;
		int runStringY = 50;
		runString = new RasterString(gp, "RUN", runStringX, runStringY);
		runString.visible=true;
		arial16 = new Font("Arial",Font.PLAIN,16);
		arial20 = new Font("Arial",Font.BOLD,20);
		//mboxTextVisible=false;
		itemEqBrcOffsetY = GamePanel.HEIGHT -ITEM_EQ_OFFSET_Y -ITEM_EQ_FRAME_SIZE; // y position of equipped item frame
		initDialogTextBox();
		initImages();
	}
	
	private void initImages() {
		try {

			this.images = new BufferedImage[5];
			this.images[0] = ImageIO.read(getClass().getResourceAsStream(SINGLE_INVENTORY));
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initDialogTextBox() {
		int HEIGHT  = GamePanel.HEIGHT;
		int WIDTH = GamePanel.WIDTH;
		int dialogTextBoxPositionH = HEIGHT / 10;
		int dialogTextBoxPositionW = WIDTH/2;
		int dialogTextBoxPositionX = (WIDTH/2) - (dialogTextBoxPositionW/2);
		int dialogTextBoxPositionY = (HEIGHT) - (dialogTextBoxPositionH*2);
		dialogTextBoxPosition = new Position(this.gp,
				dialogTextBoxPositionX,
				dialogTextBoxPositionY,
				dialogTextBoxPositionW,
				dialogTextBoxPositionH);
		this.dialogTextBox = new TextBox(this.gp, dialogTextBoxPosition);
		this.dialogTextBox.backgroundColor =new Color(200, 200, 200, 100);
		
		int toolTipTextBoxPositionH = HEIGHT / 20;
		int toolTipTextBoxPositionW = WIDTH/6;
		int toolTipTextBoxPositionX = (WIDTH) - (toolTipTextBoxPositionW) - (toolTipTextBoxPositionH);;
		int toolTipTextBoxPositionY = (HEIGHT) - (toolTipTextBoxPositionH*2);
		toolTipTextBoxPosition = new Position(this.gp,
				toolTipTextBoxPositionX,
				toolTipTextBoxPositionY,
				toolTipTextBoxPositionW,
				toolTipTextBoxPositionH);
		this.promptTextBox = new TextBox(this.gp, toolTipTextBoxPosition);
		this.promptTextBox.backgroundColor =new Color(255, 200, 200, 150);
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
		}
		if(showPrompt) {
			promptTextBox.draw();
		}
		if(showEquippedItemFrame) {
			gp.g2.drawImage(images[0],ITEM_EQ_OFFSET_X,itemEqBrcOffsetY,ITEM_EQ_FRAME_SIZE,ITEM_EQ_FRAME_SIZE,null);
		}
		
		// by default hide the text boxes unless another class needs them.
		
		//showPrompt=false; //press e
		//showDialog=false;
		
	}
	
	public void update() {
		
		if(this.g2==null) {
			this.g2 = gp.g2;
			
		}
		//lcText = "wX: "+ gp.player.worldX+" wY: "+ gp.player.worldY ;
		gp.clamp(1, 100, health);
		float green = (float) ((float)health/maxHealth * 255.0);
		this.health = gp.player.health;
		int red = (int) (400-green);
		//int red=100;
		red=gp.clamp(0, 254, red);
		green=gp.clamp(0, 254, (int)green);
		//System.out.println(green);
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

}
