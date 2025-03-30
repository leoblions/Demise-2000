package main;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public class Inventory {
	private final int BLANK_ITEM_KIND = -1;
	private HashMap<Integer,Integer>inventoryItems;
	GamePanel gp;
	public int activeItem = BLANK_ITEM_KIND;
	public final int BLANK_PRIJECTILE_TYPE = -1;
	public int projectileType = -1;
	public Inventory(GamePanel gp) {
		this.gp=gp;
		inventoryItems = new HashMap<Integer, Integer>();
		testInv();
		
	}
	
	private void testInv() {
		
		addItem(4, 1);
		selectItem(4);
		addItem(2, 1);
		selectItem(4);
		addItem(7, 1);
		selectItem(4);
		
	}
	
	public void addItem(int itemID, int amountToAdd) {
		
		int oldValue = inventoryItems.getOrDefault(itemID,0);
		//System.out.println("add item");
		int newValue=0;
		if(amountToAdd > 0) {
			newValue = oldValue+amountToAdd;
			inventoryItems.put(itemID,newValue);
			System.out.printf("Inventory added itemID: %d , amount: %d , total: %d \n",itemID,amountToAdd,newValue);
		}else {
			System.err.printf("Inventory cannot add itemID: %d , amount: %d \n",itemID,amountToAdd);
		}
	}
	
	public int[][]queryKindAndAmount(){
		// get 2d array of item kinds and amounts
		int amountOfItemKindsPresent = inventoryItems.size();
		int[][] kvpOuterArray = new int[amountOfItemKindsPresent][];
		int iter = 0;
		Set<Entry<Integer, Integer>> es = inventoryItems.entrySet();
		for(Entry<Integer, Integer> entry: es) {
			int k = entry.getKey();
			int v = entry.getValue();
			int[] pair = new int[]{k,v};

			System.out.println("Key "+pair[0]);

			kvpOuterArray[iter]= pair;
			iter+=1;
		}
		//return new int[][]{{0,1},{1,1},{3,1}};
		return kvpOuterArray;
	}
	
	public int queryItemAmount(int itemID ) {
		
		return inventoryItems.get(itemID);
	}
	
	public int selectItem(int itemType ) {
		
		int amount = inventoryItems.getOrDefault(itemType, BLANK_ITEM_KIND);
		if (amount!=BLANK_ITEM_KIND) {
			activeItem = itemType;
		}
		selectProjectileType();
		return amount;
	}
	
	public int selectProjectileType( ) {
		System.out.println("projectileType activeItem "+activeItem);
		switch(activeItem) {
		case 14:
			projectileType = 0;
			break;
		case 17:
			projectileType = 4;
			break;
		case 23:
			projectileType = 3;
			break;
		default:
			projectileType = BLANK_ITEM_KIND;
			break;
		
		}
		return projectileType;
	}
	
	public void spendItem(int itemID) {
		
		int oldValue = inventoryItems.getOrDefault(itemID,0);
		int newValue=0;
		if(oldValue > 0) {
			newValue = oldValue-1;
			inventoryItems.put(itemID,newValue);
			System.out.printf("Inventory used itemID: %d , total: %d \n",itemID,newValue);
		}else {
			System.err.printf("Inventory cannot use itemID: %d ,  \n",itemID);
		}
	}

}
