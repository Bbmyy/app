package io.dcloud.uniplugin.txtParse.format;

public class HTTPRequest implements DataBase{
    private String time;
    private String uuid;
    private int type;
    private int length;
    private String method;
    private String host;
    private String data;
    private String RequestID;

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

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "HTTPRequest{" +
                "time='" + time + '\'' +
                ", uuid='" + uuid + '\'' +
                ", type=" + type +
                ", length=" + length +
                ", method='" + method + '\'' +
                ", host='" + host + '\'' +
                ", data='" + data + '\'' +
                ", RequestID='" + RequestID + '\'' +
                '}';
    }
}
