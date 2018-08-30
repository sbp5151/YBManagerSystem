package com.jld.yb.util;

/**
 * 项目名称：YBManagerSystem
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/7/11 19:42
 */
public class Constant {


    /**
     * shared_key
     */
    public static final String SHARED_KEY = "yb_shared_key";

    /**
     * 本地IP地址
     */
    public static final String SHARED_LOCAL_IP = "local_ip";
    /**
     * 组播地址
     * <p>
     * 224.0.0.0--- 239.255.255.255
     */
    public static final String MULTICAST_IP = "235.2.5.250";

    /**
     * 客户端端口号
     * <p>
     * 0-65535  1024-65535 可用
     */
    public static final int CLIENT_PORT = 25432;

    /**
     * 服务器端口号
     * <p>
     * 0-65535  1024-65535 可用
     */
    public static final int SERVICE_PORT = 25431;

    /**
     * 开房指令
     */
    public static final String ORDER_OPEN_ROOM = "0x81";
    /**
     * 退房指令
     */
    public static final String ORDER_CLOSE_ROOM = "0x82";
    /**
     * 续时指令
     */
    public static final String ORDER_CONTINUED = "0x83";

    /**
     * 房间管理密码
     */
    public static final String ROOM_MANAGE_PASSWORD = "room_password";


    /**
     * 房间状态离线
     */
    public static final String ROOM_STATE_OFF_LINE = "-1";
    /**
     * 房间状态未开房
     */
    public static final String ROOM_STATE_OFF = "0";
    /**
     * 房间状态已开房
     */
    public static final String ROOM_STATE_ON = "1";
    /**
     * 房间状态超时
     */
    public static final String ROOM_STATE_TIME_OUT = "2";
}
