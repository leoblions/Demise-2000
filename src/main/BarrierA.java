package main;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

public class BarrierA {
	/*
	 * grid x,   grid y,   grid width,  grid height,  kind, id
	 * 
	 * kinds:
	 * 0 fragile wall
	 * 1 gate
	 * 2 cobweb
	 * 3 laser
	 */
	ArrayList<BarrierUnit> barriers;
	HashMap<Integer,BufferedImage[]> imageMap;
	GamePanel gp;
	public BarrierA(GamePanel gp) {
		this.gp=gp;
		barriers=new ArrayList<>();
		
	}
	
	public void initImages() {
		imageMap = new HashMap<>();
	}
	
	public void draw() {
		
		
		}
	
	public void update() {
		
		
	}
	
	public class BarrierUnit{
		int kind, gx, gy, gw, gh;
		boolean active;
		public BarrierUnit(int gx, int gy, int gw, int gh, int kind) {
			this.gx = gy;
			this.gy = gy;
			this.gh=gx;
			this.gw=gw;
			active=true;
			
		}
	}

}
