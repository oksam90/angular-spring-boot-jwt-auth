package sn.gainde2000.AngularSpringBootJwtAuth.payload.response;

//class qui définit le payload des messages
public class MessageResponse {
	
	private String message;

	public MessageResponse(String message) {
	    this.message = message;
	  }

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
