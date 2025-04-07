package main;

import java.awt.BasicStroke;

public class CommandFactory {
	/*
	 * Creates Command interface objects, which have an execute method that gets overridden to perform groups of tasks.
	 * Uses Factory and Command design patterns
	 */
	GamePanel gp;
	public CommandFactory(GamePanel gp) {
		this.gp=gp;
		
		
	}
	
	public ICommand NewSellItemCommand(int itemKind, int amount) {
		// creates sell command
		// does not validate prices, if quantities are allowed/nonnegative, etc
		// calling function should ensure player has sufficient amount of item to sell
		int cashValue = gp.inventory.getItemCashValue(itemKind);
		int total = amount * cashValue;
		ICommand ic = new ICommand() {
			
			@Override
			public void execute() {
				gp.player.money += total;
				gp.inventory.removeItem(itemKind, amount);
			}
		};
		return ic;
	}
	
	public ICommand NewBuyItemCommand(int itemKind, int amount) {
		// creates buy command
		// does not validate sufficient funds, if quantities are allowed/nonnegative, etc
		// calling method should ensure player has sufficient funds
		int cashValue = gp.inventory.getItemCashValue(itemKind);
		int total = amount * cashValue;
		ICommand ic = new ICommand() {
			
			@Override
			public void execute() {
				gp.player.money -= total;
				gp.inventory.addItem(itemKind, amount);
			}
		};
		return ic;
	}
	
	
	
	public void update() {
		
		
	}

}
