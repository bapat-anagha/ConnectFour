package com.example.connectfour;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_SCREEN=1500;
    Animation topAnimation,bottomAnimation;
    TextView tv_title;
    ImageView iv_icon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //added 30 may
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash);

        //added 30may
        tv_title=findViewById(R.id.tv_title);
        iv_icon=findViewById(R.id.iv_icon);
        topAnimation= AnimationUtils.loadAnimation(SplashScreen.this,R.anim.top_animation);
        bottomAnimation= AnimationUtils.loadAnimation(SplashScreen.this,R.anim.bottom_animation);
        topAnimation.setDuration(990);
        bottomAnimation.setDuration(990);
        iv_icon.setAnimation(topAnimation);
        tv_title.setAnimation(bottomAnimation);

        FirebaseAuth fbauth=FirebaseAuth.getInstance();
        FirebaseUser fbuser=fbauth.getCurrentUser();

         new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (fbuser!=null)
                {
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                }
                else{
                Intent intent=new Intent(SplashScreen.this, loginActivity.class);
                startActivity(intent);
                finish();}
            }
        },SPLASH_SCREEN);
    }
}
