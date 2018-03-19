package xyz.robertsen.wecomserver;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.TreeSet;

public class WeComServer {

  private ServerSocket serverSocket;
  private Socket socket;
  private ObjectInputStream input;
  private ObjectOutputStream output;
  private ArrayList<ObjectOutputStream> outputs;
  private TreeSet<ClientHandler> clients;
  private Object obj;

  public final String[] login = {"Kristian", "Atle", "Nikolai", "Ane"};
  public final String[] pwd = {"heihei", "lol", "kek", "fuck"};

  public WeComServer() {
	outputs = new ArrayList<>();
	clients = new TreeSet<>();

	try {
	  serverSocket = new ServerSocket(8000);
	  System.out.println("Server running.");
	} catch (IOException ex) {
	  System.out.println("Error creating socket.");
	}
  }

  public static void main(String[] args) {

	WeComServer server = new WeComServer();
	new Thread(() -> {
//			server.createStreams();
	  server.createHandlers();
	}).start();
  }

  private void createHandlers() {
	while (true) {
	  try {
		ClientHandler client = new ClientHandler(
			this,
			serverSocket.accept(),
			clients
		);
	  } catch (IOException ex) {
		System.out.println("Error creating client-handler.");
	  }
	}
  }

  private void createStreams() {
	try {
	  while (true) {
		socket = serverSocket.accept();
		System.out.println("Socket request received.");

		input = new ObjectInputStream(socket.getInputStream());
		output = new ObjectOutputStream(socket.getOutputStream());
		System.out.println("Streams created.");

		outputs.add(output);
		System.out.println("Current amount of outputstreams: " + outputs.size());

		new Thread(() -> {
		  try {
			while (true) {
			  obj = input.readObject();
			  System.out.println("Object received!");
			  if (obj instanceof String) {
				for (ObjectOutputStream o : outputs) {
				  o.writeObject(obj);
				  System.out.println("Text relayed.");
				}
			  } else if (obj.getClass().isArray()) {
				String[] userdata = (String[]) (obj);
				for (int i = 0; i < login.length; i++) {
				  if (login[i].equalsIgnoreCase(userdata[0])) {
					if (pwd[i].equalsIgnoreCase(userdata[1])) {
					  userdata[0] = login[i];
					  output.writeObject(userdata);
					  System.out.println("Login accepted.");
					}
				  }
				}
			  } else {
				System.out.println("No idea what this shit is.\n");
			  }
			}
		  } catch (IOException ex) {
			System.out.println("Error reading object");
		  } catch (ClassNotFoundException ex) {
			System.out.println("Unknown class.");
		  }
		}).start();
	  }
	} catch (IOException ex) {
	  System.out.println("Error creating streams.");
	}
  }

  public void removeClient(ClientHandler client) {
	clients.remove(client);
	clients.forEach(System.out::println);
  }

  public void addClient(ClientHandler client) {
	clients.add(client);
	client.run();
  }
}
