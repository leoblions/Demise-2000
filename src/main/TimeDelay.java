package main;
import static java.lang.Math.abs;

public class TimeDelay {
	long interval = 10000;
	long last;
	
	public TimeDelay(long milliSeconds) {
		interval = milliSeconds;
		last = System.currentTimeMillis();
	}
	
	public void reset() {
		last = System.currentTimeMillis();
	}
	public void setInterval(long interval) {
		if(interval>0) {
			this.interval=interval;
		}
	}
	public boolean check() {
		var now = System.currentTimeMillis();
		if(abs(now-last)>interval) {
			last=now;
			return true;
		}else {
			return false;
		}
		
	}
}
