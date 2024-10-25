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

import androidx.fragment.app.FragmentActivity;

public class LoginActivity extends FragmentActivity implements View.OnClickListener {

    private ImageView displayPassword;
    private EditText etUserName;        
    private EditText etPassWord;        
    private CheckBox rememberMe;        
    private boolean isHideFirst = true;
    private boolean isLogin;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUserName = findViewById(R.id.userName);
        etPassWord = findViewById(R.id.password);
        rememberMe = findViewById(R.id.rememberMe);

        sharedPreferences = getSharedPreferences("user",MODE_PRIVATE);

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

                                doRememberMe(userName,passWord);

                                doRememberToken(result);

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

        rememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isLogin = isChecked;
            }
        });
    }

    private void doRememberMe(String userName,String passWord) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("isLogin", isLogin);   
        editor.putString("userName",userName); 
        editor.putString("passWord",passWord);  
        
        editor.apply();
//        editor.commit();
    }

    private void doRememberToken(AjaxResult result) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("token",result.getToken());  //保存Token
      
        editor.apply();
//        editor.commit();
    }

    private void doRememberUserNick(AjaxResult result) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("nickName",result.getUser().getNickName());  
   
        editor.commit();
    }

    private void loginSuccess() {
//        Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.display_password:{
                if (isHideFirst) {
                    displayPassword.setImageResource(R.drawable.eye_close);
                   
                    etPassWord.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    isHideFirst = false;
                } else {
                    displayPassword.setImageResource(R.drawable.eye);
                   
                    etPassWord.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    isHideFirst = true;
                }
                break;
            }
        }
    }
}