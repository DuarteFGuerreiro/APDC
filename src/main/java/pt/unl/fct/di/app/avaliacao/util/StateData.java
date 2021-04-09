package pt.unl.fct.di.app.avaliacao.util;

public class StateData {

	private String tokenID, admin, username;
	
	public StateData() {}
	
	public StateData(String tokenID, String admin, String username) {
		this.tokenID=tokenID;
		this.admin=admin;
		this.username=username;
	}

	public String getTokenID() {
		return tokenID;
	}

	public String getUsername() {
		return username;
	}

	public String getAdmin() {
		return admin;
	}
}
