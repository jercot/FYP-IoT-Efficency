package ie.fyp.jer.domain;

import java.util.ArrayList;

import ie.fyp.jer.config.Token;

public class Logged {
	private int id;
	private String title, token;
	private ArrayList<String> buildings;

	public Logged(String title, int id) {
		super();
		this.title = title;
		this.id = id;
		generate();
		buildings = new ArrayList<>();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public void editBuilding(String pName, String bName) {
		for(int i=0; i<buildings.size(); i++) {
			if(buildings.get(i).equals(pName))
				buildings.set(i, bName);
		}
	}
}