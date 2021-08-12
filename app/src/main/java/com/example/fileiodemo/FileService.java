package com.example.fileiodemo;

import com.example.fileiodemo.model.UploadHandShakeReq;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface FileService {

    @POST("upload_hs.php")
    Observable<ResponseBody> uploadHandShake(@Body UploadHandShakeReq uploadHandShakeReq);

    @POST("upload.php")
    @Multipart
    Observable<ResponseBody> upload(@Part("Data\"; filename=\"data.txt") RequestBody description,
                                        @Part ("Upload\"; filename=\"upload.txt")RequestBody file);
}
