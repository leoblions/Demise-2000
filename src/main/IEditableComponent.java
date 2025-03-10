package main;

public interface IEditableComponent {
	
	/**
	 * 
	 * @param testAssetID requested asset ID, might not be possible
	 * @return actual selected id
	 */
	public boolean validateAssetID(int testAssetID); 
	
	public int maxAssetID(); 
	
	public String getDataFilename();
	
	public EditMode getEditMode();
	
	public int[][] getGridData();
	
	public void setGridData(int[][] data);
	
	
	public void paintAsset(int gridX, int gridY, int kind); 

}
