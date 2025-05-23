package main;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;

import main.GamePanel.InputAction;

public class Conversation implements IInputListener {
	public static final String CONV_FILES_FOLDER = "conversations";
	public static final String DATA_FOLDER = "leveldata";
	public static final String CONV_FILES_PREFIX = "conv";
	public static final String CHAIN_FILES_PREFIX = "conversationChains";
	public static final String DATA_FILE_SUFFIX = ".cfg";
	public static final String CHAIN_FILE_SUFFIX = ".csv";
	public static final String CONV_FILES_SEP = "`";
	public static final int CONV_FILES_FIELDS = 3;
	public static final boolean LOAD_FIRST_ROOM = true;
	public static final boolean FREEZE_PLAYER_ON_CONVERSATION = true;
	
	// string subtitutions
	public static final String PC_NAME = "Mike";
	public static final String TOWN_NAME = "Milkington";
	public static final String CITY_NAME = "Garry";
	public static final String ACTOR = "ACTOR";
	public static final char DOUBLE_QUOTE = '"';
	public static final int ACTOR_NUMBER_STRING_LENGTH = 2;

	public int activeMessageID = 0;
	public int activeMessageIterator = 0;
	public int activeChainID = 0;
	public boolean displayTextBox = false;
	public boolean dialogTextChanged = false;
	public boolean chainActive = false;
	public ConvRecord activeRecord = null;
	public String currentDialogText = null;

	GamePanel gp;
	ArrayList<ConvRecord> currentConvRecords;
	HashMap<Integer, Integer[]> conversationChains; // chain ID : messageIDS
	Integer[] activeChainArray = null;
	public int speakerNPC;

	/*
	 * conv.cfg files: messageID`actorID`MessageString
	 * 
	 * conversationChains file: chainID,messageID1,messageID2,messageID3...
	 * 
	 * 
	 */

	public Conversation(GamePanel gp) {
		this.gp = gp;
		currentConvRecords = new ArrayList<>();
		if (LOAD_FIRST_ROOM) {
			loadDataFromFileConversationChains();
			loadDataFromFileCurrentRoom();
			printDialogChainsToConsole();
		}
		gp.input.addListener(this);
	}

	public void draw() {

	}
	
	public void setSpeakerNPC(int entityKind){
		if (entityKind>0) {
			this.speakerNPC = entityKind;
		}
	}
	
	public ConvRecord getConvRecordByID(int recordID) {
		for (ConvRecord cr : currentConvRecords) {
			if (cr.id()==recordID) {
				return cr;
			}
		}
		return null;
	}

	public void update() {
		//gp.hud.setShowDialogBox(true);
		if (displayTextBox) {
			gp.hud.setShowDialogBox(true);
		}

		if (dialogTextChanged) {
			gp.hud.dialogTextBox.updateTextContent(currentDialogText);
			dialogTextChanged =false;
		}

	}

