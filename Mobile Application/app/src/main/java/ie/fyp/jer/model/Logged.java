package ie.fyp.jer.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Logged implements Parcelable {
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

    protected Logged(Parcel in) {
        id = in.readInt();
        email = in.readString();
        if (in.readByte() == 0x01) {
            buildings = new ArrayList<>();
            in.readList(buildings, String.class.getClassLoader());
        } else {
            buildings = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(email);
        if (buildings == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(buildings);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Logged> CREATOR = new Parcelable.Creator<Logged>() {
        @Override
        public Logged createFromParcel(Parcel in) {
            return new Logged(in);
        }

        @Override
        public Logged[] newArray(int size) {
            return new Logged[size];
        }
    };
}