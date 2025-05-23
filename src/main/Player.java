package main;

import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import main.GamePanel.GameState;
import main.GamePanel.InputAction;

public class Player implements IInputListener {

	GamePanel gp;
	BufferedImage[] bufferedImages;

	// worldX and worldY are the TLC of the hitbox in world
	public int gridX, gridY, worldX, worldY;
	public int collision;
	
	public Rectangle wpSolidArea; //for collisions
	public Rectangle wpActivateArea;// for activating widgets, NPCs
	public Rectangle spriteRect;
	private Rectangle proposedMove;
	private int spriteHitboxOffsetX = -20;
	private int spriteHitboxOffsetY = 0;
	public int velocity = 5;
	public int defaultVelocity = 5;
	
	private static final int PLAYER_COLLIDER_W = 15;
	private static final int PLAYER_COLLIDER_H = 35;
	private static final float PLAYER_ACTIVATE_RECT_MULTIPLIER = 3.0f;
	private int activateAreaOffsetX, activateAreaOffsetY;

	public final float DIAGONAL_FACTOR = 0.71f;
	private boolean run = false;
	private boolean heal = true;
	public boolean frozen = false;
	public boolean attack = false;;
	public int velX, velY;
	public int playerScreenX, playerScreenY;
	public int health=100;
	public int stamina=100;
	public int money=110;
	public PlayerState state;
	private final int CYCLE_IMAGE_FREQ = 9;
	private final int MAX_HEALTH = 100;
	private final int MAX_STAMINA = 100;
	private final int HEAL_RATE = 120;
	private final int STAM_HEAL_RATE = 12;
	private final int START_HEALTH = 80;
	private final int STAMINA_DRAIN = 1;
	
	// tool
	public AttackMode attackMode = AttackMode.NONE;
	
	
	private final String SPRITE_SHEET = "/images/entityP.png";
	private final String SPRITE_SHEET_ATTACK = "/images/entityPA.png";
	private Pacer animationPacer, healPacer, staminaPacer;
	private Delay attackTimeout, walkTimeout, enemyPlayerDamageTimeout;
	private int currentImageIndex = 0;
	public char direction = 'd';
	private final int ENEMY_PLAYER_DAMAGE_DELAY = 150;
	int frame = 0;
	public int[] tileForward = new int[2];
	public int[] tilePlayer = new int[2];
	// up down left right
	public boolean[] movesRequested;
	public boolean[] stopRequested;

