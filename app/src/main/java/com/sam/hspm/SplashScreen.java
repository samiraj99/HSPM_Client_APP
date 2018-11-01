package com.sam.hspm;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new CountDownTimer(3000, 500) {
            @Override
            public void onTick(long l) {

            }
            @Override
            public void onFinish() {
                Intent login = new Intent(SplashScreen.this,LoginScreen.class);
                startActivity(login);
                finish();
            }
        }.start();
    }
}
