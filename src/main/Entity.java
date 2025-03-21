package main;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import main.Collision.Direction4W;
import main.Player.PlayerState;

public class Entity {

	private final boolean DRAW_SENSE_BLOCKS = false;
	private final String IMAGE_URI_TEMPLATE_W = "/images/entity%dW.png";
	private final int SS_COLS = 4;
	private final int SS_ROWS = 4;
	private final int SS_CELL_W = 50;
	private final int SS_CELL_H = 50;
	private final int DEFAULT_SPEED = 5;
	private BufferedImage[] bufferedImages;
	private Pacer animationPacer;
	static HashMap<Integer, BufferedImage[]> entImageStoreW; // memoize image arrays
	static HashMap<Integer, BufferedImage[]> entImageStoreD; // dead
	public Rectangle wpSolidArea;
	public Rectangle wpProposedMove, testRect;
	int startGX, startGY, kind, UID;
	int spriteHitboxOffsetX, spriteHitboxOffsetY;
	int currentImageIndex = 0;
	int currentSpeed = 5;
	int spriteWidth = 25;
	int spriteHeight = 25;
	int velX = 0;
	int velY = 0;
	int health = 100;
	boolean alive = true;
	int frame = 0;
	char state = 'w'; // w=walk, s=stand, a=attack, d=dead, h=hit
	char direction = 'n';// n u d l r
	int ENEMY_DAMAGE = 1;
	final int DEF_DAMAGE_FROM_PLAYER = 10;
	int rightTurnDebounceWait = 0; // prevent making too many right turns in quick succession
	boolean foundWall = false;
	public boolean enemy = false;
	public boolean chasePlayer = false;
	public boolean playerPressToActivate = false;
	int[] tileRight;
	int[] currTileYX; // x and y position in tile grid
	GamePanel gp;
	Direction4W currDirection = Direction4W.NONE;
	private boolean[] movesRequested;
	private int[] tileForward;
	public Position position, testPosition;
	public Rectangle collider;
	
	/*
	 * Types: 
	 * 0 Orb 
	 * 1 Bat 
	 * 2 Centipede 
	 * 3 Spider 
	 * 4 Maggot 
	 * 5 Earwig 
	 * 6 Zombie1 
	 * 7 Zombie2 
	 * 8 Mercenary 1 
	 * 9 Mercenary 2 
	 * 10 Sam 
	 * 11 Elissa 
	 * 12 Trent 
	 * 13 Otto
	 * 
	 */

