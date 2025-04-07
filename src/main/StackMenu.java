package main;

import java.awt.Font;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.GamePanel.GameState;

public class StackMenu implements IClickableElement{
	private final int DEFAULT_BUTTON_AMOUNT = 7;
	private final int BUTTON_WIDTH = 200;
	private final int BUTTON_HEIGHT = 50;
	private final int BUTTON_SPACING_Y = 10;
	private final int STACK_Y_ADJUSTMENT = -100;
	private final int FONTISPIECE_Y_ADJUSTMENT = 25;
	private final int FONTISPIECE_X_ADJUSTMENT = -10;
	private Font arial20;

	private final String fileURL0 = "/images/title.png";
	private final String fileURL1 = "/images/paused.png";
	
	GamePanel gp;
	private static String[] mainMenuLabels = new String[] {
			"Continue","New Game","Load Game","Options","Quit"
	};
	private static String[] inGameMenuLabels = new String[] {
			"Continue","Save Game","Load Game","Options","Quit"
	};
	private static String[] optionsMenuLabels = new String[] {
			"Music","SFX Volume","Day Cycle","Autosave","Exit"
	};
	private static String[] storeMenuLabels = new String[] {
			"Buy Items","Sell Items","Talk","Exit"
	};
	private static String[] responseLabels = new String[] {
			"Yes","No","Not sure"
	};
	
	String[] currentButtonStrings;
	Button[] buttons ;
	
	BufferedImage fontispiece;
	int fontispieceX,fontispieceY,fontispieceW,fontispieceH;
	
	int screenX,screenY;
	int stackHeight;
	boolean visible;
	int lastSelected = 0;
	int buttonsAmount;
	int kind;
	// @formatter:off
		/*
		 * 
		 * stack menu types:
		 * 0 = main menu
		 * 1 = ingame menu
		 * 2 = options
		 * 3 = options 2
		 * 4 = shop
		 * 5 = shop2
		 * 6 = question
		
		 * 
		 */
		// @formatter:on
	private boolean playerClicked;
	private int[] mouseClickData;
	
	public StackMenu(GamePanel gp, int kind) {
		this.gp=gp;
		this.kind=kind;
		this.buttons = new Button[DEFAULT_BUTTON_AMOUNT];
		arial20 = new Font("Arial",Font.BOLD,20);
		initImages();
		setButtonStackPosition();
		mouseClickData = new int[3];
		initButtons();
		stackHeight=calcStackHeight();
		//System.out.println("stackheight "+stackHeight);
		repositionButtons();
		setButtonStackPosition();
		gp.clickableElements.add(this);
		
		
	}
	
