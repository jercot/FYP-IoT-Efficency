package ie.fyp.jer.model;

public class RoomDash {
	private String name;
	private int floor;
	
	public RoomDash(String name, int floor) {
		super();
		this.name = name;
		this.floor = floor;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getFloor() {
		return floor;
	}
	
	public void setFloor(int floor) {
		this.floor = floor;
	}
}