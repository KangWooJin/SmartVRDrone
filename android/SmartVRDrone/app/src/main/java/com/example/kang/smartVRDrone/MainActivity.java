package com.example.kang.smartVRDrone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;


import java.io.DataInputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends Activity {
    ImageButton connectImgBtn, droneBtn, vrBtn, gpsBtn;


    private Button connectBtn;


    boolean isConnect = false;

    private BackPressCloseHandler backPressCloseHandler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setTheme(android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        setContentView(R.layout.activity_main);


        backPressCloseHandler = new BackPressCloseHandler(this);

        connectBtn = (Button) findViewById(R.id.connetBtn);
        connectImgBtn = (ImageButton)findViewById(R.id.connectImgBtn);
        droneBtn = (ImageButton)findViewById(R.id.droneBtn);
        vrBtn = (ImageButton)findViewById(R.id.vrBtn);
        gpsBtn = (ImageButton)findViewById(R.id.gpsBtn);

        connectBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isConnect == true) {
                    isConnect = false;
                    connectBtn.setBackgroundResource(R.drawable.switchoff);
                    droneBtn.setImageResource(R.drawable.offdronebutton);
                    vrBtn.setImageResource(R.drawable.offvrbutton);
                    gpsBtn.setImageResource(R.drawable.offgpsbutton);


                } else {
                    isConnect = true;
                    connectBtn.setBackgroundResource(R.drawable.switchon);
                    droneBtn.setImageResource(R.drawable.ondronebutton);
                    vrBtn.setImageResource(R.drawable.onvrbutton);
                    gpsBtn.setImageResource(R.drawable.ongpsbutton);
                }
            }
        });

        connectImgBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isConnect == true) {
                    isConnect = false;
                    connectBtn.setBackgroundResource(R.drawable.switchoff);
                    droneBtn.setImageResource(R.drawable.offdronebutton);
                    vrBtn.setImageResource(R.drawable.offvrbutton);
                    gpsBtn.setImageResource(R.drawable.offgpsbutton);


                } else {
                    isConnect = true;
                    connectBtn.setBackgroundResource(R.drawable.switchon);
                    droneBtn.setImageResource(R.drawable.ondronebutton);
                    vrBtn.setImageResource(R.drawable.onvrbutton);
                    gpsBtn.setImageResource(R.drawable.ongpsbutton);
                }
            }
        });

        droneBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isConnect == false) return;

                Intent intent = new Intent(MainActivity.this, PointSettingActivity.class);
                startActivity(intent);

            }
        });

        vrBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isConnect == false) return;
                Intent intent = new Intent(MainActivity.this, ShowImageActivity.class);
                startActivity(intent);

            }
        });

        gpsBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isConnect == false) return;
                Intent intent = new Intent(MainActivity.this, CurrentGPSActivity.class);
                startActivity(intent);

            }
        });


    }

    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }



}
