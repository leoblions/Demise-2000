package main;

import java.io.File;
import java.util.ArrayList;

import javax.naming.directory.InvalidAttributeValueException;

public class Conversation {
	public static final String CONV_FILES_FOLDER = "conversations";
	public static final String CONV_FILES_PREFIX = "conv";
	public static final String CONV_FILES_SUFFIX = ".cfg";
	public static final String CONV_FILES_SEP = "`";
	public static final int CONV_FILES_FIELDS = 3;
	GamePanel gp;
	ArrayList<ConvRecord> currentConvRecords;

	public Conversation(GamePanel gp) {
		this.gp = gp;
		currentConvRecords = new ArrayList<>();

	}

	public void draw() {

	}

	public void update() {

	}
	
	public void displayConversation() {

	}
	
	public void advanceConversation() {

	}

	public void loadRecordsFromFile(int conversationID)  {
		currentConvRecords = new ArrayList<Conversation.ConvRecord>();
		String filename = this.getConversationFileNameFromID(conversationID);
		String[] recordsAsStrings = Utils.getStringsFromFile(filename);
		String[] subStrings=null;
		for(String st : recordsAsStrings) {
			subStrings = st.split(CONV_FILES_SEP);
			if (subStrings.length!=CONV_FILES_FIELDS) {
				System.err.println("Conversation loadRecordsFromFile wrong amount of fields "+filename);
			}else {
				int id = Integer.parseInt(subStrings[0]);
				int actor = Integer.parseInt(subStrings[1]);
				ConvRecord cr = new ConvRecord(id,actor,subStrings[2]);
				currentConvRecords.add(cr);
			}
		}

	}

	private String getConversationFileNameFromID(int conversationID) {
		return CONV_FILES_PREFIX+ String.format("%03d",conversationID)+CONV_FILES_SUFFIX;
	}

	private boolean folderExists(String folderName) {
		File file = new File(folderName);
		if (file.mkdir()) {
			System.out.println("folder created " + folderName);
			return true;
		}
		return false;

	}

	public record ConvRecord(int id, int actor, String message) {
	}

}
