package pt.unl.fct.di.app.avaliacao.util;

import java.util.regex.*;

public class ProfileData {

	private String profile, address1, address2, locality, phone, mobile, state, removed;
	
	public ProfileData () {}
	
	public ProfileData (String profile, String address1, String address2, String locality, String phone, String mobile, String state, String removed) {
		this.profile=profile;
		this.address1=address1;
		this.address2=address2;
		this.locality=locality;
		this.phone=phone;
		this.mobile=mobile;
		this.state=state;
		this.removed = removed;
	}
	
	public boolean validateProfileData () {
		if (!profile.equals("PUBLICO") || !profile.equals("PRIVADO"))
				return false;
		if (mobile!=null) {
			String regexMobile = "([+]351\\s)?[9][0-9]{8}";
			Pattern mobilePattern = Pattern.compile(regexMobile);
			Matcher mobileMatcher = mobilePattern.matcher(mobile);
			if (!mobileMatcher.matches())
				return false;
		}
		if (phone!=null) {
			String regexPhone = "([+]351\\s)?[23][0-9]{8}";
			Pattern phonePattern = Pattern.compile(regexPhone);
			Matcher phoneMatcher = phonePattern.matcher(phone);
			if (!phoneMatcher.matches())
				return false;
		}
		if (locality != null) {
			String regexLocality = "/[A-Z]*[a-z]* [A-Z]*[a-z]* : \\d{4}\\-\\d{3]/";
			Pattern localityPattern = Pattern.compile(regexLocality);
			Matcher localityMatcher = localityPattern.matcher(locality);
			if (!localityMatcher.matches())
				return false;
		}
		
		return true;
		
	}
	
	public String getRemoved () {
		return removed;
	}
	
	public String getState() {
		return state;
	}

	public String getPhone() {
		return phone;
	}

	public String getProfile() {
		return profile;
	}

	public String getMobile() {
		return mobile;
	}

	public String getAddress2() {
		return address2;
	}

	public String getAddress1() {
		return address1;
	}

	public String getLocality() {
		return locality;
	}

}
