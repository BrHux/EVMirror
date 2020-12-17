package cn.ieway.evmirror.webrtcclient.entity;

public class MessageBean {


    /**
     * data : {"data":[{"devicetype":0,"id":"{adba86a8-2d48-470c-a4db-b5bb99a513cc}","nickname":"Xiaomi - MI 8 Lite"}],"data_type":"allUser"}
     * erro_code : 0
     * error :
     * sender : null
     * target : {adba86a8-2d48-470c-a4db-b5bb99a513cc}
     * type : data
     */

    private DataBeanX data;
    private int erro_code;
    private String error;
    private Object sender;
    private String target;
    private String type;

    public DataBeanX getData() {
        return data;
    }

    public void setData(DataBeanX data) {
        this.data = data;
    }

    public int getErro_code() {
        return erro_code;
    }

    public void setErro_code(int erro_code) {
        this.erro_code = erro_code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Object getSender() {
        return sender;
    }

    public void setSender(Object sender) {
        this.sender = sender;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static class DataBeanX {
        /**
         * data : [{"devicetype":0,"id":"{adba86a8-2d48-470c-a4db-b5bb99a513cc}","nickname":"Xiaomi - MI 8 Lite"}]
         * data_type : allUser
         */

        private String data_type;

        private DataBean data;

        private String request_data;

        public String getRequest_data() {
            return request_data;
        }

        public void setRequest_data(String request_data) {
            this.request_data = request_data;
        }

        public String getData_type() {
            return data_type;
        }

        public void setData_type(String data_type) {
            this.data_type = data_type;
        }

        public DataBean getData() {
            return data;
        }

        public void setData(DataBean data) {
            this.data = data;
        }
    }



    public static class DataBean {
        /**
         * devicetype : 0
         * id : {adba86a8-2d48-470c-a4db-b5bb99a513cc}
         * nickname : Xiaomi - MI 8 Lite
         */
        private String modify_type;
        private String data;
        private int devicetype;
        private String data_type;

        public String getData_type() {
            return data_type;
        }

        public void setData_type(String data_type) {
            this.data_type = data_type;
        }


        public int getDevicetype() {
            return devicetype;
        }

        public void setDevicetype(int devicetype) {
            this.devicetype = devicetype;
        }

        public String getModify_type() {
            return modify_type;
        }

        public void setModify_type(String modify_type) {
            this.modify_type = modify_type;
        }

        public String getData() {
            return data;
        }

        public void setData(String name) {
            this.data = name;
        }

    }
}
