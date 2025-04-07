package main;

import java.awt.Color;
import java.awt.event.KeyEvent;

import main.GamePanel.GameState;

public class Console {
	/*
	 * Activate the dev console with `
	 * parses single or multi part commands to activate cheats and some editing tasks
	 */
	GamePanel gp;
	private boolean active=false;

	private boolean isReset=false;
	boolean allowed;
	String text;
	String lastCommand;
	Color background = new Color(100,100,100,150);
	Color frame = new Color(100,200,100,150);
	Color fontColor = Color.white;
	int height, width, screenX, screenY;
	int currentLineY, prevLineY;
	private final int Y_SPACING = 15;
	private final String STRING_SEPARATOR = " ";
	public Console(GamePanel gp) {
		this.gp=gp;
		allowed =true;
		active=false;
		height = 50;
		width = 300;
		screenX = 150;
		screenY = 500;
		text = "";
		lastCommand = "Dev console:";
		currentLineY = (screenY +height - Y_SPACING);
		prevLineY = screenY +height - (2*Y_SPACING);
		
	}
	
	public void draw() {
		if(active) {
			gp.g2.setColor(background);
			gp.g2.fillRect(screenX, screenY, width, height);
			gp.g2.setColor(frame);
			gp.g2.drawRect(screenX, screenY, width, height);
			gp.g2.setColor(fontColor);
			gp.g2.drawString(lastCommand, screenX, prevLineY);
			gp.g2.drawString(text, screenX, currentLineY);
		}
		
		}
	private void runCommand(String command) {
		String[] splitCommand = command.split(STRING_SEPARATOR);
		int wordAmount = splitCommand.length;
		System.out.println("The command is "+command);
		int kind;
		if(wordAmount==0) {
			System.out.println("Err: command evaluated to zero symbols");
			return;
		}else {
			switch (splitCommand[0]) {
			case "HEAL":
				gp.player.fullHeal();
				break;
			case "TAI":
				gp.entityManager.frozen =!gp.entityManager.frozen ;
				System.out.printf("Entity frozen %b\n",gp.entityManager.frozen);
				break;
			case "WARP":
				if(wordAmount!=2) break;
				int warpID = Integer.parseInt(splitCommand[1]);
				System.out.println("Warp to ID "+warpID);
				gp.warp.warpToID(warpID);
				break;
			case "WARPLOC":
				if(wordAmount!=3) break;
				int level = Integer.parseInt(splitCommand[1]);

				int gridX = Integer.parseInt(splitCommand[2]);

				int gridY = Integer.parseInt(splitCommand[3]);
				gp.warp.warpToLocation(level, gridX, gridY);
				break;
			case "FILLTILE":
				 kind = Integer.parseInt(splitCommand[1]);
				gp.tileManager.fillTile(kind);
				break;
			case "ADDITEM":
				 kind = Integer.parseInt(splitCommand[1]);
				gp.inventory.addItem(kind, 1);
				break;
			case "TILE":
				gp.editor.editMode=EditMode.TILE;
				if(wordAmount!=2) break;
				kind = Integer.parseInt(splitCommand[1]);
				gp.editor.setAssetID(  kind) ;
				break;
			case "DECOR":
				gp.editor.editMode=EditMode.DECOR;
				if(wordAmount!=2) break;
				kind = Integer.parseInt(splitCommand[1]);
				gp.editor.setAssetID(  kind) ;
				break;
			case "BARRIER":
				gp.editor.editMode=EditMode.BARRIER;
				if(wordAmount!=2) break;
				kind = Integer.parseInt(splitCommand[1]);
				gp.editor.setAssetID(  kind) ;
				break;
			case "WIDGET":
				gp.editor.editMode=EditMode.WIDGET;
				if(wordAmount!=2) break;
				kind = Integer.parseInt(splitCommand[1]);
				gp.editor.setAssetID(  kind) ;
				break;
			case "ENTITY":
				gp.editor.editMode=EditMode.ENTITY;
				if(wordAmount!=2) break;
				kind = Integer.parseInt(splitCommand[1]);
				gp.editor.setAssetID(  kind) ;
				break;
			case "GRID":
				
				String locationString = String.format("Player gridX: %d, gridY: %d\n",gp.player.tilePlayer[0],gp.player.tilePlayer[1]);
				System.out.println(locationString);
				break;
			case "LOC":
				String locationStringM = String.format("Player worldX: %d, worldY: %d\n",gp.player.worldX,gp.player.worldY);
				System.out.println(locationStringM);
				break;
			case "LEVEL":
				String levelString = String.format("Current level is: %d\n",gp.level);
				System.out.println(levelString);
				break;
				
			default:
				System.err.printf("M No command found: %s\n",command);
			}
		}
		
	}
	
	private void runCommand_0(String command) {
		String[] splitCommand = command.split(STRING_SEPARATOR);
		int wordAmount = splitCommand.length;
		System.out.println("The command is "+command);
		
		switch (wordAmount) {
		case 0:
			System.out.println("The command was empty");
			break;
		case 1:
			switch(splitCommand[0]) {
			case "HEAL":
				gp.player.fullHeal();
				break;
			case "TAI":
				gp.entityManager.frozen =!gp.entityManager.frozen ;
				System.out.printf("Entity frozen %b\n",gp.entityManager.frozen);
				break;
			default:
				System.err.printf("0 No command found: %s\n",splitCommand[0]);
			}
			break;
		case 2:
			break;
			
		default:
			System.err.printf("M No command found: %s\n",command);
		}
	}
	
	public void sendKeyEvent(KeyEvent e) {
		if(!active) {
			return;
		}
		String test = KeyEvent.getKeyText(e.getKeyCode());
		switch(test) {
		case "Backspace":
			int len = text.length();
			if (len>0) {
				text = text.substring(0, len-1);
				break;
			}
			break;
		case "Enter":
			runCommand(text);
			lastCommand = text;
			text = "";
			break;
		case "Space":
			text += " ";
			break;
		case "Up":
			text = "" + lastCommand;
			break;
		case "Down":
			text = "" ;
			break;
		case "Back Quote":
			this.active=false;
			this.isReset = false;
			break;
		default:
			text += test;
			break;
		}
	}
	
//	public void requestActivate() {
//	if(!active && isReset) {
//
//		gp.gameState=GameState.PAUSED;
//		active = true;
//	}else {
//		gp.gameState=GameState.PLAY;
//	}
//	}
	
	public void update() {
		//isReset = true;
		if(gp.gameState==GameState.CONSOLE) {
			active=true;
		}else {
			active=false;
		}
		
	}

//	public void toggleDevConsole() {
//		if(!active) {
//			gp.gameState=GameState.PAUSED;
//			this.active = true;
//		}else {
//			gp.gameState=GameState.PLAY;
//		}
//		
//	}

}
