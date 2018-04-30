package ie.fyp.jer.model;

import java.util.ArrayList;

import ie.fyp.jer.config.Token;

public class Logged {
	private int id;
	private String email;
	private transient String token, type;

	private ArrayList<String> buildings;

	public Logged(String email, int id) {
		super();
		this.email = email;
		this.id = id;
		generate();
		buildings = new ArrayList<>();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public void editBuilding(String pName, String bName) {
		for(int i=0; i<buildings.size(); i++) {
			if(buildings.get(i).equals(pName))
				buildings.set(i, bName);
		}
	}
	
	public boolean houseExists(String house) {
		for(String s:  buildings)
			if(s.equals(house))
				return true;
		return false;
	}
}