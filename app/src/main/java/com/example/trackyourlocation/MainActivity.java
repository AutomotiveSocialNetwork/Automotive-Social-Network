package com.example.trackyourlocation;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
    private Button mBtnGps;
    private Button mBtnFb;
    private Button mBtnAzure;



    ///Facebook main activit
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnAzure=(Button)findViewById(R.id.btn_azure);
        mBtnFb=(Button)findViewById(R.id.btn_fb);
        mBtnGps=(Button)findViewById(R.id.btn_gps);

        mBtnGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),MapsActivity.class);
                startActivity(intent);

            }

        });


        mBtnFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),TestConnect.class);
                startActivity(intent);
            }
        });


        mBtnAzure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),AzureActivity.class);
                startActivity(intent);
            }
        });
    }



}