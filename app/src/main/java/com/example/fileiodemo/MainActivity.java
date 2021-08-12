package com.example.fileiodemo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fileiodemo.model.UploadHandShakeReq;
import com.example.fileiodemo.model.UploadHandShakeResp;
import com.example.fileiodemo.model.UploadReq;
import com.example.fileiodemo.model.UploadResp;
import com.example.fileiodemo.util.FileUtil;
import com.example.fileiodemo.util.SHA256;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_upload)
    Button btn_upload;
    @BindView(R.id.tv_response)
    TextView tv_response;
    @BindView(R.id.tv_fileInfo)
    TextView tv_fileInfo;
    @BindView(R.id.et_fileName)
    EditText et_fileName;

    Context context;
    private FileService fileService;
    Gson gson = new Gson();
    FileUtil file = new FileUtil();
    UploadHandShakeResp uploadHandShakeResp;

    String showString = "";
    String uploadRespString = "";
    String fileName;
    int times;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        context = getApplicationContext();

        checkPermission();
        fileService = RetrofitManager.getInstance().create(FileService.class);
        tv_response.setMovementMethod(new ScrollingMovementMethod());
    }

    // 檢查權限
    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_PERMISSION_STORAGE = 100;
            String[] permissions = {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };

            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    this.requestPermissions(permissions, REQUEST_CODE_PERMISSION_STORAGE);
                    return;
                }
            }
        }
    }

    // 上傳按鈕的監聽事件
    public void btnUpload(View view) {
        times = 0;
        fileName = et_fileName.getText().toString();

        if (fileName.equals("") || fileName.isEmpty()) {
            Toast.makeText(context, "請輸入檔案名稱！", Toast.LENGTH_SHORT).show();
        } else {
            uploadHandShake(fileName);
        }

        et_fileName.setText("");
    }

    // Upload 的 HandShake
    public void uploadHandShake(String fileName) {
        UploadHandShakeReq uploadHandShakeReq = new UploadHandShakeReq();
        byte[] read;
        read = file.readFile(fileName, context);
        uploadHandShakeReq.setFile_name(fileName);
        uploadHandShakeReq.setFile_size(file.getFileSize(fileName));
        uploadHandShakeReq.setChecksum(SHA256.shaEncrypt(read));

        fileService.uploadHandShake(uploadHandShakeReq)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {
                        Log.d("TAG", "Connect onSubscribe");
                    }

                    @Override
                    public void onNext(@NotNull ResponseBody responseBody) {
                        String uploadHandShakeRespString = "";
                        try {
                            uploadHandShakeRespString = responseBody.string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        showString = "Upload HandShake Response\n" + uploadHandShakeRespString + "\n";
                        uploadHandShakeResp = gson.fromJson(uploadHandShakeRespString, UploadHandShakeResp.class);

                        if (uploadHandShakeResp.getStatus() == 0) {
                            int begin = 0;
                            int count = (int) Math.ceil((float) file.getFileSize(fileName) / uploadHandShakeResp.getUpload_maximum());
                            upload(begin, count);
                        }
                        Log.d("TAG", "Connect onNext");
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.d("TAG", "Connect onError: " + e);
                    }

                    @Override
                    public void onComplete() {
                        Log.d("TAG", "Connect onComplete");
                    }

                });

        String uploadHandShakeInfo = fileName + "/" +
                uploadHandShakeReq.getFile_size() + "\n" +
                uploadHandShakeReq.getChecksum();

        tv_fileInfo.setText(uploadHandShakeInfo);
    }

    // Upload
    public void upload(int begin, int count) {
        int readLength = uploadHandShakeResp.getUpload_maximum();

        UploadReq uploadReq = new UploadReq();
        uploadReq.setFile_id(uploadHandShakeResp.getFile_id());

        byte[] read;
        if (count > 1) {
            read = file.readFile(fileName, context, begin * readLength, readLength);
            uploadReq.setChecksum(SHA256.shaEncrypt(read));
            uploadReq.setUpload_status(1);
        } else {
            read = file.readFile(fileName, context, begin * readLength, (file.getFileSize(fileName) - begin * readLength));
            uploadReq.setChecksum(SHA256.shaEncrypt(read));
            uploadReq.setUpload_status(2);
        }
        RequestBody requestFile = RequestBody.create(MediaType.parse("application/json"), gson.toJson(uploadReq));
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), read);
        fileService.upload(requestFile, requestBody)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {
                        Log.d("TAG", "onSubscribe ");
                    }

                    @Override
                    public void onNext(@NotNull ResponseBody responseBody) {
                        ++times;
                        try {
                            uploadRespString = responseBody.string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        UploadResp uploadResp = gson.fromJson(uploadRespString, UploadResp.class);
                        showString += times + ".upload: " + uploadRespString + "\n";
                        int finalCount = count;
                        int finalBegin = begin;
                        if (uploadResp.getStatus() == 0 && finalCount >1) {
                            ++finalBegin;
                            --finalCount;
//                            Log.d("TAG", "finalCount/finalBegin in if: " + finalCount + "/" + finalBegin);
                            upload(finalBegin, finalCount);
                        }
                        Log.d("TAG", "onNext ");
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.d("TAG", "onError : " + e);
                    }

                    @Override
                    public void onComplete() {
                        runOnUiThread(() -> tv_response.setText(showString));
                        Log.d("TAG", "onComplete ");
                    }
                });
    }

}