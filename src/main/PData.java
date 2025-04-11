package main;

import java.io.Serializable;
import java.util.HashMap;

public class PData implements Serializable{
		private static final long serialVersionUID = 1795948036539464428L;
		public int[][] plantData;
		public HashMap<Integer,Integer> inventoryData;
		public int health;
		public int level;
		public int money;
		public int stamina;
		public int worldX,worldY;
		
		public PData() {
			this.plantData=null;
			this.inventoryData=null;
			this.health = 0;
			this.level = 0;
			this.money = 0;
			this.stamina = 0;
			this.worldX=0;
			this.worldY=0;
		}
		
	
}
