package com.example.shi_pc.diffie3;

import android.os.Handler;
import android.os.Message;

import com.example.shi_pc.diffie3.common.Package.BasePackage;
import com.example.shi_pc.diffie3.common.Package.BindRequestPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketThread {
    public SocketServerThread sst;
    public SocketOutputThread sot;

    public SocketThread(Handler h){
        sst=new SocketServerThread(h);
    }
    public SocketThread(String ip,String packet){
        sot=new SocketOutputThread(ip,packet);
    }
    class SocketServerThread extends Thread{
        private Handler mHandler;
        public SocketServerThread(Handler h){
            mHandler=h;
        }
        @Override
        public void run(){
            try {
                System.out.println("server监听线程：run");
                ServerSocket service = new ServerSocket(30000);
                while (true) {
                    System.out.println("server监听线程：正在监听");
                    Socket socket = service.accept();//该方法会阻塞线程
                    System.out.println("server监听线程：接收到一次请求");
                    new SocketInputThread(socket,mHandler).start();
                    //IP获取
                    InetAddress deviceIP=socket.getInetAddress();
                    String dIP=deviceIP.toString();
                    System.out.println("server监听线程：设备IP："+dIP);
                    //把设备IP发送给主线程
                    Message msg =new Message();
                    msg.obj = dIP;
                    msg.what=MsgType.getDevIP;
                    mHandler.sendMessage(msg);
                }
            } catch (Exception e){
                System.out.println(e.getMessage());
            } finally {
                System.out.println("server监听线程：退出");
            }
        }
        //socket接收线程
        class SocketInputThread extends Thread{
            private Socket socket=null;
            private Handler minputHandler;
            public SocketInputThread(Socket skt,Handler h){
                socket=skt;
                minputHandler=h;
            }
            @Override
            public void run(){
                System.out.println("接收线程：run");
                try {
                    //获得读取客户端数据的流
                    DataInputStream input = new DataInputStream(socket.getInputStream());
                    //读取
                    String clientInputStr = input.readUTF();//这里要注意和客户端输出流的写方法对应,否则会抛 EOFException

                    //简单回复
                    //System.out.println("接收线程：客户端发过来的内容:" + clientInputStr);
                    //DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    //out.writeUTF("收到!");

                    Message msg =new Message();//handler的消息

                    byte packetType=clientInputStr.getBytes()[0];
                    System.out.println((int)packetType);
                    switch ((int)packetType-48) {
                        case MsgType.reqBind:
                            BasePackage bp= new BindRequestPacket(clientInputStr);
                            msg.obj = bp;//打包到消息里
                            msg.what=MsgType.reqBind;
                            mHandler.sendMessage(msg);  //发送
                         break;
                    }


                    //out.close();
                    input.close();
                } catch (Exception e) {
                    System.out.println("接收线程：服务器 run 异常: " + e.getMessage());
                } finally {
                    if (socket != null) {
                        try {
                            socket.close();
                        } catch (Exception e) {
                            socket = null;
                            System.out.println("接收线程：服务端 finally 异常:" + e.getMessage());
                        }
                        finally{
                            System.out.println("接收线程：退出");
                        }
                    }
                }
            }
        }
    }

    //socket发送线程
    class SocketOutputThread extends Thread{
        //private 未加密的数据类 info
        private String IP;
        private String packet;
        public SocketOutputThread(String ip,String packet/*未加密的数据类 info*/){
            IP=ip;
            this.packet=packet;
        }
        @Override
        public void run(){
            System.out.println("发送线程：run");
            Socket socket = null;
            try {
                //创建一个流套接字并将其连接到指定主机上的指定端口号
                socket = new Socket(IP, 30000);

                //获得读取服务器端数据的流
                //DataInputStream input = new DataInputStream(socket.getInputStream());
                //获得向服务器端发送数据的流
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                //发送
                System.out.println("发送线程：待发送的数据"+packet);
                out.writeUTF(packet);
                //读取回复，读取到的回复仅作测试用
                //String ret = input.readUTF();
                //System.out.println("发送线程：服务器端返回过来的是: " + ret);

                out.close();
                //input.close();
            } catch (Exception e) {
                System.out.println("发送线程：客户端异常:" + e.getMessage());
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        socket = null;
                        System.out.println("发送线程：客户端 finally 异常:" + e.getMessage());
                    }
                    finally{
                        System.out.println("发送线程：退出");
                    }
                }
            }
        }
    }

}
