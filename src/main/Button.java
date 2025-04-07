package main;

import java.awt.Color;
import java.awt.Font;
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
	public BufferedImage icon;
	public int iconSize = 40;
	private final int buttonHeightSS = 50;
	private final int buttonWidthSS = 200;
	public int textOffsetY = 30;
	public int textOffsetX = 20;
	public final int letterX = 11;
	private String label;
	public int id;
	private Font arial20;
	
		
	public Button(GamePanel gp, int screenX, int screenY, int width, int height) {
		
		super(screenX,   screenY,   width,   height);
		if(width==0||height==0) {
			throw new IllegalArgumentException("Button: dimension cannot be 0");
		}
		this.gp = gp;
		initImages();
		arial20 = new Font("Arial",Font.BOLD,20);
	}
	
	public static Button InventoryButton(GamePanel gp, int screenX, int screenY, int width, int height) {
		Button ib = new Button(gp,screenX,screenY,width,height);
		ib.kind=1;
		return ib;
	}
	
	public void setLabel(String label) {
		this.label=label;
		this.textOffsetX = (buttonWidthSS/2) - (label.length()*letterX)/2;
	}
	
	public String getLabel(   ) {
		return this.label;
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
	
	public Button setID(int id) {
		this.id=id;
		return this;
	}
	

	public void draw() {

		GamePanel.g2.drawRect(this.x, this.y, width, height);
		GamePanel.g2.drawImage(buttonImages[kind], x, y, width, height, null);
		if (kind==0) {
			GamePanel.g2.setColor(Color.BLACK);
			GamePanel.g2.setFont(arial20);
			GamePanel.g2.drawString(label, x + textOffsetX, y + textOffsetY);
		}else if(kind==1) {
			GamePanel.g2.drawImage(icon, x + textOffsetX, y + textOffsetY,iconSize,iconSize,null);
		}
		}
	
	public void update() {
		
		
	}

}
