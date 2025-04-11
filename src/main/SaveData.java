package main;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

public class SaveData {
	private static final long serialVersionUID = 1L;
	static String fileURL = "save0.ser";
	
	GamePanel gp;
	PData currentData;
	public SaveData(GamePanel gp) {
		  this.gp = gp;
		  this.currentData = new PData();
	}
	
	public void updateSaveData() {
		if(currentData==null) {
			currentData = new PData();
		}
		this.currentData.inventoryData = gp.inventory.getInventoryDataCopy();
		this.currentData.plantData = gp.plant.copyPlantData();
		this.currentData.health = gp.player.health;
		this.currentData.level = gp.level;
		this.currentData.money = gp.player.money;
		this.currentData.stamina=gp.player.stamina;
		this.currentData.worldX = gp.player.worldX;
		this.currentData.worldY = gp.player.worldY;
		
	}
	
	public void loadSaveDataToClasses() {
		gp.inventory.setInventoryData(this.currentData.inventoryData);
		gp.plant.setGridData(this.currentData.plantData);
		gp.player.health=this.currentData.health;
		gp.player.stamina=this.currentData.stamina;
		gp.level=this.currentData.level;
		gp.player.money = this.currentData.money;
		gp.player.worldX=this.currentData.worldX;
		gp.player.worldY=this.currentData.worldY;
		
	}
	
	public static void loadSaveDataFromFile(PData sd) {
		
	}
	
	public void saveSaveDataToFile(PData sd) {
		FileOutputStream fileOut = null;
		ObjectOutputStream out;
		try {
			fileOut = new FileOutputStream(fileURL);
			 out = new ObjectOutputStream(fileOut);
			 out.writeObject(sd);
			 out.close();
			 fileOut.close();
			 System.out.println("Read save file success: "+fileURL);
		} catch (IOException e) { 
			System.err.println("Read save file error: "+fileURL);
			e.printStackTrace();
		}finally {
			
		}
	}
	
	public void updateAndSaveDataToFile( ) {
		updateSaveData();
		if(null==this.currentData) {
			System.err.println("SaveData: input data is null");
		}
		FileOutputStream fileOut = null;
		ObjectOutputStream out=null;
		try {
			fileOut = new FileOutputStream(fileURL);
			 if(fileOut != null) System.out.println("Create file handle OK : "+fileURL);
			 out = new ObjectOutputStream(fileOut);
			 out.writeObject(this.currentData);
			 out.close();
			 fileOut.close();
			 System.out.println("Write save file success: "+fileURL);
		} catch (IOException e) { 
			System.err.println("Write save file error: "+fileURL);
			e.printStackTrace();
		}finally {
			
		}
	}
	
	public void loadSaveDataFromFileAndUpdateClasses( ) {
		updateSaveData();
		if(null==this.currentData) {
			System.err.println("SaveData: input data is null");
		}
		FileInputStream fileIn = null;
		ObjectInputStream objIn=null;
		try {
			fileIn = new FileInputStream(fileURL);
			 if(fileIn != null) System.out.println("Create file handle OK : "+fileURL);
			 objIn = new ObjectInputStream(fileIn);
			 this.currentData = (PData) objIn.readObject();
			 objIn.close();
			 fileIn.close();
			 System.out.println("Read save file success: "+fileURL);
		} catch (IOException | ClassNotFoundException e) { 
			System.err.println("Read save file error: "+fileURL);
			e.printStackTrace();
		}finally {
			
		}
		// copy data from save object into component classes
		loadSaveDataToClasses();
		
		//switch the level to one in save data
		gp.loadComponentData();
		
		//move camera to player position
		gp.camera.recenterCamera();
		
	}
	
	public byte[] updateAndSaveDataToFileC( ) {
		PData object = new PData();
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(object);
 
            byte[] byteArray = bos.toByteArray();
            return byteArray;
 
        } catch (IOException e) {
            e.printStackTrace();
            return null;
 
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (IOException ex) {
            }
            try {
                bos.close();
            } catch (IOException ex) {
            }
        }

        
        
    
	}
	
	
	

}
