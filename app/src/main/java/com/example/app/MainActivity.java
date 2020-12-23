package com.example.app;

import android.app.Activity;
import android.os.Bundle;

import com.meng.dialogwindow.PopWindowUtil;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PopWindowUtil.Builder builder = new PopWindowUtil.Builder(this);
        

    }
}
