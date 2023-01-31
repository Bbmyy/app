package io.dcloud.uniplugin.txtParse.format;

public class HTTPResponse implements DataBase{
    private String time;
    private String uuid;
    private int type;
    private int length;
    private String status;
    private String RequestID;
    private String data;

    public String getRequestID() {
        return RequestID;
    }
    public void setRequestID(String requestID) {
        RequestID = requestID;
    }
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "HTTPResponse{" +
                "time='" + time + '\'' +
                ", uuid='" + uuid + '\'' +
                ", type=" + type +
                ", length=" + length +
                ", status='" + status + '\'' +
                ", RequestID='" + RequestID + '\'' +
                ", data='\n" + data + '\'' +
                '}';
    }
}
