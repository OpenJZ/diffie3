package com.example.shi_pc.diffie3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PasswordActivity extends AppCompatActivity {

    TextView tv_curP,tv_newP,tv_reP;
    EditText et_curP,et_newP,et_reP;
    Button btn_save,btn_cancel,btn_enrol,btn_delete,btn_update;
    String newPassword="";
    String repPassword="";
    Password intent_Password;

    //按钮Listener
    private Button.OnClickListener mOnClicklistener=new Button.OnClickListener(){
        @Override
        public void onClick(View v){
            switch(v.getId()){
                case R.id.button_enrolPassword: //创建密码
                    btn_enrol.setEnabled(false); //创建新口令 不可用
                    //输入当前口令 不可见
                    tv_curP.setVisibility(View.INVISIBLE);
                    et_curP.setVisibility(View.INVISIBLE);

                    btn_delete.setEnabled(false);btn_update.setEnabled(false);
                    //以下可用
                    tv_newP.setEnabled(true);et_newP.setEnabled(true);et_newP.setText("");
                    tv_reP.setEnabled(true);et_reP.setEnabled(true);et_reP.setText("");
                    btn_save.setEnabled(true);btn_cancel.setEnabled(true);
                    return;
                case R.id.button_deletePassword:    //关闭口令
                    intent_Password.setStatus(false).setValue("000000");//更新数据！！！
                    btn_enrol.setEnabled(true); //创建新口令 可用
                    btn_enrol.setVisibility(View.VISIBLE);//创建新口令 可见
                    //输入当前口令 不可见
                    tv_curP.setVisibility(View.INVISIBLE);
                    et_curP.setVisibility(View.INVISIBLE);
                    //以下均不可用
                    btn_delete.setEnabled(false);btn_update.setEnabled(false);
                    tv_newP.setEnabled(false);et_newP.setEnabled(false);
                    tv_reP.setEnabled(false);et_reP.setEnabled(false);
                    btn_save.setEnabled(false);btn_cancel.setEnabled(false);
                    return;
                case R.id.button_updatePassword:    //修改口令
                    btn_enrol.setVisibility(View.INVISIBLE);

                    tv_curP.setEnabled(false);
                    et_curP.setEnabled(false);

                    btn_delete.setEnabled(false);btn_update.setEnabled(false);

                    tv_newP.setEnabled(true);et_newP.setEnabled(true);et_newP.setText("");
                    tv_reP.setEnabled(true);et_reP.setEnabled(true);et_reP.setText("");
                    btn_save.setEnabled(true);btn_cancel.setEnabled(true);
                    return;
                case R.id.button_savePassword:  //保存更改
                    if(!newPassword.equals(repPassword)) {    //输入不相等
                        Toast.makeText(PasswordActivity.this, "两次输入不一致,请重新输入", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(newPassword.equals("")&&repPassword.equals("")){ //没有输入任何密码,直接退出
                        return;
                    }
                    intent_Password.setStatus(true).setValue(newPassword);  //更新数据！！！
                    btn_enrol.setVisibility(View.INVISIBLE);

                    tv_curP.setEnabled(true);
                    tv_curP.setVisibility(View.VISIBLE);
                    et_curP.setEnabled(true);
                    et_curP.setVisibility(View.VISIBLE);et_curP.setText("");

                    btn_delete.setEnabled(false);btn_update.setEnabled(false);
                    tv_newP.setEnabled(false);et_newP.setEnabled(false);
                    tv_reP.setEnabled(false);et_reP.setEnabled(false);
                    btn_save.setEnabled(false);btn_cancel.setEnabled(false);
                    return;
                case R.id.button_noSavePassword:    //取消更新密码
                    if(intent_Password.getStatus()==true) {  //如果当前有密码
                        btn_enrol.setVisibility(View.INVISIBLE);

                        tv_curP.setEnabled(true);
                        tv_curP.setVisibility(View.VISIBLE);
                        et_curP.setEnabled(true);
                        et_curP.setVisibility(View.VISIBLE);et_curP.setText("");

                        btn_delete.setEnabled(false);btn_update.setEnabled(false);
                        tv_newP.setEnabled(false);et_newP.setEnabled(false);
                        tv_reP.setEnabled(false);et_reP.setEnabled(false);
                        btn_save.setEnabled(false);btn_cancel.setEnabled(false);
                        return;
                    }
                    else {    //如果当前是默认密码
                        btn_enrol.setVisibility(View.VISIBLE);
                        btn_enrol.setEnabled(true);

                        tv_curP.setVisibility(View.INVISIBLE);
                        et_curP.setVisibility(View.INVISIBLE);

                        btn_delete.setEnabled(false);btn_update.setEnabled(false);
                        tv_newP.setEnabled(false);et_newP.setEnabled(false);
                        tv_reP.setEnabled(false);et_reP.setEnabled(false);
                        btn_save.setEnabled(false);btn_cancel.setEnabled(false);
                        return;
                    }
            }
        }
    };
    //cur编辑框监听
    private TextWatcher mEditCurPListener=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }
        @Override
        public void afterTextChanged(Editable s) {
            String str=s.toString();
            if(str.equals(intent_Password.getValue())){
                btn_enrol.setVisibility(View.INVISIBLE);; //创建新口令 不可见
                //输入当前口令 不可用
                tv_curP.setEnabled(false);
                et_curP.setEnabled(false);
                //以下均不可用
                btn_delete.setEnabled(true);btn_update.setEnabled(true);
                tv_newP.setEnabled(false);et_newP.setEnabled(false);
                tv_reP.setEnabled(false);et_reP.setEnabled(false);
                btn_save.setEnabled(false);btn_cancel.setEnabled(false);
            }
        }
    };
    //new编辑框监听
    private TextWatcher mEditNewPListener=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }
        @Override
        public void afterTextChanged(Editable s) {
            newPassword=s.toString();
        }
    };
    //rep编辑框监听
    private TextWatcher mEditRepPListener=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }
        @Override
        public void afterTextChanged(Editable s) {
            repPassword=s.toString();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        Intent i = getIntent();
        intent_Password = (Password)i.getSerializableExtra("password");
        //测试
        //intent_Password.setValue("111111").setStatus(true);

        tv_curP=(TextView)findViewById(R.id.textView_curPassword);//文本框：请输入当前口令
        tv_newP=(TextView)findViewById(R.id.textView_newPassword);//文本框：请输入新口令
        tv_reP=(TextView)findViewById(R.id.textView_repeatPassword);//文本框：请重复新口令
        et_curP=(EditText)findViewById(R.id.editText_curPassword);//编辑框：请输入当前口令
        et_newP=(EditText)findViewById(R.id.editText_newPassword);//编辑框：请输入新口令
        et_reP=(EditText)findViewById(R.id.editText_repeatPassword);//编辑框：请重复新口令
        btn_save=(Button)findViewById(R.id.button_savePassword);//按钮：保存
        btn_cancel=(Button)findViewById(R.id.button_noSavePassword);//按钮：取消
        btn_enrol=(Button)findViewById(R.id.button_enrolPassword);//按钮：创建新口令
        btn_delete=(Button)findViewById(R.id.button_deletePassword);//按钮：删除口令
        btn_update=(Button)findViewById(R.id.button_updatePassword);//按钮：修改口令

        //当前没有口令
        if(intent_Password.getStatus()==false) {
            btn_enrol.setEnabled(true); //创建新口令 可用
            //输入当前口令 不可见
            tv_curP.setVisibility(View.INVISIBLE);
            et_curP.setVisibility(View.INVISIBLE);
            //以下均不可用
            btn_delete.setEnabled(false);btn_update.setEnabled(false);
            tv_newP.setEnabled(false);et_newP.setEnabled(false);
            tv_reP.setEnabled(false);et_reP.setEnabled(false);
            btn_save.setEnabled(false);btn_cancel.setEnabled(false);
        }
        //当前有口令
        else{
            btn_enrol.setVisibility(View.INVISIBLE);; //创建新口令 不可见
            //输入当前口令 可用
            tv_curP.setEnabled(true);
            et_curP.setEnabled(true);
            //以下均不可用
            btn_delete.setEnabled(false);btn_update.setEnabled(false);
            tv_newP.setEnabled(false);et_newP.setEnabled(false);
            tv_reP.setEnabled(false);et_reP.setEnabled(false);
            btn_save.setEnabled(false);btn_cancel.setEnabled(false);
        }

        btn_enrol.setOnClickListener(mOnClicklistener);
        btn_delete.setOnClickListener(mOnClicklistener);
        btn_update.setOnClickListener(mOnClicklistener);
        btn_save.setOnClickListener(mOnClicklistener);
        btn_cancel.setOnClickListener(mOnClicklistener);
        et_curP.addTextChangedListener(mEditCurPListener);
        et_newP.addTextChangedListener(mEditNewPListener);
        et_reP.addTextChangedListener(mEditRepPListener);

    }
    @Override
    public void onBackPressed() {
        System.out.println("OnBackPressed方法已调用");
        Intent i = new Intent();
        i.putExtra("password",intent_Password);
        setResult(0, i);  // 0表示成功
        super.onBackPressed();
        return;
    }
    @Override
    protected void onDestroy(){
        System.out.println("onDestroy方法已调用");
        super.onDestroy();
        return;
    }
}
