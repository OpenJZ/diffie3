package com.example.shi_pc.diffie3;


public class Archive {
    //用于存储的信息
    private static byte[] gHashMac;//设备ID,暂定一字节
    private static String publicSecret="";  //公共秘密
    private static byte[] masterKey;   //masterkey
    private static byte[] sessionkey;   //sessionkey
    private static byte[] randomnumber; //randomnumber

    //文件，filekey
    //TODO


    //暂存
    private byte[] randomNumber=new byte[1]; //randomnumber
    private byte[] sessionKey=new byte[1];  //sessionKey
}
