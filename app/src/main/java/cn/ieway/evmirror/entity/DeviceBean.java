package cn.ieway.evmirror.entity;

/**
 * 设备Bean
 * 只要IP一样，则认为是同一个设备
 */

public class DeviceBean {
    String name;    // 设备名称
//    String url;    // 设备所在房间
    /**
     * ip : 192.168.1.209
     * port : 10020
     */

    private String ip; //服务器地址
    private Integer port; //端口号


    public DeviceBean() {
    }

    public DeviceBean(String name, String ip, Integer port) {
        this.name = name;
        this.ip = ip;
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
//
//    public String getUrl() {
//        return url;
//    }
//
//    public void setUrl(String url) {
//        this.url = url;
//    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "DeviceBean{" +
                "name='" + name + '\'' +
//                ", url='" + url + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }
}
