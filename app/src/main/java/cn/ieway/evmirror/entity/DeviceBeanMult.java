package cn.ieway.evmirror.entity;

import java.util.Arrays;
import java.util.List;

/**
 * 设备Bean
 * 只要IP一样，则认为是同一个设备
 */
public class DeviceBeanMult {
    String name;    // 设备名称
    List<String> url;    // 设备所在房间

    public DeviceBeanMult() {
    }

    public DeviceBeanMult(String name, List<String> url) {
        this.name = name;
        this.url = url;
    }

    public List<String> getUrl() {
        return url;
    }

    public void setUrl(List<String> url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "DeviceBeanMult{" +
                "name='" + name + '\'' +
                ", url=" + url +
                '}';
    }
}
