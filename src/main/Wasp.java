package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Dictionary;

import javax.imageio.ImageIO;

import main.Collision.Direction4W;




public class Wasp  {
	
	Color color ;
	GamePanel gp;
	BufferedImage[] playerimages; //holds 1 or more player images\
	BufferedImage currentImage;
	int currentImageIndex =0;
	int targetWorldX, targetWorldY;
	int goalX =0;
	int goalY = 0;
	boolean somethingBlocked;
	int vel = 3;
	private boolean showHitbox = false;
	private final long CHANGE_PICTURE_TICKS =5;
	private EnemyState state;
	//EnemyType enemyType = EnemyType.GREENSLIME;
	//EntityType entityType = EntityType.GREENSLIME;
	public final int SPRITE_SA_TLC_OFFSET = 5;
	public final int ENEMY_DAMAGE = 1;
	public boolean isDead = false;
	private final int RT_DEBOUNCE_INITIAL = 100;
	//public boolean markForDeletion = false;
	
	//public Rectangle solidArea;
	public Rectangle wpSolidArea;
	public Rectangle spriteRect;

	public boolean collDirU,collDirD,collDirL,collDirR;
	public boolean alerted = false;
	public double distanceToPlayer;
	public int health;
	public final int maxHealth =100;
	public boolean visible = true;
	public boolean markForDeletion=false;
	public boolean collision;
	
	public final int agroDistance = 350;
	//public boolean pathToPlayerBlockedByTile;
	
	public int worldX; //actual position in world
	public int worldY;
	public int screenX,screenY; //only used to display sprite
	
	public int spriteWidth = 50;
	public int spriteHeight = 50;
	
	public enum EnemyState{
		CHASE,
		DEAD,
		WAIT,
		FOLLOW_WALL
	}
	
	public Wasp(GamePanel gp, int startWorldX, int startWorldY) {
		//super(gp,width,height);
		this.gp = gp;
		//initializing variables is optional
		wpSolidArea = new Rectangle();
		wpSolidArea.height = 50;
		wpSolidArea.width = 50;
		this.health=100;
		color = Color.red;
		//this.isEnemy=true;
		this.markForDeletion = false;
		this.state = EnemyState.WAIT;
		
		worldX = startWorldX;//these are difference top left of hitbox from top left of sprite
		worldY = startWorldY;
		collision=true;
		loadImages();
		if(currDirection==null)currDirection=Direction4W.UP;
		this.currTileYX=new int[2];
		this.tileForward=new int[2];
		this.tileRight=new int[2];
		
		//solid area Rect in world coordinate space
		//this.wpSolidArea = new Rectangle(); //world
		
		this.wpSolidArea.x = this.worldX;
		this.wpSolidArea.y = this.worldY;

	}
	
	public void takeDamage(int x) {
		if (this.health >=0) {
			health -=x;
			this.alerted = true;
			//gp.hud.enemyHealth = health;
			
		}
		
	}
	


