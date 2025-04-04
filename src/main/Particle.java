package main;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class Particle {

	private final String SPRITE_SHEET = "/images/particle.png";
	private final int FRAME_MAX = 3;
	private final int FRAME_PACE = 3;
	private final int SWOSH_KIND = 2;
	private final int DEFAULT_SPEED = 3;
	private final int DEFAULT_TTL = 10;
	private Pacer framePacer = new Pacer(5);
	GamePanel gp;
	ArrayList<ParticleUnit> particleUnits;
	BufferedImage[] bufferedImages;
	int frame;

	public Particle(GamePanel gp) {
		this.gp = gp;
		particleUnits = new ArrayList<>();
		initImages();
		particleUnits.add(new ParticleUnit(10, 10, 1));

	}
	
	public ParticleUnit Swosh (int startX, int startY, char direction) {
		
		
		 ParticleUnit pu = new ParticleUnit(startX, startY, SWOSH_KIND);

		pu.timeToLive = DEFAULT_TTL;
		int swoshSpeed =DEFAULT_SPEED;
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

	public ParticleUnit addParticle(int worldX, int worldY, int kind) {
		ParticleUnit pu = new ParticleUnit(worldX, worldY, kind);
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
		for (int i = 0; i < particleUnits.size(); i++) {
			if (pu==null) {
				particleUnits.set(i,pu);
				insertedPU=true;
			}
		}
		if (!insertedPU) {
			particleUnits.add(pu);
		}
		return pu;
	}
	
	public void  addParticleUnit(ParticleUnit pu) {
		pu.timeToLive = DEFAULT_TTL;
		boolean insertedPU = false;
		for (int i = 0; i < particleUnits.size(); i++) {
			if (pu==null) {
				particleUnits.set(i,pu);
				insertedPU=true;
			}
		}
		if (!insertedPU) {
			particleUnits.add(pu);
		}
	}

	private void initImages() {
		try {
			bufferedImages = new Utils().spriteSheetCutter(SPRITE_SHEET, 4, 6, 50, 50);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void draw() {

		for (ParticleUnit pu : particleUnits) {
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
		for (int i = 0; i < particleUnits.size(); i++) {
			ParticleUnit pu = particleUnits.get(i);
			if (pu!=null) {
				if(pu.timeToLive>0) {
					pu.timeToLive-=1;
				}
				
				//pu.frame = pu.baseFrame;
				if (pu.timeToLive<=0) {
					particleUnits.set(i,null);
				}
				if(pacerValue  ) {
					pu.tick();
				}

				pu.worldX += pu.velX;
				pu.worldY += pu.velY;
			}
			
		}

	}
	

	class ParticleUnit {
		int worldX, worldY, timeToLive, kind;
		int fmin, fmax, frame, velX, velY;
		IImageSelector selector;

		public ParticleUnit(int startX, int startY, int kind) {
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
			case 4:
				fmin = 16;
				fmax = 19;
				break;
			case 5:
				fmin = 20;
				fmax = 23;
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
		
//		private void initImageSelectorByKind(int kind) {
//			IImageSelector is = new IImageSelector() {
//				int kind ;
//				int min ;
//				int max ;
//				int current = min;
//				int limit ;
//				int changeImageCounter = 0;
//				@Override
//				public int getNextImageID() {
//					this.tick();
//					return current;
//				}
//				
//				public void tick() {
//					if(changeImageCounter < limit) {
//						//delay
//						changeImageCounter +=1;
//					}else {
//						//change image
//						if(current < max) {
//							current +=1;
//						}else {
//							current = min;
//						}
//						
//						changeImageCounter = 0;
//					}
//				}
//
//				@Override
//				public void setParams(int kind, int min, int max, int limit) {
//					this.kind = kind;
//					this.min = min;
//					this.max=max;
//					this.limit=limit;
//					
//					
//				}
//			};
//			
//			switch(kind) {
//			case 0://leaves
//				is.setParams(0, 0, 3, 10);
//				break;
//			case 1://blood
//				is.setParams(1, 4, 7, 10);
//				break;
//			case 2://swoosh
//				is.setParams(2, 8, 11, 10);
//				break;
//			case 3:
//				is.setParams(3, 12, 15, 10);
//				break;
//			default:
//				is.setParams(0, 0, 3, 10);
//				break;
//			}
//			
//			selector = is;
//		}
	}

}