	private void initImages() {
		fontispiece = null;
		String fileURL = null;
		switch(kind) {
		case 0:
			fileURL = fileURL0;
			break;
		case 1:
			fileURL = fileURL1;
			break;
		default:
			return;
		}
		try {
			fontispiece = ImageIO.read(getClass().getResourceAsStream(fileURL));
			fontispieceW = fontispiece.getWidth();
			fontispieceH = fontispiece.getHeight();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private int getButtonYOffset(int buttonID) {
		int leadingButtons = buttonID;
		return leadingButtons * (BUTTON_HEIGHT+BUTTON_SPACING_Y);
		
	}
	
	private void setButtonStackPosition() {
		this.screenX = (GamePanel.WIDTH / 2) - (BUTTON_WIDTH/2); 
		this.screenY = (GamePanel.HEIGHT / 2) - (stackHeight/2) + STACK_Y_ADJUSTMENT; 
		this.fontispieceX = (GamePanel.WIDTH / 2) - (this.fontispieceW/2) + FONTISPIECE_X_ADJUSTMENT;
		this.fontispieceY = ((GamePanel.HEIGHT / 2) - (stackHeight/2) - fontispieceH) + FONTISPIECE_Y_ADJUSTMENT ;
		//System.out.println("fontispieceY "+(screenY+ STACK_Y_ADJUSTMENT));
	}
	private void assignLabels(String[] labels) {
		if(null==buttons)System.err.println("StackMenu tried assigning labels to buttons, but the button list is null");
		int lastIndex = (labels.length<buttonsAmount)?labels.length:buttonsAmount;
		for(int i = 0;i< lastIndex;i++) {
			buttons[i].setLabel(labels[i]);
		}
		
	}
	
	private int calcStackHeight() {
		return((BUTTON_HEIGHT+BUTTON_SPACING_Y)*buttonsAmount)-BUTTON_SPACING_Y;
	}
	
	private void repositionButtons() {
		for(int i = 0; i< buttonsAmount;i++) {
			int x = screenX;
			int y = screenY + getButtonYOffset(i);
		
			buttons[i].x=x;
			buttons[i].y=y;
		}
	}
	

	
	private void initButtons() {
		this.currentButtonStrings = switch (kind) {
		case 0 -> mainMenuLabels;
		case 1 -> inGameMenuLabels;
		case 2 -> optionsMenuLabels;
		case 3 -> optionsMenuLabels;
		case 4 -> storeMenuLabels;
		case 5 -> storeMenuLabels;
		case 6 -> responseLabels;
		default -> responseLabels;
		};
		
		buttonsAmount=this.currentButtonStrings.length;
		if(buttonsAmount>this.buttons.length)this.buttons=new Button[buttonsAmount];
		
		for(int i = 0; i< buttonsAmount;i++) {
			int x = screenX;
			int y = screenY + getButtonYOffset(i);
			int w = BUTTON_WIDTH;
			int h = BUTTON_HEIGHT;
			buttons[i]=new Button(gp,x,y,w,h);
			buttons[i].setLabel(currentButtonStrings[i]);
			buttons[i].id=i;
		}
		
		assignLabels(currentButtonStrings);
		
	}
	
	
	
	public void draw() {
		if(!visible)return;
		//GamePanel.g2.drawRect(screenX, screenY, BUTTON_WIDTH, stackHeight);
		
		for(Button b: buttons) {
			if (null!=b) {
				b.draw();
			}
		}
		if(kind==0||kind==1) {
			if(fontispiece!=null) {
				GamePanel.g2.drawImage(fontispiece,fontispieceX, fontispieceY, fontispieceW, fontispieceH,null);
			}
		}
	}
	
	private boolean isActive() {
		switch (kind){
		case 0:
			if (gp.gameState==GameState.MENU)return true;
			break;
		case 1:
			if (gp.gameState==GameState.PAUSED)return true;
			break;
		case 2:
			if (gp.gameState==GameState.OPTION)return true;
			break;
		case 3:
			if (gp.gameState==GameState.OPTION)return true;
			break;
		}
		
		return false;
		
	}
	
	public void update() {
		if(isActive() ) {
			visible=true;
			if(playerClicked) {
				playerClicked=false;
				handleClickData();
			}
		}else {
			visible=false;
		}
		for(Button b: buttons) {
			if (null!=b) b.update();
		
		}
		
	}
	private void handleClickData() {
		System.out.println("player clicked inventory");
		Point clickPoint = new Point(mouseClickData[1],mouseClickData[2]);
		for (Button button:buttons) {
			if(null!=button&&button.contains(clickPoint)) {
				System.out.printf("Button number %d pressed \n",button.id);
				handleMenuButtonPress(button.id);
			}
		}
	}
	private void handleMenuButtonPress(int id) {
		switch (gp.gameState) {
		case MENU:
			switch (id) {
			case 0:
				gp.gameState=GameState.PLAY;
				break;
			case 1:
				gp.gameState=GameState.PLAY;
				break;
			case 2:
				gp.gameState=GameState.PLAY;
				break;
			case 3:
				gp.gameState=GameState.PLAY;
				break;
			case 4:
				gp.gameThread.interrupt();
				System.exit(0);
				
				break;
			}
		break;	
		case PAUSED:
			switch (id) {
			case 0:
				gp.gameState=GameState.PLAY;
				break;
			case 1:
				gp.gameState=GameState.PLAY;
				break;
			case 2:
				gp.gameState=GameState.PLAY;
				break;
			case 3:
				gp.gameState=GameState.PLAY;
				break;
			case 4:
				gp.gameThread.interrupt();
				System.exit(0);
				
				break;
			}
		break;
		default:
			break;
		}
		
	}

	public void click(int kind, int mouseX, int mouseY) {
		if(visible) {
			playerClicked = true;
			mouseClickData[0]=kind;
			mouseClickData[1]=mouseX;
			mouseClickData[2]=mouseY;
		}
		
	}

}
