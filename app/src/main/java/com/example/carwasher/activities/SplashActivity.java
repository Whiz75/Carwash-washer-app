package com.example.carwasher.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.carwasher.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        /*----------call methods------------*/
        splashStart();
    }

    private void splashStart()
    {
        /*new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },3000);*/

        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    Thread.sleep(3000);
                    startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
