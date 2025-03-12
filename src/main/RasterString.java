package main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;

import javax.imageio.ImageIO;

public class RasterString {
	static final int CHARACTER_ORDINAL_OFFSET = 48;
	static final int RAST_SS_CHAR_PIX_H = 15;
	static final int RAST_SS_CHAR_PIX_W = 10;
	static final int SPRITESHEET_ROWS = 10;
	static final int SPRITESHEET_COLS = 10;
	static final int RAST_SS_CHAR_KERNING = 10;
	static final int RAST_SS_BG_ALPHA = 0x8f;
	static final boolean RAST_SS_BG_FILL = true;
	static final String SPRITE_SHEET = "letterSpritesT.png";
	static final String IMAGES_SUBDIR = "images";
	static final String LETTERS_LOCATION_FILE = "letters.json";
	static final String LETTERS_CHARMAP_FILE = "charmap_letters.txt";
	static final String DATA_FOLDER = "leveldata";
	
	static BufferedImage spritesheet;
	static BufferedImage[] letterSprites;

	static HashMap<Character, BufferedImage> charImageMap;

	GamePanel gp;
	String stringContent, fileURL;
	//ScreenPosition screenPosition;
	
	BufferedImage currImage;
	BufferedImage stringImage;
	int letterHeight, letterWidth, letterKerning, screenX, screenY, width, height;
	Color backgroundColor;
	boolean visible, fixedPosition;

	public record CharacterRecord(int col, int row, char letter) {
	}

	public RasterString(GamePanel gp, String content, int screenX, int screenY) {
		//this.screenPosition = new ScreenPosition(gp, screenX, screenY, RAST_SS_CHAR_PIX_W, RAST_SS_CHAR_PIX_H);
		this.gp = gp;
		this.screenX = screenX;
		this.screenY = screenY;
		
		fileURL = "/images/letterSpritesT.png";
		stringContent = content;
		backgroundColor = new Color(0x0f, 0x0f, 0x0f, RAST_SS_BG_ALPHA);
		letterHeight = RAST_SS_CHAR_PIX_H;
		letterWidth = RAST_SS_CHAR_PIX_W;
		letterKerning = RAST_SS_CHAR_KERNING;
		fixedPosition = false;
		visible = true;
		if (null==charImageMap) {
			charImageMap = initializeLetterSprites();
		}
		try {
			spritesheet = ImageIO.read(getClass().getResourceAsStream(fileURL));
			letterSprites = new Utils().spriteSheetCutter(fileURL, SPRITESHEET_COLS, SPRITESHEET_ROWS,
					RAST_SS_CHAR_PIX_W, RAST_SS_CHAR_PIX_H);
			
			stringImage = getRasterStringAsSingleImage();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private BufferedImage getRasterStringAsSingleImage() {
		int width = stringContent.length() * letterWidth;
		WritableRaster wr = null;
		Graphics2D graphics = null;
		ColorModel cm = null;
				if( width < 1) {
					width = 1;
				}
				int ImageType = spritesheet.getType();
				cm = spritesheet.getColorModel();
				BufferedImage stringImage = new BufferedImage(width,letterHeight,ImageType);
				int xOffsetTotal = 0;
				if (RAST_SS_BG_FILL) {
					graphics = stringImage.createGraphics();
					graphics.setColor(backgroundColor);
					graphics.fillRect(0, 0, width, letterHeight);
				}
				for (int i = 0; i<stringContent.length();i++ ){
					char letter = stringContent.charAt(i);
					BufferedImage letterImage = charImageMap.get(letter);

					if( letterImage != null) {

						int w = letterImage.getWidth();
						int h = letterImage.getHeight();
						graphics.drawImage(letterImage, xOffsetTotal, 0, w, h , null);
						xOffsetTotal += letterKerning;
					} else if (letter == ' ') {
						xOffsetTotal += letterKerning;
					}

				}
				//stringImage = new BufferedImage(cm, wr, visible, null);
				return stringImage;
	}

	public HashMap<Character, BufferedImage> initializeLetterSprites() {
		LinkedList<CharacterRecord> recordsList = readSpriteLocationTableFile();
		BufferedImage rawImage = null;
		//Path imageDir = Paths.get(IMAGES_SUBDIR, IMAGES_SUBDIR);
		//String imageDirS = "/images/letterSpritesT.png";
		try {
			rawImage = ImageIO.read(getClass().getResourceAsStream(fileURL));
		} catch (IOException e) {
			e.printStackTrace();
		}

		HashMap<Character, BufferedImage> charImageMap = new HashMap<Character, BufferedImage>();
		for (CharacterRecord rec : recordsList) {
			int x = rec.col() * letterWidth;
			int y = rec.row() * letterHeight;
			int h = letterHeight;
			int w = letterWidth;
			Character letter = rec.letter();
			BufferedImage letterImage = rawImage.getSubimage(x, y, w, h);
			charImageMap.put(letter, letterImage);
		}
		return charImageMap;
	}

	private LinkedList<CharacterRecord> readSpriteLocationTableFile() {
		String charmapDataPath = Paths.get(DATA_FOLDER, LETTERS_CHARMAP_FILE).toString();
		String[][] stringsArray = Utils.openCSVto2DA( charmapDataPath);
		LinkedList<CharacterRecord>  recordsList = new LinkedList<>();
				for (String[] inner : stringsArray) {
					
					try {
						String rowStr = Character.toString(inner[0].charAt(1));
						int row = Integer.parseInt(rowStr) ;
						int col = Integer.parseInt(Character.toString(inner[0].charAt(0)));
						char letter = inner[0].charAt(2);
						//System.out.println(letter);
						CharacterRecord cr = new CharacterRecord(col, row, letter );
						recordsList.add(cr);
					}catch(Exception e) {
						System.err.println("readSpriteLocationTableFile invalid row");
					}
				}
				return recordsList;
	}

	public Rectangle getColliderRect() {
		// position on screen
		return new Rectangle(screenX, screenY,
				width, height);
	}

	public int midpointX() {
		return screenX + (width / 2);
	}

	public void draw() {
		if(visible) {
			gp.g2.drawImage(stringImage,
					screenX,
					screenY,
					stringImage.getWidth(), 
					stringImage.getHeight(),
					null);
		}

	}

	public void update() {

	}

}
