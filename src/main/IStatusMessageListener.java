package main;

public interface IStatusMessageListener {
	/**triggerAction is implemented in the Listener class that mplements this interface.
	 * That class should also call the .add() method of the Sender object with iitself as argument
	 * 
	 * Sender does not implement anything, but maintains a list of listeners
	 * 
	 * @param eventname
	 */
	void triggerAction(String eventname);
}
