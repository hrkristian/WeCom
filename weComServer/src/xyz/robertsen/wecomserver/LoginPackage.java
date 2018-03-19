
package xyz.robertsen.wecomserver;

import java.io.Serializable;

/**
 *
 * @author kris
 */
public class LoginPackage implements Serializable {
	public String name;
	
	public LoginPackage(String name) {
		this.name = name;
	}
}
