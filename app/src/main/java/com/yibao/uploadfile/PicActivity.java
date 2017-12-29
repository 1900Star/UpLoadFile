package com.yibao.uploadfile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.io.File;

public class PicActivity extends AppCompatActivity {

    private ImageView mIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic);
        initView();

    }

    private void initView() {
        mIv = findViewById(R.id.iv);
        File file = Environment.getExternalStorageDirectory();
        File fileName = new File(file, "xzl.jpeg");
        Bitmap bitmap = BitmapFactory.decodeFile(fileName.getAbsolutePath());
        mIv.setImageBitmap(bitmap);

    }
}
