package main;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Button extends Rectangle{
	private static final long serialVersionUID = 1L;
	GamePanel gp; 
	//int x,  y,  width, height;
	int kind = 0;
	private Color highlight;
	private final String SPRITE_SHEET_URL = "/images/buttons2.png";
	private static BufferedImage[] buttonImages; 
	private final int buttonHeightSS = 50;
	private final int buttonWidthSS = 200;
	public String label;
	public int id;
	
		
	public Button(GamePanel gp, int screenX, int screenY, int width, int height) {
		
		super(screenX,   screenY,   width,   height);
		if(width==0||height==0) {
			throw new IllegalArgumentException("Button: dimension cannot be 0");
		}
		this.gp = gp;
		initImages();
	}
	
	private void initImages() {
		if (buttonImages==null) {
			try {
				buttonImages = (new Utils()).spriteSheetCutter(SPRITE_SHEET_URL, 1, 4, buttonWidthSS, buttonHeightSS);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	

	public void draw() {

		GamePanel.g2.drawRect(this.x, this.y, width, height);
		GamePanel.g2.drawImage(buttonImages[kind], x, y, width, height, null);
		
		}
	
	public void update() {
		
		
	}

}
