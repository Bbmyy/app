package io.dcloud.uniplugin.txtParse.format;

public class DefaultParse implements DataBase{
    private String time;
    private String uuid;
    private int type;
    private int length;
    private String content;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "DefaultParse{" +
                "time='" + time + '\'' +
                ", uuid='" + uuid + '\'' +
                ", type=" + type +
                ", length=" + length +
                ", content='\n" + content + '\'' +
                '}';
    }
}
