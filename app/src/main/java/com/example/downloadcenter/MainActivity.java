package com.example.downloadcenter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private TextView textView = null;
    private Button button = null;
    private String texUrl = "http://download.bxwx666.org/txt/33/82/82.txt?txtkey=e3a568db4088370b17dc0636fb104a0a";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        textView = findViewById(R.id.tv);
        button = findViewById(R.id.bt);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(Environment.getExternalStorageDirectory(), "Text");
                File textFile = new File(file, "testTex");
                StringBuilder stringBuilder = new StringBuilder();
                byte[] bytes = new byte[1024];
                int len = -1;
                try {
                    InputStream inputStream = new FileInputStream(textFile);
                    while ((len = inputStream.read(bytes)) != -1) {
                        stringBuilder.append(bytes.toString());
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                }
                textView.setText(stringBuilder.toString());
            }
        });

        DownloadTxt downloadTxt = new DownloadTxt.Builder().fileName("testTex").threadSize(5).urlPath(texUrl).builder();
        downloadTxt.download(downloadTxt);
    }
}