	public void startConversation(int chainID) {
		System.out.println("start conv");
		if (chainActive) {
			return;
			// don't allow this function to run if conversation already in progress
		}
		chainActive = true;
		Integer[] currChain = conversationChains.get(chainID);
		if (currChain == null) {
			System.out.println("Conversation: chain is null " + chainID);
			return;
		}
		activeChainID = chainID;
		chainActive = true;
		displayTextBox = true;
		dialogTextChanged = true;
		activeChainArray = currChain;
		activeMessageIterator = 0;
		try {
			activeMessageID = activeChainArray[activeMessageIterator];
			currentDialogText = getConvRecordByID(activeMessageID).message();
			String actorName = getActorNameFromID(getConvRecordByID(activeMessageID).actor());
			currentDialogText = substituteNamesInString(  currentDialogText,   actorName);
	
			gp.hud.dialogTextBox.setTextContent(currentDialogText);
			gp.hud.speakerString.updateString(actorName);
			if(FREEZE_PLAYER_ON_CONVERSATION) {
				gp.player.frozen=true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private String substituteNamesInString(String rawString, String actorName) {
		if(rawString.charAt(0)==DOUBLE_QUOTE) {
			rawString = rawString.substring(1);
		} 
		if(rawString.charAt(rawString.length()-1)==DOUBLE_QUOTE) {
			rawString = rawString.substring(0,rawString.length()-1);
		}
		rawString = rawString.replaceAll("TOWN_NAME", TOWN_NAME);
		rawString = rawString.replaceAll("PC_NAME", PC_NAME);
		rawString = rawString.replaceAll("ACTOR_NAME", actorName);
		rawString = rawString.replaceAll("CITY_NAME", CITY_NAME);
		int location = rawString.indexOf("ACTOR[0-9]+",0);
		if (-1 != location) {
			int end = location + ACTOR.length() + ACTOR_NUMBER_STRING_LENGTH;
			String actorNumberString = rawString.substring(location, end);
			String actorOther = getActorNameFromID(Integer.parseInt(actorNumberString));
			rawString = rawString.replaceAll("ACTOR[0-9]+", actorOther);
		}
		return rawString;
	}

	public void advanceConversation() {
		if(chainActive) {
			activeMessageIterator +=1;
			System.out.println("advanceConversation "+activeMessageID);
			try {
				activeMessageID = activeChainArray[activeMessageIterator];
				int actorID = getConvRecordByID(activeMessageID).actor();
				String actorName = getActorNameFromID(actorID);
				currentDialogText = getConvRecordByID(activeMessageID).message();
				currentDialogText = substituteNamesInString(  currentDialogText,   actorName);

				gp.hud.dialogTextBox.setTextContent(currentDialogText);
				
				//System.out.println(actorID);
				gp.hud.speakerString.updateString(actorName);
			} catch (Exception e) {

				conversationOver();
				return;
				//e.printStackTrace();
			}
		}
		

	}
	
	public void conversationOver() {
		System.out.println("conversationOver "+activeMessageID);
		chainActive = false;
		displayTextBox = false;
		gp.hud.setShowDialogBox(false);
		gp.player.frozen = false;
		gp.brain.endConversationNPC(this.speakerNPC);
	}

	public void loadRecordsFromFile(int conversationID) {
		currentConvRecords = new ArrayList<Conversation.ConvRecord>();
		String filename = this.getConversationFileNameFromID(conversationID);
		String[] recordsAsStrings = Utils.getStringsFromFile(filename);
		String[] subStrings = null;
		for (String st : recordsAsStrings) {
			subStrings = st.split(CONV_FILES_SEP);
			if (subStrings.length != CONV_FILES_FIELDS) {
				System.err.println("Conversation loadRecordsFromFile wrong amount of fields " + filename);
			} else {
				int id = Integer.parseInt(subStrings[0]);
				int actor = Integer.parseInt(subStrings[1]);
				ConvRecord cr = new ConvRecord(id, actor, subStrings[2]);
				currentConvRecords.add(cr);
			}
		}

	}

	private String getConversationFileNameFromID(int conversationID) {
		return CONV_FILES_PREFIX + String.format("%03d", conversationID) + DATA_FILE_SUFFIX;
	}

	private boolean folderExists(String folderName) {
		File file = new File(folderName);
		if (file.mkdir()) {
			System.out.println("folder created " + folderName);
			return true;
		}
		return false;

	}

	public String getDataFilename() {
		return Utils.getLevelresourceFilename(this.gp.level, CONV_FILES_PREFIX, DATA_FILE_SUFFIX);
	}

	public void displayLine(int id) {
		for (ConvRecord cr : currentConvRecords) {
			if (cr.id() == id) {
				gp.hud.dialogTextBox.setTextContent(cr.message());
			}
		}
	}

	public void printRecordsToConsole() {
		for (ConvRecord cr : currentConvRecords) {
			System.out.printf("record id %d   actor %d  message %s \n", cr.id(), cr.actor(), cr.message());
		}
	}

	public void printDialogChainsToConsole() {
		Set<Entry<Integer, Integer[]>> entrySet = this.conversationChains.entrySet();
		Set<Integer> keySet = this.conversationChains.keySet();
		for (Integer key : keySet) {
			Integer[] value = this.conversationChains.getOrDefault(key, null);
			String combo = "";
			for (Integer convID : value) {
				if (null != convID) {
					String numberAsString = Integer.toString(convID);
					combo += numberAsString + ",";
				}
			}
			System.out.printf("Chain id %d   combo: %s  \n", key, combo);
		}
	}

	public void loadDataFromFileConversationChains() {
		String mapFileName = getDataFilename();
		currentConvRecords = new ArrayList<Conversation.ConvRecord>();
		String URI = CHAIN_FILES_PREFIX + CHAIN_FILE_SUFFIX;
		Path tilePathP = Paths.get(DATA_FOLDER, URI);
		if (Utils.createFileIfNotExist(DATA_FOLDER, URI))
			return;
		ArrayList<int[]> rawChainData = null;
		try {
			rawChainData = Utils.openCSVto2DAIntListJagged(tilePathP.toString());
		} catch (Exception e) {
			System.err.println("Conversation loadDataFromFileConversationChains unable to read the file " + URI);
			e.printStackTrace();
		}
		conversationChains = new HashMap<Integer, Integer[]>();
		for (int[] inner : rawChainData) {
			Integer[] cutValues = new Integer[inner.length - 1];
			int key = 0;
			for (int i = 0; i < inner.length; i++) {
				if (i == 0) {
					key = inner[i];
				} else {
					cutValues[i - 1] = inner[i];
				}
			}
			conversationChains.put(key, cutValues);
		}

	}

	public void loadDataFromFileCurrentRoom() {
		String mapFileName = getDataFilename();
		currentConvRecords = new ArrayList<Conversation.ConvRecord>();
		String URI = getDataFilename();
		Path tilePathP = Paths.get(DATA_FOLDER, URI);
		if (Utils.createFileIfNotExist(DATA_FOLDER, URI))
			return;
		String[] rawData = Utils.getStringsFromFile(tilePathP.toString());
		LinkedList<String[]> splitData = new LinkedList<>();
		String[] seperatedLine;

		for (String line : rawData) {
			seperatedLine = line.split(CONV_FILES_SEP);
			if (seperatedLine.length == CONV_FILES_FIELDS) {

				splitData.add(seperatedLine);
			} else {
				System.err.printf("Conversation file data incorrect length %s /n", line);
			}

		}
		for (String[] lineData : splitData) {
			int id = Integer.parseInt(lineData[0]);
			int actor = Integer.parseInt(lineData[1]);
			String message = lineData[2];
			ConvRecord cr = new ConvRecord(id, actor, message);
			currentConvRecords.add(cr);

		}

		// printRecordsToConsole();
	}

	public record ConvRecord(int id, int actor, String message) {
	}

	@Override
	public void inputListenerAction(InputAction action) {
		if (action==InputAction.ACTION) {
			//startConversation(activeChainID);
			advanceConversation();
		}

	}
	
	public String getActorNameFromID(int ID) {
		switch(ID) {
		case -1:
			return PC_NAME;
		case 10:
			return "Rick";
		case 11:
			return "Lilly";
		case 12:
			return "Rodney";
		case 13:
			return "Nicole";
		case 14:
			return "Chuck";
		case 15:
			return "Daniel";
		default:
			return "Anna";
		}
	}

}
