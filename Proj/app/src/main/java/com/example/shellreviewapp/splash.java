package com.example.shellreviewapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class splash extends AppCompatActivity {

    ImageView logo,slogan;
    Animation top,bottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        top = AnimationUtils.loadAnimation(this,R.anim.top_anim);
        bottom = AnimationUtils.loadAnimation(this,R.anim.bottom_anim);

        logo = (ImageView) findViewById(R.id.logo);
        slogan = (ImageView) findViewById(R.id.slogan);


        top.setDuration(1500);
        bottom.setDuration(1500);
        logo.setAnimation(top);
        slogan.setAnimation(bottom);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(splash.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        },3000);


    }
}
