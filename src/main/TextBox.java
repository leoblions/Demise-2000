package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Stroke;

public class TextBox {
	private final int BORDER_WIDTH = 5;
	int screenX, screenY, width, height;
	GamePanel gp;
	Rectangle backgroundRect;
	Color backgroundColor,backgroundColorBorder;
	String boxtext;
	boolean visible;
	Stroke s1;

	public TextBox(GamePanel gp, int screenX, int screenY, int width, int height) {
		this.gp = gp;
		this.screenX = screenX;
		this.screenY = screenY;
		this.width = width;
		this.height = height;
		this.visible = true;
		this.s1 = new BasicStroke(BORDER_WIDTH);
		// background
		backgroundColor = new Color(100, 100, 100, 100);
		backgroundColorBorder= new Color(100, 70, 70, 100);
		backgroundRect = new Rectangle(screenX, screenY, width, height);
		// text content
		boxtext = new String();

	}
	
	public TextBox(GamePanel gp, Position position) {
		this.gp = gp;
		this.screenX = position.getWorldX();
		this.screenY = position.getWorldY();
		this.width = position.getWidth();
		this.height = position.getHeight();
		this.visible = true;
		this.s1 = new BasicStroke(BORDER_WIDTH);
		// background
		backgroundColor = new Color(100, 100, 100, 100);
		backgroundColorBorder= new Color(100, 70, 70, 100);
		backgroundRect = new Rectangle(screenX, screenY, width, height);
		// text content
		boxtext = new String();

	}

	public void draw() {
		drawBackground();

	}

	public void drawBackground() {

		gp.g2.setColor(backgroundColor);
		
		gp.g2.fillRect(backgroundRect.x, backgroundRect.y, backgroundRect.width, backgroundRect.height);
		gp.g2.setStroke(s1);
		gp.g2.setColor(backgroundColorBorder);
		gp.g2.drawRect(backgroundRect.x, backgroundRect.y, backgroundRect.width, backgroundRect.height);

	}

	public void update() {

	}

}
