package main;

public class ScreenPosition extends Position{
	private int worldX,  worldY,  width,  height,tileSize;
	GamePanel gp;

	
	public ScreenPosition(GamePanel gp, int screenX, int screenY, int width, int height) {
		super(gp, 0, 0, width, height);
		setLocationByScreenX(screenX);
		setLocationByScreenY(screenY);
		this.worldX = worldX;
		this.worldY = worldY;
		this.width = width;
		this.height = height;
		this.gp=gp;
		this.tileSize = gp.TILE_SIZE_PX;
		
	}
	
	public int getWorldX() {
		return this.worldX;
	}
	
	public int getWorldY() {
		return this.worldY;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public void setWH(int width, int height) {
		if (width>0){
			this.width = width;
		}
		if (height>0){
			this.height = height;
		}
	}
	
	public void applyVelocityXY(int velX, int velY) {
		this.worldX += velX;
		this.worldY += velY;
	}
	
	public void setPositionToGridXY(int gridX, int gridY) {
		this.worldX = gridX * tileSize;
		this.worldY = gridY * tileSize;
	}
	
	public void setPositionToWorldXY(int worldX, int worldY) {
		this.worldX = worldX;
		this.worldY = worldY;
	}
	
	public void setLocationByScreenX(int screenX) {
		this.worldX = screenX + gp.wpScreenLocX;
	}
	public void setLocationByScreenY(int screenY) {
		this.worldY = screenY + gp.wpScreenLocY;
	}
	
	public int getScreenPositionX() {
		return this.worldX - gp.wpScreenLocX;
	}
	
	public int getScreenPositionY() {
		return this.worldY - gp.wpScreenLocY;
	}

}
