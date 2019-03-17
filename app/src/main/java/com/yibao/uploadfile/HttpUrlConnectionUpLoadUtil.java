package com.yibao.uploadfile;

/*
 *  @项目名：  UpLoadFile
 *  @包名：    com.yibao.uploadfile
 *  @文件名:   HttpUrlConnectionUpLoadUtil
 *  @创建者:   Stran
 *  @创建时间:  2017/12/26 17:44
 *  @描述：    TODO
 */

import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.yibao.uploadfile.inte.UpLoadRequest;
import com.yibao.uploadfile.listener.OnUploadProgressListener;

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
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUrlConnectionUpLoadUtil {

    private String url;
    private String fileName;

    public HttpUrlConnectionUpLoadUtil(String url, String fileName) {
        this.url = url;
        this.fileName = fileName;
    }

    /**
     * 这个是用HttpURLConnection上传文件最基本的的方式，支持所有文件( jpg mp3 mp4 txt .....)。
     * 括号里面的就是上传一张图片，浏览器产生的标准报文，安卓客户端上传时同样要按照这样的规则拼接，具体的实现参考下方代码
     * {
     * -----------------------------7e1712b2e09ce
     * Content-Disposition: form-data; name="file"; filename="C:\Users\Stran\Desktop\Smartisan.jpg"
     * Content-Type: image/jpeg
     * <p>
     * -----------------------------7e1712b2e09ce--
     * }
     *
     * @param url      方式一
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
            System.out.println("原生上传图片成功 ==  " + fileName);
            out.close();
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.d("上传失败=================");
        }
    }

    /**
     * @param host       方式二
     * @param uploadFile HttpURLConnection 带进度上传图片文件至Server的方法
     */
    public static void httpUrlLoadProgress(String host, String uploadFile, OnUploadProgressListener listener) {
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
            long startLength = 0L,
                    endLength = 0L;
            /* 从文件读取数据至缓冲区 */
            while ((length = fStream.read(buffer)) != -1) {
                /* 将资料写入DataOutputStream中 */
                startLength += length;
                listener.upLoadProgress(new UploadBean(length, ds.size(), startLength == endLength));
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
//                listener.upLoadProgress(new UploadBean());
                b.append((char) ch);
                LogUtil.d("时时上传的进度   原生   " + ch);
                /* 将Response显示于Dialog */
            }
            LogUtil.d("上传成功 带进度  原生  " + ch);
            /* 关闭DataOutputStream */
            ds.close();
        } catch (Exception e) {
            LogUtil.d("上传失败  带进度 原生  " + e);
        }
    }


    /**
     * @param host url
     * @param name name
     * @param age  HttpURLConnection 模拟用户提交用户名和密码给服务器，验证通过后服务器返回户头像图片的url地址。
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


    public static void okHttpUpImage(String url, File file, OnUploadProgressListener uploadProgressListener, Callback callback) {
        OkHttpClient mOkHttpClent = new OkHttpClient();

        MultipartBody.Builder builder = new MultipartBody.Builder();
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/png"), file);

        MultipartBody multipartBody = builder.addFormDataPart("file", "Girl.jpg", requestBody).build();

        UpLoadRequest upLoadRequest = new UpLoadRequest(multipartBody, uploadProgressListener);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Google", "Star")
                .post(upLoadRequest)
                .build();
        Call call = mOkHttpClent.newCall(request);
        call.enqueue(callback);

    }

}
