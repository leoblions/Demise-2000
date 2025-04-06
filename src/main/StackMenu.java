package main;

import java.awt.BasicStroke;

import main.GamePanel.GameState;

public class StackMenu {
	private final int DEFAULT_BUTTON_AMOUNT = 7;
	private final int BUTTON_WIDTH = 200;
	private final int BUTTON_HEIGHT = 50;
	private final int BUTTON_SPACING_Y = 10;
	GamePanel gp;
	private static String[] mainMenuLabels = new String[] {
			"Continue","New Game","Load Game","Options","Exit"
	};
	private static String[] inGameMenuLabels = new String[] {
			"Continue","Save Game","Load Game","Options","Exit"
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
	
	public StackMenu(GamePanel gp, int kind) {
		this.gp=gp;
		this.kind=kind;
		this.buttons = new Button[DEFAULT_BUTTON_AMOUNT];
		setButtonStackPosition();
		
		initButtons();
		stackHeight=calcStackHeight();
		System.out.println("stackheight "+stackHeight);
		repositionButtons();
		setButtonStackPosition();
		
	}
	
	private int getButtonYOffset(int buttonID) {
		int leadingButtons = ((buttonID - 1>=0)?buttonID-1:0);
		return leadingButtons * (BUTTON_HEIGHT+BUTTON_SPACING_Y);
		
	}
	
	private void setButtonStackPosition() {
		this.screenX = (GamePanel.WIDTH / 2) - (BUTTON_WIDTH/2); 
		this.screenY = (GamePanel.HEIGHT / 2) - (stackHeight/2); 
	}
	private void assignLabels(String[] labels) {
		if(null==buttons)System.err.println("StackMenu tried assigning labels to buttons, but the button list is null");
		int lastIndex = (labels.length<buttonsAmount)?labels.length:buttonsAmount;
		for(int i = 0;i< lastIndex;i++) {
			buttons[i].label=labels[i];
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
			buttons[i].label=currentButtonStrings[i];
			buttons[i].id=i;
		}
		
		assignLabels(currentButtonStrings);
		
	}
	
	
	
	public void draw() {
		if(!visible)return;
		GamePanel.g2.drawRect(screenX, screenY, BUTTON_WIDTH, stackHeight);
		
		for(Button b: buttons) {
			if (null!=b) {
				b.draw();
			}
		}
	}
	
	public void update() {
		if(gp.gameState==GameState.MENU) {
			visible=true;
		}else {
			visible=false;
		}
		for(Button b: buttons) {
			if (null!=b) b.update();
		
		}
		
	}

}
