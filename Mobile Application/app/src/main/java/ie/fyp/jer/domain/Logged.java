package ie.fyp.jer.domain;

import java.util.ArrayList;

public class Logged {
    private int id;
    private String email;
    private ArrayList<String> buildings;

    public Logged(String title, int id) {
        super();
        this.email = title;
        this.id = id;
        buildings = new ArrayList<>();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String title) {
        this.email = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<String> getBuildings() {
        return buildings;
    }

    public void setBuildings(ArrayList<String> buildings) {
        this.buildings = buildings;
    }
}