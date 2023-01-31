package io.dcloud.uniplugin.txtParse.format;

public class Result {
    private DataBase dataBase;
    private String header;

    public DataBase getDataBase() {
        return dataBase;
    }

    public void setDataBase(DataBase dataBase) {
        this.dataBase = dataBase;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    @Override
    public String toString() {
        return "Result{" +
                "dataBase=" + dataBase +
                ", header='" + header + '\'' +
                '}';
    }
}
