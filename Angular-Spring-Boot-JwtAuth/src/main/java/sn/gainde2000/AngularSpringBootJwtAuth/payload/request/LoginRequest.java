package sn.gainde2000.AngularSpringBootJwtAuth.payload.request;

import javax.validation.constraints.NotBlank;

//class qui définit le payload de connextion
public class LoginRequest {
	
	@NotBlank
	private String username;

	@NotBlank
	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
