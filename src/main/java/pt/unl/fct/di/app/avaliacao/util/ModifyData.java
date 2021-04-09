package pt.unl.fct.di.app.avaliacao.util;

public class ModifyData {

	private String username, tokenID, password, email, profile, address1, address2, locality, phone, mobile, state, removed;
	
	public ModifyData () {}
	
	public ModifyData (String username, String tokenID, String password, String email, String profile, String address1, String address2, String locality, String phone, String mobile, String state, String removed) {
		this.username=username;
		this.tokenID=tokenID;
		this.password=password;
		this.email=email;
		this.profile=profile;
		this.address1=address1;
		this.address2=address2;
		this.locality=locality;
		this.phone=phone;
		this.mobile=mobile;
		this.state=state;
		this.removed = removed;
	}

	public String getState() {
		return state;
	}

	public String getMobile() {
		return mobile;
	}

	public String getRemoved() {
		return removed;
	}

	public String getProfile() {
		return profile;
	}

	public String getLocality() {
		return locality;
	}

	public String getPhone() {
		return phone;
	}

	public String getAddress1() {
		return address1;
	}

	public String getAddress2() {
		return address2;
	}

	public String getUsername() {
		return username;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public String getTokenID() {
		return tokenID;
	}
	

}
