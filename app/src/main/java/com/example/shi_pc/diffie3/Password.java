package com.example.shi_pc.diffie3;

import java.io.Serializable;

public class Password implements Serializable {
    private String value;   //密码值
    private boolean status;   //是否启用密码

    public Password(String value, boolean status){
        this.value=value;
        this.status=status;
    }
    public Password setValue(String value){
        this.value=value;
        return this;
    }
    public Password setStatus(boolean status){
        this.status=status;
        return this;
    }
    public String getValue(){ return value;}
    public boolean getStatus(){ return status;}
}