	public Player(GamePanel gp) {
		this.gp = gp;
		this.wpSolidArea = new Rectangle();
		this.wpActivateArea = new Rectangle();
		this.proposedMove = new Rectangle();
		this.wpSolidArea.width = PLAYER_COLLIDER_W;
		this.wpSolidArea.height = PLAYER_COLLIDER_H;
		this.wpActivateArea.width = (int)PLAYER_ACTIVATE_RECT_MULTIPLIER * this.wpSolidArea.width;
		this.wpActivateArea.height = (int)PLAYER_ACTIVATE_RECT_MULTIPLIER * this.wpSolidArea.height;
		activateAreaOffsetX = this.wpActivateArea.width/2;
		activateAreaOffsetY = this.wpActivateArea.height/2;
		this.proposedMove.width = wpSolidArea.width;
		this.proposedMove.height = wpSolidArea.height;
		this.worldX = 200;
		this.worldY = 200;
		health = START_HEALTH;
		animationPacer = new Pacer(CYCLE_IMAGE_FREQ);
		healPacer = new Pacer(HEAL_RATE);
		staminaPacer = new Pacer(STAM_HEAL_RATE);
		attackTimeout = new Delay();
		walkTimeout = new Delay();
		enemyPlayerDamageTimeout = new Delay();
		this.movesRequested = new boolean[] { false, false, false, false };
		this.stopRequested = new boolean[] { false, false, false, false };

		try {
			initImages();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public enum PlayerState {
		WALK, STAND, DEAD, ZOMBIE, POD, ATTACK, DIZZY
	}
	
	public enum AttackMode {
		SHOOT, GRENADE, HOE, DIG, PICK, SLASH, SPRAY, NONE,SEED, BOMB
	}
	
	public boolean getRun() {
		return run;
	}
	
	public void fullHeal() {
		this.health = MAX_HEALTH;
		this.stamina = MAX_STAMINA;
		this.state = PlayerState.STAND;

		
	}

	int walkCycleCounter = 0;

	public void cycleSprite() {

		if (state == PlayerState.STAND) {
			int directionIndexpart = 0;
			switch (direction) {
			case 'u':
				directionIndexpart = 0;
				break;
			case 'd':
				directionIndexpart = 4;
				break;
			case 'l':
				directionIndexpart = 8;
				break;
			case 'r':
				directionIndexpart = 12;
				break;
			}
			currentImageIndex = directionIndexpart;
			return;
		} else if (state == PlayerState.ATTACK) {
			int directionIndexpart = 16;
			switch (direction) {
			case 'u':
				directionIndexpart = 16;
				break;
			case 'd':
				directionIndexpart = 20;
				break;
			case 'l':
				directionIndexpart = 24;
				break;
			case 'r':
				directionIndexpart = 28;
				break;
			}
			if (animationPacer.check()) {
				if (frame < 3) {
					frame++;
				} else {
					frame = 0;
				}

			}
			currentImageIndex = frame + directionIndexpart;
			return;
		} else if (state == PlayerState.WALK) {

			int directionIndexpart = 0;

			switch (direction) {
			case 'u':
				directionIndexpart = 0;
				break;
			case 'd':
				directionIndexpart = 4;
				break;
			case 'l':
				directionIndexpart = 8;
				break;
			case 'r':
				directionIndexpart = 12;
				break;
			}

			if (animationPacer.check()) {
				if (frame < 3) {
					frame++;
				} else {
					frame = 0;
				}

			}

			currentImageIndex = frame + directionIndexpart;

		} else {
			currentImageIndex = 0;
		}

	}

	public void draw() {
		gp.g2.setColor(Color.orange);
		if (gp.drawCollRect)
			gp.g2.drawRect(worldX - gp.wpScreenLocX, worldY - gp.wpScreenLocY, wpSolidArea.width, wpSolidArea.height);
		gp.g2.drawImage(bufferedImages[currentImageIndex], worldX - gp.wpScreenLocX + spriteHitboxOffsetX,
				worldY - gp.wpScreenLocY + spriteHitboxOffsetY, 50, 50, null);

	}
	
	private void updateFrozenState() {
		if (gp.gameState==GameState.PLAY) {
			frozen=false;
		}else {
			frozen=true;
		}
	}

	public void update() {
		
		updateFrozenState();
		
		if (!frozen) {
			movePlayer();
			calculateTileForward();
		} else {
			state = PlayerState.STAND;
		}

		updateStamina();
		setPlayerState();
		cycleSprite();
		playerHeal();
		this.wpSolidArea.x = worldX;
		this.wpSolidArea.y = worldY;
		this.wpActivateArea.x = worldX - activateAreaOffsetX;
		this.wpActivateArea.y = worldY - activateAreaOffsetY;
		attack = false; //reset attack flag

	}

	public void calculateTileForward() {
		int tx = this.worldX / gp.TILE_SIZE_PX;
		int ty = this.worldY / gp.TILE_SIZE_PX;
		int fx = tx;
		int fy = ty;
		switch (direction) {
		case 'u':
			fy -= 1;
			break;
		case 'd':
			fy += 1;
			break;
		case 'l':
			fx -= 1;
			break;
		case 'r':
			fx += 1;
			break;
		default:
			break;
		}
		tileForward[0] = Utils.clamp(0, gp.MAP_TILES_X - 1, fx);
		tileForward[1] = Utils.clamp(0, gp.MAP_TILES_Y - 1, fy);
		tilePlayer[0] = Utils.clamp(0, gp.MAP_TILES_X , tx);
		tilePlayer[1] = Utils.clamp(0, gp.MAP_TILES_Y , ty);
	}

	public void toggleRun() {
		if (velocity == defaultVelocity) {
			velocity *= 1.5;
			run = true;
		} else {
			velocity = defaultVelocity;
			run = false;
		}
	}

	private int decay(int number) {
		if (number < 0) {
			number++;
			return number;
		} else if (number > 0) {
			number--;
			return number;
		} else {
			return number;
		}
	}

	private void bumpPlayer(boolean[] colls) {

		if (colls[0]) {
			worldY += 1;
		}
		if (colls[1]) {
			worldY -= 1;
		}
		if (colls[2]) {
			worldX += 1;
		}
		if (colls[3]) {
			worldX -= 1;
		}
	}

	/**
	 * return false if a move is blocked by tile
	 * 
	 * @return
	 */
	private boolean moveAllowed() {

		// this.proposedMove = new Rectangle();

		int pvelX = decay(velX);
		int pvelY = decay(velY);

		if (this.movesRequested[0]) {
			pvelY = -velocity;
			direction = 'u';
			// this.movesRequested[0]=false;
		}
		if (this.movesRequested[1]) {
			pvelY = velocity;
			direction = 'd';
			// this.movesRequested[1]=false;
		}
		if (this.movesRequested[2]) {
			pvelX = -velocity;
			direction = 'l';
			// this.movesRequested[2]=false;
		}
		if (this.movesRequested[3]) {
			pvelX = velocity;
			direction = 'r';
			// this.movesRequested[3]=false;
		}

		this.proposedMove.x = worldX + pvelX;
		this.proposedMove.y = worldY + pvelY;

		boolean[] colls = null;
		try {
			colls = gp.collision.collideTileRect(this.proposedMove);
		} catch (Exception e) {

			return false;
		}
		// bump the player away from wall
		bumpPlayer(colls);

		for (int i = 0; i < colls.length; i++) {
			if (colls[i] == true) {

				// stop player if collision function goes OOB

				return false;
			}

		}
//		if (run && stamina > 0) {
//			stamina -= 1;
//			gp.hud.stamina = stamina;
//		}
		return true;

	}

	int walkingCounter = 0;

	private void movePlayer() {

		if (!moveAllowed()) {
			if(run) {
				gp.sound.playSE(1);
			}
			

			return;
		}

		velX = decay(velX);
		velY = decay(velY);

		if (this.movesRequested[0]) {
			this.velY = -velocity;
		}
		if (this.movesRequested[1]) {
			this.velY = velocity;
		}
		if (this.movesRequested[2]) {
			this.velX = -velocity;
		}
		if (this.movesRequested[3]) {
			this.velX = velocity;
		}
		


		this.worldX += velX;
		this.worldY += velY;

		

	}
	public void warpPlayer(int gridX, int gridY) {


		velX = 0;
		velY = 0;

		


		this.worldX =gridX * GamePanel.TILE_SIZE_PX;
		this.worldY =gridY * GamePanel.TILE_SIZE_PX;

		

	}

	public void setPlayerState() {
		// change state to walking if recent movement
		
		attackTimeout.reduce();
		walkTimeout.reduce();
		boolean playerMoved = false;
		boolean attacking = false;
		
		if (velX != 0 || velY != 0) {
			playerMoved = true;

		} 
		if (attack || !attackTimeout.delayExpired()) {
			attacking = true;
		}
		
		if (!attack && playerMoved) {
			walkTimeout.setDelay(30);
		} 
		
		if (attack  ) {
			walkTimeout.reset();
			attackTimeout.setDelay(30);
			this.state = PlayerState.ATTACK;

		} 
		if(!playerMoved && !attacking && walkTimeout.delayExpired()){
			this.state = PlayerState.STAND;
		}
		
		if (playerMoved && !walkTimeout.delayExpired() && !attacking ) {
			this.state = PlayerState.WALK;
		} 
	}

	public void initImages() throws IOException {
		// bufferedImages = new Utils().spriteSheetCutter(SPRITE_SHEET, 4, 4, 50, 50);

		BufferedImage[] tilesA = new Utils().spriteSheetCutter(SPRITE_SHEET, 4, 4, 50, 50);
		BufferedImage[] tilesB = new Utils().spriteSheetCutter(SPRITE_SHEET_ATTACK, 4, 4, 50, 50);
		bufferedImages = Utils.appendArray(tilesA, tilesB);

	}

	public BufferedImage[] scaleImages(BufferedImage[] bufferedImages, int width, int height) {
		Image tmp_image;
		BufferedImage tmp_bimage;
		BufferedImage[] outputBufferedImage = new BufferedImage[bufferedImages.length];
		for (int i = 0; i < bufferedImages.length; i++) {
			if (bufferedImages[i] == null)
				continue;
			tmp_image = bufferedImages[i].getScaledInstance(width, height, Image.SCALE_SMOOTH);
			tmp_bimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			tmp_bimage.getGraphics().drawImage(tmp_image, 0, 0, null);
			outputBufferedImage[i] = tmp_bimage;
		}
		return outputBufferedImage;
	}

	private void playerHeal() {
		if (heal && healPacer.check() && state != PlayerState.DEAD) {
			if (health < MAX_HEALTH) {
				health += 1;
			}
		}
	}
	
	public void takeDamageFromEnemy(int damage) {
		int newHealth = health - damage;
		if(enemyPlayerDamageTimeout.delayExpired()) {
			if (newHealth>0 ) {
				health = newHealth;
			}else {
				health = 0;
			}
			if(health <= 0) { 
				state = PlayerState.DEAD;
			}
			enemyPlayerDamageTimeout.setDelay(ENEMY_PLAYER_DAMAGE_DELAY);
		}else {
			enemyPlayerDamageTimeout.reduce();
		}
		
	}
	
	public void updateStamina() {
		if(staminaPacer.check()) {
			int oldStam = stamina;
			boolean moving = (velX!=0||velY!=0);
			int regen = (run)? 1 : 2;
			
			//drain
			int newStamina = stamina;
			if (run && moving) {
				newStamina -= 1   ;
			}else if (!moving) {
				newStamina +=regen   ;
			}
			// bounds check
			if (newStamina < 0 ) {
				stamina = 0;
			}else if(newStamina >MAX_STAMINA) {
				stamina=100;
			}else {
				stamina=newStamina;
			}
			
			if(stamina <= 0) { 
				run = false;
			}
			
			if (oldStam!=stamina) {
				gp.hud.stamina = stamina;
			}
			
		}
		
		
	}

	private void attack() {
		attack = true;
		attackTimeout.setDelay(60);
		walkCycleCounter = 0;
		calculateTileForward();
		
		
		gp.inventory.selectProjectileType();
		switch(attackMode) {
		case SHOOT:
		case BOMB:
			gp.projectile.playerFireProjectile();
			break;
		case SLASH:
			gp.widget.playerAttackWidgetMelee();
			gp.particle.addParticleUnit(gp.particle.Swosh(worldX, worldY, direction));
			gp.barrier.playerAttackBarrierMelee();
			gp.entityManager.playerAttackEntityMelee();
			break;
		case SEED:
			gp.plant.plantSeed();
			break;
		case HOE:
			gp.plant.hoeGround();
			break;
		default:
			break;
		}
		
		
		
		

	}

	private void attack_0() {
		attack = true;
		attackTimeout.setDelay(60);
		walkCycleCounter = 0;
		int ax = tileForward[0] * gp.TILE_SIZE_PX;
		int ay = tileForward[1] * gp.TILE_SIZE_PX;
		Particle.ParticleUnit pu = gp.particle.addParticle(ax, ay, 2);
		int swoshSpeed = 4;
		switch (direction) {
		case 'u':
			pu.velY = -swoshSpeed;
			pu.frame = 8;
			pu.fmin = 8;
			pu.worldY += 20;
			break;
		case 'd':
			pu.velY = swoshSpeed;
			pu.frame = 9;
			pu.fmin = 8;
			pu.worldY -= 20;
			break;
		case 'l':
			pu.velX = -swoshSpeed;
			pu.frame = 10;
			pu.fmin = 8;
			pu.worldX += 20;
			break;
		case 'r':
			pu.velX = swoshSpeed;
			pu.frame = 11;
			pu.fmin = 8;
			pu.worldX -= 20;
			break;
		}
		pu.timeToLive = 10;
	}

	@Override
	public void inputListenerAction(InputAction action) {

		switch (action) {
		case UP:
			this.movesRequested[0] = true;
			break;
		case DOWN:
			this.movesRequested[1] = true;
			break;
		case LEFT:
			this.movesRequested[2] = true;
			break;
		case RIGHT:
			this.movesRequested[3] = true;
			break;
		case UPSTOP:
			// System.out.println("up");
			this.movesRequested[0] = false;
			break;
		case DOWNSTOP:
			this.movesRequested[1] = false;
			break;
		case LEFTSTOP:
			this.movesRequested[2] = false;
			break;
		case RIGHTSTOP:
			this.movesRequested[3] = false;
			break;
		case FIRE:
			attack();
			break;

		case RUN:
			toggleRun();
			break;
		default:
			break;
		}

	}

}
