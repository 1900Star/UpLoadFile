package com.yibao.uploadfile.inte;

import com.yibao.uploadfile.LoginBean;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * @ Author: Luoshipeng
 * @ Name:   ApiServices
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/4/26/ 22:28
 * @ Des:    //TODO
 */
public interface ApiServices {
    @GET()
    Call<LoginBean> login();
}
