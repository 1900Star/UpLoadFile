package com.yibao.uploadfile.inte;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.yibao.uploadfile.LogUtil;
import com.yibao.uploadfile.UpLoadFileInterFace;
import com.yibao.uploadfile.UploadBean;
import com.yibao.uploadfile.listener.OnUploadProgressListener;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * @ Author: Luoshipeng
 * @ Name:   UpLoadRequest
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/5/4/ 23:56
 * @ Des:    //TODO
 */
public class UpLoadRequest extends RequestBody {
    private RequestBody mRequestBody;
    private BufferedSink mSink;
    private OnUploadProgressListener mUploadProgressListener;

    public UpLoadRequest(RequestBody requestBody, OnUploadProgressListener progressListener) {
        this.mRequestBody = requestBody;
        this.mUploadProgressListener = progressListener;
    }

    @Nullable
    @Override
    public MediaType contentType() {

        return mRequestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return mRequestBody.contentLength();
    }

    @Override
    public void writeTo(@NonNull BufferedSink sink) throws IOException {
        if (mSink == null) {
            mSink = Okio.buffer(getSink(sink));
        }
        mRequestBody.writeTo(mSink);
        mSink.flush();


    }

    private Sink getSink(BufferedSink sink) {

        return new ForwardingSink(sink) {
            long startLength = 0L, endLength = 0L;

            @Override
            public void write(@NonNull Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if (endLength == 0) {
                    endLength = contentLength();
                }
                startLength += byteCount;
                if (mUploadProgressListener != null) {

                    mUploadProgressListener.upLoadProgress(new UploadBean(startLength * 100 / endLength, endLength, startLength == endLength));
                }
//                LogUtil.d("时时上传的进度   " + startLength * 100 / endLength);

            }
        };

    }
}
