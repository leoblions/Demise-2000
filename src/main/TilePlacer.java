package main;

import java.awt.image.BufferedImage;
import java.util.Random;

public class TilePlacer {

	GamePanel gp;
	public Tile[][] tileGrid;
	BufferedImage[] bufferedImages;
	Rooms10x10 rooms;
	Random random;
	public final int MAX_FLOOR_TILE_INDEX = 11;
	public final int MAX_ROOM_TYPE_INDEX = 21;
	 

	public TilePlacer(GamePanel gp, Tile[][] tileGridIn) {
		this.gp=gp;
		this.tileGrid = tileGridIn;
		
		
	}
	
	public void placeTiles() {
		
		rooms = new Rooms10x10();
		borderWall(1);

		wallGrid2(10,1,0);
	
		
		placeRoomsOnGrid();
		int randFloor = random.nextInt(MAX_FLOOR_TILE_INDEX);
		if(randFloor==1)randFloor=0;
		int randRoom = random.nextInt(5);
		drawArray2Tswap(0,0,rooms.rooms[1],randFloor);
		drawArray2Tswap(10,0,rooms.rooms[randRoom],randFloor);
		drawArray2Tswap(0,10,rooms.rooms[randRoom],randFloor);
		drawArray2Tswap(50,50,rooms.rooms[1],randFloor);
		borderWall(1);
		
		gp.tilesPlacedComplete = true;
	}
	int currFloorIndex = 0;
	
	public void placeRoomsOnGrid() {
		random = new Random();
		int roomType;
		int floorType;
		for(int y = 0; y< GamePanel.MAP_TILES_Y;y+=10) {
			
			for(int x = 0; x< GamePanel.MAP_TILES_Y;x+=10) {
				roomType = random.nextInt(MAX_ROOM_TYPE_INDEX);
				floorType = random.nextInt(MAX_FLOOR_TILE_INDEX);
				
				if(currFloorIndex<MAX_FLOOR_TILE_INDEX) {
					if(x%3==0)currFloorIndex++;
					
				}else {
					currFloorIndex=0;
				}
				if (currFloorIndex>MAX_FLOOR_TILE_INDEX)currFloorIndex=0;
				floorType=currFloorIndex;
				
				if (floorType==1)floorType=2;
				drawArray2Tswap(x,y,rooms.rooms[roomType],floorType);
				
			}
			
		}
	}
	
	
	public void drawRectangle(int tileX, int tileY, int width, int height, int kind) {
		
		for (int y = tileY; y < tileY+height; y++) {
			for (int x = tileX; x < tileX+width; x++) {
				try {
					tileGrid[y][x].kind = kind;
				}catch(Exception e){
					//do nothing if it accesses subscript that are out of bounds
				}
				
			}
		}
		
	}
	
	public void drawArray2T(int tileX, int tileY, int[][] array) {
		int kind=0;
		for (int y = 0; y < array.length;y++) {
			for (int x = 0; x< array[0].length; x++) {
				kind = array[y][x];
				try {
					tileGrid[y+tileY][x+tileX].kind = kind;
				}catch(Exception e){
					
				}
			}
		}
	}
	
	public void drawArray2Tswap(int tileX, int tileY, int[][] array, int floorSwapKind) {
		int kind=0;
		for (int y = 0; y < array.length;y++) {
			for (int x = 0; x< array[0].length; x++) {
				kind = array[y][x];
				try {
					if (kind==1) {
						tileGrid[y+tileY][x+tileX].kind = kind;
					}else {
						tileGrid[y+tileY][x+tileX].kind = floorSwapKind;
					}
					
				}catch(Exception e){
					
				}
			}
		}
	}
	
	public void borderWall(int kind) {
		//horizontal
		for(int x = 0; x< tileGrid[0].length;x++) {
			tileGrid[0][x].kind = kind;
			tileGrid[tileGrid.length-1][x].kind = kind;
		}
		for(int y = 0; y< tileGrid.length;y++) {
			tileGrid[y][0].kind = kind;
			tileGrid[y][tileGrid[0].length-1].kind = kind;
		}
	}
	
	public void wallGrid( int spacing, int wallKind, int floorKind) {
		int startX = 0;
		int startY=0;
		int endX = tileGrid[0].length;
		int endY = tileGrid.length;
		for(int x = startX; x< endX;x++) {
			for(int y = startY; x< endY;y++) {
				try {
					tileGrid[y][x].kind = floorKind;
				}catch(Exception e) {break;}
				try {
					if(x%spacing==0||y%spacing==0) {
						tileGrid[y][x].kind = wallKind;
					}
					
				}catch(Exception e) {break;}
				
			}
		}
	}
	
	public void wallGrid2( int spacing, int wallKind, int floorKind) {
		int startX = 0;
		int startY=0;
		int door = 5;
		int endX = 49;
		int endY = 49;
		for(int y = startY; y< endY;y++) {
			for(int x = startX; x < endX;x++) {
				
				
				tileGrid[y][x].kind = floorKind;
				
				
				if(x%spacing==0||y%spacing==0) {
					tileGrid[y][x].kind = wallKind;
				}
				
				if(x%door==0&&y%door==0&&(x%(2*door)!=0||y%(2*door)!=0)) {
					tileGrid[y][x].kind = floorKind;
				}
				
				
				
			}
		}
	}
	
	public void enclosure(int startX, int startY, int width, int height, int kind) {
		//horizontal
		for(int x = startX; x< startX+width;x++) {
			tileGrid[startY][x].kind = kind;
			tileGrid[startY+height-1][x].kind = kind;
		}
		for(int y = startY; y< startY+height;y++) {
			tileGrid[y][startX].kind = kind;
			tileGrid[y][startX+width-1].kind = kind;
		}
	}
	
	public void drawHallHoriz(int startX, int startY, int width, int height, int doorSpacing, int Wallkind,int floorKind) {
		//horizontal
		for(int y = startY; y< startY+height;y++) {
			for(int x = startX; x< startX+width;x++) {
				try {
					tileGrid[y][x].kind = floorKind;
				}catch(Exception e) {break;}
				
			}
		}
		
		for(int x = startX; x< startX+width;x++) {
			if(x%doorSpacing==0)continue;
			tileGrid[startY][x].kind = Wallkind;
			tileGrid[startY+height-1][x].kind = Wallkind;
		}
		
	}
	
	public void roomWithDoors(int startX, int startY, int width, int height, int wallKind, int floorKind) {
		int middleY = startY + (height /2);
		int middleX = startX + (width /2);
		//floor
		for(int x = startX; x< startX+width;x++) {
			for(int y = startY; x< startY+height;y++) {
				try {
					tileGrid[y][x].kind = floorKind;
				}catch(Exception e) {break;}
				
			}
		}
		
		
		for(int x = startX; x< startX+width;x++) {
			
			 
			try {
				
					tileGrid[startY][x].kind = wallKind;
					tileGrid[startY+height-1][x].kind = wallKind;
					
				if (x==middleX) {
					tileGrid[startY][x].kind = floorKind;
					tileGrid[startY+height-1][x].kind = floorKind;
				}
				
			}catch(Exception e) {break;}
				
				
			
		}
		for(int y = startY; y< startY+height;y++) {
			try {
			tileGrid[y][startX].kind = wallKind;
			tileGrid[y][startX+width-1].kind = wallKind;
			if (y==middleY) {
				tileGrid[y][startX].kind = floorKind;
				tileGrid[y][startX+width-1].kind = floorKind;
			}
			}catch(Exception e) {break;}
		}
	}

}
