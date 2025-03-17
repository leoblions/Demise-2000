package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.IOException;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable{
	//runnable interface needed for using threads, 
	// thread will run the run() method
	//launchGame starts the thread
	
	
	//settings flags
	public static boolean drawShadows = false;
	public static boolean drawCollRect = false;
	public static boolean godMode = false;
	public static boolean noClip = false;
	
	
	//consts dealing with game-loop
	
	public static final long FRAMES_PER_SECOND = 60;
	public static final long TICKS_PER_SECOND = 60;
	Thread gameThread;
	
	public static final boolean LOAD_LEVEL_ON_START = true;
	public static final int LEVEL_TO_LOAD_ON_START = 0;
	
	public final String SETTINGS_FILE="settings.ini";
	public static final int WIDTH = 720;
	public static final int HEIGHT = 600;
	public static final int TILE_SIZE_PX = 50; 
	public static final int MAP_TILES_X = 100; 
	public static final int MAP_TILES_Y = 100;
	public static final String LEVEL_DATA_SUBDIR = "leveldata";
	
	// variables relating to camera
	public static final int worldSizePxX = TILE_SIZE_PX*MAP_TILES_X;
	public static final int worldSizePxY = TILE_SIZE_PX*MAP_TILES_Y;
	/** 
	 * location of the top left corner of the screen
	 *  in the world coord space, in pixels
	 */
	public static int wpScreenLocX =0;
	public static int wpScreenLocY =0;
	public static int mouseX =0;
	public static int mouseY =0;
	//public static int[][] tileGrid;
	public int[] visibleArea;
	
	/**
	 * flag to tell certain methods not to run until map tiles are done being placed
	 */
	public boolean tilesPlacedComplete = false;
	public int level = 0;
	boolean playerPressActivate = false;;
	
	/**
	 * Declare main game objects
	 * 
	 */
	Graphics  g;
	static Graphics2D g2;
	TileManager tileManager;
	Player player;
	Input input;
	GameState gameState;
	Camera camera;
	Collision collision;
	Raycast raycast;
	Shadow shadow;
	EntityManager entityManager;
	Item item;
	HUD hud;
	SettingsIO settingsio;
	Decor decor;
	Sound sound;
	Thread soundThread;
	Widget widget;
	Editor editor;
	PathFind pathFind;
	//RasterString rs1;
	Inventory inventory;
	Conversation conversation;
	Zone zone;
	Brain brain;
	public enum InputAction{
		UP,
		DOWN,
		LEFT,
		RIGHT,
		UPSTOP,
		DOWNSTOP,
		LEFTSTOP,
		RIGHTSTOP,
		PAUSE,
		MENU,
		ACTION,
		FIRE,
		MESSAGE,
		INFO,
		RUN, MUTE, ACTIONRELEASE
	}
	
	public enum GameState{
		GAME,
		PAUSED,
		GAMEOVER,
		MENU
	}
	
	private static final long serialVersionUID = 6644375181764124582L;

	public GamePanel() {
		// panel settings
		this.setPreferredSize(new Dimension(WIDTH,HEIGHT));
		this.setBackground(Color.black);
		this.setLayout(null);
		this.setFocusable(true);
		this.requestFocus(true);
		
		loadSettingsFile();
		
		addMouseListener(new MouseAdapter() {
			 @Override
             public void mousePressed(MouseEvent e) {
				 mouseX = e.getX();
				 mouseY = e.getY();
				 
				 int kind = e.getButton();
				 editor.handleClick(kind, 1);
                 System.out.printf("Mouse down %d %d %d\n",mouseX, mouseY, kind);
                 switch(kind) {
                 case 1:
                	 editor.paintAsset();
                	 
                	 break;
                 case 2:
                	 editor.incrementAssetID(1);
                	 break;
                 case 3:
                	 editor.incrementAssetID(-1);
                	 break;
                default:
                	break;
                
                 }
             }

             @Override
             public void mouseReleased(MouseEvent e) {
            	 int kind = e.getButton();
            	 editor.handleClick(kind, 0);
            	 System.out.printf("Mouse up %d %d %d\n",mouseX, mouseY,kind);
             }
            
		});
		
		editor = new Editor(this);
		tileManager = new TileManager(this);
		player = new Player(this);
		input = new Input(this);
		this.addKeyListener(input);
		gameState = GameState.GAME;
		camera = new Camera(this);
		collision = new Collision(this);
		raycast = new Raycast(this);
		shadow = new Shadow(this);
		hud = new HUD(this);
		pathFind = new PathFind(this);
		widget = new Widget(this);
		visibleArea = Utils.getVisibleArea(this);
		entityManager = new EntityManager(this);
		inventory = new Inventory(this);
		item = new Item(this);
		zone = new Zone(this);
		conversation = new Conversation(this);
		brain = new Brain(this);
		//rs1 = new RasterString(this, "TEST", 45, 45);
		
		
		
		decor = new Decor(this);
		
		sound = new Sound(this);
		if(LOAD_LEVEL_ON_START) {
			level = LEVEL_TO_LOAD_ON_START;
			this.editor.loadComponentData();
			conversation.loadDataFromFileCurrentRoom();
		}
		
		//this must come last, put nothing below it
		launchGame();
	}
	
	
	
	public void launchGame() {
		gameThread = new Thread(this);
		gameThread.start();
		
	}

	@Override
	public void run() {
		// game loop code here
		long lastTime = System.currentTimeMillis();
		long thisTime = System.currentTimeMillis();
		long deltaTime=0;
		long overshoot =0;
		long msPerFrame = 1000 / FRAMES_PER_SECOND;
		
		while(gameThread!=null) {
			thisTime= System.currentTimeMillis();
			deltaTime = thisTime-lastTime;
			overshoot = deltaTime - msPerFrame;
			
			if (deltaTime>=msPerFrame-overshoot) {
				lastTime = thisTime;
				deltaTime=0;
				update();
				repaint();
				
				
			}
			//repaint();
		}
		
	}
	
	public int clamp(int min, int max, int test) {
		if (test>max)return max;
		if (test<min)return min;
		return test;
	}
	
	public void loadSettingsFile() {
		
		try {
			settingsio = new SettingsIO(SETTINGS_FILE);
			settingsio.readFile();
			String tmp;
			
			 
			if(settingsio.stringsDict.get("drawShadows").equals("true")) {
				GamePanel.drawShadows=true;
			}else {
				GamePanel.drawShadows=false;
				settingsio.stringsDict.put("drawShadows","false");
			}
			
			if(settingsio.stringsDict.get("godMode").equals("true")) {
				godMode=true;
			}else {
				godMode=true;
				settingsio.stringsDict.put("godMode","false");
			}
			
			if(settingsio.stringsDict.get("noClip").equals("true")) {
				noClip=true;
			}else {
				noClip=true;
				settingsio.stringsDict.put("noClip","false");
			}
			settingsio.writeFile();
			
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}
	
	public int counter=0;
	
	public void update() {
		visibleArea = Utils.getVisibleArea(this);
		this.collision.collideTilePlayer();
		this.tileManager.update();
		this.player.update();
		this.camera.update();
		raycast.update();
		shadow.update();
		entityManager.update();
		item.update();
		decor.update();
		hud.update();
		editor.update();
		pathFind.update();
		widget.update();
		zone.update();
		conversation.update();
		Point p = this.getMousePosition();
		if (p != null){
			this.mouseX = (int) p.getX();
			this.mouseY = (int) p.getY();
		}
		playerPressActivate = false;
		
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.g = g;
		this.g2 = (Graphics2D)g;
		draw();
		g2.dispose();
		
	}
	
	public void draw() {
		tileManager.draw();
		decor.draw();
		player.draw();
		raycast.draw();
		
		entityManager.draw();
		
		item.draw();
		shadow.draw();
		widget.draw();
		//rs1.draw();
		zone.draw();
		
		// foreground
		hud.draw();
		editor.draw();
		pathFind.draw();
		
		
	}

}
