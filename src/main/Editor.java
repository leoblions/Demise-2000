package main;

import java.awt.Color;
import java.awt.Font;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.naming.directory.InvalidAttributeValueException;
/*
 * 
 * Numpad 0 to swap edit mode
 * Mouse Left: paint
 * Mouse middle/right click change asset
 * Numpad decimal: enable latch/painting mode
 * 
 */

public class Editor {
	GamePanel gp;
	Color smBorder = new Color(50, 50, 50, 50);
	private static String editModeString, selectedAssetIDstring,latchString;
	Font arial16;
	public EditMode editMode;
	public boolean delete = false;
	public int selectedAssetID;
	IEditableComponent activeComponent = null;
	//public ArrayList<IEditableComponent> components = new ArrayList<>();
	public boolean latchEnable = false;
	public boolean latchActive = false;

	public Editor(GamePanel gp) {
		this.gp = gp;
		arial16 = new Font("Arial", Font.PLAIN, 16);
		this.editMode = EditMode.NORMAL;
		editModeString = this.editMode.toString();
		selectedAssetIDstring = getAssetIDString();
		this.latchString = "";

	}
	
	public IEditableComponent getActiveComponent() {
		for (IEditableComponent c: gp.components) {
			if (c.getEditMode() == this.editMode){
				return c;
			}
		}
		return null;
	}
	
	public void addComponent_0(IEditableComponent ec) {
		this.gp.components.add(ec);
	}

	public String getAssetIDString() {
		return "Asset: "+ String.format(" %d", selectedAssetID);
	}


	public void updateStrings() {
		editModeString = this.editMode.toString();
		selectedAssetIDstring = getAssetIDString();
	}

	public void incrementAssetID(int delta) {
		int tempAssetID = delta + selectedAssetID;
		IEditableComponent ec ; 
		this.activeComponent = this.getActiveComponent(); 
		if (this.activeComponent!=null && this.activeComponent.validateAssetID(tempAssetID)){
			this.selectedAssetID = tempAssetID; 
		}
		this.updateStrings();
	}
	
	public void setAssetID(int assetID) { 
		int tempAssetID = assetID;
		IEditableComponent ec ; 
		this.activeComponent = this.getActiveComponent(); 
		if (this.activeComponent!=null && this.activeComponent.validateAssetID(tempAssetID)){
			this.selectedAssetID = tempAssetID; 
		}
		this.updateStrings();
	}

	public void paintAsset() {
		int gridX = (this.gp.mouseX + gp.wpScreenLocX) / this.gp.TILE_SIZE_PX;
		int gridY = (this.gp.mouseY + gp.wpScreenLocY) / this.gp.TILE_SIZE_PX;
		this.activeComponent = this.getActiveComponent();
		if (this.activeComponent!=null){
			this.activeComponent.paintAsset(gridX, gridY, this.selectedAssetID);
		}
		

	}

	public void draw() {
		gp.g2.setColor(Color.white);
		gp.g2.setFont(arial16);
		gp.g2.drawString(editModeString, 10, 70);
		gp.g2.drawString(selectedAssetIDstring, 10, 90);
		gp.g2.drawString(latchString, 10, 110);

	}

	public void update() {
		
		if (latchEnable && latchActive) {
			paintAsset();
			System.out.println("painting");
		}

	}

	public void handleClick(int button , int upDown) {
		if(button==1) {
			if (upDown==1 && this.latchEnable) {
				this.latchActive =true;
			}else if(upDown==0 && this.latchEnable) {
				this.latchActive =false;
				
			}
		}

	}

	public void toggleEditMode() {
		EditMode[] emVals = EditMode.values();
		for (int i = 0; i < emVals.length; i++) {
			if (this.editMode == emVals[i]) {
				try {
					this.editMode = emVals[i + 1];
				} catch (Exception e) {
					this.editMode = emVals[0];
				}
				break;
			}
		}
		// this.editMode =
		this.activeComponent = getActiveComponent();
		editModeString = this.editMode.toString();
		System.out.printf("tgl %d", emVals.length);
	}

	public void saveComponentData_0() {
	 
		String dataFolderName = GamePanel.LEVEL_DATA_SUBDIR;
		Utils.createDirectoryIfNotExist(dataFolderName);
		String currentWorkingDirectory = System.getProperty("user.dir");
		Path dataPath = Paths.get(currentWorkingDirectory, dataFolderName);
		System.out.println("Save component data "+ dataPath.toString());
		String componentName;
		for (IEditableComponent ec: this.gp.components) {
			componentName = ec.getEditMode().toString();
			if(!ec.isModified()) {
				System.out.println("Skip saving "+componentName);
				continue;
			}else {
				String tilePath = ec.getDataFilename();
				Path tilePathP = Paths.get(dataFolderName, tilePath);
				try {
					int[][] data = ec.getGridData();
					if (data==null) {
						throw new InvalidAttributeValueException("Component % componentName returned invalid data while saving.");
					}
					Utils.writeInt2DAToCSV(ec.getGridData(), tilePathP.toString());
				} catch (Exception e) {
					
					e.printStackTrace();
				}
				System.out.println("Saved changes: "+componentName);
			}
			
		}
		

	}
	
	public void loadComponentData_0() {
		 
		String dataFolderName = GamePanel.LEVEL_DATA_SUBDIR;
		Utils.createDirectoryIfNotExist(dataFolderName);
		String currentWorkingDirectory = System.getProperty("user.dir");
		Path dataPath = Paths.get(currentWorkingDirectory, dataFolderName);
		System.out.println(dataPath.toString());
		String componentName;
		for (IEditableComponent ec: this.gp.components) {
			componentName = ec.getEditMode().toString();
			String tilePath = ec.getDataFilename();
			Path tilePathP = Paths.get(dataFolderName, tilePath);
			//Utils.writeInt2DAToCSV(ec.getGridData(), tilePathP.toString());
			int[][] data=null;
			try {
				System.out.print("Loading component data :"+componentName);
				try {

					data = Utils.openCSVto2DAInt(tilePathP.toString());

					System.out.println("..OK" );
				}catch(FileNotFoundException e) {

					System.out.println("..No FILE" );
				}
				
				ec.setGridData(data);
			} catch (NegativeArraySizeException e) {
				System.err.println("..FAIL" );
				System.err.println("Error while loading file: "+ tilePathP.toString());
				e.printStackTrace();
				continue;
			} catch (Exception e){
				
			}
		}

	}

	public void toggleLatch() {
		this.latchEnable = !this.latchEnable;
		if (this.latchEnable) {
			this.latchString = "LATCH";
		}else {
			this.latchString = "";
		}
		System.out.println("Latch: "+this.latchEnable);
	}

	public void toggleEditDeleteMode() {
		delete = ! delete;
		if (delete) {
			System.out.println("delete mode on");
		}else {
			System.out.println("delete mode off");
		}
		
	}

}
