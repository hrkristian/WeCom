package xyz.robertsen.wecomserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.TreeSet;

/**
 *
 * @author Kristian Robertsen
 */
public class ClientHandler implements Runnable, Comparable<ClientHandler> {

  private Socket socket;
  private WeComServer server;
  private ObjectInputStream input;
  private ObjectOutputStream output;
  private TreeSet<ClientHandler> clients;
  private String clientID = null;
  private LoginManager lm;

  public ClientHandler(WeComServer server, Socket socket, TreeSet<ClientHandler> clients) throws IOException {
	this.socket = socket;
	this.server = server;
	this.clients = clients;
	this.lm = new LoginManager();
	System.out.println("Socket request received.");

	input = new ObjectInputStream(socket.getInputStream());
	output = new ObjectOutputStream(socket.getOutputStream());
	System.out.println("Streams created for client: ");
	// Loop for login validation.
	while (clientID == null) {
	  try {
		Object obj = input.readObject();
		System.out.println("Login-object received by ClientHandler, processing...");
		if (obj instanceof String[]) {
		  String[] userdata = (String[]) (obj);
		  if (lm.validateLogin(userdata[0], userdata[1])) {
			this.clientID = userdata[0].toLowerCase();
			System.out.println("Login validated. User is: " + clientID);
			writeObject(new LoginPackage(clientID));
			clients.add(this);
		  } else {
			System.out.println("Login failed. Login details: \nUser: " + userdata[0] + "\nPwd: " + userdata[1]);
		  }
		}
	  } catch (ClassNotFoundException e) {
		System.out.println("Received class not recognised.");
	  }
	}
	// Login validated, regular parsing loop.
	run();
  }

  @Override
  public final void run() {
	for (ClientHandler c : clients) {
	  System.out.println(c.toString());
	}
	boolean cont = true;
	Object obj;
	while (cont) {
	  try {
		while ((obj = input.readObject()) != null) {
		  System.out.println("Communications-object received by ClientHandler, processing...");
		  if (obj instanceof String) {
			for (ClientHandler cht : clients) {
			  cht.writeObject(obj);
			  System.out.println("Text relayed.");
			}
		  } else {
			System.out.println("No idea what this shit is.\n");
		  }
		}
	  } catch (IOException ex) {
		System.out.println("Error processing object in ClientHandler.");
		System.out.println(ex.toString());
	  } catch (ClassNotFoundException cnfe) {
		System.out.println("Class not found; ClientHandler");
		System.out.println(cnfe.toString());
	  } finally {
		try {
		  input.close();
		  output.close();
		  cont = false;
		  for (ClientHandler cht : clients) {
			writeObject(clientID + " has left the chat.");
		  }
		  clients.remove(this);
		} catch (IOException ex1) {
		  System.out.println(ex1.toString());
		}
	  }
	}
  }

  @Override
  public int compareTo(ClientHandler cht) {
	return clientID.compareTo(cht.clientID);
  }

  public void writeObject(Object obj) throws IOException {
	output.writeObject(obj);
  }
}
