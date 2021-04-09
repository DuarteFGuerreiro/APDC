package pt.unl.fct.di.app.avaliacao.util;

public class LoginData {

	private String username, password;
	
	public LoginData() {}
	
	public LoginData (String username, String password) {
		this.username = username;
		this.password = password;
	
	}
	
	public String getPassword() {
		return password;
	}

	public String getUsername() {
		return username;
	}
}
