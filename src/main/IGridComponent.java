package main;

public interface IGridComponent {
	
	public void saveDataToFile(int levelID);
	
	public int[][] loadDataFromFile(int levelID);

}
