package main;

import java.io.IOException;

import javax.swing.JFrame;

public class Main {
	
	public final static String GAME_TITLE = "BOMB TOWN";
	public final static String DEFAULT_TITLE = "DEMISE 2000";
	static JFrame jframe;
	
	public static void main(String[] args) {
		jframe = new JFrame(GAME_TITLE);
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setResizable(false);
		
		//add gamepanel to window
		
		GamePanel gp = new GamePanel();
		gp.parentFrame = jframe;
		setFrameTitle(gp.parentFrameTitle);
		
		jframe.add(gp);
		jframe.pack();
		
		jframe.setLocationRelativeTo(null);
		jframe.setVisible(true);
		
		
		
		
	}
	
	public static void setFrameTitle(String title) {
		jframe.setTitle(title);
	}

}
