package com.example.connectfour;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class GameRules extends AppCompatActivity {
private TextView tv_rules;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_rules);
        tv_rules=findViewById(R.id.tv_rules);
        String text="";

        text="Connect4 is a game played by 2 players (player vs player, or player vs computer)";
        text+="\n\n";
        text+="Each player plays one after another, by putting down one piece at the lowest box of a slot. The goal is to be the first to connect 4 pieces in a line, horizontally, vertically or diagonally, before your opponent in a 7X6 grid.";
        text+="\n\n";
        text+="You get a point for every win vs player or vs the computer. You will be able to see your scores on User Profile page.";
        tv_rules.setText(text);
    }
}
