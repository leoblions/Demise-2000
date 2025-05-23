package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import main.GamePanel.GameState;
import main.GamePanel.InputAction;

public class Input implements KeyListener{
	ArrayList<IInputListener> listeners = new ArrayList<>();
	public boolean upPressed, downPressed, leftPressed, rightPressed;
	GamePanel gp;
	
	public Input(GamePanel gp) {
		this.gp=gp;
		this.addListener(gp.player);

		this.addListener(gp.hud);
		System.out.println("input created");
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public void addListener(IInputListener listener) {
		this.listeners.add(listener);
		
	}
	public void notifyListeners(InputAction action) {
		for(IInputListener listener: listeners) {
			listener.inputListenerAction( action);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		gp.console.sendKeyEvent(e);
		if (true) {
			
			switch(key) {
			case KeyEvent.VK_UP:
			case KeyEvent.VK_W:
				notifyListeners(InputAction.UP);
			//System.out.println("kp W");
				break;
			case KeyEvent.VK_DOWN :
			case KeyEvent.VK_S:
				notifyListeners(InputAction.DOWN);
				//gp.player.screenY += 5;
				break;
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_A:
				notifyListeners(InputAction.LEFT);
				break;
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_D:
				notifyListeners(InputAction.RIGHT);
				break;
			case KeyEvent.VK_F :
				notifyListeners(InputAction.FIRE);
				break;
			case KeyEvent.VK_SPACE:
				notifyListeners(InputAction.MESSAGE);
				break;
			case KeyEvent.VK_E:
				gp.playerPressActivate = true;
				notifyListeners(InputAction.ACTION);
				break;
			case KeyEvent.VK_SHIFT:
				notifyListeners(InputAction.RUN);
				break;
			
			case KeyEvent.VK_M:
				notifyListeners(InputAction.MUTE);
				break;
			case KeyEvent.VK_Q:
				gp.toggleMode(GameState.TOOLBAR);
//				if(gp.gameState==GameState.PLAY||gp.gameState==GameState.INVENTORYSCREEN||gp.gameState==GameState.TOOLBAR) {
//
//
//					gp.hud.toolbarModeToggle();
//				}
//				
				break;
			case KeyEvent.VK_I:
				
				gp.toggleMode(GameState.INVENTORY);
//				if(gp.gameState==GameState.PLAY||gp.gameState==GameState.INVENTORYSCREEN) {
//
//					notifyListeners(InputAction.INVENTORY);
//					gp.hud.toggleInventoryScreen();
//				}
				
				break;
			case KeyEvent.VK_ESCAPE:
				if(GamePanel.inGameMenu==1) {
					gp.toggleMode(GameState.PAUSED);
				}else {

					gp.toggleInGameMenu();
				}
				break;
			case KeyEvent.VK_BACK_QUOTE:
				gp.toggleMode(GameState.CONSOLE);
//				if(gp.gameState==GameState.PLAY||gp.gameState==GameState.INVENTORYSCREEN||gp.gameState==GameState.PAUSED) {
//
//
//					gp.console.requestActivate();
//				}
//				
				break;
			case KeyEvent.VK_NUMPAD0:
			case KeyEvent.VK_HOME:
				gp.editor.toggleEditMode();
				break;
			case 107:
				gp.editor.toggleEditDeleteMode();
				break;
			case KeyEvent.VK_DECIMAL:
				gp.editor.toggleLatch();
				break;
			case KeyEvent.VK_F1:
				gp.saveComponentData();
				break;
			case KeyEvent.VK_F2:
				gp.loadComponentData();
				break;
			default:
				System.out.println("Input no action " + e.getKeyCode());
				;
				//notifyListeners("playermove_nokeys");
			}
		}
		
		
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// when keys are released
		int key = e.getKeyCode();
		switch(key) {
		case KeyEvent.VK_UP:
		case KeyEvent.VK_W:
			notifyListeners(InputAction.UPSTOP);
		//System.out.println("kp W");
			break;
		case KeyEvent.VK_DOWN:
		case KeyEvent.VK_S:
			notifyListeners(InputAction.DOWNSTOP);
			//gp.player.screenY += 5;
			break;
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_A:
			notifyListeners(InputAction.LEFTSTOP);
			break;
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_D:
			notifyListeners(InputAction.RIGHTSTOP);
			break;
		case KeyEvent.VK_E:
			notifyListeners(InputAction.ACTIONRELEASE);
			break;
		}
		
	}

}
