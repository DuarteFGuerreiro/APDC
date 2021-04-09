package pt.unl.fct.di.app.avaliacao.util;

public class LogoutData {

	private String username, tokenID;
	
	public LogoutData() {}
	
	public LogoutData (String username, String tokenID) {
		this.username = username;
		this.tokenID = tokenID;
	
	}
	
	public String getTokenID() {
		return tokenID;
	}

	public String getUsername() {
		return username;
	}
}
