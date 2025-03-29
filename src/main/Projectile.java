package main;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class Projectile {

	private final String SPRITE_SHEET = "/images/particle.png";
	private final int FRAME_MAX = 3;
	private final int FRAME_PACE = 3;
	private final int SWOSH_KIND = 2;
	private Pacer framePacer = new Pacer(5);
	GamePanel gp;
	ArrayList<ProjectileUnit> ProjectileUnits;
	BufferedImage[] bufferedImages;
	int frame;

	public Projectile(GamePanel gp) {
		this.gp = gp;
		ProjectileUnits = new ArrayList<>();
		initImages();
		ProjectileUnits.add(new ProjectileUnit(10, 10, 1));

	}
	
	public ProjectileUnit Swosh (int startX, int startY, char direction) {
		
		
		 ProjectileUnit pu = new ProjectileUnit(startX, startY, SWOSH_KIND);

		pu.timeToLive = 10;
		int swoshSpeed =3;
		switch (direction) {
		case 'u':
			pu.velY = -swoshSpeed;
			pu.frame = 8;
			pu.fmin = 8;
			pu.worldY -= 35;
			pu.worldX -= 20;
			break;
		case 'd':
			pu.velY = swoshSpeed;
			pu.frame = 9;
			pu.fmin = 8;
			pu.worldY += 35;
			pu.worldX -= 20;
			break;
		case 'l':
			pu.velX = -swoshSpeed;
			pu.frame = 10;
			pu.fmin = 8;
			pu.worldX -= 60;
			
			break;
		case 'r':
			pu.velX = swoshSpeed;
			pu.frame = 11;
			pu.fmin = 8;
			pu.worldX += 25;
			
			break;
		}
		return pu;
	}

	public ProjectileUnit addParticle(int worldX, int worldY, int kind) {
		ProjectileUnit pu = new ProjectileUnit(worldX, worldY, kind);
		if (kind==0) {
			pu.worldY-=10;
			pu.velY=1;
		}
		if (kind==1) {
			pu.worldY-=20;
			pu.worldX -=20;
		}
		pu.timeToLive = 30;
		boolean insertedPU = false;
		for (int i = 0; i < ProjectileUnits.size(); i++) {
			if (pu==null) {
				ProjectileUnits.set(i,pu);
				insertedPU=true;
			}
		}
		if (!insertedPU) {
			ProjectileUnits.add(pu);
		}
		return pu;
	}
	
	public void  addProjectileUnit(ProjectileUnit pu) {
		pu.timeToLive = 30;
		boolean insertedPU = false;
		for (int i = 0; i < ProjectileUnits.size(); i++) {
			if (pu==null) {
				ProjectileUnits.set(i,pu);
				insertedPU=true;
			}
		}
		if (!insertedPU) {
			ProjectileUnits.add(pu);
		}
	}

	private void initImages() {
		try {
			bufferedImages = new Utils().spriteSheetCutter(SPRITE_SHEET, 4, 4, 50, 50);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void draw() {

		for (ProjectileUnit pu : ProjectileUnits) {
			if (null == pu|| pu.timeToLive<=0) {
				continue;
			}
			//pu.frame = pu.selector.getNextImageID();
			
			gp.g2.drawImage(bufferedImages[pu.frame], pu.worldX - gp.wpScreenLocX, pu.worldY - gp.wpScreenLocY, 50, 50,
					null);
		}

	}

	public void update() {
		boolean pacerValue = framePacer.check();
		for (int i = 0; i < ProjectileUnits.size(); i++) {
			ProjectileUnit pu = ProjectileUnits.get(i);
			if (pu!=null) {
				if(pu.timeToLive>0) {
					pu.timeToLive-=1;
				}
				
				//pu.frame = pu.baseFrame;
				if (pu.timeToLive<=0) {
					ProjectileUnits.set(i,null);
				}
				if(pacerValue  ) {
					pu.tick();
					pu.worldX += pu.velX;
					pu.worldY += pu.velY;
				}
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
			this.kind = kind;
			switch (kind) {
			case 0:
				fmin = 0;
				fmax = 3;
				break;
			case 1:
				fmin = 4;
				fmax = 7;
				break;
			case 2:
				fmin = 8;
				fmax = 11;
				break;
			case 3:
				fmin = 12;
				fmax = 15;
				break;
			default:
				fmin = 0;
				fmax = 0;
			}
			frame = fmin;
		}
		
		public void tick() {
			if (kind==SWOSH_KIND) {
				
			}else{
				if(frame < fmax) {
					frame +=1;
				}else {
					frame = fmin;
				}
			}
		}
		
		private void initImageSelectorByKind(int kind) {
			IImageSelector is = new IImageSelector() {
				int kind ;
				int min ;
				int max ;
				int current = min;
				int limit ;
				int changeImageCounter = 0;
				@Override
				public int getNextImageID() {
					this.tick();
					return current;
				}
				
				public void tick() {
					if(changeImageCounter < limit) {
						//delay
						changeImageCounter +=1;
					}else {
						//change image
						if(current < max) {
							current +=1;
						}else {
							current = min;
						}
						
						changeImageCounter = 0;
					}
				}

				@Override
				public void setParams(int kind, int min, int max, int limit) {
					this.kind = kind;
					this.min = min;
					this.max=max;
					this.limit=limit;
					
					
				}
			};
			
			switch(kind) {
			case 0://leaves
				is.setParams(0, 0, 3, 10);
				break;
			case 1://blood
				is.setParams(1, 4, 7, 10);
				break;
			case 2://swoosh
				is.setParams(2, 8, 11, 10);
				break;
			case 3:
				is.setParams(3, 12, 15, 10);
				break;
			default:
				is.setParams(0, 0, 3, 10);
				break;
			}
			
			selector = is;
		}
	}

}


