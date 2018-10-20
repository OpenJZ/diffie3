package com.example.shi_pc.diffie3;

public class RcvDataPack {
    private String msg="";//整个报文
    private int msgType=0;//命令类型
    private byte[] deviceID=new byte[1];//设备ID

    public RcvDataPack setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public RcvDataPack setMsgType(int msgType) {
        this.msgType = msgType;
        return this;
    }

    public RcvDataPack setDeviceID(byte[] deviceID) {
        return this;
    }

    public int getMsgType() {
        return msgType;
    }

    public String getMsg() {
        return msg;
    }

    public byte[] getDeviceID() {
        return deviceID;
    }
}
