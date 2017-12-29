package com.yibao.uploadfile;

/*
 *  @项目名：  UpLoadFile 
 *  @包名：    com.yibao.uploadfile
 *  @文件名:   httpUrlConnectionUpLoadUtil
 *  @创建者:   Stran
 *  @创建时间:  2017/12/26 17:44
 *  @描述：    TODO
 */

import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class httpUrlConnectionUpLoadUtil {

    private static String url;
    private static String fileName;

    public httpUrlConnectionUpLoadUtil(String url, String fileName) {
        this.url = url;
        this.fileName = fileName;
    }

    /**
     * 这个是用HttpURLConnection最基本的上传文件的方式，支持所有文件( jpg mp3 mp4 txt .....)。
     * 括号里面的就是一个上传图片浏览器产生的标准报文，安卓客户端上传时同样要按照这样的规则拼接，具体的实现参考下方代码
     * {
     * -----------------------------7e1712b2e09ce
     * Content-Disposition: form-data; name="file"; filename="C:\Users\Stran\Desktop\Smartisan.jpg"
     * Content-Type: image/jpeg
     * <p>
     * -----------------------------7e1712b2e09ce--
     * }
     *
     * @param url
     * @param fileName
     */
    //    Content-Type: multipart/form-data; boundary=---------------------------7e1108b20f8c
    public static void upHttpLoadPic(String url, String fileName) {
        String boundary = "-----------------------------7e12d81b0fb2";
        String prefix = "--";
        String end = "\r\n";
        try {
            URL u = new URL(url);
            System.out.println("url ==  " + u);
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            DataOutputStream out = new DataOutputStream(connection.getOutputStream()); //得到一个文件输出流
            out.writeBytes(prefix + boundary + end);
            out.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + "Sky.jpg" + "\"" + end);
//            下面两行可写可不写
//            out.writeBytes("Content-Type:image/pjpeg");
//            out.writeBytes(end);
            out.writeBytes(end);
            FileInputStream fis = new FileInputStream(new File(fileName));
            byte[] buff = new byte[1024 * 8];
            int len;
            while ((len = fis.read(buff)) != -1) {
                out.write(buff, 0, len);
            }
            out.writeBytes(end);
            out.writeBytes(prefix + boundary + prefix + end);
//            fis.close();
            out.flush();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
            System.out.println("fileName ==  " + fileName);
            out.close();
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.d("上传失败=================");
        }
    }


    /**
     * @param host
     * @param name
     * @param age  模拟用户提交用户名和密码给服务器，验证通过后服务器返回户头像图片的url地址。
     */
    public static String httpUrlLoadText(String host, String name, String age) {

        try {
            URL url = new URL(host);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            /* 允许Input、Output，不使用Cache */
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            /* 设置传送的method=POST */
            con.setRequestMethod("POST");
            /* setRequestProperty */
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            OutputStream outputStream = con.getOutputStream(); //得到一个普通的文本输出流
            String content = "name=" + name + "&age=" + age;
            outputStream.write(content.getBytes());
            /* 取得Response内容 */
            InputStream is = con.getInputStream();
            int code = con.getResponseCode();

            if (code == 200) {
                String s = dealResponseResult(is); //用户头像图片url地址
                LogUtil.d("返回的数据 ==     " + s);
                return s;

            }

            /* 关闭DataOutputStream */
        } catch (Exception e) {
            LogUtil.d("上传失败     " + e);
            return "300";
        }
        return "500";
    }

    private static String dealResponseResult(InputStream inputStream) {
        String resultData;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(data)) != -1) {
                byteArrayOutputStream.write(data, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        resultData = new String(byteArrayOutputStream.toByteArray());
        return resultData;
    }

    /**
     * @param host
     * @param uploadFile 带进度上传文件至Server的方法
     */
    public static void httpUrlLoadProgress(String host, String uploadFile) {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        String newName = "image.jpg";
        try {
            URL url = new URL(host);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            /* 允许Input、Output，不使用Cache */
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            /* 设置传送的method=POST */
            con.setRequestMethod("POST");
            /* setRequestProperty */
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);
            /* 设置DataOutputStream */
            DataOutputStream ds = new DataOutputStream(con.getOutputStream());
            ds.writeBytes(twoHyphens + boundary + end);
            ds.writeBytes("Content-Disposition: form-data; "
                    + "name=\"file\";filename=\"" + newName + "\"" + end);
            ds.writeBytes(end);
            /* 取得文件的FileInputStream */
            FileInputStream fStream = new FileInputStream(uploadFile);
            /* 设置每次写入1024bytes */
            int bufferSize = 1024 * 8;
            byte[] buffer = new byte[bufferSize];
            int length;
            /* 从文件读取数据至缓冲区 */
            while ((length = fStream.read(buffer)) != -1) {
                /* 将资料写入DataOutputStream中 */
                ds.write(buffer, 0, length);
            }
            ds.writeBytes(end);
            ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
            /* close streams */
            fStream.close();
            ds.flush();
            /* 取得Response内容 */
            InputStream is = con.getInputStream();
            int ch;
            StringBuffer b = new StringBuffer();
            while ((ch = is.read()) != -1) {
                b.append((char) ch);
            /* 将Response显示于Dialog */
            }
            LogUtil.d("上传成功     " + ch);
			/* 关闭DataOutputStream */
            ds.close();
        } catch (Exception e) {
            LogUtil.d("上传失败     " + e);
        }
    }

    public static void okHttpUpImage(String url, File file) {
        OkHttpClient mOkHttpClent = new OkHttpClient();
//        File file = new File(Environment.getExternalStorageDirectory()+"/HeadPortrait.jpg");
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "HeadPortrait.jpg",
                        RequestBody.create(MediaType.parse("image/png"), file));

        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Call call = mOkHttpClent.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.d("onFailure:  " + e);

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtil.d("成功  + == " + response.body().toString());

            }
        });

    }

    public static void sendMultipart(String url, String path1, String path2) {
//        提交文字
        MediaType MEDIA_TYPE_text
                = MediaType.parse("text/x-markdown; charset=utf-8");
//        提交图片
        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        //设置超时时间及缓存
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS);


        OkHttpClient mOkHttpClient = builder.build();

        MultipartBody.Builder mbody = new MultipartBody.Builder().setType(MultipartBody.FORM);

        List<File> fileList = new ArrayList<>();
        File img1 = new File(path1);
        fileList.add(img1);
        File img2 = new File(path2);
        fileList.add(img2);
        int i = 0;
        for (File file : fileList) {
            if (file.exists()) {
                Log.i("imageName:", file.getName());//经过测试，此处的名称不能相同，如果相同，只能保存最后一个图片，不知道那些同名的大神是怎么成功保存图片的。
                mbody.addFormDataPart("file" + i, file.getName(), RequestBody.create(MEDIA_TYPE_PNG, file));
                i++;
            }
        }

        RequestBody requestBody = mbody.build();
        Request request = new Request.Builder()
                .header("Authorization", "Client-ID " + "...")
                .url(url)
                .post(requestBody)
                .build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("InfoMSG", response.body().string());
            }
        });
    }

}
