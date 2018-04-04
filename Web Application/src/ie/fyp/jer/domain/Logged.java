package ie.fyp.jer.domain;

import java.util.Random;

public class Logged {
	private int id;
	private String token;

	public Logged(int id) {
		super();
		this.id = id;
		generate();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public boolean compare(String token) {
		String temp = this.token;
		generate();
		if(token.equals(temp))
			return true;
		return false;
	}
	
	private void generate() {
		String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        token = salt.toString();
	}
}