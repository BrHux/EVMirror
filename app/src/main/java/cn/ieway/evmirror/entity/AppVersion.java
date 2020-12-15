package cn.ieway.evmirror.entity;

/**
 * FileName: AppVersion
 * Author: Admin
 * Date: 2020/8/17 19:41
 * Description:
 */
public class AppVersion {

    /**
     * id : 4
     * app_name : EVRemote
     * app_version : 1.0.0
     * platform : 2
     * current_version : 1.0.0
     * img_url : null
     * download_url : ieway.cn
     * brief : 测试app
     * configs : {"login_type":1,"chat_host":"https://s ksvip-test.ieway.cn/chatApi/","chat_hostwss":"wss://sksvip-test.ieway.cn/chatApiWS/"}
     * update_brief : 测试更新
     * force_update : 0
     * pay_time : null
     * logic_del : 0
     * end_time : 2020-08-11T10:27:59.000Z
     * start_time : 2020-08-11T10:28:03.000Z
     * end_error : 当前版本已经废弃！
     * create_time : 2020-08-11T10:28:15.000Z
     * update_time : 2020-08-11T10:28:19.000Z
     * remark1 : null
     */

    private int id;
    private String app_name;
    private String app_version;
    private int platform;
    private String current_version;
    private Object img_url;
    private String download_url;
    private String brief;
    private String configs;
    private String update_brief;
    private int force_update;
    private Object pay_time;
    private int logic_del;
    private String end_time;
    private String start_time;
    private String end_error;
    private String create_time;
    private String update_time;
    private Object remark1;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    public String getApp_version() {
        return app_version;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }

    public int getPlatform() {
        return platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }

    public String getCurrent_version() {
        return current_version;
    }

    public void setCurrent_version(String current_version) {
        this.current_version = current_version;
    }

    public Object getImg_url() {
        return img_url;
    }

    public void setImg_url(Object img_url) {
        this.img_url = img_url;
    }

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getConfigs() {
        return configs;
    }

    public void setConfigs(String configs) {
        this.configs = configs;
    }

    public String getUpdate_brief() {
        return update_brief;
    }

    public void setUpdate_brief(String update_brief) {
        this.update_brief = update_brief;
    }

    public int getForce_update() {
        return force_update;
    }

    public void setForce_update(int force_update) {
        this.force_update = force_update;
    }

    public Object getPay_time() {
        return pay_time;
    }

    public void setPay_time(Object pay_time) {
        this.pay_time = pay_time;
    }

    public int getLogic_del() {
        return logic_del;
    }

    public void setLogic_del(int logic_del) {
        this.logic_del = logic_del;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_error() {
        return end_error;
    }

    public void setEnd_error(String end_error) {
        this.end_error = end_error;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public Object getRemark1() {
        return remark1;
    }

    public void setRemark1(Object remark1) {
        this.remark1 = remark1;
    }
}
