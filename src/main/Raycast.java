package main;

import java.awt.Color;
import java.awt.Rectangle;

public class Raycast {
	
	public final int RAY_SIZE = 4;
	public final boolean SHOW_DOTS =false;
	public final int RAY_LENGTH =15;
	public final int RAY_DOT_SPACING = 25;
	Rectangle square;
	Color TRANSPARENT = new Color(0.5f,0.5f,0.5f,0.0f);
	GamePanel gp;
	Ray[] rays;
	
	double playerCenterDistance;
	
	public Raycast(GamePanel gp) {
		this.gp=gp;
		
		square = new Rectangle();
		initRays360();
	
		
	}
	
	public void initRays() {
		rays = new Ray[40];
		rays[0] = new Ray(gp.player.worldX,gp.player.worldY,0,-25);
		rays[1] = new Ray(gp.player.worldX,gp.player.worldY,0,25);
		rays[2] = new Ray(gp.player.worldX,gp.player.worldY,17,-17);
		rays[3] = new Ray(gp.player.worldX,gp.player.worldY,25,0);
		rays[4] = new Ray(gp.player.worldX,gp.player.worldY,17,17);
		rays[5] = new Ray(gp.player.worldX,gp.player.worldY,-17,-17);
		rays[6] = new Ray(gp.player.worldX,gp.player.worldY,-25,0);
		rays[7] = new Ray(gp.player.worldX,gp.player.worldY,-17,17);
		
		rays[10] = new Ray(gp.player.worldX,gp.player.worldY,9,-23);
		rays[11] = new Ray(gp.player.worldX,gp.player.worldY,9,23);
		rays[12] = new Ray(gp.player.worldX,gp.player.worldY,23,-9);
		rays[13] = new Ray(gp.player.worldX,gp.player.worldY,23,9);
		rays[14] = new Ray(gp.player.worldX,gp.player.worldY,-9,23);
		rays[15] = new Ray(gp.player.worldX,gp.player.worldY,-9,-23);
		rays[16] = new Ray(gp.player.worldX,gp.player.worldY,-23,9);
		rays[17] = new Ray(gp.player.worldX,gp.player.worldY,-23,-9);
		
		rays[20] = new Ray(gp.player.worldX,gp.player.worldY,4,-24);
		rays[21] = new Ray(gp.player.worldX,gp.player.worldY,4,24);
		rays[22] = new Ray(gp.player.worldX,gp.player.worldY,24,-4);
		rays[23] = new Ray(gp.player.worldX,gp.player.worldY,24,4);
		rays[24] = new Ray(gp.player.worldX,gp.player.worldY,-4,24);
		rays[25] = new Ray(gp.player.worldX,gp.player.worldY,-4,-24);
		rays[26] = new Ray(gp.player.worldX,gp.player.worldY,-24,4);
		rays[27] = new Ray(gp.player.worldX,gp.player.worldY,-24,-4);
		
		rays[30] = new Ray(gp.player.worldX,gp.player.worldY,14,-21);
		rays[31] = new Ray(gp.player.worldX,gp.player.worldY,14,21);
		rays[32] = new Ray(gp.player.worldX,gp.player.worldY,21,-14);
		rays[33] = new Ray(gp.player.worldX,gp.player.worldY,21,14);
		rays[34] = new Ray(gp.player.worldX,gp.player.worldY,-21,14);
		rays[35] = new Ray(gp.player.worldX,gp.player.worldY,-21,-14);
		rays[36] = new Ray(gp.player.worldX,gp.player.worldY,-14,21);
		rays[37] = new Ray(gp.player.worldX,gp.player.worldY,-14,-21);
	}
	
	public void initRays360() {
		rays = new Ray[360];
		int rise,run;
		for (int deg = 0; deg <360; deg++) {
			//RAY_DOT_SPACING is the hypotenuse
			rise = (int) (RAY_DOT_SPACING * Math.sin((double)deg));
			run  = (int) (RAY_DOT_SPACING * Math.cos((double)deg));
			rays[deg] = new Ray(gp.player.worldX,gp.player.worldY,rise,run);
		}
	}
	class Ray{
		int startX, startY, stepX, stepY;

		public Ray(int startX, int startY, int stepX, int stepY) {
			super();
			this.startX = startX;
			this.startY = startY;
			this.stepX = stepX;
			this.stepY = stepY;
		}
	}
	
	public void draw() {
		if(GamePanel.drawShadows==false)return;
		int currY, currX;
		for (int j = 0; j< rays.length ;j++) {
			Ray ray =rays[j];
			if(ray==null)continue;
			gp.g2.setColor(Color.black);
			boolean wallHit = false;
			for(int i = 1;i<RAY_LENGTH;i++) {
				
				currX =  ray.startX + i*ray.stepX ;
				currY =  ray.startY + i*ray.stepY ;
				//boolean wallHit = false;
				//square.x = currX;
				//square.y = currY;
				//square.x = gp.player.worldX  + i*ray.stepX ;
				//square.y = gp.player.worldY  + i*ray.stepY;
				//square.width = RAY_SIZE;
				//square.height = RAY_SIZE;
				
				try {
					int kind = Collision.tileAtWorldCoord(gp, currX, currY);
					if(!Collision.tileKindIsSolid(kind) && wallHit==false ) {
						gp.g2.setColor(Color.white);
						gp.shadow.setTransparentW(currX, currY);
						//wallHit = true;
						if (SHOW_DOTS) gp.g2.fillRect(currX-gp.wpScreenLocX, currY-gp.wpScreenLocY , 6, 6);
						continue;
					}else {
						gp.shadow.setShadowW(currX, currY);
						
						gp.g2.setColor(Color.blue);
						wallHit=true;
						if (SHOW_DOTS)gp.g2.fillRect(currX-gp.wpScreenLocX, currY-gp.wpScreenLocY, RAY_SIZE, RAY_SIZE);
						 
					}
					
					

					//gp.g2.setColor(Color.black);
					
				}catch (Exception e) {continue;}
				
				
				
				
			}
		}
		
		
	}
	
	public void update() {
		if(GamePanel.drawShadows==false)return;
		for (Ray ray: rays) {
			if(ray==null)continue;
			ray.startX = gp.player.worldX;
			ray.startY = gp.player.worldY;
		}
		
		
	}

}