	/**
	 * 
	 * @param gp
	 * @param startGX
	 * @param startGY
	 * @param kind
	 * @param UID
	 */
	public Entity(GamePanel gp, int startGX, int startGY, int kind, int UID) {
		// TODO Auto-generated constructor stub
		this.gp = gp;
		this.startGX = startGX;
		this.startGY = startGY;
		this.kind = kind;
		this.UID = UID;
		this.currTileYX = new int[2];
		int[] dimensions = getEntityWHFromKind(kind);
		entImageStoreW = new HashMap<>();
		animationPacer = new Pacer(10);
		int worldX = startGX * GamePanel.TILE_SIZE_PX;
		int worldY = startGY * GamePanel.TILE_SIZE_PX;
		position = new Position(gp, 0, 0, dimensions[0], dimensions[1]);
		testPosition = new Position(gp, 0, 0, dimensions[0], dimensions[1]);
		this.collider = new Rectangle(0, 0, dimensions[0], dimensions[1]);
		position.setPositionToGridXY(startGX, startGY);
		wpSolidArea = new Rectangle();
		wpProposedMove = new Rectangle();
		testRect = new Rectangle();
		movesRequested = new boolean[4];
		tileForward = new int[2];
		tileRight = new int[2];
		spriteHitboxOffsetX = -30;
		spriteHitboxOffsetY = -30;

		if (kind < 10) {
			//enemies
			enemy = true;
			chasePlayer = true;

		} else {
			//NPC
			currDirection = Direction4W.DOWN;
			state = 's';
			direction = 'D';
			playerPressToActivate=true;
		}

		wpProposedMove.height = spriteHeight;
		wpProposedMove.width = spriteWidth;
		wpSolidArea.height = spriteHeight;
		wpSolidArea.width = spriteWidth;
		testRect.height = spriteHeight;
		testRect.width = spriteWidth;

		try {
			initImages();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.bufferedImages = entImageStoreW.get(kind);
		// currDirection = Direction4W.NONE;
	}

	public int[] getEntityWHFromKind(int kind) {
		return new int[] { 50, 50 };
	}
	


	public void draw() {
		gp.g2.setColor(Color.orange);
		int worldX = position.getWorldX();
		int worldY = position.getWorldY();

		if (GamePanel.drawCollRect)
			gp.g2.drawRect(worldX - gp.wpScreenLocX, worldY - gp.wpScreenLocY, wpSolidArea.width, wpSolidArea.height);
		gp.g2.drawImage(bufferedImages[currentImageIndex], (worldX - gp.wpScreenLocX) + spriteHitboxOffsetX,
				(worldY - gp.wpScreenLocY) + spriteHitboxOffsetY, 50, 50, null);

		if (DRAW_SENSE_BLOCKS) {
			gp.tileManager.highlightTile(tileForward[0], tileForward[1], Color.yellow);

			gp.tileManager.highlightTile(currTileYX[0], currTileYX[1], Color.white);

			gp.tileManager.highlightTile(tileRight[0], tileRight[1], Color.pink);
		}

	}
	
	public void takeDamageFromPlayer(int damage) {
		int newHealth = health - damage;
		if (newHealth>0 ) {
			health = newHealth;
		}else {
			health = 0;
		}
		if(health <= 0) {
			alive = false;
			state = 'd';
		}
	}

	public Direction4W translateDirectionLetterToEnum(char letter) {
		switch (letter) {
		case 'U':
			return Direction4W.UP;
		// break;
		case 'D':
			return Direction4W.DOWN;
		// break;
		case 'L':
			return Direction4W.LEFT;
		// break;
		case 'R':
			return Direction4W.RIGHT;
		// break;
		default:
			return Direction4W.NONE;
		// break;

		}
	}

	public void cycleSprite() {
		int directionIndexpart = 0;

		switch (currDirection) {
		case UP:
			directionIndexpart = 0;
			break;
		case DOWN:
			directionIndexpart = 4;
			break;
		case LEFT:
			directionIndexpart = 8;
			break;
		case RIGHT:
			directionIndexpart = 12;
			break;
		default:
			directionIndexpart = 4;
		}
		if (state == 's' && animationPacer.check()) {

			if (frame==0) {
				frame=2;
			}else {
				frame=0;
			}

		} else if (state == 'w' && animationPacer.check()) {

			if (frame < 3) {
				frame++;
			} else {
				frame = 0;
			}

			

		} else if (state=='d'|| !alive) {
			frame = 0;
		}
		currentImageIndex = frame + directionIndexpart;

	}

	public void moveDirection() {
		if (state == 'w') {
			currentSpeed = DEFAULT_SPEED;
		} else {
			currentSpeed = 0;
		}
		switch (currDirection) {
		case UP:
			position.applyVelocityXY(0, -currentSpeed);
			break;
		case DOWN:
			position.applyVelocityXY(0, currentSpeed);
			break;
		case LEFT:
			position.applyVelocityXY(-currentSpeed, 0);
			break;
		case RIGHT:
			position.applyVelocityXY(currentSpeed, 0);
			break;
		default:
			position.applyVelocityXY(0, 0);
			break;

		}
	}

	public boolean inbounds() {
		int worldX = position.getWorldX();
		int worldY = position.getWorldY();
		if (worldX >= 0 && worldX + spriteWidth < GamePanel.worldSizePxX && worldY >= 0
				&& worldY + spriteHeight < GamePanel.worldSizePxY) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * returns true if character bumped the main border
	 * 
	 * @return
	 */
	public boolean borderBump() {
		int worldX = position.getWorldX();
		int worldY = position.getWorldY();
		int velXLocal = 0;
		int velYLocal = 0;

		if (worldX < 0) {
			velXLocal = currentSpeed;

		} else if (worldX + spriteWidth >= GamePanel.worldSizePxX) {
			velXLocal = -currentSpeed;

		} else if (worldY < 0) {
			velYLocal = currentSpeed;

		} else if (worldY + spriteHeight >= GamePanel.worldSizePxY) {
			velYLocal = -currentSpeed;

		} else {
			return false;
		}
		position.applyVelocityXY(velXLocal, velYLocal);

		return true;
	}

	/**
	 * true if a tile is blocking the path ahead
	 * 
	 * @return
	 */
	public boolean tileAhead(Rectangle prop) {
		return gp.collision.collideTileRectDirection(prop, currDirection);
	}

	private void setDirectionByPathFind() {
		int worldX = position.getWorldX();
		int worldY = position.getWorldY();
		Point worldPoint = new Point(worldX, worldY);
		this.direction = gp.pathFind.getDirectionTowardsPlayer(worldPoint);
		currDirection = translateDirectionLetterToEnum(direction);

	}

	public boolean moveOverlapsOtherEntity() {
		// stop enemies and NPCs from overlapping
		testPosition.setPositionToWorldXY(position.getWorldX(), position.getWorldY());
		switch (currDirection) {
		case UP:
			testPosition.applyVelocityXY(0, -currentSpeed);
			break;
		case DOWN:
			testPosition.applyVelocityXY(0, currentSpeed);
			break;
		case LEFT:
			testPosition.applyVelocityXY(-currentSpeed, 0);
			break;
		case RIGHT:
			testPosition.applyVelocityXY(currentSpeed, 0);
			break;
		default:
			testPosition.applyVelocityXY(0, 0);
			break;

		}
		testRect.x = testPosition.getWorldX();
		testRect.y = testPosition.getWorldY();
		for (Entity entity : gp.entityManager.entityList) {
			if (this.testRect.intersects(entity.wpSolidArea) && !(this == entity)) {

				return true;
			}
		}
		return false;
	}

	public void update() {
		int worldX = position.getWorldX();
		int worldY = position.getWorldY();
		wpSolidArea.x = worldX;
		wpSolidArea.y = worldY;
		if (alive && chasePlayer) {
			state = 'w';
			setDirectionByPathFind();

		}else {
			bufferedImages=entImageStoreD.get(kind);
		}
		if (!moveOverlapsOtherEntity()) {

			moveDirection();
		} else {
			state = 's';
		}
		cycleSprite();
		playerMeleeEnemy();
		enemyCollidePlayer();

	}

	private void playerMeleeEnemy() {
		wpSolidArea.x = position.getWorldX();
		wpSolidArea.y = position.getWorldY();
		if(gp.entityManager.playerMelee) {
			if(gp.entityManager.playerHitbox.intersects(this.wpSolidArea)) {
				takeDamageFromPlayer(DEF_DAMAGE_FROM_PLAYER);
				System.out.println("player hit enemy");
				gp.particle.addParticle(wpSolidArea.x, wpSolidArea.y, 1);
			}
		}
		
	}

	public void enemyCollidePlayer() {
		if ( this.wpSolidArea.intersects(gp.player.wpSolidArea)) {
			
			if(enemy && alive) {
				//gp.player.health -= ENEMY_DAMAGE;
				gp.player.takeDamageFromEnemy(DEF_DAMAGE_FROM_PLAYER);
			}else if( // player in range of NPC
					 gp.entityManager.entityActivateDalay.delayExpired() &&
					!gp.entityManager.entityTouchedList.contains(this)){

				gp.hud.showActionPromptDelay.setDelay(60);
				if(gp.entityManager.activateEntityFlag) {
					//player pressed activate
					gp.entityManager.entityActivateDalay.setDelay(EntityManager.ENTITY_ACTIVATE_DELAY_TICKS);
					gp.brain.playerActivateNPC(this,gp.playerPressActivate);
					gp.entityManager.playerTouchedActorSincelastTick = true;
					gp.entityManager.entityTouchedList.add(this);
				}
				System.out.println("player touch ent");
				
			}
		}
	}

	public void initImages() throws IOException {
		if (null == entImageStoreW) {
			entImageStoreW = new HashMap<>();
		}
		if (null == entImageStoreD) {
			entImageStoreD = new HashMap<>();
		}
		// this.bufferedImages = new BufferedImage[20];
		String URLString = String.format(IMAGE_URI_TEMPLATE_W, this.kind);
		// bufferedImages[0] =
		// ImageIO.read(getClass().getResourceAsStream("/characters/orb4.png"));
		BufferedImage[] tempBI = null;
		try {
			tempBI = new Utils().spriteSheetCutter(URLString, SS_COLS, SS_ROWS, SS_CELL_W, SS_CELL_H);

		} catch (Exception e) {
			System.err.println("Entity Failed to open the resource: " + URLString);
			e.printStackTrace();
		}
		if (null == tempBI) {
			tempBI = new Utils().spriteSheetCutterBlank(SS_COLS, SS_ROWS, SS_CELL_W, SS_CELL_H);
		}
		entImageStoreW.put(kind, tempBI);

		BufferedImage[] tempBI2 = null;
		tempBI2 = new BufferedImage[tempBI.length];
		for(int i = 0; i < tempBI.length;i++) {
			
			tempBI2[i]= Utils.convertBufferedImageBW(tempBI[i] );
		}
		entImageStoreD.put(kind, tempBI2);
	}

}
