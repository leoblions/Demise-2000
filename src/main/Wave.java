package main;

public class Wave {
	/*
	 * returns numbers based on multiplying a given number by a sine wave
	 */
	public int multiplier;
	public int progress = 0;
	static float toRad = 0.017f;
	public Wave(int multiplier) {
		this.multiplier = multiplier;
	}
	
	public void reset() {
		progress =0;;
	}
	public int getValue(){
		int value =  (int) (multiplier * Math.sin(progress*toRad));
		if (progress<360) {
			progress++;
		}else {
			progress=0;
		}
		return value;
	}
}
