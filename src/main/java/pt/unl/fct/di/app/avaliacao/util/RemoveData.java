package pt.unl.fct.di.app.avaliacao.util;

public class RemoveData {

	private String username, tokenID, admin;
	
	public RemoveData() {}
	
	public RemoveData (String username, String tokenID, String admin) {
		this.username = username;
		this.tokenID = tokenID;
		this.admin = admin;
	
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
