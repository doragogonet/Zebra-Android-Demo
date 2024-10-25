package com.example.ebskk.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

//import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSON;
import com.example.ebskk.R;
import com.example.ebskk.bean.AjaxResult;
import com.example.ebskk.bean.LoginBody;
import com.example.ebskk.controller.LoginController;

import android.util.Log;

public class LoginActivity extends BaseTabFragmentActivity implements View.OnClickListener {

    private ImageView displayPassword;
    private EditText etUserName;        //用户名
    private EditText etPassWord;        //密码
    private CheckBox rememberMe;        //记住密码
    private boolean isHideFirst = true;
    private boolean isLogin;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //初始化控件
        etUserName = findViewById(R.id.userName);
        etPassWord = findViewById(R.id.password);
        rememberMe = findViewById(R.id.rememberMe);

        //获取sharedPreferences实例
        sharedPreferences = getSharedPreferences("user",MODE_PRIVATE);

        //是否勾选了“remember me”
        isLogin = sharedPreferences.getBoolean("isLogin", false);
        if (isLogin) {
            String userName = sharedPreferences.getString("userName",null);
            String passWord = sharedPreferences.getString("passWord",null);
            etUserName.setText(userName);
            etPassWord.setText(passWord);
            rememberMe.setChecked(true);
        }

        displayPassword = findViewById(R.id.display_password);
        displayPassword.setOnClickListener(this);
        displayPassword.setImageResource(R.drawable.eye_close);

        //登录
        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = etUserName.getText().toString();
                String passWord = etPassWord.getText().toString();
                if (TextUtils.isEmpty(userName) && TextUtils.isEmpty(passWord)) {
                    Toast.makeText(LoginActivity.this,"ユーザー名とパスワードを入力してください。",Toast.LENGTH_LONG).show();
                } else {

                    LoginBody loginBody = new LoginBody();
                    loginBody.setUsername(userName);
                    loginBody.setPassword(passWord);

                    new LoginController() {
                        @Override
                        public void onResult(AjaxResult result) {
                            if (result.isSuccess()){

                                Log.i("ccc","result"+JSON.toJSONString(result));

                                //记住密码
                                doRememberMe(userName,passWord);

                                doRememberToken(result);

                                //登录成功
                                loginSuccess();

                                new LoginController() {
                                    @Override
                                    public void onResult(AjaxResult result) {
                                        if (result.isSuccess()){

                                            doRememberUserNick(result);
                                            Log.i("ccc","result111"+JSON.toJSONString(result));

                                        } else {
                                            Toast.makeText(LoginActivity.this,"ユーザー情報を取得できませんでした。",Toast.LENGTH_LONG).show();
                                        }

                                    }
                                }.getInfo(LoginActivity.this,result.getToken());

                            } else {
                                Toast.makeText(LoginActivity.this,"ユーザー名又はパスワードが正しくありません。",Toast.LENGTH_LONG).show();
                            }

                        }
                    }.login(LoginActivity.this,loginBody);

                }
            }
        });

        //remember me的点击事件
        rememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isLogin = isChecked;
            }
        });
    }

    private void doRememberMe(String userName,String passWord) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("isLogin", isLogin);   //保存“remember me”
        editor.putString("userName",userName);  //保存“ユーザー名”
        editor.putString("passWord",passWord);  //保存“パスワード”
        //提交
        editor.apply();
//        editor.commit();
    }

    private void doRememberToken(AjaxResult result) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("token",result.getToken());  //保存Token
        //提交
        editor.apply();
//        editor.commit();
    }

    private void doRememberUserNick(AjaxResult result) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("nickName",result.getUser().getNickName());  //保存显示用户名
        //提交
        editor.commit();
    }

    private void loginSuccess() {
//        Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
        Intent intent = new Intent(LoginActivity.this,UHFMainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.display_password:{
                if (isHideFirst) {
                    displayPassword.setImageResource(R.drawable.eye_close);
                    // 用户想要隐藏密码
                    etPassWord.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    isHideFirst = false;
                } else {
                    displayPassword.setImageResource(R.drawable.eye);
                    // 用户想要显示密码
                    etPassWord.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    isHideFirst = true;
                }
                break;
            }
        }
    }
}