package main;

import java.awt.Color;
import java.awt.event.KeyEvent;

public class Console {
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
				gp.entityManager.entityAIEnabled =!gp.entityManager.entityAIEnabled ;
				System.out.printf("Entity enabled %b\n",gp.entityManager.entityAIEnabled);
				break;
			default:
				System.err.printf("0 No command found: %s\n",splitCommand[0]);
			}
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
	
	public void requestActivate() {
	if(!active && isReset) {
		active = true;
	}
	}
	
	public void update() {
		isReset = true;
		
	}

	public void toggleDevConsole() {
		if(!active) {

			this.active = true;
		}
		
	}

}
