package com.yibao.uploadfile;

/**
 * @ Author: Luoshipeng
 * @ Name:   LoginBean
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/5/4/ 22:32
 * @ Des:    //TODO
 */
public class UploadBean {
    private long currentProgress;
    private long toteProgress;
    private boolean isUploadComplete;

    public UploadBean(long currentProgress, long toteProgress, boolean isUploadComplete) {
        this.currentProgress = currentProgress;
        this.toteProgress = toteProgress;
        this.isUploadComplete = isUploadComplete;
    }

    public long getCurrentProgress() {
        return currentProgress;
    }

    public long getToteProgress() {
        return toteProgress;
    }

    public boolean isUploadComplete() {
        return isUploadComplete;
    }
}
