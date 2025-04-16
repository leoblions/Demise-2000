package main;

import java.util.HashMap;
import java.util.Map.Entry;

import main.Player.AttackMode;

import java.util.Set;

public class Inventory {
	private final int BLANK_ITEM_KIND = -1;
	private HashMap<Integer, Integer> inventoryItems;
	private HashMap<Integer, Integer> storeItems;
	GamePanel gp;

	public final int WOOD = 24;
	public final int STONE = 25;
	public final int IRON = 26;

	// only place player's active item should be referenced
	private static final int BLANK_ITEM_ID = -1;
	private int equippedItem;

	public int activeItem = BLANK_ITEM_KIND;
	public final int BLANK_PRIJECTILE_TYPE = -1;
	public int projectileType = -1;
	public String[] itemNames;

	public Inventory(GamePanel gp) {
		this.gp = gp;
		inventoryItems = new HashMap<Integer, Integer>();
		storeItems = new HashMap<Integer, Integer>();
		//stestInv();
		itemNames = new String[] { "blue briefcase", "brown briefcase ", "bucket", "gold coin", "diamond", "emerald",
				"ruby", "sapphire", "fire extinguisher", "iron key", "brass key", "anodized key", "medkit",
				"healing herb", "semi auto pistol", "snap trap",
				// sheet 2
				"health", "seed packet", "machete", "garden hoe", "watering can", "bear trap", "pickaxe", "cherry bomb",
				"wood", "stone", "iron"

		};
		
		testStore();

	}
	
	public HashMap<Integer, Integer> getInventoryDataCopy() {
		HashMap<Integer, Integer> dataCopy = new HashMap<Integer, Integer>();
		dataCopy = (HashMap<Integer, Integer>) inventoryItems.clone();
		return dataCopy;
	}
	
	public HashMap<Integer, Integer> copyHashMap(HashMap<Integer, Integer>  oldData ) {
		HashMap<Integer, Integer> dataCopy = new HashMap<Integer, Integer>();
		dataCopy = (HashMap<Integer, Integer>) oldData.clone();
		return dataCopy;
	}
	
	public void setInventoryData(HashMap<Integer, Integer> newInventoryData) {
		HashMap<Integer, Integer> dataCopy = new HashMap<Integer, Integer>();
		dataCopy = (HashMap<Integer, Integer>) newInventoryData.clone();
		inventoryItems = dataCopy;
	}

	public void setEquippedItemType(int kind) {
		equippedItem = kind;
		//int equippedItem = gp.inventory.getEquippedItemType();
		
		gp.player.attackMode = switch(equippedItem) {
		case 8-> AttackMode.SPRAY;
		case 14->AttackMode.SHOOT;
		case 17->AttackMode.SEED;
		case 18->AttackMode.SLASH;
		case 19->AttackMode.HOE;
		case 20->AttackMode.SPRAY;
		case 21->AttackMode.SHOOT;
		case 22->AttackMode.HOE;
		case 23->AttackMode.BOMB;
		default->AttackMode.SLASH;
		};
	}

	public int getEquippedItemType() {
		return equippedItem;
	}

	private void testInv() {

		addItem(1, 1);
		selectItem(4);
		addItem(2, 1);
		selectItem(4);
		addItem(3, 1);
		selectItem(4);
		addItem(4, 1);
		selectItem(4);
		addItem(5, 1);
		selectItem(4);
		addItem(6, 1);
		selectItem(4);
		addItem(7, 1);
		selectItem(4);
		addItem(8, 1);
		selectItem(4);
		addItem(9, 1);
		selectItem(4);

	}
	
	private void testStore() {

		addItemTo(1, 10,storeItems);
		addItemTo(2, 10,storeItems);
		addItemTo(3, 10,storeItems);
		addItemTo(4, 10,storeItems);
		addItemTo(5, 10,storeItems);
		addItemTo(6, 10,storeItems);
		addItemTo(7, 10,storeItems);
		addItemTo(8, 10,storeItems);
		addItemTo(9, 10,storeItems);


	}

