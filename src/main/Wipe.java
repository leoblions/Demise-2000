package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

public class Wipe {
	GamePanel gp;
	boolean wipeActive = false;
	int activeWipeType;
	int activeWipeDuration;
	int activeWipeTicks;
	int centerX, centerY;
	int wipeDirection;
	int screenX,screenY;
	int width, height;
	int mode,progress;
	
	private final int IMAGE_CHANGE_AMOUNT = 30;

	private final int CHANGE_RATE = 10;

	Pacer changePacer = new Pacer(CHANGE_RATE);
	Polygon UP,DP,LP,RP;
	
	int[] UXpoints, DXpoints,LXpoints,RXpoints;
	int[] UYpoints, DYpoints,LYpoints,RYpoints;
	
	private final int MAX_PROGRESS = 250;
	private final Color CLEAR = new Color(0xff,0xff,0xff,0x00);

	private final Color BG = new Color(0x00,0x00,0x00,0xff);
	private final int BUFFERED_IMAGE_TYPE = 6;

	//private final Color CLEAR = Color.GREEN;
	BufferedImage wipeOverlay, originalCopy, shadow;
	
	public Wipe(GamePanel gp) {
		this.gp=gp;
		
		
	}
	
	public void start() {
		height = gp.getHeight();
		width = gp.getWidth();
		centerX = width/2;
		centerY = height/2;

		copyScreen();
		wipeActive = true;
		shadow = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics shadowg = shadow.getGraphics();
		shadowg.setColor(Color.black);
		shadowg.fillRect(0, 0, width, height);
	}
	

	
	public void reset() {
		wipeActive = false;
		screenX = 0;
		screenY = 0;
		progress = 0;
	}
	
	private void copyScreen() {

		height = gp.getHeight();
		width = gp.getWidth();
		BufferedImage g2copy = new BufferedImage(width,height, BufferedImage.TYPE_INT_ARGB) ;
		Graphics copyGraphics = g2copy.getGraphics();
		gp.printAll(copyGraphics);
		wipeOverlay = g2copy;
		originalCopy = deepCopy(g2copy);
		shadow = deepCopy(g2copy);
	}
	
	static BufferedImage deepCopy(BufferedImage bi) {
		 ColorModel cm = bi.getColorModel();
		 boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		 WritableRaster raster = bi.copyData(null);
		 return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
		}
	
	public void draw() {
		if(wipeActive) {
			gp.g2.drawImage(wipeOverlay, screenX, screenY, width, height, null);
			
		}
		}
	
	
	
	public void shadeOut() {
		wipeOverlay = new BufferedImage(width, height, BUFFERED_IMAGE_TYPE);
		Graphics wol = wipeOverlay.getGraphics();
		wol.drawImage(originalCopy,0,0,width,height,null);
		int alpha = Utils.clamp(0,255,progress);
		Color shade = new Color(0,0,0,alpha);
		wol = wipeOverlay.getGraphics();
		wol.setColor(shade);
		wol.fillRect(0, 0, width, height);
		
	}
	
	
	
	public void update() {
		if(wipeActive) {
			if(progress<MAX_PROGRESS) {

				if (changePacer.check()) {
					progress +=IMAGE_CHANGE_AMOUNT;

					shadeOut();
				}

				System.out.println(progress);

			}else {
				reset();
			}
			
		}
		
		
		
	}

}
