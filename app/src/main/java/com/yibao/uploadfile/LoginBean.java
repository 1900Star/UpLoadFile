package com.yibao.uploadfile;

/**
 * @ Author: Luoshipeng
 * @ Name:   LoginBean
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/5/4/ 22:32
 * @ Des:    //TODO
 */
public class LoginBean {
    private String okData;
    private String okError;
    private String imgUrl;

    public LoginBean(String okData, String okError, String imgUrl) {
        this.okData = okData;
        this.okError = okError;
        this.imgUrl = imgUrl;
    }

    public String getOkData() {
        return okData;
    }

    public void setOkData(String okData) {
        this.okData = okData;
    }

    public String getOkError() {
        return okError;
    }

    public void setOkError(String okError) {
        this.okError = okError;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
