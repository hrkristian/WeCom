
package xyz.robertsen.wecomserver;

import java.util.TreeSet;

/**
 *
 * @author Kristian Robertsen
 */
public class LoginManager {
  
  TreeSet<Login> loginDB;
  
  public LoginManager() {
	loginDB = new TreeSet<>();
	loginDB.add(new Login("kris", "heihei"));
	loginDB.add(new Login("atle", "heihei"));
	loginDB.add(new Login("nikolai", "heihei"));
  }
  
  public boolean validateLogin(String user, String password) {
	for (Login l : loginDB) {
	  System.out.println("DB-entry: "+l.getUser());
	  if ( l.getUser().equalsIgnoreCase(user) ) {
		System.out.println("Validator: User "+user+" recognised.");
		return l.validatePassword(password);
	  }
	}
	return false;
  }
  
  public void printContent() {
	loginDB.forEach(System.out::println);
  }

}

class Login implements Comparable<Login>{
  
  private String user;
  private String password;

  public Login(String user, String password) {
	this.user = user;
	this.password = password;
  }
  
  public String getUser() {
	return user;
  }
  
  public boolean validatePassword(String password) {
	return (this.password.equals(password));
  }

  @Override
  public int compareTo(Login l) {
	return (user.compareToIgnoreCase(l.getUser()));
  }
  
  public static void main(String[] args) {
	LoginManager lm = new LoginManager();
	System.out.println("Validated: "+lm.validateLogin("kris", "heihei")); // Fungerer
  }
}