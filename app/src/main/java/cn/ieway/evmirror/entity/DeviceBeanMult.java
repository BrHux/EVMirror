package cn.ieway.evmirror.entity;

import java.util.List;

/**
 * 设备Bean
 * 只要IP一样，则认为是同一个设备
 */
public class DeviceBeanMult {
    /**
     * ip : ["192.168.1.128"]
     * port : 10020
     * serverName : EVMirror[123456]
     */

    private Integer port;
    private String serverName;
    private List<String> ip;


    public DeviceBeanMult() {
    }

    public DeviceBeanMult(Integer port, String serverName, List<String> ip) {
        this.port = port;
        this.serverName = serverName;
        this.ip = ip;
    }


    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public List<String> getIp() {
        return ip;
    }

    public void setIp(List<String> ip) {
        this.ip = ip;
    }


    @Override
    public String toString() {
        return "DeviceBeanMult{" +
                "port=" + port +
                ", serverName='" + serverName + '\'' +
                ", ip=" + ip +
                '}';
    }
}
