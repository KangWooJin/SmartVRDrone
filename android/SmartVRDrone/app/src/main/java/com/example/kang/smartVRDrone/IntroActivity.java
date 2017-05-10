package com.example.kang.smartVRDrone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;



/**
 * Created by Jeonghee Choi on 2016-05-16.
 */
public class IntroActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_activity);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }
}
