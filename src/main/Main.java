package main;

import java.io.IOException;

import javax.swing.JFrame;

public class Main {
	
	public final static String GAME_TITLE = "DEMISE 2000";
	
	public static void main(String[] args) {
		JFrame jframe = new JFrame(GAME_TITLE);
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setResizable(false);
		
		//add gamepanel to window
		
		GamePanel gp = new GamePanel();
		jframe.add(gp);
		jframe.pack();
		
		jframe.setLocationRelativeTo(null);
		jframe.setVisible(true);
		
		
		
		
	}

}
