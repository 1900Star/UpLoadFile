package com.yibao.uploadfile;

/*
 *  @项目名：  UpLoadFile 
 *  @包名：    com.yibao.uploadfile
 *  @文件名:   UpLoadThreed
 *  @创建者:   Stran
 *  @创建时间:  2017/12/26 17:44
 *  @描述：    TODO
 */

import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UpLoadFileUtil {

    private static ArrayList<String> list;

    /* 带进度上传文件至Server的方法 */
    public static int httpUrlLoadProgress(String[] picPaths, String requestURL) {

        String boundary = UUID.randomUUID().toString(); // 边界标识 随机生成
        String prefix = "--", end = "\r\n";
        String content_type = "multipart/form-data"; // 内容类型
        String CHARSET = "utf-8"; // 设置编码
        int TIME_OUT = 10 * 10000000; // 超时时间
        try {
            URL url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true); // 允许输入流
            conn.setDoOutput(true); // 允许输出流
            conn.setUseCaches(false); // 不允许使用缓存
            conn.setRequestMethod("POST"); // 请求方式
            conn.setRequestProperty("Charset", "utf-8"); // 设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", content_type + ";boundary=" + boundary);
            /**
             * 当文件不为空，把文件包装并且上传
             */
            OutputStream outputSteam = conn.getOutputStream();
            DataOutputStream dos = new DataOutputStream(outputSteam);

            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(prefix);
            stringBuffer.append(boundary);
            stringBuffer.append(end);
            dos.write(stringBuffer.toString().getBytes());

            String name = "userName";
            dos.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"" + end);
            dos.writeBytes(end);
            dos.writeBytes("zhangSan");
            dos.writeBytes(end);


            for(int i = 0; i < picPaths.length; i++){
                File file = new File(picPaths[i]);

                StringBuffer sb = new StringBuffer();
                sb.append(prefix);
                sb.append(boundary);
                sb.append(end);

                /**
                 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的 比如:abc.png
                 */
                sb.append("Content-Disposition: form-data; name=\"" + i + "\"; filename=\"" + file.getName() + "\"" + end);
                sb.append("Content-Type: application/octet-stream; charset=" + CHARSET + end);
                sb.append(end);
                dos.write(sb.toString().getBytes());

                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[8192];//8k
                int len;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(end.getBytes());//一个文件结束标志
            }
            byte[] end_data = (prefix + boundary + prefix + end).getBytes();//结束 http 流
            dos.write(end_data);
            dos.flush();
            /**
             * 获取响应码 200=成功 当响应成功，获取响应的流
             */
            int res = conn.getResponseCode();
            Log.e("TAG", "response code:" + res);
            if (res == 200) {
                return 200;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 300;

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
