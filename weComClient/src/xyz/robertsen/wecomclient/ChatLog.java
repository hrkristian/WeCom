package xyz.robertsen.wecomclient;

import java.io.ObjectInputStream;
import javafx.scene.control.TextArea;

/**
 * This class is responsible for the showing of communication content.
 *
 * @author Kristian Robertsen
 * @version 0.1 
 * @todo Swap from TextArea to an expandable solution.
 */
public class ChatLog extends TextArea {

	private WeComClient client;
	private ObjectInputStream input;

	public ChatLog(WeComClient client) {
		super();
		setEditable(false);
		setFocusTraversable(false);

		this.client = client;
	}

}
