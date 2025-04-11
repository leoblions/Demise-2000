package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.naming.directory.InvalidAttributeValueException;
import javax.swing.JFrame;
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
	public JFrame parentFrame;
	public String parentFrameTitle;
	public static final boolean LOAD_LEVEL_ON_START = true;
	public static final int LEVEL_TO_LOAD_ON_START = 0;
	public static final boolean CREATE_BLANK_MAP_IF_FILE_NOT_FOUND = true;
	public ArrayList<IEditableComponent> components = new ArrayList<>();
	public ArrayList<IClickableElement> clickableElements= new ArrayList<>();
	public final String SETTINGS_FILE="settings.ini";
	public static final int WIDTH = 720;
	public static final int HEIGHT = 600;
	public static final int TILE_SIZE_PX = 50; 
	public static final int MAP_TILES_X = 100; 
	public static final int MAP_TILES_Y = 100;
	private static final long MENU_DEBOUNCE_INTERVAL_MS = 1000L;
	public static final String LEVEL_DATA_SUBDIR = "leveldata";
	public final GameState DEFAULT_GAME_STATE = GameState.PLAY;
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
	public boolean loadInProgress = false;
	//public static int[][] tileGrid;
	public int[] visibleArea;
	public TimeDelay stateChangeDelay = new TimeDelay(MENU_DEBOUNCE_INTERVAL_MS);
	
	/**
	 * flag to tell certain methods not to run until map tiles are done being placed
	 */
	public boolean tilesPlacedComplete = false;
	public int level = 0;
	boolean playerPressActivate = false;;
	public static int inGameMenu = 1;
	
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
	Particle particle;
	Brain brain;
	Barrier barrier;
	Wipe wipe;
	Console console;
	Projectile projectile;
	Warp warp;
	Plant plant;
	StackMenu mainMenu;
	StackMenu pauseMenu;
	Spinner spinner;
	SaveData saveData;
	
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
		RUN, MUTE, ACTIONRELEASE, INVENTORY
	}
	
	public enum GameState{
		
		PAUSED,
		GAMEOVER,
		INVENTORY,
		TOOLBAR,
		CONVERSATION,
		MENU,
		PLAY,
		CONSOLE,
		OPTION,
		STORE,
		QUESTION
		
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
				 

            	 for (var ce : clickableElements) {
            		 ce.click(kind,mouseX,mouseY);
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
		hud = new HUD(this);
		input = new Input(this);
		this.addKeyListener(input);
		gameState = GameState.PLAY;
		camera = new Camera(this);
		collision = new Collision(this);
		raycast = new Raycast(this);
		shadow = new Shadow(this);
		pathFind = new PathFind(this);
		widget = new Widget(this);
		visibleArea = Utils.getVisibleArea(this);
		entityManager = new EntityManager(this);
		inventory = new Inventory(this);
		item = new Item(this);
		zone = new Zone(this);
		conversation = new Conversation(this);
		brain = new Brain(this);
		particle = new Particle(this);
		barrier = new Barrier(this);
		wipe = new Wipe(this);
		projectile=new Projectile(this);
		spinner = new Spinner(this,(int)WIDTH/2,(int)(HEIGHT*0.20),200);
		mainMenu=new StackMenu(this,0);
		pauseMenu=new StackMenu(this,1);
		saveData = new SaveData(this);

		warp=new Warp(this);
		console = new Console(this);

		plant = new Plant(this);
		//rs1 = new RasterString(this, "TEST", 45, 45);
		
		
		
		decor = new Decor(this);
		
		sound = new Sound(this);
		if(LOAD_LEVEL_ON_START) {
			level = LEVEL_TO_LOAD_ON_START;
			this.loadComponentData();
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
			
			if(settingsio.stringsDict.get("title")!=null ) {
				String title = settingsio.stringsDict.get("title");
				if(parentFrame!=null) {
					parentFrame.setTitle(title);
				}
				parentFrameTitle = title;
				
			}
			
			 
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
		if(loadInProgress) {
			
			return;
		}
		visibleArea = Utils.getVisibleArea(this);
		this.collision.collideTilePlayer();
		this.tileManager.update();

		decor.update();
		this.player.update();
		this.camera.update();
		raycast.update();
		shadow.update();
		entityManager.update();
		item.update();
		hud.update();
		editor.update();
		pathFind.update();
		widget.update();
		zone.update();
		conversation.update();
		particle.update();
		barrier.update();
		sound.update();
		wipe.update();
		console.update();
		projectile.update();
		plant.update();
		spinner.update();
		mainMenu.update();
		pauseMenu.update();
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
		if(loadInProgress) {
			return;
		}
		tileManager.draw();
		decor.draw();
		plant.draw();
		player.draw();
		entityManager.draw();
		decor.drawLower();
		plant.drawLower();
		
		raycast.draw();
		
		
		item.draw();
		shadow.draw();
		widget.draw();
		//rs1.draw();
		zone.draw();
		particle.draw();
		barrier.draw();
		projectile.draw();
		
		// foreground
		hud.draw();
		editor.draw();
		pathFind.draw();
		wipe.draw();
		console.draw();
		
		spinner.draw();
		mainMenu.draw();
		pauseMenu.draw();
		
	}



	public void unpause_() {
		player.frozen = false;
		entityManager.frozen = false;
		
	}
	public void pause_() {
		player.frozen = true;
		entityManager.frozen = true;
		
	}
	
	public void addComponent(IEditableComponent ec) {
		this.components.add(ec);
	}
	
	public boolean requestStateChange(GameState state) {
		if(stateChangeDelay.check()) {
			this.gameState=state;
			return true;
		}else {
			return false;
		}
		
	}
	
	public void toggleInGameMenu() {
		if (gameState==GameState.MENU) {
			gameState=DEFAULT_GAME_STATE;
		}else {
			gameState=GameState.MENU;
		}
	}
	public void toggleMode(GameState state) {
		System.out.println("Toggle game mode: "+state.toString());
		if (gameState==state) {
			gameState=DEFAULT_GAME_STATE;
		}else if (gameState==DEFAULT_GAME_STATE) {
			gameState=state;
		}else {
			
		}

		System.out.println("Current state: "+gameState.toString());
	}
	
	public void saveComponentData() {
		 
		String dataFolderName = GamePanel.LEVEL_DATA_SUBDIR;
		Utils.createDirectoryIfNotExist(dataFolderName);
		String currentWorkingDirectory = System.getProperty("user.dir");
		Path dataPath = Paths.get(currentWorkingDirectory, dataFolderName);
		System.out.println("Save component data "+ dataPath.toString());
		String componentName;
		for (IEditableComponent ec: this.components) {
			componentName = ec.getEditMode().toString();
			if(!ec.isModified()) {
				System.out.println("Skip saving "+componentName);
				continue;
			}else {
				String tilePath = ec.getDataFilename();
				Path tilePathP = Paths.get(dataFolderName, tilePath);
				try {
					int[][] data = ec.getGridData();
					if (data==null) {
						throw new InvalidAttributeValueException("Component % componentName returned invalid data while saving.");
					}
					Utils.writeInt2DAToCSV(ec.getGridData(), tilePathP.toString());
				} catch (Exception e) {
					
					e.printStackTrace();
				}
				System.out.println("Saved changes: "+componentName);
			}
			
		}
		

	}
	
	
	
	public void loadComponentData() {
		loadInProgress = true;
		 
		String dataFolderName = GamePanel.LEVEL_DATA_SUBDIR;
		Utils.createDirectoryIfNotExist(dataFolderName);
		String currentWorkingDirectory = System.getProperty("user.dir");
		Path dataPath = Paths.get(currentWorkingDirectory, dataFolderName);
		System.out.println(dataPath.toString());
		String componentName;
		for (IEditableComponent ec: this.components) {
			componentName = ec.getEditMode().toString();
			String tilePath = ec.getDataFilename();
			Path tilePathP = Paths.get(dataFolderName, tilePath);
			//Utils.writeInt2DAToCSV(ec.getGridData(), tilePathP.toString());
			int[][] data=null;
			try {
				System.out.print("Loading component data :"+componentName);
				try {

					data = Utils.openCSVto2DAInt(tilePathP.toString());

					System.out.println("..OK" );
				}catch(FileNotFoundException e) {

					System.out.println("..No FILE" );
					if(CREATE_BLANK_MAP_IF_FILE_NOT_FOUND) {
						System.out.println("creating blank data...");
						ec.initBlank();
					}
				}
				
				ec.setGridData(data);
			} catch (NegativeArraySizeException e) {
				System.err.println("..FAIL" );
				System.err.println("Error while loading file: "+ tilePathP.toString());
				e.printStackTrace();
				continue;
			} catch (Exception e){
				
			}
		}
		loadInProgress = false;
	}

}
