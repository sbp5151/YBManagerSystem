package com.jld.service.bean;

/**
 * 项目名称：YBManagerSystem
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/7/12 19:55
 */
public class OrderBean {

    private String order;//指令
    private String dec_id;//设备唯一标识
    private String continued_time;//开房时间
    private String client_name;//客户名称

    public String getClient_name() {
        return client_name;
    }

    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getDec_id() {
        return dec_id;
    }

    public void setDec_id(String dec_id) {
        this.dec_id = dec_id;
    }

    public String getContinued_time() {
        return continued_time;
    }

    public void setContinued_time(String continued_time) {
        this.continued_time = continued_time;
    }

    @Override
    public String toString() {
        return "OrderBean{" +
                "order='" + order + '\'' +
                ", dec_id='" + dec_id + '\'' +
                ", continued_time='" + continued_time + '\'' +
                ", client_name='" + client_name + '\'' +
                '}';
    }
}
