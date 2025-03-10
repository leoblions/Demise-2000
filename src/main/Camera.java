package main;

public class Camera {
	//leash camera
	GamePanel gp;
	int screenCenterWorldX, screenCenterWorldY;
	double playerCenterDistance;
	public final double MOVE_CAMERA_THRESHOLD_DISTANCE = 100;
	public Camera(GamePanel gp) {
		this.gp=gp;
		//this.addListener(gp.player);
		System.out.println("camera created");
		screenCenterWorldX=gp.wpScreenLocX+(gp.WIDTH/2);
		screenCenterWorldY=gp.wpScreenLocY+(gp.HEIGHT/2);
	}
	
	public double distance(int x1, int y1, int x2, int y2) {
		double x1d = (double)x1;
		double y1d = (double)y1;
		double x2d = (double)x2;
		double y2d = (double)y2;
		double retval=0;
		//pythagorean theorem
		
		retval = Math.sqrt(Math.pow( (y2d - y1d ),2)+ Math.pow( (x2d-x1d),2)) ;
		return retval;
	}
	
	
	
	
	public void update() {
		int playerScreenX = gp.player.worldX - gp.wpScreenLocX;
		int playerScreenY = gp.player.worldY - gp.wpScreenLocY;
		//move camera up
		if(playerScreenY < (gp.HEIGHT/2) - MOVE_CAMERA_THRESHOLD_DISTANCE) {
			gp.wpScreenLocY -= gp.player.velocity;
		}
		
		//move camera down
		if(playerScreenY > (gp.HEIGHT/2) + MOVE_CAMERA_THRESHOLD_DISTANCE) {
			gp.wpScreenLocY += gp.player.velocity;
		}
		//move cam left
		if(playerScreenX < (gp.WIDTH/2) - MOVE_CAMERA_THRESHOLD_DISTANCE) {
			gp.wpScreenLocX -= gp.player.velocity;
		}
		
		//move camera down
		if(playerScreenX > (gp.WIDTH/2) + MOVE_CAMERA_THRESHOLD_DISTANCE) {
			gp.wpScreenLocX += gp.player.velocity;
		}
		
		gp.player.playerScreenX = playerScreenX;
		gp.player.playerScreenY = playerScreenY;
		
		
	}

}
