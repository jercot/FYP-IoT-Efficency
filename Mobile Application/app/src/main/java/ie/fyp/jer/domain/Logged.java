package ie.fyp.jer.domain;

import java.util.ArrayList;

public class Logged {
    private int id;
    private String title, token;
    private ArrayList<String> buildings;

    public Logged(String title, int id) {
        super();
        this.title = title;
        this.id = id;
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
}