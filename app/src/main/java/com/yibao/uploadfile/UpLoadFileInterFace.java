package com.yibao.uploadfile;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * @ Author: Luoshipeng
 * @ Name:   UpLoadFileInterFace
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/4/22/ 21:23
 * @ Des:    //TODO
 */
public interface UpLoadFileInterFace {
    @Multipart
    @POST("upload")
    Call<ResponseBody> upload(@Part("description") RequestBody description,
                              @Part MultipartBody.Part file);


}
