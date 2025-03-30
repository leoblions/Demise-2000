package main;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Projectile {
	/*
	 * Projectile class is moving damaging sprites. They are not loaded/saved with
	 * the level Player and enemies can fire them.
	 */

	private final String SPRITE_SHEET = "/images/projectile.png";
	private final int FRAME_MAX = 3;
	private final int FRAME_PACE = 3;
	private final int SWOSH_KIND = 2;
	private final int DEFAULT_PROJECTILE_SPEED = 10;
	private final int BOMB_SPEED = 5;
	private final int MAX_IMAGE_TYPES = 6;
	private final int MAX_FRAMES = 4;
	private final int DEFAULT_TTL = 400;
	private final int SPRITE_SIZE = 50;
	private Pacer framePacer = new Pacer(5);
	GamePanel gp;
	ArrayList<ProjectileUnit> projectileUnits;
	BufferedImage[][] imageArray= new BufferedImage[MAX_IMAGE_TYPES][MAX_FRAMES];
	BufferedImage[] currentImages;
	int frame;

	public Projectile(GamePanel gp) {
		this.gp = gp;
		projectileUnits = new ArrayList<>();
		initImages();
		projectileUnits.add(new ProjectileUnit(10, 10, 1));

	}

	
	
	public void setProjectileUnitMotion(ProjectileUnit pu) {
		

		int projectileSpeed = DEFAULT_PROJECTILE_SPEED;
		switch (gp.player.direction) {
		case 'u':
			pu.velY = -projectileSpeed;
	
			pu.worldY -= 35;
			pu.worldX -= 20;
			break;
		case 'd':
			pu.velY = projectileSpeed;
	
			pu.worldY += 35;
			pu.worldX -= 20;
			break;
		case 'l':
			pu.velX = -projectileSpeed;

			pu.worldX -= 60;

			break;
		case 'r':
			pu.velX = projectileSpeed;
	
			pu.worldX += 25;

			break;
		}
	}

	public ProjectileUnit addProjectile(int worldX, int worldY, int kind) {
		ProjectileUnit pu = new ProjectileUnit(worldX, worldY, kind);
		if (kind == 0) {
			pu.worldY -= 10;
			pu.velY = 1;
		}
		if (kind == 1) {
			pu.worldY -= 20;
			pu.worldX -= 20;
		}
		pu.timeToLive = 30;
		boolean insertedPU = false;
		for (int i = 0; i < projectileUnits.size(); i++) {
			if (pu == null) {
				projectileUnits.set(i, pu);
				insertedPU = true;
			}
		}
		if (!insertedPU) {
			projectileUnits.add(pu);
		}
		return pu;
	}

	public ProjectileUnit replaceProjectileUnit(ProjectileUnit pu) {
		pu.timeToLive = DEFAULT_TTL;
		boolean insertedPU = false;
		for (int i = 0; i < projectileUnits.size(); i++) {
			if (pu == null) {
				projectileUnits.set(i, pu);
				insertedPU = true;
			}
		}
		if (!insertedPU) {
			projectileUnits.add(pu);
		}
		return pu;
	}

	public boolean playerFireProjectile() {
		int kind = gp.inventory.projectileType;
		if (kind<0) {
			return false;
		}
		ProjectileUnit pu = addProjectile(gp.player.worldX, gp.player.worldY, kind);
		setProjectileUnitMotion(pu);
		return true;
	}

	private void initImages() {
		
		try {

			BufferedImage[] bufferedImages = new Utils().spriteSheetCutter(SPRITE_SHEET, MAX_FRAMES, MAX_IMAGE_TYPES , SPRITE_SIZE, 50);
			BufferedImage[] tempRow ;
			for (int kind = 0; kind< MAX_IMAGE_TYPES;kind++) {
				tempRow = new BufferedImage[MAX_FRAMES];
				for (int frame = 0; frame< MAX_FRAMES;frame++) {
					int offset = (kind*MAX_FRAMES) + frame;
					tempRow[frame] = bufferedImages[offset];
				}
				imageArray[kind] = tempRow;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		

	}

	public void draw() {

		for (ProjectileUnit pu : projectileUnits) {
			if (null == pu || pu.timeToLive <= 0) {
				continue;
			}
			// pu.frame = pu.selector.getNextImageID();
			BufferedImage image = imageArray[pu.kind][pu.frame];
			gp.g2.drawImage(image, pu.worldX - gp.wpScreenLocX, pu.worldY - gp.wpScreenLocY, 50, 50,
					null);
		}

	}

	public void update() {
		boolean pacerValue = framePacer.check();
		for (int i = 0; i < projectileUnits.size(); i++) {
			ProjectileUnit pu = projectileUnits.get(i);
			if (pu != null) {
				if (pu.timeToLive > 0) {
					pu.timeToLive -= 1;
				}

				// pu.frame = pu.baseFrame;
				if (pu.timeToLive <= 0) {
					projectileUnits.set(i, null);

					System.out.println("nul PROJ");
				}
				if (pacerValue) {
					pu.tick();
					
				}else if(Collision.pointCollideWithSolidTile(gp,pu.worldX,pu.worldY)){
					pu.timeToLive=0;
				}
				pu.worldX += pu.velX;
				pu.worldY += pu.velY;
			}

		}

	}

	class ProjectileUnit {
		int worldX, worldY, timeToLive, kind;
		int fmin, fmax, frame, velX, velY;
		IImageSelector selector;

		public ProjectileUnit(int startX, int startY, int kind) {
			this.worldX = startX;
			this.worldY = startY;
			this.timeToLive = DEFAULT_TTL;
			this.kind = kind;
			
			frame = 0;
		}

		public void tick() {
			if (frame < FRAME_MAX) {
				frame++;
			} else {
				frame=0;
			}
		}

		

			
	}

}