	public void addItem(int itemID, int amountToAdd) {

		int oldValue = inventoryItems.getOrDefault(itemID, 0);
		// System.out.println("add item");
		int newValue = 0;
		if (amountToAdd > 0) {
			newValue = oldValue + amountToAdd;
			inventoryItems.put(itemID, newValue);
			System.out.printf("Inventory added itemID: %d , amount: %d , total: %d \n", itemID, amountToAdd, newValue);
		} else {
			System.err.printf("Inventory cannot add itemID: %d , amount: %d \n", itemID, amountToAdd);
		}
	}
	
	public void addItemTo(int itemID, int amountToAdd, HashMap<Integer,Integer>collection) {

		int oldValue = collection.getOrDefault(itemID, 0);
		// System.out.println("add item");
		int newValue = 0;
		if (amountToAdd > 0) {
			newValue = oldValue + amountToAdd;
			collection.put(itemID, newValue);
			System.out.printf("Collection added itemID: %d , amount: %d , total: %d \n", itemID, amountToAdd, newValue);
		} else {
			System.err.printf("Collection cannot add itemID: %d , amount: %d \n", itemID, amountToAdd);
		}
	}

	public void removeItem(int itemID, int amountToRemove) {

		int oldValue = inventoryItems.getOrDefault(itemID, 0);
		// System.out.println("add item");
		int newValue = 0;
		if (amountToRemove > 0 && amountToRemove >= oldValue) {
			newValue = oldValue - amountToRemove;
			inventoryItems.put(itemID, newValue);
			System.out.printf("Inventory removed itemID: %d , amount: %d , new total: %d \n", itemID, amountToRemove,
					newValue);
		} else {
			System.err.printf("Inventory cannot remove itemID: %d , amount: %d \n", itemID, amountToRemove);
		}
	}

	public void deleteAllItemOfType(int itemID) {

		inventoryItems.remove(itemID);
	}
	
	public int[][] queryKindAndAmount(){
		return queryKindAndAmount(  0);
	}
	
	private HashMap<Integer,Integer> getCollectionByID(int collectionID){
		HashMap<Integer,Integer> collection = null;
		switch(collectionID) {
		case 0:
			collection = inventoryItems;
			// player inventory
		case 1:
			collection = storeItems;
			break;
		default:
			collection = inventoryItems;
		}
		return collection;
	}

	public int[][] queryKindAndAmount(int collectionID) {
		HashMap<Integer,Integer>collection=getCollectionByID( collectionID);
		// get 2d array of item kinds and amounts
		int amountOfItemKindsPresent = collection.size();
		int[][] kvpOuterArray = new int[amountOfItemKindsPresent][];
		System.out.println("amountOfItemKindsPresent " + amountOfItemKindsPresent);
		int iter = 0;
		Set<Entry<Integer, Integer>> es = collection.entrySet();
		for (Entry<Integer, Integer> entry : es) {
			int k = entry.getKey();
			int v = entry.getValue();
			int[] pair = new int[] { k, v };

			// System.out.println("Key "+pair[0]);

			kvpOuterArray[iter] = pair;
			iter += 1;
		}
		// return new int[][]{{0,1},{1,1},{3,1}};
		return kvpOuterArray;
	}
	
	

	public int queryItemAmount(int itemID, int collectionID) {
		HashMap<Integer,Integer>collection=getCollectionByID( collectionID);

		return collection.get(itemID);
	}

	public int selectItem(int itemType) {

		int amount = inventoryItems.getOrDefault(itemType, BLANK_ITEM_KIND);
		if (amount != BLANK_ITEM_KIND) {
			activeItem = itemType;
		}
		selectProjectileType();
		return amount;
	}

	public int selectProjectileType() {
		// System.out.println("projectileType activeItem "+activeItem);
		switch (activeItem) {
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

		int oldValue = inventoryItems.getOrDefault(itemID, 0);
		int newValue = 0;
		if (oldValue > 0) {
			newValue = oldValue - 1;
			inventoryItems.put(itemID, newValue);
			System.out.printf("Inventory used itemID: %d , total: %d \n", itemID, newValue);
		} else {
			System.err.printf("Inventory cannot use itemID: %d ,  \n", itemID);
		}
	}

	public int getItemCashValue(int itemType) {
		// TODO: implement properly later
		return 10;
	}

}
