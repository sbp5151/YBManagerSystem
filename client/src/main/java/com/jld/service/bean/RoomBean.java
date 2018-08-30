package com.jld.service.bean;

/**
 * 项目名称：YBManagerSystem
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/7/12 10:38
 */
public class RoomBean {

    private String room_name;//房间名
    private String dev_id;//设备唯一标识
    private String dev_ip;//设备IP
    private String room_state;//房间状态(0未开房，1已开房，2超时)
    private String remaining_time;//剩余时间（如果有）
    private String update_time;//房间更新时间（判断是否离线）
    private String client_name;//客户姓名
    private String manage_password;//管理密码

    public RoomBean() {
    }
    public RoomBean(String room_name, String dev_mac, String dev_ip, String room_state, String remaining_time, String update_time) {
        this.room_name = room_name;
        this.dev_id = dev_mac;
        this.dev_ip = dev_ip;
        this.room_state = room_state;
        this.remaining_time = remaining_time;
        this.update_time = update_time;
    }

    public String getManage_password() {
        return manage_password;
    }

    public void setManage_password(String manage_password) {
        this.manage_password = manage_password;
    }

    public String getClient_name() {
        return client_name;
    }

    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getDev_id() {
        return dev_id;
    }

    public void setDev_id(String dev_id) {
        this.dev_id = dev_id;
    }

    public String getDev_ip() {
        return dev_ip;
    }

    public void setDev_ip(String dev_ip) {
        this.dev_ip = dev_ip;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public String getRoom_state() {
        return room_state;
    }

    public void setRoom_state(String room_state) {
        this.room_state = room_state;
    }

    public String getRemaining_time() {
        return remaining_time;
    }

    public void setRemaining_time(String remaining_time) {
        this.remaining_time = remaining_time;
    }

    @Override
    public String toString() {
        return "RoomBean{" +
                "room_name='" + room_name + '\'' +
                ", dev_id='" + dev_id + '\'' +
                ", dev_ip='" + dev_ip + '\'' +
                ", room_state='" + room_state + '\'' +
                ", remaining_time='" + remaining_time + '\'' +
                ", update_time='" + update_time + '\'' +
                ", client_name='" + client_name + '\'' +
                '}';
    }

}
