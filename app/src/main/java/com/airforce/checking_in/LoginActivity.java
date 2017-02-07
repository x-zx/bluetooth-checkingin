package com.airforce.checking_in;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.listener.SaveListener;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("蓝牙考勤");
        Button btn = (Button)findViewById(R.id.button);


        Bmob.initialize(this, "4ae9c5baf17de569f655e221a6999397");

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText user = (EditText)findViewById(R.id.user);
                EditText pw = (EditText)findViewById(R.id.pw);


                final User u = new User();
                u.setUsername(user.getText().toString());
                u.setPassword(pw.getText().toString());
                u.login(LoginActivity.this, new SaveListener() {
                    @Override
                    public void onSuccess() {

                        Integer uid = (Integer)u.getObjectByKey(LoginActivity.this, "user_id");
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(LoginActivity.this,TaskListActivity.class);
                        intent.putExtra("uid", String.valueOf(uid));
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Toast.makeText(LoginActivity.this, "登陆失败：" + s, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
