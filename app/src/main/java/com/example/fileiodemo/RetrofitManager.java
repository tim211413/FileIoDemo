package com.example.fileiodemo;

public class RetrofitManager {

    // 建立 Retrofit 連線
    private RetrofitManager() {

    }

    // 取得 RetrofitManager
    public static RetrofitManager getInstance() {
        return RetrofitManager.getInstance();
    }

    // create Retrofit
    public <T> T create(Class<T> service){
        return null;
    }
}
