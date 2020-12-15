package cn.ieway.evmirror.net;

/**
 * FileName: HttpApi
 * Author: Admin
 * Date: 2020/7/21 14:20
 * Description:
 */
public class ApiHelper {

    //测试环境域名
//    public static String BASE_URL = "https://svip-test.ieway.cn/vip/"; //通用接口访问地址
//    public static String SOCKET_BASE_URL = "https://sksvip-test.ieway.cn/chatApi/";//WEBSOCKET接口访问地址
//    public static String WSS_BASE_URL = "wss://sksvip-test.ieway.cn/chatApiWS/"; //聊天室长连接地址

    public static String BASE_URL = "https://svip.ieway.cn/vip/"; //通用接口访问地址
    public static String SOCKET_BASE_URL = "https://sksvip.ieway.cn/chatApi/";//WEBSOCKET接口访问地址
    public static String WSS_BASE_URL  = "wss://sksvip.ieway.cn/chatApiWS/"; //聊天室长连接地址

    public static String LOGIN_URL = BASE_URL+"login/clientPasswordLogin"; //账号密码登录
    public static String QUERY_DEVICE = BASE_URL+"user/account/queryAccountDeviceById"; //查询设备列表
    public static String GET_COTURN = BASE_URL+"coturn/getCoturnInfo"; //获取coturn信息
    public static String UPDATE_COTURN = BASE_URL+"coturn/updateCoturnInfo"; //更新coturn信息
    public static String GET_PRODUCT_STATE = BASE_URL+"user/account/queryProductStateByType"; //根据商户号与产品类型查询商户产品状态
    public static String UPLOAD_FEEDBACK = BASE_URL+"product/addFeedBack"; //根据商户号与产品类型查询商户产品状态
    public static String CHECK_APP_VERSION= BASE_URL+"product/checkAppVersion"; //版本号检测
    public static String LOFIN_WEIXIN_URL= BASE_URL+"login/clientAppWxLoginByCode"; //微信登录

    ///  https://sksvip_test.ieway.cn/chatApi/r/房间id/login
    public static String JOIN_ROOM = SOCKET_BASE_URL+"r/"; //请求加入房间 1
    public static String JOIN_ROOM_END = "/login"; //请求加入房间 2

    public static String CREATE_ROOM = SOCKET_BASE_URL+"r/create"; //创建房间 1





}
