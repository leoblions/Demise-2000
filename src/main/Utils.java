package main;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class Utils {
	
	public final static String COL_SEPARATOR = ",";
	public final static String ROW_SEPARATOR = "\n";
	public final static String IMAGE_PLACEHOLDER = "/images/ph.png";
	public final static boolean MOCK_WRITE_FILE = false;
	public final static boolean WRITE_NEW_FILE = false;

	public Utils() {
		
		
		
		
		
	}
	
	public static String[][] openCSVto2DA(String filePath){
		File dataFile = new File(filePath);
		if(!dataFile.exists()) {
			System.out.println("file not found "+filePath);
		}
		String[][] outerArray = null;
		Scanner scanner;
		LinkedList<String[]> allLines = new LinkedList<>();
		int lineLength = -1;
		try {
			scanner = new Scanner(dataFile);
			while(scanner.hasNextLine()) {
				String[] currentLineStrings  ;
				
				String data = scanner.nextLine();
				currentLineStrings = data.split(COL_SEPARATOR);
				if(lineLength==-1) {
					lineLength = currentLineStrings.length;
				}
				allLines.add(currentLineStrings);
				//System.out.println(currentLineStrings);
			}
			int outerArrayLength = allLines.size();
			outerArray  = new String[outerArrayLength][lineLength];
			int rows = outerArrayLength;
			int cols = lineLength;
			for (int y = 0; y < rows; y++) {
				for (int x = 0; x < cols; x++) {
					outerArray[y][x] = allLines.get(y)[x];
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return outerArray;
		
	}
	
	public static String[] getStringsFromFile(String filePath){
		File dataFile = new File(filePath);
		if(!dataFile.exists()) {
			System.out.println("file not found "+filePath);
		}
		String[] outputArray = null;
		Scanner scanner;
		LinkedList<String> allLines = new LinkedList<>();
		try {
			scanner = new Scanner(dataFile);
			while(scanner.hasNextLine()) {
				String currentLineStrings  ;
				
				String data = scanner.nextLine();
				currentLineStrings = data;
				allLines.add(currentLineStrings);
			}
			int outerArrayLength = allLines.size();
			outputArray  = new String[outerArrayLength];
			int rows = outerArrayLength;
			for (int y = 0; y < rows; y++) {
				outputArray[y] = allLines.get(y);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return outputArray;
		
	}
	
	
	public static int[][] openCSVto2DAInt(String filePath) throws Exception{
		
		String[][] strings2DA = openCSVto2DA(filePath);
		if (null==strings2DA) {
			throw new Exception("openCSVto2DAInt: no valid data");
		}
		int rows = strings2DA.length;
		int cols = strings2DA[0].length;
		int[][] int2DA = new int[rows][cols];
		
		
		for (int y = 0; y < rows; y++) {
			for (int x = 0; x < cols; x++) {
				int2DA[y][x] = Integer.parseInt(strings2DA[y][x]);
			}
		}
		
		return int2DA;
		
	}
	
	public static int[][] initBlankGrid(int cols , int rows , int fill  ){
		int[][] grid = new int[rows][cols];
		for (int r= 0; r<rows; r++) {
			for(int c= 0; c< cols;c++) {
				grid[r][c] = fill;
				
			}
		}
		return grid;
	}
	
	
	public static  void print2DAofStrings(String[][] str2DA){
		 
			int rows = str2DA.length;
			int cols = str2DA[0].length;
			for (int y = 0; y < rows; y++) {
				for (int x = 0; x < cols; x++) {
					 
					System.out.printf("%s,",str2DA[y][x]);
				}
				System.out.println();
			}
		
		
		
	}
	
	public static  int[][]  convert2DAstringTo2DAint(String[][] str2DA){
		
		int rows = str2DA.length;
		int cols = str2DA[0].length;
		int[][] outputIntArray = new int[rows][cols];
		for (int y = 0; y < rows; y++) {
			for (int x = 0; x < cols; x++) {
				 
				//System.out.printf("%s,",str2DA[y][x]);
				outputIntArray[y][x] = Integer.parseInt(str2DA[y][x]);
			}
			//System.out.println();
		}
		return outputIntArray;
	
	
	
}
	public static void writeInt2DAToCSV_0(int[][] int2DA, String filePath) throws IOException {
		File fhandle = null;
		FileWriter writer = null;
		if (MOCK_WRITE_FILE) {
			filePath = filePath + "_mock.csv";
		}
		 try {
		      fhandle = new File(filePath);
		      writer = new FileWriter(filePath);
		      if (fhandle.createNewFile()) {
		        System.out.println("File created: " + fhandle.getName());
		        
		        
		      } else {
		        System.out.println("File already exists. deleting old one");
		        fhandle.delete();
		        fhandle.createNewFile();
		       
		      }
		      if(!fhandle.canWrite()) {
		    	  System.err.println("Can't write the file "+filePath);
		      }
		      int rows = int2DA.length;
			int cols = int2DA[0].length;
			StringBuilder sb = new StringBuilder();
			String currentPart;
		      for (int y = 0; y < rows; y++) {
					for (int x = 0; x < cols; x++) {
						 
						currentPart = Integer.toString(int2DA[y][x]);
						sb.append(currentPart);
						if(x!=cols-1) {
							sb.append(COL_SEPARATOR);
						}else {
							sb.append(ROW_SEPARATOR);
						}
						
						
					}
					//System.out.println("print to file" +sb.toString());
					writer.write(sb.toString());
					writer.flush();
					sb = new StringBuilder();
				}
		    } catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }finally {
		    	writer.close();
		    }
		 
	}
	
	public static void writeInt2DAToCSV(int[][] int2DA, String filePath) throws IOException {
		File fhandle = null;
		FileWriter writer = null;
		if (MOCK_WRITE_FILE) {
			filePath = filePath + "_mock.csv";
		}
		 try {
		      fhandle = new File(filePath);
		      writer = new FileWriter(filePath);
		      if(WRITE_NEW_FILE) {
		    	  if (fhandle.createNewFile()) {
				        System.out.println("File created: " + fhandle.getName());
				        
				        
				      } else {
				        System.out.println("File already exists. deleting old one");
				        fhandle.delete();
				        fhandle.createNewFile();
				       
				      }
				      if(!fhandle.canWrite()) {
				    	  System.err.println("Can't write the file "+filePath);
				      }
		      }
		      int rows = int2DA.length;
			int cols = int2DA[0].length;
			StringBuilder sb = new StringBuilder();
			String currentPart;
		      for (int y = 0; y < rows; y++) {
					for (int x = 0; x < cols; x++) {
						 
						currentPart = Integer.toString(int2DA[y][x]);
						sb.append(currentPart);
						if(x!=cols-1) {
							sb.append(COL_SEPARATOR);
						}else {
							sb.append(ROW_SEPARATOR);
						}
						
						
					}
					//System.out.println("print to file" +sb.toString());
					writer.write(sb.toString());
					writer.flush();
					sb = new StringBuilder();
				}
		    } catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }finally {
		    	writer.close();
		    }
		 
	}
	
	public static String getLevelresourceFilename(int level, String prefix, String suffix) {
		return prefix+ String.format("%03d",level)+suffix;
	}
	
	public  static boolean checkFolderExists(  String dirpath) {
		Path path = Paths.get(dirpath);
		
		return Files.exists(path, LinkOption.NOFOLLOW_LINKS) && Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS);
	}
	/**
	 * 
	 * @param dirpath
	 * @return true if created a directory.  False if failed, or already exists
	 */
	public static boolean createDirectoryIfNotExist(  String dirpath) {
		if (!checkFolderExists(dirpath)) {
			Path path = Paths.get(dirpath);
			new File(dirpath).mkdirs();
			System.out.println("created folder "+dirpath);
			return true;
			
		}else {
			System.out.println("did not create folder "+dirpath);
		}
		
		return false;
	}
	
	public static ArrayList<String> splitStringIntoLinesAtWordEnding(  String inputString, int lengthLimit) {
		String[] words = inputString.split(" ");
		int currWord=0;
		int lastWord = words.length-1;
		String currentLine = "";
		String testLine = "";

		
		ArrayList<String>lines = new ArrayList<String>();
		while(currWord<=lastWord) {
			
			testLine += words[currWord] + " ";
			int tlLength = testLine.length();
			//System.out.println(tlLength);
			//System.out.println(currentLine);
			if( tlLength > lengthLimit) {
				// make new line
				lines.add(currentLine);
				currentLine = "";
				testLine = "";
			}else {
				// append to current line
				currentLine = testLine;
				
				currWord++;
			}
		}
		if( !testLine.equals("")) {
			// make new line
			lines.add(testLine);
			currentLine = "";
			testLine = "";
		}
		return lines;
	}
	
	/** 
	 * return visible screen area in world tiles X,Y,X,Y
	 * @return
	 */
	public static int[] getVisibleArea(GamePanel gp) {
		int ULCX = gp.wpScreenLocX / gp.TILE_SIZE_PX;
		int ULCY = gp.wpScreenLocY / gp.TILE_SIZE_PX;
		int BRCX = (gp.wpScreenLocX+gp.WIDTH) / gp.TILE_SIZE_PX;
		int BRCY = (gp.wpScreenLocY+gp.HEIGHT) / gp.TILE_SIZE_PX;
		return new int[] {ULCX,ULCY,BRCX,BRCY};
		
	}
	
	/**
	 * 
	 * @param fileURL
	 * @param cols
	 * @param rows
	 * @param width
	 * @param height
	 * @return
	 * @throws IOException
	 */
	public   BufferedImage[] spriteSheetCutter(String fileURL , int cols , int rows , int width , int height ) throws IOException {
		BufferedImage[] images = new BufferedImage[rows*cols];
		BufferedImage spriteSheet=null;
		try {
			spriteSheet = ImageIO.read(getClass().getResourceAsStream(fileURL));
		} catch (IOException e) {
			e.printStackTrace();
		}
		int subscript = 0;
		BufferedImage tempImage=null;
		for(int r = 0;r< rows; r++) {
			for(int c = 0;  c< rows; c++) {
				int x = c * width;
				int y = r * height;
				tempImage = spriteSheet.getSubimage(x, y, width, height);
				images[subscript] = tempImage;
				subscript++;
			}
		}
		return images;
	}
	
	public   BufferedImage[] spriteSheetCutterBlank( int cols , int rows , int width , int height ) throws IOException {
		String fileURL = IMAGE_PLACEHOLDER;
		BufferedImage[] images = new BufferedImage[rows*cols];
		BufferedImage img=null;
		try {
			img = ImageIO.read(getClass().getResourceAsStream(fileURL));
		} catch (IOException e) {
			e.printStackTrace();
		}
		int arrayLength = cols * rows;
		Graphics2D g2 = img.createGraphics();
		g2.drawImage(img, 0, 0, width, height, null);
		for (int i = 0; i < arrayLength;i++ ) {
			images[i]=img;
		}
		
		return images;
	}
	
	public static boolean createFileIfNotExist(String subdir, String filename) {
		Path filePath = Paths.get(subdir,filename);
		File file = new File(filePath.toString());
		try {
			return file.createNewFile();
		} catch (IOException e) { 
			System.err.println("Failed to create file: "+filePath);
			e.printStackTrace();
			return false;
		}
				
	}
	
	public static BufferedImage[] appendArray(BufferedImage[] arr1, BufferedImage[] arr2) {
		int index = 0;
		int len1 = arr1.length;
		int len2 = arr2.length;
		int len3 = len1 + len2;
		BufferedImage[] arr3 = new BufferedImage[len3];
		for(BufferedImage b: arr1) {
			arr3[index]=b;
			index++;
		}
		for(BufferedImage b: arr2) {
			arr3[index]=b;
			index++;
		}
		return arr3;
		
	}

}
