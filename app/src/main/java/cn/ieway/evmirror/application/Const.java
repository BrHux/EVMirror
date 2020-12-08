package cn.ieway.evmirror.application;

public class Const {
    //第三方平台KEY
//    public static final String WCHAT_APPID = "wx848004b51b020520";
//    public static final String WCHAT_APPSECRET = "aa5db49d8fbbc33eac23bbf74e9ac90f";
//    public static final String WCHAT_UNIVERSAL_LINK = "https://www.ieway.cn/";
    //================================================================================
    //网络访问
    public static final String AES_SIGN_KEY_TEST = "ieway.cn_2020_07"; // AES加密KRY(测试)
    public static final String AES_SIGN_KEY = "ieway.cn_!@#$_20"; // AES加密KRY(正式)
    public static final String MD5_SIGN_KEY = "ieway.cn@20200611"; // MD5KRY (正式环境)
    public static final int PRODUCT_TYPE = 3000; //产品类型 1000 录屏 2000 剪辑 4000 android录屏 5000 视频转换器 6000 录像大师 7000 LiveView 3000 ev远控
    public static final int PLATFORM_TYPE = 2; //设备类型 1 windows 2 android 4 iphone 8 ipad 16 mac 32 pc网校 64 小程序 128 h5网页
    //聊天室
    public static final String MESSAGEVERSION = "0.1.6"; //聊天室信息传输参数--版本号
    public static final String CHAT_APP_ID = "ev_remote_ctrl"; //app标识  (正式版暂定使用"ev_remote_ctrl"，测试版暂定使用"ev_remote_ctrl_test")


    //==============================================================================================
    //SharedPreferences KEY
    public static final String DEVICE_CONNECT_CODE = "device_connect_code"; //设备识别码和验证码列表
    public static final String DEVICE_LIST = "device_list"; //设备列表

    public static final String LOGGED_IN_USER = "logged_in_user"; //登录账户信息
    public static final String IS_FIRST_START = "is_first_start"; //首次登录标记
    public static final String IS_FIRST_CONNECT = "is_first_connect"; //首次进入远程桌面标记
    public static final String IS_AGREE_CLAUSE = "is_agree_clause"; //是否同意服务协议

    //==============================================================================================
    //设置
    public static final String ONLINE_TIPS = "online_tips"; //主机上下线提示开关
    public static final String MOBILE_NETWORK_TIPS = "mobile_network_tips"; //移动网络提示开关
    public static final String ONLY_NET_TIPS = "only_net_tips"; //仅WIFI网络上传/下载文件开关
    public static final String VOICE_TRANSMISSION = "voice_transmission"; //语音传输开关

    //设备列表显示样式
    public static final int LIST_THUMB = 1; //设备列表以缩略图卡片样式展示
    public static final int LIST_NORMAL = 0; //设备列表以精简样式展示
    public static final int LIST_DEFAULT = LIST_THUMB; //设备列表以缩略图卡片样式展示

}
