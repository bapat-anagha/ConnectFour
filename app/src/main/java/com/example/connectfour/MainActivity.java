package com.example.connectfour;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    Button computer, friend, profile, btn_signout,btn_rules;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        computer =(Button)findViewById(R.id.button);
        friend =(Button)findViewById(R.id.button2);
        profile =(Button)findViewById(R.id.button3);
        btn_signout=findViewById(R.id.btn_signout);
        btn_rules=findViewById(R.id.btn_rules);
        btn_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation bounce_anim= AnimationUtils.loadAnimation(MainActivity.this,R.anim.bounce_anim);
                btn_signout.startAnimation(bounce_anim);
                FirebaseAuth.getInstance().signOut();
                finishAffinity();
                Intent intent = new Intent(MainActivity.this, loginActivity.class);
                startActivity(intent);
            }
        });

        btn_rules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation bounce_anim= AnimationUtils.loadAnimation(MainActivity.this,R.anim.bounce_anim);
                btn_rules.startAnimation(bounce_anim);
                Intent intent = new Intent(MainActivity.this, GameRules.class);
                startActivity(intent);
            }
        });


        computer.setOnClickListener(new MainActivity.PlayWithComputer());
        friend.setOnClickListener(new MainActivity.PlayWithFriend());
        profile.setOnClickListener(new MainActivity.ViewProfile());
    }



    private class PlayWithComputer implements View.OnClickListener {
        public void onClick(View v) {
            Animation bounce_anim= AnimationUtils.loadAnimation(MainActivity.this,R.anim.bounce_anim);
            //BounceInterpolator interpolator=new BounceInterpolator(0.2,20);
            //bounce_anim.setInterpolator(interpolator);
            computer.startAnimation(bounce_anim);
            startActivity(new Intent(MainActivity.this, GameActivity.class));
        }
    }

    private class PlayWithFriend implements View.OnClickListener {
        public void onClick(View v) {
            Animation bounce_anim= AnimationUtils.loadAnimation(MainActivity.this,R.anim.bounce_anim);
            friend.startAnimation(bounce_anim);
            startActivity(new Intent(MainActivity.this, GameActivity.class));
        }
    }

    private class ViewProfile implements View.OnClickListener {
        public void onClick(View v) {
            Animation bounce_anim= AnimationUtils.loadAnimation(MainActivity.this,R.anim.bounce_anim);
            profile.startAnimation(bounce_anim);
            startActivity(new Intent(MainActivity.this, userHomeActivity.class));
        }
    }
}
