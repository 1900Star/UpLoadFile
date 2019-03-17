package com.yibao.uploadfile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private EditText mEdtName;
    private EditText mEdtAge;
    /**
     * submit
     */
    private ImageView mView;
    String baseHost = "http://192.168.43.199:8080";
    // 多图

    String url = baseHost + "/upLoadFile/UpPic";
    String textUrl = baseHost + "/PostText/PostTextServlet";

    String okHttpUrl = baseHost + "/UpLoadBigPic/UpLoadPic";
    String host = baseHost + "/UpLoadMutiFile/FileImageUploadServlet";
    File file = Environment.getExternalStorageDirectory();
    File bigFile = new File(file, "Pro.S");
    File fileName = new File(file, "a1.jpg");
    //    File fileName = new File(file, "Sky.jpg");
    File fileName2 = new File(file, "phone.jpg");
    String[] str = {fileName.getAbsolutePath(), fileName2.getAbsolutePath()};
    private Button mSubmitText;
    private Button mSubmitPic;
    private TextView mTvUserInfo;
    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @SuppressLint("ShowToast")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 1) {
                String str = (String) msg.obj;
                mTvUserInfo.setText(str);
                Toast.makeText(MainActivity.this, "用户信息上传成功！", Toast.LENGTH_LONG).show();
                Glide.with(MainActivity.this).load(str).into(mView);

            } else if (msg.what == 2) {
                mPbLoading.setVisibility(View.GONE);
                mPbLoad.setVisibility(View.VISIBLE);
                UploadBean uploadBean = (UploadBean) msg.obj;
                int cureentProgress = (int) uploadBean.getCurrentProgress();
                mPbLoad.setMax(100);
                mPbLoad.setProgress(cureentProgress);
                LogUtil.d("=== okhttp  progress " +" ==  " + cureentProgress);
                if (uploadBean.isUploadComplete()) {
                    Toast.makeText(MainActivity.this, "上传完成！", Toast.LENGTH_LONG).show();
                }

            } else if (msg.what == 3) {
                String str = (String) msg.obj;
                Toast.makeText(MainActivity.this, str, Toast.LENGTH_LONG).show();
            }


        }
    };
    private ProgressBar mPbLoad;
    private ProgressBar mPbLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();

    }


    private void initListener() {
        mSubmitPic.setOnClickListener(view -> new Thread(() -> {
            okhttpUploadPic();
//            upLoadProgress();
            LogUtil.d("");
        }).start());


        mSubmitText.setOnClickListener(view -> {
            String name = mEdtName.getText().toString().trim();
            String age = mEdtAge.getText().toString().trim();
            if (TextUtils.isEmpty(name) && TextUtils.isEmpty(age)) {
                Message message = new Message();
                message.obj = "用户名和密码不能为空!";
                message.what = 3;
                mHandler.sendMessage(message);
            } else {
                new Thread(() -> {
                    String result = HttpUrlConnectionUpLoadUtil.httpUrlLoadText(textUrl, name, age);
                    LogUtil.d("客户端收到返回的信息 ： " + result);
                    switch (result) {
                        case "400":
                            LogUtil.d(" 没有消息返回 ！");
//                    MainActivity.this.upLoad();
                            break;
                        case "300":
                            LogUtil.d("用户信息上传失败，请重试！");
//                            upLoadProgress();
                            break;
                        default:
                            Message message = new Message();
                            message.obj = result;
                            message.what = 1;
                            mHandler.sendMessage(message);
//                            upLoad();
                            break;
                    }
                }).start();
            }
        });
    }

    private void okhttpUploadPic() {
        HttpUrlConnectionUpLoadUtil.okHttpUpImage(okHttpUrl, fileName, uploadBean -> {
            Message message = new Message();
            message.what = 2;
            message.obj = uploadBean;
            mHandler.sendMessage(message);

        }, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                LogUtil.d("onFailure: 大图片上传失败  " + e.toString());

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                LogUtil.d("==============图片上传成功  ================ ");

            }
        });
    }

    // 用原生   HttpUrlConnection 上传文件的两种方式

    private void upLoadProgress() {

        HttpUrlConnectionUpLoadUtil.httpUrlLoadProgress(okHttpUrl, bigFile.getAbsolutePath(), uploadBean -> {
            LogUtil.d("  ====  原生 进度  aa  " + uploadBean.getCurrentProgress() + "  ===  " + uploadBean.getToteProgress());
            mPbLoad.setMax((int) uploadBean.getToteProgress());
            mPbLoad.setProgress((int) uploadBean.getCurrentProgress());
        });
//        HttpUrlConnectionUpLoadUtil.upHttpLoadPic(okHttpUrl, fileName.getAbsolutePath());

    }


    private void initView() {
        mEdtName = findViewById(R.id.edt_name);
        mEdtAge = findViewById(R.id.edt_age);
        mSubmitText = findViewById(R.id.btn_text);
        mSubmitPic = findViewById(R.id.btn_pic);
        mView = findViewById(R.id.image_viwe);
        mTvUserInfo = findViewById(R.id.tv_userinfo);
        mPbLoad = findViewById(R.id.pb_uplaod);
        mPbLoading = findViewById(R.id.pb_loading);
        mPbLoading.setVisibility(View.GONE);

    }


}
