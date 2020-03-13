package com.protobuf.pojo;

public class ServiceStatus {

    private int id;
    private String name;
    private String ip;
    private String start_time;
    private int online;

    public ServiceStatus(){}

    public ServiceStatus(int id, String name, String ip, String start_time, int online) {
        this.id = id;
        this.name = name;
        this.ip = ip;
        this.start_time = start_time;
        this.online = online;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    @Override
    public String toString() {
        return "ServiceStatus{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", ip='" + ip + '\'' +
                ", start_time='" + start_time + '\'' +
                ", online=" + online +
                '}';
    }
}
