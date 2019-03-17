package com.yibao.uploadfile.inte;

import android.util.Log;

import com.yibao.uploadfile.UpLoadFileInterFace;
import com.yibao.uploadfile.inte.RetrofitHelper;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @ Author: Luoshipeng
 * @ Name:   RetrofitUtil
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/4/22/ 21:26
 * @ Des:    //TODO
 */
public class RetrofitUtil {
    public static void upLoadFile(File file) {
        String okHttpUrl = "http://192.168.31.115:8080/UpLoadBigPic/UpLoadPic";
        // 创建 RequestBody，用于封装构建RequestBody
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);

        // MultipartBody.Part  和后端约定好Key，这里的partName是用image
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        // 添加描述
        String descriptionString = "hello, 这是文件描述";
        RequestBody description =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), descriptionString);
        UpLoadFileInterFace gankApi = RetrofitHelper.getGankApi(okHttpUrl);
        // 执行请求
        Call<ResponseBody> call = gankApi.upload(description, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                Log.v("Upload", "success");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }


}
