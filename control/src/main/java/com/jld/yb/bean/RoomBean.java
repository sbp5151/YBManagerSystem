package com.jld.yb.bean;

import com.jld.yb.util.Constant;
import com.jld.yb.util.LogUtil;

import java.util.TimerTask;

/**
 * 项目名称：YBManagerSystem
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/7/12 14:42
 */
public class RoomBean {

    private String room_name;//房间名称
    private String client_name;//客户名称
    private String remaining_time;//剩余时间
    private String dev_ip;//dev_ip
    private String room_state;//状态(-1离线，0未开房，1已开房，2超时)
    private String last_room_state = Constant.ROOM_STATE_OFF;//上个状态
    private String dev_id;//设备唯一标识
    private String update_time;//房间更新时间（判断是否离线）
    private String manage_password = "";//管理密码

    public RoomBean(String room_name, String client_name, String remaining_time, String dev_ip, String room_state, String dev_id, String update_time, String manage_password) {
        this.room_name = room_name;
        this.client_name = client_name;
        this.remaining_time = remaining_time;
        this.dev_ip = dev_ip;
        this.room_state = room_state;
        this.dev_id = dev_id;
        this.update_time = update_time;
        this.manage_password = manage_password;
    }

    public String getManage_password() {
        return manage_password;
    }

    public void setManage_password(String manage_password) {
        this.manage_password = manage_password;
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

    TimerTask task = new TimerTask() {
        public void run() {
            LogUtil.d("TimerTask:" + update_time + "---ip:" + dev_ip);
            if (room_state != null)
                room_state = "-1";
        }
    };

    public RoomBean() {
//        Timer timer = new Timer();
//        timer.schedule(task, 1000 * 15);
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public String getClient_name() {
        return client_name;
    }

    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    public String getRemaining_time() {
        return remaining_time;
    }

    public void setRemaining_time(String remaining_time) {
        this.remaining_time = remaining_time;
    }

    public String getDev_ip() {
        return dev_ip;
    }

    public void setDev_ip(String dev_ip) {
        this.dev_ip = dev_ip;
    }

    public String getRoom_state() {
        return room_state;
    }

    public void setRoom_state(String room_state) {
        this.room_state = room_state;
    }

    public String getLast_room_state() {
        return last_room_state;
    }

    public void setLast_room_state(String last_room_state) {
        this.last_room_state = last_room_state;
    }

    @Override
    public String toString() {
        return "RoomBean{" +
                "room_name='" + room_name + '\'' +
                ", client_name='" + client_name + '\'' +
                ", remaining_time='" + remaining_time + '\'' +
                ", dev_ip='" + dev_ip + '\'' +
                ", room_state='" + room_state + '\'' +
                ", dev_id='" + dev_id + '\'' +
                ", update_time='" + update_time + '\'' +
                ", manage_password='" + manage_password + '\'' +
                ", last_room_state='" + last_room_state + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        RoomBean room = (RoomBean) obj;
        return dev_id.equals(room.dev_id);
    }
}
