package ie.fyp.jer.model;

public class HouseData {
	String name, token;
	int floor, hum, light, heat, lum;
	float temp;
	
	public HouseData(String name, String token, int floor, int hum, int light, float temp) {
		super();
		this.token = token;
		this.name = name;
		this.floor = floor;
		this.hum = hum;
		this.light = light;
		this.temp = temp;
		if(this.temp>30)
			heat = 5;
		else if(this.temp>=25)
			heat = 4;
		else if(this.temp>=20)
			heat = 3;
		else if(this.temp>=15)
			heat = 2;
		else if(this.temp>=10) 
			heat = 1;
		else
			heat = 0;
		if(this.light>=800)
			lum = 5;
		else if(this.light>=600)
			lum = 4;
		else if(this.light>=400)
			lum = 3;
		else if(this.light>=200)
			lum = 2;
		else if(this.light>=100)
			lum = 1;
		else
			lum = 0;
	}

	public int getHeat() {
		return heat;
	}

	public void setHeat(int heat) {
		this.heat = heat;
	}

	public int getLum() {
		return lum;
	}

	public void setLum(int dark) {
		this.lum = dark;
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

	public int getHum() {
		return hum;
	}

	public void setHum(int hum) {
		this.hum = hum;
	}

	public int getLight() {
		return light;
	}

	public void setLight(int light) {
		this.light = light;
	}

	public float getTemp() {
		return temp;
	}

	public void setTemp(float temp) {
		this.temp = temp;
	}
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}