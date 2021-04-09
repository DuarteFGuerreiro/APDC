package pt.unl.fct.di.app.avaliacao.util;

public class RegisterData {

	private String username, password, password2, email;
	
	public RegisterData() {}
	
	public RegisterData (String username, String password, String password2, String email) {
		this.username = username;
		this.password = password;
		this.password2 = password2;
		this.email = email;
	}

	public boolean validRegistration() {
		if (username==null || password == null || email == null || !validEmail() || !password.equals(password2) || !longEnough())
			return false;
		return true;
	}
	
	private boolean longEnough() {
		if (password.length() >= 6)
			return true;
		return false;
	}
	
	private boolean validEmail() {
		String part1 = email.substring(0, email.indexOf("@"));
		String part2 = email.substring(email.indexOf("@"), email.lastIndexOf("."));
		String part3 = email.substring(email.lastIndexOf("."));
		if (part1 != null && part2 != null && part3 != null)
			return true;
		return false;
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

	public String getPassword2() {
		return password2;
	}
}
