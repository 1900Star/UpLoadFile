package com.yibao.uploadfile.inte;


import com.yibao.uploadfile.UpLoadFileInterFace;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Author：Sid
 * Des：${Retrofit 帮助类}
 * Time:2017/4/10 17:22
 *
 * @author Stran
 */
public class RetrofitHelper {

    private static Retrofit retrofit;

    public static UpLoadFileInterFace getGankApi(String baseUrl) {
        if (retrofit == null) {
            synchronized (RetrofitHelper.class) {
                if (retrofit == null) {
                    retrofit = new Retrofit.Builder().baseUrl(baseUrl)
                            .client(new OkHttpClient())
                            .build();
                }

            }

        }

        return retrofit.create(UpLoadFileInterFace.class);


    }


}
