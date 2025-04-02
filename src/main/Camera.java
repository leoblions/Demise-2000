package main;

public class Camera {
	//leash camera
	GamePanel gp;
	int screenCenterWorldX, screenCenterWorldY;
	double playerCenterDistance;
	public final double MOVE_CAMERA_THRESHOLD_DISTANCE = 100;
	private final int SPEEDUP_THRESHOLD = 450;
	public Camera(GamePanel gp) {
		/*
		 * The camera lazily tracks the player position.
		 * Changes screen to world offset as player moves
		 */
		this.gp=gp;
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
	
	public void recenterCamera() {
		//int playerScreenX = gp.player.worldX - gp.wpScreenLocX;
		//int playerScreenY = gp.player.worldY - gp.wpScreenLocY;
		gp.wpScreenLocY =  gp.player.worldY - (gp.HEIGHT/2);
		gp.wpScreenLocX =  gp.player.worldX - (gp.WIDTH/2);
		//gp.player.playerScreenX = playerScreenX;
		//gp.player.playerScreenY = playerScreenY;
	}
	
	
	public void update() {
		int playerScreenX = gp.player.worldX - gp.wpScreenLocX;
		int playerScreenY = gp.player.worldY - gp.wpScreenLocY;
		int cameraSpeed = gp.player.velocity;
		
		//move camera up
		if(playerScreenY < (gp.HEIGHT/2) - MOVE_CAMERA_THRESHOLD_DISTANCE) {
			gp.wpScreenLocY -= cameraSpeed;
		}
		
		//move camera down
		if(playerScreenY > (gp.HEIGHT/2) + MOVE_CAMERA_THRESHOLD_DISTANCE) {
			gp.wpScreenLocY += cameraSpeed;
		}
		//move cam left
		if(playerScreenX < (gp.WIDTH/2) - MOVE_CAMERA_THRESHOLD_DISTANCE) {
			gp.wpScreenLocX -= cameraSpeed;
		}
		
		//move camera down
		if(playerScreenX > (gp.WIDTH/2) + MOVE_CAMERA_THRESHOLD_DISTANCE) {
			gp.wpScreenLocX += cameraSpeed;
		}
		
		gp.player.playerScreenX = playerScreenX;
		gp.player.playerScreenY = playerScreenY;
		
		
	}

}
