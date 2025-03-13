package main;

import java.util.HashMap;

public class Inventory {
	private HashMap<Integer,Integer>inventoryItems;
	GamePanel gp;
	public Inventory(GamePanel gp) {
		this.gp=gp;
		inventoryItems = new HashMap<Integer, Integer>();
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
	
	public int queryItemAmount(int itemID ) {
		
		return inventoryItems.get(itemID);
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
