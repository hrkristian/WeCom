package xyz.robertsen.wecomclient;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.Socket;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import xyz.robertsen.wecomserver.LoginPackage;

/**
 *
 * @author Kristian Robertsen
 */
public class WeComClient extends Application {

  private double SF = 1;
  private LoginPackage lp = new LoginPackage("reference");

  private String weComServerURL = "localhost";
  private Socket socket = null;
  private ObjectOutputStream outputStream;
  private ObjectInputStream inputStream;
  private boolean poll = true;
  private Object received;
  private String userID;

  private Scene scene;
  private VBox root;
  private TextField textInputField, userNameField, passwordField;
  private ChatLog chatLog;
  private Button sendBtn, loginButton;

  @Override
  public void start(Stage primaryStage) {

	new Thread(() -> {
	  while (socket == null) {
		try {
		  socket = new Socket(weComServerURL, 8000);
		  outputStream = new ObjectOutputStream(socket.getOutputStream());
		  inputStream = new ObjectInputStream(socket.getInputStream());
		} catch (IOException ex) {
		  System.out.println(ex.toString());
		}
	  }
	}).start();

	// Layout start
	root = new VBox();
	root.setSpacing(5);
	root.setAlignment(Pos.CENTER);
	root.setPadding(new Insets(20 * SF));
	// Innloggingsskjerm
	userNameField = new TextField();
	passwordField = new TextField();
	loginButton = new Button("Login");
	root.getChildren().addAll(userNameField, passwordField, loginButton);

	if (Screen.getPrimary().getBounds().getWidth() > 2000) {
	  SF = 2;
	  root.setStyle("-fx-font-size: 26px;");
	} // Scales up on HiDPI screens.

	// Chat-GUI
	chatLog = new ChatLog(this);
	textInputField = new TextField();
	sendBtn = new Button("Send");
	// Layout end

	loginButton.setOnAction(login -> {
	  login();
	});
	userNameField.setOnAction(login -> {
	  login();
	});
	passwordField.setOnAction(login -> {
	  login();
	});
	sendBtn.setOnAction(send -> {
	  sendObject((userID + ": " + textInputField.getText()));
	});
	textInputField.setOnAction(send -> {
	  sendObject((userID + ": " + textInputField.getText()));
	});

	scene = new Scene(root);
	primaryStage.setOnCloseRequest(cr -> {
	  try {
		poll = false;
		if (socket != null) {
		  socket.close();
		}
		primaryStage.close();

	  } catch (IOException ex) {
		System.out.println("Feil ved lukking av socket:\n" + ex.toString());
	  }
	});
	primaryStage.setTitle("WeCom");
	primaryStage.setScene(scene);
	primaryStage.show();
	
	new Thread(() -> {
	  while (poll) {
		try {
		  received = inputStream.readObject();
		  //System.out.println("Object received. Processing...");
		  if (received instanceof String) {
			Platform.runLater(() -> {
			  chatLog.appendText((String) received + "\n");
			});
		  } else if (received instanceof Image) {

		  } else if (received.getClass().getCanonicalName().equals(lp.getClass().getCanonicalName())) {
			LoginPackage login = (LoginPackage) received;
			userID = login.name;
			Platform.runLater(() -> {
			  chatGUI(primaryStage);
			});
		  }
		} catch (IOException ex) {
		  System.out.println("Error reading incoming object.");
		} catch (ClassNotFoundException ex) {
		  System.out.println("Class of incoming object not recognised.");
		} catch (NullPointerException npe) {
		  System.out.println(npe.toString());
		}
	  }
	}).start();

  }

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
	launch(args);
  }

  private void sendObject(Object output) {
	try {
	  outputStream.writeObject(output);
	  textInputField.clear();
	} catch (IOException ex) {
	  System.out.println("Error writing object to stream.\n" + ex.toString());
	}
  }

  private void login() {
	try {
	  String[] userdata = {
		userNameField.getText().trim(),
		passwordField.getText().trim()
	  };
	  outputStream.writeObject(userdata);
	} catch (IOException ex) {
	  System.out.println("Error sending login information.\n" + ex.toString());
	}
  }

  void chatGUI(Stage stage) {
	root.getChildren().clear();
	root.getChildren().addAll(chatLog, textInputField, sendBtn);
	stage.sizeToScene();
	textInputField.requestFocus();
  }
}
