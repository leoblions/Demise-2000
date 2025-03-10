package main;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.Collision.Direction4W;

public class Orb2  {

	private final boolean DRAW_SENSE_BLOCKS = false;
	private BufferedImage[] bufferedImages;
	public Rectangle wpSolidArea;
	public Rectangle wpProposedMove;
	int worldX, worldY,spriteHitboxOffsetX,spriteHitboxOffsetY;
	int currentImageIndex = 0;
	int velocity = 5;
	int spriteWidth = 25;
	int spriteHeight =25;
	int velX = 1;
	int velY=1;
	int ENEMY_DAMAGE = 1;
	int rightTurnDebounceWait =0;  //prevent making too many right turns in quick succession
	boolean foundWall = false;
	int[] tileRight;
	int[] currTileYX; //x and y position in tile grid
	GamePanel gp;
	Direction4W currDirection;
	private boolean[] movesRequested;
	private int[] tileForward;
	
	
	

	public Orb2(GamePanel gp) {
		// TODO Auto-generated constructor stub
		this.gp=gp;
		this.currTileYX =new int[2];
		wpSolidArea = new Rectangle();
		wpProposedMove = new Rectangle();
		movesRequested = new boolean[4];
		tileForward = new int[2];
		tileRight = new int[2];
		
		wpProposedMove.height = spriteHeight;
		wpProposedMove.width = spriteWidth;
		wpSolidArea.height = spriteHeight;
		wpSolidArea.width = spriteWidth;
		
		worldX = 250;
		worldY = 250;
		try {
			initImages();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		currDirection = Direction4W.DOWN;
	}

	
	public void draw() {
		gp.g2.setColor(Color.orange);
		if(GamePanel.drawCollRect)gp.g2.drawRect(
				worldX-gp.wpScreenLocX, 
				worldY-gp.wpScreenLocY, 
				wpSolidArea.width, 
				wpSolidArea.height);
		gp.g2.drawImage(bufferedImages[currentImageIndex],
				worldX-gp.wpScreenLocX+spriteHitboxOffsetX,
				worldY-gp.wpScreenLocY+spriteHitboxOffsetY,
				25, 
				35,
				null);
		
		if(DRAW_SENSE_BLOCKS) {
			gp.tileManager.highlightTile(tileForward[0],
					tileForward[1], Color.yellow);
			
			gp.tileManager.highlightTile(currTileYX[0],
					currTileYX[1], Color.white);
			
			gp.tileManager.highlightTile(tileRight[0],
					tileRight[1], Color.pink);
		}
		
		
		
	}
	
	public Direction4W translateDirectionLetterToEnum(char letter) {
		switch(letter) {
		case 'U':
			return Direction4W.UP;
			//break;
		case 'D':
			return Direction4W.DOWN;
			//break;
		case 'L':
			return Direction4W.LEFT;
			//break;
		case 'R':
			return Direction4W.RIGHT;
			//break;
		default:
			return Direction4W.NONE;
			//break;
		
		
		}
	}
	
	public void moveDirection() {
		switch(currDirection) {
		case UP:
			worldY -= velocity;
			break;
		case DOWN:
			worldY += velocity;
			break;
		case LEFT:
			worldX -=velocity;
			break;
		case RIGHT:
			worldX +=velocity;
			break;
		default:
			break;
		
		
		}
	}
/**
 * returns false if next move would collide with tiles	
 * @return
 */
private boolean moveAllowedTiles() {
		
		//this.proposedMove = new Rectangle();
		
		int pvelX = velX;
		int pvelY = velY;
		
		if(this.movesRequested[0]) {
			pvelY = -velocity;
			//this.movesRequested[0]=false;
		}
		if(this.movesRequested[1]) {
			pvelY= velocity;
			//this.movesRequested[1]=false;
		}
		if(this.movesRequested[2]) {
			pvelX = -velocity;
			//this.movesRequested[2]=false;
		}
		if(this.movesRequested[3]) {
			pvelX = velocity;
			//this.movesRequested[3]=false;
		}
		
		this.wpProposedMove.x = worldX+pvelX;
		this.wpProposedMove.y = worldY+pvelY;
		
		boolean[] colls = null;
		try {
			 colls = gp.collision.collideTileRect(this.wpProposedMove);
		}catch(Exception e) {
			return false;
		}
		//bump the player away from wall
		//bumpPlayer( colls);
		
		for(int i=0;i<colls.length;i++) {
			if (colls[i]==true){
				//stop player if collision function goes OOB
				 
				return false;
			}
			
		}
		//System.out.println("moveAllowedTiles true");
		return true;
		
	}
	
	public void setMovesRequested( ) {
		//System.out.println(currDirection.name());
		switch(currDirection) {
		
		case UP:
			movesRequested[0]=true;
			movesRequested[1]=false;
			movesRequested[2]=false;
			movesRequested[3]=false;
			break;
		case RIGHT:
			movesRequested[0]=false;
			movesRequested[1]=false;
			movesRequested[2]=false;
			movesRequested[3]=true;
			break;
		case DOWN:
			movesRequested[0]=false;
			movesRequested[1]=true;
			movesRequested[2]=false;
			movesRequested[3]=false;
			break;
		case LEFT:
			movesRequested[0]=false;
			movesRequested[1]=false;
			movesRequested[2]=true;
			movesRequested[3]=false;
			break;
		default:
			break;
		
		
		
		}
	}
	
	public void changeDirection() {
		switch(currDirection) {
		
		case UP:
			if(!gp.collision.collideTileRectDirection(wpProposedMove, Direction4W.RIGHT)) {
				currDirection=Direction4W.RIGHT;
			}else {
				currDirection=Direction4W.LEFT;
			}
			
			break;
		case RIGHT:
			if(!gp.collision.collideTileRectDirection(wpProposedMove, Direction4W.DOWN)) {
				currDirection=Direction4W.DOWN;
			}else {
				currDirection=Direction4W.UP;
			}
			
			break;
		case DOWN:
			
			if(!gp.collision.collideTileRectDirection(wpProposedMove, Direction4W.LEFT)) {
				currDirection=Direction4W.LEFT;
			}else {
				currDirection=Direction4W.RIGHT;
			}
			
		case LEFT:
			if(!gp.collision.collideTileRectDirection(wpProposedMove, Direction4W.UP)) {
				currDirection=Direction4W.UP;
			}else {
				currDirection=Direction4W.DOWN;
			}
			break;
		default:
			break;
		}
		//System.out.println("currDirection "+currDirection.toString());
	}
	
	private void changeDirection2(boolean[] colls){
		
		
	}
	
	public void cycleDirectionL() {
		switch(currDirection) {
		case UP:
			currDirection=Direction4W.LEFT;
			break;
		case RIGHT:
			currDirection=Direction4W.UP;
			break;
		case DOWN:
			currDirection=Direction4W.RIGHT;
			break;
		case LEFT:
			currDirection=Direction4W.DOWN;
			break;
		default:
			break;
		}
	}
	
	public void cycleDirectionR() {
		switch(currDirection) {
		case UP:
			currDirection=Direction4W.RIGHT;
			break;
		case RIGHT:
			currDirection=Direction4W.DOWN;
			break;
		case DOWN:
			currDirection=Direction4W.LEFT;
			break;
		case LEFT:
			currDirection=Direction4W.UP;
			break;
		default:
			break;
		}
	}
	
	
	
	public boolean inbounds() {
		if (worldX>=0&&worldX+spriteWidth<GamePanel.worldSizePxX&&
				worldY>=0&&worldY+spriteHeight<GamePanel.worldSizePxY) {
			return true;
		}else {
			return false;
		}
	}
	/**
	 * returns true if character bumped the main border
	 * @return
	 */
	public boolean borderBump() {
		if (worldX<0) {
			worldX +=velocity;
			return true;
		}else if(worldX+spriteWidth>=GamePanel.worldSizePxX) {
			worldX -=velocity;
			return true;
		}else if(worldY<0) {
			worldY +=velocity;
			return true;
		}else if(worldY+spriteHeight>=GamePanel.worldSizePxY) {
			worldY -=velocity;
			return true;
		}else {
			return false;
		}
			
	}
	/** true if a tile is blocking the path ahead
	 * 
	 * @return
	 */
	public boolean tileAhead(Rectangle prop) {
		return gp.collision.collideTileRectDirection(prop, currDirection);
	}
	/**
	 * based on the entity's direction, obtains the coordinates of forward and 
	 * right bordering tiles to be used later to decide which way to turn
	 */
	private void senseTiles() {
		switch(currDirection) {
		case UP:
			tileForward[0]=this.currTileYX[0];
			tileForward[1]=this.currTileYX[1]-1;
			tileRight[0]=this.currTileYX[0]+1;
			tileRight[1]=this.currTileYX[1];
			break;
		case DOWN:
			tileForward[0]=this.currTileYX[0];
			tileForward[1]=this.currTileYX[1]+1;
			tileRight[0]=this.currTileYX[0]-1;
			tileRight[1]=this.currTileYX[1];
			break;
		case LEFT:
			tileForward[0]=this.currTileYX[0]-1;
			tileForward[1]=this.currTileYX[1];
			tileRight[0]=this.currTileYX[0];
			tileRight[1]=this.currTileYX[1]-1;
			break;
		case RIGHT:
			tileForward[0]=this.currTileYX[0]+1;
			tileForward[1]=this.currTileYX[1];
			tileRight[0]=this.currTileYX[0];
			tileRight[1]=this.currTileYX[1]+1;
			break;
		
		}
	}
		
	
	/**
	 * check the tile grid, move forward if tile not blocked, then right if it is
	 */
	
	private void moveByTile() {
		this.currTileYX[0] = (this.worldX+this.spriteWidth/2)/GamePanel.TILE_SIZE_PX;
		this.currTileYX[1] = (this.worldY+this.spriteHeight/2)/GamePanel.TILE_SIZE_PX;

		senseTiles();

		int tTileForward = 0;
		try {
		tTileForward = GamePanel.tileGrid[tileForward[1]][tileForward[0]];
		}catch(Exception e) {
			cycleDirectionL();cycleDirectionL();
			senseTiles();
			tTileForward = GamePanel.tileGrid[tileForward[1]][tileForward[0]];
		}
		int tTileRight = GamePanel.tileGrid[tileRight[1]][tileRight[0]];
		
		
		if(!Collision.tileKindIsSolid(tTileRight)  && rightTurnDebounceWait <=0 ) {

			cycleDirectionR();
		
			rightTurnDebounceWait = 190;
			foundWall=false;

		}else {
			rightTurnDebounceWait --;
		}

		if( Collision.tileKindIsSolid(tTileForward) ) {
			foundWall =true;
			cycleDirectionL();
		}

	}
	
	private void setDirectionByPathFind() {
		 Point worldPoint = new Point(this.worldX,this.worldY);
		 char currDirectionL = gp.pathFind.getDirectionTowardsPlayer(worldPoint);
		 currDirection = translateDirectionLetterToEnum(currDirectionL);
		

	}
	

	
	public void update() {
		wpSolidArea.x = worldX;
		wpSolidArea.y = worldY;
		setDirectionByPathFind();
		//moveByTile() ;
		//borderBump();
		moveDirection();
		
		
		enemyCollidePlayer();

		
	}
	
	public void enemyCollidePlayer() {
		if(this.wpSolidArea.intersects(gp.player.wpSolidArea)) {
			gp.player.health-=ENEMY_DAMAGE;
		}
	}


	public void initImages() throws IOException {
		this.bufferedImages = new BufferedImage[20];
		bufferedImages[0] = ImageIO.read(getClass().getResourceAsStream("/characters/orb4.png"));
		
		
	}

}
