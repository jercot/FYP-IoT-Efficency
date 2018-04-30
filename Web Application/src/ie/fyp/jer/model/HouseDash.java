package ie.fyp.jer.model;

import java.util.ArrayList;

public class HouseDash {
	String name, location;
	private ArrayList<RoomDash> rooms;
	
	public HouseDash(String name, String location) {
		this.name = name;
		this.location = location;
		rooms = new ArrayList<>();
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public ArrayList<RoomDash> getRooms() {
		return rooms;
	}

	public void setRooms(ArrayList<RoomDash> rooms) {
		this.rooms = rooms;
	}
	
	public void addRoom(String name, int floor) {
		rooms.add(new RoomDash(name, floor));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}