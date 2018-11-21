package my.vaadin.vcapp;

public class User {
	private String name;
	private String email;
	private boolean admin = false; // true wenn ein User ein admin ist - Projekte anlegen kann
	private boolean loggedIn; // true wenn sich ein User angelegt hat / angemeldet hat
	private static User instance ;
	
	public static User getInstance() {
		if (instance == null) {
			instance = new User();
		}
		return instance;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}
	
	public void logOut() {
		instance = new User();
		loggedIn = false;
		admin = false;
	}

}
