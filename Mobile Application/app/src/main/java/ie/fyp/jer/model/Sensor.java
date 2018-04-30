package ie.fyp.jer.model;

public class Sensor {
    private int code, subnet;
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
        this.room = room;
    }

    public int getSubnet() {
        return subnet;
    }

    public void setSubnet(int subnet) {
        this.subnet = subnet;
    }
}
