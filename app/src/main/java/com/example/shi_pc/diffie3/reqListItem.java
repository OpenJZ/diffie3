package com.example.shi_pc.diffie3;

import java.io.Serializable;


public class reqListItem implements Serializable
{
    private String deviceName;
    private String requestType;
    public String getDeviceName() {
        return deviceName;
    }
    public void setDeviceName(String dn) {
        deviceName = dn;
    }
    public String getRequestType() {
        return requestType;
    }
    public void setRequestType(String rt) {
        requestType = rt;
    }

    //构造方法
    public reqListItem(String dn, String rt) {
        super();
        deviceName = dn;
        requestType = rt;
    }
    public reqListItem() {
        super();
    }

}