	public void loadImages() {
		playerimages = new BufferedImage[10];
		try {
			playerimages[0]=(ImageIO.read(
					getClass().getResourceAsStream("/characters/waspR1.png")));
			playerimages[1]=(ImageIO.read(
					getClass().getResourceAsStream("/characters/waspR2.png")));
			playerimages[2]=(ImageIO.read(
					getClass().getResourceAsStream("/characters/waspL1.png")));
			playerimages[3]=(ImageIO.read(
					getClass().getResourceAsStream("/characters/waspL2.png")));
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}
	
	private long tickcounter = 0;
	private int[] currTileYX;
	private int[] tileForward;
	private int[] tileRight;
	private int rightTurnDebounceWait;
	private boolean foundWall=false;
	private Direction4W currDirection;
	
	
	public void cycleImages() {
		 
		if (this.state==EnemyState.WAIT||this.state==EnemyState.CHASE) {
			if (tickcounter > CHANGE_PICTURE_TICKS) {
				
				if(this.worldX>gp.player.worldX) {
					if (currentImageIndex==2) {
						currentImageIndex=3;
					}else if(currentImageIndex==3) {
						currentImageIndex=2;
					}else {
						currentImageIndex=3;
					}
					
				}else {
					if (currentImageIndex==0) {
						currentImageIndex=1;
					}else if(currentImageIndex==1) {
						currentImageIndex=0;
					}else {
						currentImageIndex=1;
					}
					
				}
				
				
				currentImage = playerimages[currentImageIndex];
				tickcounter=0;
			}else {
				tickcounter++;
			}
			
		}
	}
	
	public void enemyTileColl() {
		
	}
	
	public void moveDirection() {
		switch(currDirection) {
		case UP:
			worldY -= vel;
			break;
		case DOWN:
			worldY += vel;
			break;
		case LEFT:
			worldX -=vel;
			break;
		case RIGHT:
			worldX +=vel;
			break;
		default:
			break;
		
		
		}
	}
	
	public void followPlayer() {
		//update enemy object's target point
		targetWorldX = gp.player.worldX;
		targetWorldY = gp.player.worldY;
		//move in X
		this.collDirD=false;
		this.collDirU=false;
		this.collDirL=false;
		this.collDirR=false;
		
		boolean[] colls = null;
		try {
			 colls = gp.collision.collideTileRect(this.wpSolidArea);
		}catch(Exception e) {
			 ;
		}
//		// colls up down left right
		 
		//left
		if (this.worldX >targetWorldX  && !colls[2]) {
			
			this.worldX -=vel;
			this.collDirD=true;
			this.currDirection=Direction4W.LEFT;
			//move left
			
		}else if(this.worldX >targetWorldX && colls[2]) {
			this.state=EnemyState.FOLLOW_WALL;
		}
		
		
		//right
		if (this.worldX <targetWorldX && !colls[3]) {
			
			this.worldX +=vel;
			this.collDirR=true;
			this.currDirection=Direction4W.RIGHT;
			//move right
			
		}else if(this.worldX <targetWorldX && colls[3]) {
			this.state=EnemyState.FOLLOW_WALL;
		}
		
		
		//down
		if (this.worldY <targetWorldY&& !colls[1]) {
			this.worldY +=vel;
			this.collDirD=true;
			this.currDirection=Direction4W.DOWN;
			
		} else if(this.worldY <targetWorldY && colls[1]) {
			this.state=EnemyState.FOLLOW_WALL;
		}
		//up
		if (this.worldY >targetWorldY && !colls[0]) {
			this.worldY -=vel;
			this.collDirU=true;;
			this.currDirection=Direction4W.UP;
		}else if(this.worldY >targetWorldY && colls[0]) {
			this.state=EnemyState.FOLLOW_WALL;
		}
		
		
		
	}
	
	public void draw() {
		 
	gp.g2.setColor(color);
	
	//gp.g2.fill3DRect(screenX, screenY, 25, 25,true);
	gp.g2.setStroke(new BasicStroke(2));
	if (this.showHitbox)gp.g2.drawRect(screenX, screenY, spriteWidth, spriteHeight);
	if (currentImage==null)  currentImage=playerimages[0];
	//currentImage = tmpImg;
	if (this.visible)gp.g2.drawImage(currentImage, screenX, screenY, spriteWidth, spriteHeight, null ); 
	
	
	
	}
	
	
	
	
	
	
	
	
	
	
	public int enemyPlayerDistance() {
		int diffX = Math.abs(gp.player.worldX - this.worldX);
		int diffY = Math.abs(gp.player.worldY - this.worldY);
		return (int) Math.sqrt(Math.pow(diffX, 2)+Math.pow(diffY, 2));
	}
	
	public void enemyCollidePlayer() {
		if(this.wpSolidArea.intersects(gp.player.wpSolidArea)) {
			gp.player.health-=ENEMY_DAMAGE;
		}
	}
	
	public void update() {
		
		updateWorldSolidArea();
		
		screenX = this.worldX - gp.wpScreenLocX + SPRITE_SA_TLC_OFFSET;
		screenY = this.worldY - gp.wpScreenLocY + SPRITE_SA_TLC_OFFSET;
		this.distanceToPlayer=enemyPlayerDistance();
		
		if (this.health <=0) {
			isDead=true;
			}
		
		if(this.alerted == false) {
			
		}
		
		checkPlayerNear();
		
		if(this.isDead==false) {
			cycleImages();
			
			if (alerted&&state==EnemyState.CHASE) {
				followPlayer(); //follow the player
			}else if(state==EnemyState.FOLLOW_WALL) {
				followWall();
			}
			
			
			//this.worldY++;
			
		}else {
			deadAction();
		}
		enemyCollidePlayer();
		
			
			
		
		}
	private void followWall() {
		
		moveByTile() ;
		borderBump();
		moveDirection();
	}
	private  boolean borderBump() {
		if (worldX<0) {
			worldX +=vel;
			return true;
		}else if(worldX+spriteWidth>=GamePanel.worldSizePxX) {
			worldX -=vel;
			return true;
		}else if(worldY<0) {
			worldY +=vel;
			return true;
		}else if(worldY+spriteHeight>=GamePanel.worldSizePxY) {
			worldY -=vel;
			return true;
		}else {
			return false;
		}
			
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
	
	//enemy will agro and chase the player if he comes near
	public void checkPlayerNear() {
		if(this.distanceToPlayer <=agroDistance) {
			this.alerted = true;
			this.state=EnemyState.CHASE;
		}else if(this.distanceToPlayer > agroDistance) {
			this.alerted = false;
			this.state=EnemyState.FOLLOW_WALL;
			//give up chasing if player escapes
		}
		
	}
	
	
	int deadActionCounter = 100;
	public void deadAction() {
		currentImage = playerimages[3];
		
		deadActionCounter --;
		
		if (deadActionCounter <=0) {
			this.markForDeletion =true;
		}
		 
		
	}

	private void updateWorldSolidArea() {
		wpSolidArea.x = worldX;
		wpSolidArea.y = worldY;
	}



	public EnemyState getState() {
		// TODO Auto-generated method stub
		return  this.state;
	}


	
	public int[] getScreenXY() {
		
		return new int[] {this.screenX,this.screenY};
		
	}




	
	public int[] getWorldXY() {
		
		return new int[] {this.worldX,this.worldY};
	}
	
	public void setWorldXY(int x , int y) {
		this.worldX=x;
		this.worldY=y;
		
		 
	}
	public void setWorldXY(int []c) {
		this.worldX=c[0];
		this.worldY=c[1];
		
		 
	}




	
	public boolean canRemove() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public void initImages() throws IOException {
		// TODO Auto-generated method stub
		
	}
		
	

}
