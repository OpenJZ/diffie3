package com.example.shi_pc.diffie3;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shi_pc.diffie3.common.Package.BasePackage;
import com.example.shi_pc.diffie3.common.Package.BindRequestPacket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ListView req_dev_List;
    //private List<reqListItem> TestData; //测试数据
    private List<Map<String,String>> reqListData=new ArrayList<Map<String,String>>();
    private ItemAdapter listAdapter=null;//列表适配器
    private AlertDialog alertDialog;//输入公共秘密的dialog
    private AlertDialog passwordDialog;//输入口令的dialog
    private int itemIndex=0;
    private String DeviceIp;
    private int currentLayout=R.layout.request_item;//当前layout
    private String str_secret="";//从公共秘密 密码输入框获得的字符串
    private String str_password="";//从口令 密码输入框获得的字符串
    private String tempData;//测试：从客户端接收数据
    private Password curPassword=new Password("000000",false); //默认口令
    SocketThread s_Server;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_requests:
                    //setTitle(R.string.title_requests);
                    //配置适配器
                    listAdapter.reviseAdapter(reqListData,R.layout.request_item);
                    req_dev_List.setAdapter(listAdapter);
                    currentLayout=R.layout.request_item;

                    return true;
                case R.id.navigation_devices:
                    //setTitle(R.string.title_devices);
                    //测试数据
                    List<Map<String,String>> testData2=new ArrayList<Map<String, String>>();
                    Map<String,String> testMap=new HashMap<String,String>();
                    testMap.put(getString(R.string.devItem_map_key1),"设备1");
                    testMap.put(getString(R.string.devItem_map_key2),"MAC地址");
                    testMap.put(getString(R.string.devItem_map_key3),"状态");
                    testData2.add(testMap);
                    Map<String,String> testMap2=new HashMap<String,String>();
                    testMap2.put(getString(R.string.devItem_map_key1),"设备2");
                    testMap2.put(getString(R.string.devItem_map_key2),"MAC地址");
                    testMap2.put(getString(R.string.devItem_map_key3),"状态");
                    testData2.add(testMap2);
                    //适配器切换
                    listAdapter.reviseAdapter(testData2,R.layout.device_item);
                    req_dev_List.setAdapter(listAdapter);
                    currentLayout=R.layout.device_item;
                    return true;
            }
            return false;
        }
    };
    //请求列表项删除按钮监听器
    private ItemAdapter.onItemDeleteListener mOnItemDeleteListener=new ItemAdapter.onItemDeleteListener()
    {
        @Override
        public void onDeleteClick(int i) {
            reqListData.remove(i);
            //原始数据也要删除
            //TestData.remove(i);
            listAdapter.notifyDataSetChanged();//适配器刷新
        }
    };

    //请求列表项同意按钮监听器
    private ItemAdapter.onItemAcceptListener mOnItemAcceptListener=new ItemAdapter.onItemAcceptListener() {
        @Override
        public void onAcceptClick(int i) {
            itemIndex=i;
            //如果是绑定请求，弹出对话框输入公共秘密
            if(reqListData.get(i).get(getString(R.string.reqItem_map_key2)).equals(getString(R.string.rt_bind))) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_public_secret, null);
                EditText et=view.findViewById(R.id.editText_secret);
                //密码编辑框监听
                et.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) { }
                    @Override
                    public void afterTextChanged(Editable s) {
                        str_secret=s.toString();
                        System.out.println(str_secret);
                    }
                });
                //确认按钮监听
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(MainActivity.this,str,Toast.LENGTH_LONG);
                        System.out.println(str_secret);
                        //TODO 如果公共秘密正确
                        BasePackage bp=GlobalData.reqPackList.get(itemIndex);
                        if(bp.isValid()) {//如果报文有效
                            alertDialog.dismiss();
                            //更新列表
                            reqListData.remove(itemIndex);
                            String str=((BindRequestPacket)bp.process()).getMSG();
                            //System.out.println("待发送报文："+str);
                            SocketThread s_Output = new SocketThread(DeviceIp,str);
                            s_Output.sot.start();
                            GlobalData.reqPackList.remove(itemIndex);

                            listAdapter.notifyDataSetChanged();//适配器刷新
                        }
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //取消按钮应该什么都不做
                        /*alertDialog.dismiss();
                        //更新列表
                        reqListData.remove(itemIndex);
                        //原始数据也要删除
                        TestData.remove(itemIndex);
                        listAdapter.notifyDataSetChanged();//适配器刷新*/
                    }
                });
                builder.setTitle("请输入公共秘密");
                builder.setView(view);
                alertDialog = builder.create();
                alertDialog.show();
            }
            else {

                reqListData.remove(itemIndex);
                //原始数据也要删除
                //TestData.remove(itemIndex);
                listAdapter.notifyDataSetChanged();//适配器刷新
            }

        }
    };
    //ListView 列表项的 Listener
    AdapterView.OnItemClickListener mOnItemClickListener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(currentLayout==R.layout.device_item){
                //测试
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_show_file, null);
                TextView tv_file=dialogView.findViewById(R.id.textView_file);
                tv_file.setText("file1\nfile2\nfile3");
                builder.setPositiveButton("好", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //alertDialog.dismiss();
                    }
                });
                builder.setTitle("已保护文件");
                builder.setView(dialogView);
                alertDialog = builder.create();
                alertDialog.show();
            }
        }
    };
    //初始化请求列表项的数据，参数需要从后台读入
    private List<Map<String,String>> InitReqListData(List<reqListItem> li){
        List<Map<String,String>> mapList=new ArrayList<Map<String,String>>();
        for(int i=0;i<li.size()-1;++i) {
            Map<String,String> map = new HashMap<String, String>();
            map.put(getString(R.string.reqItem_map_key1),li.get(i).getDeviceName());
            map.put(getString(R.string.reqItem_map_key2),li.get(i).getRequestType());
            map.put(getString(R.string.reqItem_map_key3),"docHash");
            mapList.add(map);
            //Log.v("遍历mapList","["+i+"]");//log.v
            /*for (String key : map.keySet()) {   //log.v
                Log.v("key",key );
                Log.v("value",map.get(key)+"");
            }*/
        }
        Map<String,String> map = new HashMap<String, String>();
        map.put(getString(R.string.reqItem_map_key1),li.get(2).getDeviceName());
        map.put(getString(R.string.reqItem_map_key2),li.get(2).getRequestType());
        map.put(getString(R.string.reqItem_map_key3),"");
        mapList.add(map);
        return mapList;
    }

    //子线程消息处理
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MsgType.getDevIP: {     //获取设备IP
                    //去掉获得的IP地址前面的斜杠
                    DeviceIp = ((String) msg.obj).substring(1, ((String) msg.obj).length());
                    System.out.println("UI线程：设备IP:" + DeviceIp);
                    //测试发送线程
                    //String str="msg";
                    //SocketThread s_Output = new SocketThread(DeviceIp,str);
                    //s_Output.sot.start();
                    break;
                }
                case MsgType.reqBind: { //请求绑定
                    BasePackage bp=(BasePackage) msg.obj;//从消息中拿出数据包
                    if(bp.isUseful()) {
                        Map<String, String> newMap = new HashMap<String, String>();
                        newMap.put(getString(R.string.reqItem_map_key1), "设备：" + bp.getgHashMac());
                        newMap.put(getString(R.string.reqItem_map_key2), getString(R.string.rt_bind));
                        newMap.put(getString(R.string.reqItem_map_key3), "");
                        reqListData.add(newMap);//显示
                        GlobalData.reqPackList.add(bp);//暂存报文
                        if (currentLayout == R.layout.request_item)
                            listAdapter.notifyDataSetChanged();//适配器刷新
                        break;
                    }
                }
                default:
                    break;
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //检查有无文件目录
        /*String dataFolder=getString(R.string.app_name);
        String dataDir=Environment.getExternalStorageDirectory().getPath()+File.separator+dataFolder+File.separator;
        System.out.println(dataDir);
        File dir=new File(dataDir);//新建一级主目录
        if(!dir.exists())   //判断文件夹目录是否存在
            dir.mkdir();    //如果不存在则创建*/

        //从文件中读入password
        curPassword=getPasswordFromFile();
        //让用户输入口令

        if(curPassword.getStatus()){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_public_secret, null);
            EditText et=view.findViewById(R.id.editText_secret);
            //密码编辑框监听
            et.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    str_password=s.toString();
                    System.out.println(str_password);
                }
            });

            builder.setPositiveButton("确认", null);//传入null参数，重写listener
            /*builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //关闭activity
                    mthis.finish();
                }
            });*/
            builder.setTitle("请输入口令");
            builder.setView(view);
            passwordDialog = builder.create();
            passwordDialog.setCancelable(false);
            passwordDialog.show();
            //确认按钮监听
            passwordDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(str_password.equals(curPassword.getValue()))
                                passwordDialog.dismiss();
                            else
                                Toast.makeText(MainActivity.this,"口令错误，请重新输入",Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        //<测试数据
        //TestData =new ArrayList<reqListItem>();
        //TestData.add(new reqListItem("设备1",getString(R.string.rt_bind)));
        //TestData.add(new reqListItem("设备2",getString(R.string.rt_authorize)));
        //TestData.add(new reqListItem("设备3",getString(R.string.rt_bind)));
        //测试/>

        //查找到req_dev_List
        req_dev_List=(ListView)findViewById(R.id.list);
        //ListView SDK自带适配器
        /*SimpleAdapter listAdapter=new SimpleAdapter(this,InitReqListData(ListData),R.layout.request_item,
                new String[]{"dn","rt"},new int[]{R.id.device_name,R.id.request_type});*/
        //初始化数据
        //reqListData=InitReqListData(TestData);
        //自定义适配器
        listAdapter=new ItemAdapter(this,reqListData,R.layout.request_item);
        //配置适配器
        req_dev_List.setAdapter(listAdapter);
        //ListView item 中的删除按钮的listener
        listAdapter.setOnItemDeleteClickListener(mOnItemDeleteListener);
        //ListView item 中的同意按钮的listener
        listAdapter.setOnItemAcceptClickListener(mOnItemAcceptListener);
        //ListView item的 Listener
        req_dev_List.setOnItemClickListener(mOnItemClickListener);

        //底部导航视图加载及监听
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        //测试socket
        System.out.println("MainActivity_onCreate：测试开始！！！！！！");
        //测试socket
        //启动监听线程
        s_Server=new SocketThread(mHandler);
        s_Server.sst.start();
    }
    //退出时不要存储信息，直接从后台杀死进程有时不会正确调用生命周期
    @Override
    protected void onDestroy(){
        System.out.println("MainActivity:onDestroy");
        super.onDestroy();

    }
    @Override
    protected void onPause(){
        super.onPause();
        System.out.println("MainActivity:onPause");
    }
    @Override
    protected void onStop(){
        super.onStop();
        System.out.println("MainActivity:onStop");
    }
    //actionBar 菜单按钮
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_menu, menu);
        return true;
    }
    //actionBar 菜单按钮选项点击事件监听
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.set_Password:
                Intent intent=new Intent();

                intent.putExtra("password",curPassword);
                intent.setClass(MainActivity.this,PasswordActivity.class);
                startActivityForResult(intent,0);
                return true;
            case R.id.set_showMAC:
                String macAddress=MyMacAddress.getMac(MainActivity.this);
                if(macAddress!=null)
                    Toast.makeText(MainActivity.this,macAddress,Toast.LENGTH_LONG).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //处理其他activity返回的数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("OnActivityResult方法已调用");
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0&&resultCode==0) {
            curPassword = (Password) data.getSerializableExtra("password");
            //Toast.makeText(MainActivity.this,"密码:"+curPassword.getValue()+"\n密码状态："+curPassword.getStatus(),Toast.LENGTH_LONG).show();
            writePasswordToFile(curPassword);
        }
    }

    private Password getPasswordFromFile(){
        System.out.println("系统编码："+System.getProperty("file.encoding"));
        Password p=new Password("000000",false);

        try {
            //String dataFolder=getString(R.string.app_name);
            //String dataDir=Environment.getExternalStorageDirectory().getPath()+File.separator+dataFolder+File.separator;
            //File file_password=new File(dataDir+"password.txt");
            FileInputStream fis=openFileInput("password.txt");
            //FileInputStream fis=new FileInputStream(file_password);
            InputStreamReader isr=new InputStreamReader(fis/*,System.getProperty("file.encoding")*/);
            BufferedReader br=new BufferedReader(isr);

            String p_value=br.readLine();   //读口令
            String p_status=br.readLine();  //读状态
            p.setValue(p_value).setStatus(Boolean.parseBoolean(p_status));

            br.close();
            System.out.println("从文件中读取密码："+p.getValue()+" 状态："+p.getStatus());
        }catch(Exception e){//如果发生了各种异常，就返回默认密码
            System.out.println(e.getMessage());
            return new Password("000000",false);
        }
        return p;
    }
    private void writePasswordToFile(Password p){
        try {
            //String dataFolder=getString(R.string.app_name);
            //String dataDir=Environment.getExternalStorageDirectory().getPath()+File.separator+dataFolder+File.separator;
            //File file_password=new File(dataDir+"password.txt");
            FileOutputStream fos=openFileOutput("password.txt",Context.MODE_PRIVATE);
            //FileOutputStream fos=new FileOutputStream(file_password);
            OutputStreamWriter osw=new OutputStreamWriter(fos/*,System.getProperty("file.encoding")*/);
            BufferedWriter bw=new BufferedWriter(osw);

            bw.write(p.getValue());bw.newLine();
            bw.write(String.valueOf(p.getStatus()));bw.newLine();

            bw.close();
            System.out.println("向文件写入密码："+p.getValue()+" 状态："+p.getStatus());
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    //禁用系统返回键
    @Override
    public void onBackPressed() {
        //什么事都不做
    }

}
