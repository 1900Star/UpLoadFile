package com.yibao.uploadfile.util;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @author: Luoshipeng
 * @ Name:    UploadUtil
 * @ Email:   strangermy98@gmail.com
 * @ GitHub:  https://github.com/1900Star
 * @ Time:    2019/3/17/ 19:56
 * @ Des:     TODO
 */
public class UploadUtil {
    public static void uploadImage(List<String> photoList, String url) {
        OkHttpClient okHttpClient = new OkHttpClient();
        if (photoList != null) {
            for (int i = 0; i < photoList.size(); i++) {
                File file = new File(photoList.get(i));
                String fileName = getFileName(photoList.get(i));
                Log.d("lsp", "== 文件名字  " + fileName);
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file", fileName, RequestBody.create(MediaType.parse("image/jpg"), file))
                        .build();
                Request build = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();
                okHttpClient.newCall(build).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call,@NonNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NonNull Call call,@NonNull Response response) throws IOException {
                        ResponseBody body = response.body();
                        if (body != null) {
                            setResult(body.string());
                            Log.d("lsp", "上传成功");
                        } else {
                            Log.d("lsp", "请求体为空");

                        }

                    }
                });
            }
        }
    }

    private static void setResult(final String string) {
        Log.d("lsp", "== 请求体     " + string);
        Log.d("lsp", "请求成功" );
    }

    private static String getFileName(String pathandname) {
        int start = pathandname.lastIndexOf("/");
        if (start != -1) {
            return pathandname.substring(start + 1);
        } else {
            return null;
        }
    }

}
