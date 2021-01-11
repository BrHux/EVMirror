package cn.ieway.evmirror.entity;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

import cn.ieway.evmirror.util.DataTool;

public
/**
 * FileName: ControlMessageEntity
 * Author: Admin
 * Date: 2021/1/8 11:50
 * Description: 
 */
class ControlMessageEntity implements Serializable {

    /**
     * data : {"key":"qwertyuiop","port":10001}
     * type : 0
     * version : 1.0
     */

    private DataBean data;
    private Integer type;
    private String version;


    public ControlMessageEntity() {
    }

    /**
     * data : {"key":"qwertyuiop","port":10001}
     * type : 0
     * version : 1.0
     */
    public ControlMessageEntity(Integer type, String version) {
        this.type = type;
        this.version = version;
    }



    public ControlMessageEntity(String version, Integer type) {
        this.version = version;
        this.type = type;
    }
    public ControlMessageEntity(String version, Integer type, DataBean data) {
        this.version = version;
        this.type = type;
        this.data = data;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ControlMessageEntity{" +
                "data=" + data +
                ", type=" + type +
                ", version='" + version + '\'' +
                '}';
    }

    /**
     * 打包命令协议 (4个字节头 + json字符串)
     * 4个字节保存一个int值，该值为后面的json字符串长度
     *
     * @return
     * @param control
     */
    public byte[] getSendMsg(ControlMessageEntity control) {
        return getSendMsg(toJsonString(control));
    }

    /**
     * 打包命令协议 (4个字节头 + json字符串)
     * 4个字节保存一个int值，该值为后面的json字符串长度
     *
     * @param jsonStr
     * @return
     */
    public byte[] getSendMsg(String jsonStr) {
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append(jsonStr.length());
//        stringBuilder.append(jsonStr);

        byte[] json = jsonStr.getBytes();
        byte[] len = DataTool.int2Byte(json.length);
        byte[] msg = new byte[len.length+json.length];
        System.arraycopy(len,0,msg,0,len.length);
        System.arraycopy(json,0,msg,len.length,json.length);


        return msg;
    }

    /**
     * 对象转json
     *
     * @return
     * @param control
     */
    public String toJsonString(ControlMessageEntity control) {
        return JSON.toJSONString(control);
    }

    /**
     * json转对象
     *
     * @param json
     * @return
     */
    public ControlMessageEntity toObject(String json) {
        return (ControlMessageEntity) toObject(json, this.getClass());
    }

    /**
     * json转对象
     *
     * @param json
     * @param o
     * @return
     */
    public Object toObject(String json, Object o) {
        return JSON.parseObject(json, o.getClass());
    }

    public static class DataBean {
        /**
         * key : qwertyuiop
         * port : 10001
         */

        private String key;
        private Integer port;
        private String serial;
        private String name;
        private String bite_rate;

        public DataBean() {
        }

        public DataBean(String key, Integer port) {
            this.key = key;
            this.port = port;
        }

        public DataBean(String serial, String name) {
            this.serial = serial;
            this.name = name;
        }

        public String getSerial() {
            return serial;
        }

        public void setSerial(String serial) {
            this.serial = serial;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBite_rate() {
            return bite_rate;
        }

        public void setBite_rate(String bite_rate) {
            this.bite_rate = bite_rate;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }
    }
}


