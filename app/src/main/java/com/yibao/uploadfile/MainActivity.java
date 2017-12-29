package com.yibao.uploadfile;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private EditText mEdtName;
    private EditText mEdtAge;
    /**
     * submit
     */
    private ImageView mView;
    String url = "http://192.168.31.115:8080/upLoadFile/UpPic";
    String textUrl = "http://192.168.31.115:8080/PostText/PostTextServlet";

    String okHttpUrl = "http://192.168.31.115:8080/UpLoadPic/UpLoadPic";
    String host = "http://192.168.31.115:8080/UpLoadMutiFile/FileImageUploadServlet";
    File file = Environment.getExternalStorageDirectory();
    File bigFile = new File(file, "Pro.txt");
    File fileName = new File(file, "a1.jpg");
    //    File fileName = new File(file, "Sky.jpg");
    File fileName2 = new File(file, "phone.jpg");
    String[] str = {fileName.getAbsolutePath(), fileName2.getAbsolutePath()};
    private Button mSubmitText;
    private Button mSubmitPic;
    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @SuppressLint("ShowToast")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                String str = (String) msg.obj;
                Toast.makeText(MainActivity.this, "用户信息上传成功！", Toast.LENGTH_LONG);
                Glide.with(MainActivity.this).load(str).into(mView);

            }


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        Bitmap bitmap = BitmapFactory.decodeFile(fileName.getAbsolutePath());
//        mView.setImageBitmap(bitmap);

    }

    private void initData() {
        mSubmitPic.setOnClickListener(view -> {
            String name = mEdtName.getText().toString().trim();
            String age = mEdtAge.getText().toString().trim();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        UpLoadThreed.okHttpUpImage(okHttpUrl, bigFile);
                        UpLoadThreed.upHttpLoadPic(okHttpUrl, bigFile.getAbsolutePath());
                    }
                }).start();
            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(age)) {
            }
        });
        mSubmitText.setOnClickListener(view -> {
            String name = mEdtName.getText().toString().trim();
            String age = mEdtAge.getText().toString().trim();
            new Thread(() -> {
                String result = UpLoadThreed.httpUrlLoadText(textUrl, name, age);
                LogUtil.d("客户端收到返回的信息 ： " + result);
                if (result.equals("400")) {
                    LogUtil.d(" 没有消息返回 ！");
//                    MainActivity.this.upLoad();
                } else if (result.equals("300")) {
                    LogUtil.d("用户信息上传失败，请重试！");
                } else {
                    Message message = new Message();
                    message.obj = result;
                    message.what = 1;
                    mHandler.sendMessage(message);
                    upLoad();
                }
            }).start();
        });
    }

    private void upLoad() {
//        正常的HttpUrlConnection上传图片
//        UpLoadThreed.upHttpLoadPic(url, fileName.getAbsolutePath());
//        正常的HttpUrlConnection上传图片带进度
        UpLoadThreed.httpUrlLoadProgress(url, fileName.getAbsolutePath());
//        正常OkHttp上传图片


//        UpLoadFileUtil.httpUrlLoadProgress(str, host);

//        String path1 = fileName.getAbsolutePath();
//        String path2 = fileName2.getAbsolutePath();
//        UpLoadThreed.sendMultipart(url, path2, path1);

    }


    private void initView() {
        mEdtName = findViewById(R.id.edt_name);
        mEdtAge = findViewById(R.id.edt_age);
        mSubmitText = findViewById(R.id.btn_text);
        mSubmitPic = findViewById(R.id.btn_pic);
        mView = findViewById(R.id.image_viwe);

    }


}
