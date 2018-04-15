package ie.fyp.jer.domain;

public class Response {
    private Logged log;
    private int code;


    public Response(Logged log, int code) {
        super();
        this.log = log;
        this.code = code;
    }

    public Logged getLog() {
        return log;
    }

    public void setLog(Logged log) {
        this.log = log;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}