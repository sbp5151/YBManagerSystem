package com.jld.yb.bean;

/**
 * 项目名称：YBManagerSystem
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/7/12 10:42
 */
public class ServiceBean {

    private String order;//操作指令（0退房，1开房，2续时）
    private String client_name;//客户名称
    private String time;//续时或者开房时间（如果有）
    private String mac;//客户端Mac地址

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getClient_name() {
        return client_name;
    }

    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    @Override
    public String toString() {
        return "ServiceBean{" +
                "order='" + order + '\'' +
                ", client_name='" + client_name + '\'' +
                ", time='" + time + '\'' +
                ", mac='" + mac + '\'' +
                '}';
    }
}
