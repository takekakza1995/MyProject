package com.example.takethraithip.myproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class UserNoti extends AppCompatActivity {
    Button okBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_noti);
        okBtn = (Button) findViewById(R.id.okBtn);
    }
}
