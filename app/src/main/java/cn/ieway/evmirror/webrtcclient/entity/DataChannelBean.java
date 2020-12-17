package cn.ieway.evmirror.webrtcclient.entity;

public class DataChannelBean {
    /**
     * data : begin
     * length : 2147483648
     * name : SQLite_Database_Browser_2.0.zip
     * type : send_req
     */

    private String data;
    private long length;
    private String name;
    private String type;
    private String path;

    public DataChannelBean() {
    }

    public DataChannelBean(String type, String data, String name, long length) {
        this.data = data;
        this.length = length;
        this.name = name;
        this.type = type;
    }

    public DataChannelBean(String data, long length, String name, String type, String path) {
        this.data = data;
        this.length = length;
        this.name = name;
        this.type = type;
        this.path = path;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "DataChannelBean{" +
                "data='" + data + '\'' +
                ", length=" + length +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
