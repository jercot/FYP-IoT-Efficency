package ie.fyp.jer.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Response implements Parcelable {
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

    protected Response(Parcel in) {
        log = (Logged) in.readValue(Logged.class.getClassLoader());
        code = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(log);
        dest.writeInt(code);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Response> CREATOR = new Parcelable.Creator<Response>() {
        @Override
        public Response createFromParcel(Parcel in) {
            return new Response(in);
        }

        @Override
        public Response[] newArray(int size) {
            return new Response[size];
        }
    };
}