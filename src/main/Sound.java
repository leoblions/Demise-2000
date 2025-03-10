package main;

import java.awt.Color;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;

import main.GamePanel.InputAction;

public class Sound implements IInputListener, Runnable{
	
	
	ArrayList<Integer> soundPlayList; //store subscript of sound file to play
	Iterator<Integer> splIterator ;
	
	public final int TOTAL_CLIPS = 30;
	Clip clip;
	Clip[] clips;
	URL soundURL[]  ; //use this aray to store path of sound files
	boolean soundEnabled = true;
	GamePanel gp;
	boolean runThread=true;
	Thread soundThread;
	boolean blockAddQueue = false;
	public long INITIAL_DEBOUNCE_COUNTER=3000000;
	public long DEF_DEBOUNCE_COUNTER=3000000;
	boolean clipPlayFlags[];
	boolean inputListenerAdded = false;
	
	public Sound(GamePanel gp) {
		this.gp=gp;
		initFiles();
		initClips();
		clipPlayFlags=new boolean[TOTAL_CLIPS];
		soundPlayList = new ArrayList<Integer>();
		splIterator = soundPlayList.iterator();
		soundThread = new Thread(this);
		soundThread.start();
		
		if(inputListenerAdded==false) {
			gp.input.addListener(this);
			inputListenerAdded=true;
		}
		
		//System.out.println("thread start 1");
	}
		
	
	
	private void initFiles() {
		this.soundURL = new URL[TOTAL_CLIPS];
		soundURL[0]=getClass().getResource("/sound/aah.wav");
		soundURL[1]=getClass().getResource("/sound/mmm.wav");
		soundURL[2]=getClass().getResource("/sound/boing.wav");
		soundURL[3]=getClass().getResource("/sound/coo.wav");
		soundURL[4]=getClass().getResource("/sound/plop.wav");
	}
	
	private void initClips() {
		this.clips = new Clip[TOTAL_CLIPS];
		int clipIndex = 0;
		for (URL url : soundURL) {
			if (url!=null) {
				try {
					AudioInputStream ais = AudioSystem.getAudioInputStream(url); //create AIS from URL
					clips[clipIndex] = AudioSystem.getClip(); //get clip object for playing AIS object
					clips[clipIndex].open(ais);
				}catch (Exception e){}
				
				
			}
			clipIndex++;
		}
		
	}
	
	public void setFile(int i) {
		//ethod selects a sound file and creates objects needed to play it
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]); //create AIS from URL
			clip = AudioSystem.getClip(); //get clip object for playing AIS object
			clip.open(ais);
		}catch (Exception e) {
			
		}
		
	}
	
	public void play() {
		if(true) {
			//System.out.println("play");
			clip.start();
		}
		
		
		
		
	}
	
	public void playSE(int i) {
		if(soundEnabled) {
			//System.out.println("playse");
			setFile(i);
			play();
		}
		
	}
	
	public void playClipSE(int i) {
		if(soundEnabled) {
			//System.out.println("playclipse");
			clips[i].start();
			clips[i].stop();
		}
		
	}
	
	public void update() {
		if(inputListenerAdded==false) {
			gp.input.addListener(this);
			inputListenerAdded=true;
		}
	}
	
	
	public void enqueueSE(int i) {




			
			soundPlayList.add(i);
			


		
	}
	
	public void loop() {
		if(soundEnabled) {
			
		clip.loop(Clip.LOOP_CONTINUOUSLY);
		}
	}
	
	public void stop() {
		
		clip.stop();
		
	}

	@Override
	public void inputListenerAction(InputAction action) {
		switch(action) {
		case MUTE:
			System.out.println("mute");
			soundEnabled = !soundEnabled;
		break;
		default:
			break;
		}
		
	}
	
	public void playMusic(int i) {
		setFile(i);
		play();
		loop();
		
	}
	public void stopMusic() {
		stop();
	}
		



	@Override
	public void run() {
		long debounceCounter = 0;
		long initialCountdown = DEF_DEBOUNCE_COUNTER*2;
		
		while(soundThread!=null) {
			//if(clip!=null)clip.stop();
			//System.out.println("running");
			
			for (int i = 0;i < clipPlayFlags.length;i++) {
				if(clipPlayFlags[i]==true&& (debounceCounter>=DEF_DEBOUNCE_COUNTER||initialCountdown<=0)){
					if(soundEnabled)playSE(i);
					//System.out.println("run playing");
					clipPlayFlags[i]=false;
					initialCountdown =DEF_DEBOUNCE_COUNTER*2;
					debounceCounter=0;
				}else if(clipPlayFlags[i]==true&& debounceCounter<DEF_DEBOUNCE_COUNTER) {
					debounceCounter++;
					//don't play
					initialCountdown--;
					if(initialCountdown<0)initialCountdown=0;
					
				}
				if(debounceCounter>DEF_DEBOUNCE_COUNTER)debounceCounter=DEF_DEBOUNCE_COUNTER;
			} 
			//soundPlayList.clear();
			
			
		}
		
		
 	}
		
		
		
	



	
}
