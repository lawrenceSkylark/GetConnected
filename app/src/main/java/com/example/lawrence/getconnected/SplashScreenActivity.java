package com.example.lawrence.getconnected;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lawrence.getconnected.helpers.ServiceIntro;

public class SplashScreenActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //IntentService
        startService(new Intent(this, ServiceIntro.class));


        Thread timer = new Thread(){
            public void run(){
                try{
//                  sleep(100);
                    ImageView icon = findViewById(R.id.icon);
                    Animation rotateAni = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
                    icon.startAnimation(rotateAni);

                    sleep(2000);
                }
                catch(InterruptedException e){
                    e.printStackTrace();
                } finally {
                    Intent i = new Intent(SplashScreenActivity.this,homeActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        };
        timer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
