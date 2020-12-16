package cn.ieway.evmirror.entity;

/**
 * 设备Bean
 * 只要IP一样，则认为是同一个设备
 */
public class DeviceBean {
    String name;    // 设备名称
    String url;    // 设备所在房间

    public DeviceBean() {
    }

    public DeviceBean(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "DeviceBean{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
