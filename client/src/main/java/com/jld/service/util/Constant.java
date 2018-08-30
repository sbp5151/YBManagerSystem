package com.jld.service.util;

/**
 * 项目名称：YBManagerSystem
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/7/11 19:42
 */
public class Constant {


    public static final String SHARED_KEY = "yb_service_shared_key";
    /**
     * 组播地址
     * <p>
     * 224.0.0.0--- 239.255.255.255
     */
    public static final String MULTICAST_IP = "235.2.5.250";

    /**
     * 服务器端口号
     * <p>
     * 0-65535  1024-65535 可用
     */
    public static final int SERVICE_PORT = 25431;

    /**
     * 客户端接收组播端口号
     * <p>
     * 0-65535  1024-65535 可用
     */
    public static final int CLIENT_PORT = 25432;


    /**
     * 设备ID
     */
    public static final String SHARED_DEVICE_ID = "device_id";

    /**
     * 房间名
     */
    public static final String SHARED_ROOM_NAME = "room_name";
    /**
     * 管理密码
     */
    public static final String SHARED_MANAGE_PASSWORD = "manage_password";
    /**
     * 超级管理密码
     */
    public static final String SUPER_MANAGE_PASSWORD = "TencentPassword";

    /**
     * 开房指令
     */
    public static final String ORDER_OPEN_ROOM = "0x81";

    /**
     * 开房成功反馈指令
     */
    public static final String ORDER_OPEN_ROOM_BACK = "0x81_back";
    /**
     * 退房指令
     */
    public static final String ORDER_CLOSE_ROOM = "0x82";
    /**
     * 退房成功反馈指令
     */
    public static final String ORDER_CLOSE_ROOM_BACK = "0x82_back";
    /**
     * 续时指令
     */
    public static final String ORDER_CONTINUED = "0x83";
    /**
     * 续时成功反馈指令
     */
    public static final String ORDER_CONTINUED_BACK = "0x83_back";
    /**
     * 打开遮挡dialog广播
     */
    public static final String ACTION_OPEN_KEEPOUT_DIALOG = "action_open_keep_out";
    /**
     * 关闭遮挡dialog广播
     */
    public static final String ACTION_CLOSE_KEEPOUT_DIALOG = "action_close_keep_out";

}
