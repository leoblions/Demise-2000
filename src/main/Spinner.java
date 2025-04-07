package main;

import java.awt.Color;

import main.GamePanel.GameState;

public class Spinner {
	/*
	 * makes an animated spinning triangle
	 */
	GamePanel gp;
	int[] xPoints;
	int[] yPoints;
	int[] memoX,memoY;
	int[] angles;
	int nPoints = 3;
	int radius;
	boolean visible = false;
	double adjustedRadius;
	boolean pulse = false;
	int pulseAmount = 70;
	Color color ;
	int red = 200;
	int green = 100;
	int blue = 100;
	Wave colorWave;
	int centerX,centerY;
	float toRads = 0.0175f;
	public Spinner(GamePanel gp, int centerX, int centerY, int radius) {
		this.gp=gp;
		this.colorWave = new Wave(pulseAmount);
		
		color =  new Color(red,green,blue,100);
		this.centerX=centerX;
		this.centerY=centerY;
		this.radius=radius;
		xPoints = new int[nPoints];
		yPoints = new int[nPoints];
		angles = new int[nPoints];
		initAngles();
		calculatePoint(0);
		calculatePoint(1);
		calculatePoint(2);
		memoizePoints();
		//System.out.printf("Point X%d Y%d \n",xPoints[0],yPoints[0]);
		
	}
	
	public Spinner NgonSpinner(GamePanel gp, int centerX, int centerY, int radius, int sides) {
		Spinner sp = new Spinner(gp, centerX, centerY, radius);
		sp.gp=gp;
		sp.nPoints = sides;
		sp.centerX=centerX;
		sp.centerY=centerY;
		sp.radius=radius;
		sp.xPoints = new int[nPoints];
		sp.yPoints = new int[nPoints];
		sp.angles = new int[nPoints];
		sp.initNAngles(sides);
		sp.calculatePoint(0);
		sp.calculatePoint(1);
		sp.calculatePoint(2);
		//System.out.printf("Point X%d Y%d \n",xPoints[0],yPoints[0]);
		return sp;
		
	}
	
	private void initAngles(){
		angles[0] = 0;
		angles[1] = 120;
		angles[2] = 240;
		
	}
	private void initNAngles(int sides){
		int degreesPerSide = 360 / sides;
		int degreeCounter = 0;
		for(int i = 0; i< sides;i++) {
			angles[i] = degreeCounter;
			degreeCounter +=degreesPerSide;
		}
		
	}
	private void incrementAngles(){
		for(int i = 0; i<angles.length;i++) {
			angles[i]+=1;
			if (angles[i] >= 360){
				angles[i] = 0;
			}
		}
		
	}
	
	private void calculatePoint(int pointID){
		// soh cah toa
		this.yPoints[pointID] = (int) (Math.sin(toRads*angles[pointID])*radius) + centerY;
		this.xPoints[pointID] = (int) (Math.cos(toRads*angles[pointID])*radius) + centerX;
	}
	
	private void calculatePointPulse(int pointID){
		adjustedRadius = radius - pulseAmount* Math.cos(toRads*angles[0]);
		//System.out.println(adjustedRadius);
		this.yPoints[pointID] = (int) (Math.sin(toRads*angles[pointID])*adjustedRadius) + centerY;
		this.xPoints[pointID] = (int) (Math.cos(toRads*angles[pointID])*adjustedRadius) + centerX;
	}
	
	private int getXPoint(int angle){
		return (int) (Math.cos(toRads*angle)*radius) + centerX;
	}
	
	private int getYPoint(int angle){
		return (int) (Math.sin(toRads*angle)*radius) + centerY;
		
	}
	
	private int getXPointPulse(int angle){
		adjustedRadius = radius - pulseAmount* Math.cos(toRads*angle);
		//System.out.println(adjustedRadius);
		return (int) (Math.cos(toRads*angle )*adjustedRadius) + centerX;
	}
	
	private int getYPointPulse(int angle){
		adjustedRadius = radius - pulseAmount* Math.cos(toRads*angle);
		//System.out.println(adjustedRadius);
		return (int) (Math.sin(toRads*angle )*adjustedRadius) + centerY;
		
	}
	
	public void draw() {
		if (visible) {
			gp.g2.setColor(color);
			gp.g2.fillPolygon(xPoints, yPoints, nPoints);
			
			}
		}
		
	
	private void memoizePoints() {
		memoX = new int[360];
		memoY = new int[360];
		for(int i = 0;i<360;i++) {
			if (pulse) {
				memoX[i] =  getXPointPulse(  i);
				memoY[i] =  getYPointPulse(  i);
			}else {
				memoX[i] =  getXPoint(  i);
				memoY[i] =  getYPoint(  i);
			}
		}
	}
	public void update() {
		
		if(gp.gameState==GameState.MENU||gp.gameState==GameState.PAUSED) {
			visible=true;
		}else {
			visible=false;
			return;
		}
		
		incrementAngles();
		int blueCh = blue+colorWave.getValue();
		color =  new Color(red,green,blueCh,100);
		//System.out.println(blueCh);
		for(int i = 0;i< angles.length;i++) {
			yPoints[i] = memoY[angles[i]];
			xPoints[i] = memoX[angles[i]];
		}
		

		
		
	}

}
