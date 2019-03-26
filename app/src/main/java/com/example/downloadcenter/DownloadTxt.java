package com.example.downloadcenter;

import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;

public class DownloadTxt {

    //线程
    private ExecutorService fixedPool = null;
    private int threadSize = -1;
    //文件参数
    private String fileName = null;
    private String urlPath = null;

    private RandomAccessFile randomAccessFile = null;
    private volatile static DownloadTxt downloadTxt = null;

    private DownloadTxt() {}

    private DownloadTxt(DownloadTxt downloadTxt) {
        this.fileName = downloadTxt.fileName;
        this.urlPath = downloadTxt.urlPath;
        this.threadSize = downloadTxt.threadSize;
    }

    public static class Builder {
        private DownloadTxt downloadTxt = null;

        public Builder() {
            this.downloadTxt = new DownloadTxt();
        }

        public Builder urlPath(String urlPath) {
            this.downloadTxt.urlPath = urlPath;
            return this;
        }

        public Builder fileName(String fileName) {
            this.downloadTxt.fileName = fileName;
            return this;
        }

        public Builder threadSize(int threadSize) {
            this.downloadTxt.threadSize = threadSize;
            return this;
        }

        public DownloadTxt builder() {
            return new DownloadTxt(downloadTxt);
        }
    }

    public void download(final DownloadTxt downloadTxt) {
        //开始下载
        HttpPool.getInstance().getCachePool().execute(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                try {
                    Log.d("download", downloadTxt.urlPath + "\n" + downloadTxt.fileName);
                    url = new URL(downloadTxt.urlPath);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setConnectTimeout(5000);
                    httpURLConnection.setRequestMethod("GET");
                    if (httpURLConnection.getResponseCode() == 200) {
                        int length = httpURLConnection.getContentLength();
                        File file = new File(Environment.getExternalStorageDirectory(), "Text");
                        if (!file.exists()) {
                            file.mkdir();
                        }
                        File texFile = new File(file, downloadTxt.fileName);
                        randomAccessFile = new RandomAccessFile(texFile, "rwd");
                        randomAccessFile.setLength(length);//为文件设置长度
                        randomAccessFile.close();

                        int averageLength = length / downloadTxt.threadSize;
                        for (int i = 0; i < downloadTxt.threadSize; i++) {
                            int startIndex = averageLength * i;
                            int endIndex = startIndex + averageLength - 1;
                            if (i == downloadTxt.threadSize - 1) {
                                endIndex = length;
                            }
                            partDownload(startIndex, endIndex, downloadTxt);
                        }
                    }
                    Log.d("download", "download完成");
                }catch (IOException e) {
                    Log.d("download", "出问题了");
                    e.printStackTrace();
                }
            }
        });
    }

    private void partDownload(final int startIndex, final int endIndex, final DownloadTxt downloadTxt) {
        HttpPool.getInstance().getFixedPool(downloadTxt.threadSize).execute(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                try {
                    Log.d("download", "partDownload开始");
                    url = new URL(downloadTxt.urlPath);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setConnectTimeout(5000);
                    httpURLConnection.setRequestProperty("Range", "bytes=" + startIndex + "-" + endIndex);//设置请求头
                    if (httpURLConnection.getResponseCode() == 206) {//部分读取返回时206
                        InputStream inputStream = httpURLConnection.getInputStream();
                        randomAccessFile.seek(startIndex);
                        int len = -1;
                        byte[] bytes = new byte[1024];
                        while ((len = inputStream.read(bytes)) != -1) {
                            randomAccessFile.write(bytes, 0, len);
                        }
                        inputStream.close();
                        randomAccessFile.close();
                    }
                    httpURLConnection.disconnect();
                    Log.d("download", "partDownload结束");
                } catch (IOException e) {
                    Log.d("download", "partDownload出错了");
                    e.printStackTrace();
                }
            }
        });
    }

}
