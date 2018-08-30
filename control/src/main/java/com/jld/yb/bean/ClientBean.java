package com.jld.yb.bean;

/**
 * 项目名称：YBManagerSystem
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/7/12 10:38
 */
public class ClientBean {

    private String dev_mac;//设备Mac地址
    private String dev_ip;//设备IP
    private String room_name;//房间名
    private String room_state;//房间状态(0未开房，1已开房，2超时)
    private String remaining_time;//剩余时间（如果有）

    public String getDev_mac() {
        return dev_mac;
    }

    public void setDev_mac(String dev_mac) {
        this.dev_mac = dev_mac;
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
        return "ClientBean{" +
                "dev_mac='" + dev_mac + '\'' +
                ", dev_ip='" + dev_ip + '\'' +
                ", room_name='" + room_name + '\'' +
                ", room_state='" + room_state + '\'' +
                ", remaining_time='" + remaining_time + '\'' +
                '}';
    }
}
