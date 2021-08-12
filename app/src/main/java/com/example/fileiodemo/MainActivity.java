package com.example.fileiodemo;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fileiodemo.model.UploadHandShakeResp;

public class MainActivity extends AppCompatActivity {
    UploadHandShakeResp uploadHandShakeResp;

    int times;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // 檢查權限
    public void checkPermission() {

    }

    // 上傳按鈕的監聽事件
    public void btnUpload(View view) {

    }

    // Upload 的 HandShake
    public void uploadHandShake(String fileName) {

    }

    // Upload
    public void upload(int begin, int count) {

    }

}