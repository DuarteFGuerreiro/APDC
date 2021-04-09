package pt.unl.fct.di.app.avaliacao.util;

public class RoleData {

	private String tokenID, admin, username, role;
	
	public RoleData() {}
	
	public RoleData(String tokenID, String admin, String username, String role) {
		this.tokenID=tokenID;
		this.admin=admin;
		this.username=username;
		this.role=role.toUpperCase();
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

	public String getRole() {
		return role;
	}
}
