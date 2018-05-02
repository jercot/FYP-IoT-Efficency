package ie.fyp.jer.model;

public class Sensor {
    private int code, octet;
    private String bucket, room;

    public Sensor(int code, String bucket) {
        this.code = code;
        this.bucket = bucket;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room.replace(" ", "_");
    }

    public int getOctet() {
        return octet;
    }

    public void setOctet(int octet) {
        this.octet = octet;
    }
}
