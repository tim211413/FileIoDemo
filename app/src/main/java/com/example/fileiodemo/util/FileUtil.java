package com.example.fileiodemo.util;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileUtil {

    // 讀取整個檔案
    public byte[] readFile(String filePath, Context context) {
        return readFile(filePath, context, 0, getFileSize(filePath));
    }

    // 讀取部分檔案
    public byte[] readFile(String filePath, Context context, int begin, int length) {
        byte[] read = new byte[length];

        try (FileInputStream fin = context.openFileInput(filePath);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(fin)) {

            bufferedInputStream.skip(begin);
            bufferedInputStream.read(read);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("TAG", "read: " + read);
        Log.d("TAG", "read.length(): " + read.length);
        return read;
    }

    // 獲得檔案大小
    public int getFileSize(String filePath) {
        String path = "/data/user/0/com.example.fileiodemo/files/" + filePath;
        File file = new File(path);
        int fileSize = Integer.parseInt(String.valueOf(file.length()));
        return fileSize;
    }

}