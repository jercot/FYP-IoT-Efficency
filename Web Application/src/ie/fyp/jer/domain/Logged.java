package ie.fyp.jer.domain;

import java.util.ArrayList;

import ie.fyp.jer.config.Token;

public class Logged {
	private int id;
	private String token;
	private ArrayList<String> buildings;

	public Logged(int id) {
		super();
		this.id = id;
		generate();
		buildings = new ArrayList<>();
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

	public ArrayList<String> getBuildings() {
		return buildings;
	}

	public void setBuildings(ArrayList<String> buildings) {
		this.buildings = buildings;
	}
	
	public void addBuilding(String name) {
		buildings.add(name);
	}

	public boolean compare(String token) {
		String temp = this.token;
		generate();
		if(token.equals(temp))
			return true;
		return false;
	}
	
	private void generate() {
		this.token =  Token.generate();
	}
}