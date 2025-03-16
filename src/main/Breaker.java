package main;

public class Breaker {
	/*
	 * Returns true on first get,
	 * then false on subsequent gets 
	 * until it is reset.
	 */
	private boolean state = true;
	public Breaker() {
		
	}
	public boolean get() {
		//trip breaker
		if(state==true) {
			state=false;
			return true;
		}else {
			return false;
		}
	}
	public boolean queryState() {
		return state;
	}
	public void reset() {
		state=true;
	}

}
