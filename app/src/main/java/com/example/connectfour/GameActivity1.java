package com.example.connectfour;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static com.example.connectfour.Constants.COMPUTER;
import static com.example.connectfour.Constants.PLAYER;
import static java.lang.Thread.sleep;

public class GameActivity extends AppCompatActivity {

    //2d array to store value of the game board
    static  int[][] gameBoard = new int[6][7];


    //flag to determine turn
    static boolean playerTurn;

    private static final String TAG = "GameActivity";

    /*anagha db update start*/
    private FirebaseAuth mAuth;
    static int lost;
    static int won;
    private static String status="";
    Button btnExit, playAgain;

    boolean isWifi;
    private String gameId;
    static int multiplayer;

    /*anagha db update end*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        mAuth = FirebaseAuth.getInstance();
        btnExit=findViewById(R.id.btnExit);
        playAgain = findViewById(R.id.btnPlayAgain);

        Bundle extras = getIntent().getExtras();
        Log.e("Extras--->", String.valueOf(extras));
        String type = extras.getString("type");
        if (type.equals("wifi")) {
            isWifi = true;
            String gameId = extras.getString("gameId");
            setGameId(gameId);
            setMe(extras.getString("me"));

            FirebaseDatabase.getInstance().getReference().child("games")
                    .child(gameId)
                    .setValue(null);
        } else {
            //first turn will be always of player
            playerTurn = true;
        }
        playAgain.setOnClickListener(v1 -> {
            if (!isWifi) {
                PlayAgain();
            } else {
                FirebaseDatabase.getInstance().getReference().child("games")
                        .child(gameId)
                        .setValue(null);

                FirebaseDatabase.getInstance().getReference().child("games")
                        .child(gameId)
                        .child("restart")
                        .setValue(System.currentTimeMillis());
            }
        });
        /* code added by anagha starts*/
        Button btnUserProfile=findViewById(R.id.btnUserProfile);
        btnUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(GameActivity.this, userHomeActivity.class));
            }
        });
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GameActivity.this, MainActivity.class));
            }
        });
        /* code added by anagha ends*/



        setUpBoard();
    }

    private void setUpBoard() {

        //initializing the gameBoard, all zeros, i.e. no place used
        for (int i = 0 ; i < 6 ; i++) {
            Arrays.fill(gameBoard[i], 0);
        }

        //parent is group of all views within wrapper view
        ViewGroup parent = findViewById(R.id.wrapper);

        //loop on all the 6 cols to get individual column
        for (int i = 0, k = 0; i < 6 ;k++) {
            View child = parent.getChildAt(k);

            //if child is not a column , leave it
            if (!(child instanceof ViewGroup))
                continue;

            ViewGroup column = (ViewGroup) child;

            //set click listener on every column
            column.setOnClickListener(new HandleListener(i));

            int rows = column.getChildCount();
            for (int j = 0; j < rows; j++) {
                //set click listener on each row or each button on every column
                column.getChildAt(j).setOnClickListener(new HandleListener(i));
            }
            i++;
        }

    }

    public int getColumnId(int colNumber) {
        //return View's id from column number
        switch (colNumber) {
            case 0:
                return R.id.col1;
            case 1:
                return R.id.col2;
            case 2:
                return R.id.col3;
            case 3:
                return R.id.col4;
            case 4:
                return R.id.col5;
            case 5:
                return R.id.col6;
        }
        return -1;
    }


    //method to return the first available row of any column
    public int getFirstAvailableRow(int colNumber) {
        for (int i = 0; i < 7; i++) {
            if (gameBoard[colNumber][i] == 0) {
                //Log.d(TAG, "getFirstAvailableRow: value of row " + i);
                return i;
            }
        }
        return -1;
    }

    public void play(int player, int rowNumber,int colNumber, int[][] gameBoard) {
         rowNumber = getFirstAvailableRow(colNumber);
        int columnId = getColumnId(colNumber);
        //if current player is you , change the available row to red color
        if (player == PLAYER) {
            //dropDisk(rowNumber,colNumber,player);
            ((ViewGroup) findViewById(columnId)).getChildAt(6 - rowNumber).setBackground(getResources().getDrawable(R.drawable.you_circle));
            gameBoard[colNumber][rowNumber] = PLAYER;
        } else if (player == Constants.COMPUTER) {
            //dropDisk(rowNumber,colNumber,player);
            ((ViewGroup) findViewById(columnId)).getChildAt(6 - rowNumber).setBackground(getResources().getDrawable(R.drawable.computer_circle));
            gameBoard[colNumber][rowNumber] = Constants.COMPUTER;
        }

    }
    public void setGameId(String gameId) {
        this.gameId = gameId;
        FirebaseDatabase.getInstance().getReference().child("games")
                .child(gameId)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot.getValue() == null) {
                            return;
                        }
                        String key = dataSnapshot.getKey();
                        if (!key.equals("restart")) {
                            int row = Integer.parseInt(key.substring(0, 1));
                            int col = Integer.parseInt(key.substring(2, 3));
                            Integer shape = dataSnapshot.getValue(Integer.class);

                            play(shape, row,col, GameActivity.this.gameBoard);

                            if (announceWinner(row,col)) return;
                            //player's turn, the listener will now play in the place he chooses
                            playerTurn = !playerTurn;
                        } else {
                            PlayAgain();
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot.getValue() == null) {
                            return;
                        }
                        if (dataSnapshot.getKey().equals("restart")) {
                            PlayAgain();
                        }
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
    public void setMe(String me) {
        Log.e("me===>",me);
        if (me.equals(Integer.toString(PLAYER))) {
            playerTurn = true;
            Toast.makeText(this, "Your turn", Toast.LENGTH_SHORT).show();
            multiplayer = 1;
        } else {
            playerTurn = false;
            Toast.makeText(this, "Opponent's turn", Toast.LENGTH_SHORT).show();
            multiplayer = 2;
        }
    }

    private int getWinner(int row, int col) {
        //Log.d(TAG, "getWinner: row=" + row + " col:" + col);
        //check horizontally
        int countH = 1;

        //check horizontally , right side
        int i=row ; int j=col+1;
        while(j<=6){
            if(gameBoard[row][col]==gameBoard[i][j++]){
                countH++;
                if (countH == 4){
                    //Log.d(TAG,"countH first "+countH);
                    return gameBoard[row][col];
                }
            }
            else
                break;
        }

        //check for left side
         i=row ;  j=col-1;
        while(j>=0){
            if(gameBoard[row][col]==gameBoard[i][j--]){
                //Log.d(TAG,"countH i "+i+" j"+j);
                countH++;
                if (countH == 4){
                    //Log.d(TAG,"countH second "+countH);
                    return gameBoard[row][col];
                }
            }
            else
                break;
        }

        //check Vertically
        //first check below the cell
        int countV = 1;
         i=row-1 ;  j=col;
        while(i>=0){
            if(gameBoard[row][col]==gameBoard[i--][j]){
                //Log.d(TAG,"countD1 i "+i+" j"+j);
                countV++;
                if (countV == 4){
                    //Log.d(TAG,"countV first "+countV);
                    return gameBoard[row][col];
                }
            }
            else
                break;
        }
        //check above the cell
        i=row+1 ;  j=col;
        while(i<=5){
            if(gameBoard[row][col]==gameBoard[i++][j]){
                //Log.d(TAG,"countD1 i "+i+" j"+j);
                countV++;
                if (countV == 4){
                    //Log.d(TAG,"countV second "+countV);
                    return gameBoard[row][col];
                }
            }
            else
                break;
        }

        //check diagonally
        int countD =1;
        //check for lower left diagonal
         i=row-1 ;j=col+1;
        while(i>=0 && j<=6){
            if(gameBoard[row][col]==gameBoard[i--][j++]){
                    countD++;
                if (countD == 4){
                    //Log.d(TAG,"countD "+countD);
                    return gameBoard[row][col];
                }
            }
            else
                break;
        }

        //check upper right diagonal
        i=row+1 ; j=col-1;
        while(i<=5 && j>=0){
            if(gameBoard[row][col]==gameBoard[i++][j--]){
                countD++;
                if (countD == 4){
                    //Log.d(TAG,"countD "+countD);
                    return gameBoard[row][col];
                }
            }
            else
                break;

        }

        int countD1=1;
        //check for lower right diagonal
        i=row+1 ;j=col+1;
        while(i<=5 && j<=6){
            if(gameBoard[row][col]==gameBoard[i++][j++]){
                countD1++;
                if (countD1 == 4){
                    //Log.d(TAG,"countD1 first"+countD1);
                    return gameBoard[row][col];
                }
            }
            else
                break;
        }

        //check for upper left diagonal
        i=row-1 ;j=col-1;
        while(i>=0 && j>=0){
            if(gameBoard[row][col]==gameBoard[i--][j--]){
                //Log.d(TAG,"countD1 i "+i+" j"+j);
                countD1++;
                if (countD1 == 4){
                    //Log.d(TAG,"countD1 second "+countD1);
                    return gameBoard[row][col];
                }
            }
            else
                break;
        }


        return 0;
    }

    private boolean announceWinner(int rowNumber,int colNumber) {
        int state = getWinner(colNumber,rowNumber);
        if(state==0) return false;

        findViewById(R.id.btnPlayAgain).setVisibility(View.VISIBLE);
        findViewById(R.id.btnUserProfile).setVisibility(View.VISIBLE);

        /* code added by anagha 24 may starts*/
        /*
        Button btnUserProfile=findViewById(R.id.btnUserProfile);
        btnUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GameActivity.this, userHomeActivity.class));
            }
        });*/
        /* code added by anagha 24 may ends*/


        TextView winner = findViewById(R.id.txtWinner);
        winner.setVisibility(View.VISIBLE);
        if((!isWifi && state == PLAYER) || (isWifi && state == multiplayer)){
               winner.setText(R.string.won);
               //TODO: Database update
                status="won";
                //updateDB_result(status);

        }

        else {
            winner.setText(R.string.lost);
            //TODO: Databse update
                status="lost";
                //updateDB_result(status);

        }

        return true;
    }

    /* function for db update for game score*/
    public boolean updateDB_result(String update)
    {

        String uid = mAuth.getCurrentUser().getUid();
        DatabaseReference reference_1= FirebaseDatabase.getInstance().getReference().child("Score").child(uid);
        Toast.makeText(this,"in update win/lose count"+uid,Toast.LENGTH_SHORT).show();
        reference_1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    lost=Integer.parseInt(dataSnapshot.child("lost").getValue().toString());
                    String rank=dataSnapshot.child("ranking").getValue().toString();
                    String username=dataSnapshot.child("username").getValue().toString();
                    won=Integer.parseInt(dataSnapshot.child("won").getValue().toString());

                }
                else
                {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (status.equals("lost"))
        {
            int lost1=lost+1;
            reference_1.child("lost").setValue(String.valueOf(lost1));

            /*HashMap hmap=new HashMap();
            hmap.put("lost",String.valueOf(lost1));
            reference_1.updateChildren(hmap).addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    Toast.makeText(GameActivity.this,"scores updated for lost !",Toast.LENGTH_SHORT).show();
                }
            });*/
        }
        if(status.equals("won"))
        {
            int won1=won+1;
            reference_1.child("won").setValue(String.valueOf(won1));
            /*HashMap hmap=new HashMap();
            hmap.put("won",String.valueOf(won1));
            reference_1.updateChildren(hmap).addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    Toast.makeText(GameActivity.this,"scores updated !",Toast.LENGTH_SHORT).show();
                }
            });*/
        }

        /* code added by anagha 24 may db update for game lost ends*/
        return true;
    }

    public void PlayAgain() {
        //resets the board
        if (isWifi) {
            playerTurn = multiplayer == PLAYER;
        } else {
            playerTurn = true;
        }
        ViewGroup cols =(ViewGroup) findViewById(R.id.wrapper);
        for(int i = 0, k = 0; i < 6 ;k++){
            View child = cols.getChildAt(k);
            if (!(child instanceof ViewGroup)){continue;}
            ViewGroup col = (ViewGroup) child;
            for(int j = 0 ; j < col.getChildCount() ; j++){
                col.getChildAt(j).setBackground(getResources().getDrawable(R.drawable.white_circle));
            }
            i++;
        }

        //resets the board-array
        for (int i = 0 ; i < 6 ; i++) {Arrays.fill(gameBoard[i], 0);}

        // hide the button and textview
        findViewById(R.id.btnPlayAgain).setVisibility(View.GONE);
        findViewById(R.id.btnUserProfile).setVisibility(View.GONE);

        TextView winner = findViewById(R.id.txtWinner);
        winner.setText(null);
        winner.setVisibility(View.GONE);
        //player's turn to play

    }

// ----------------------- Class listener starts -------------------------------------------------//

    public class HandleListener implements View.OnClickListener {

        int colId, colNumber, player;

        public HandleListener(int i) {
            colNumber = i;

            // TO DO : add constants for players
            player = PLAYER;
            colId = getColumnId(i);

        }

        @Override
        public void onClick(View v) {
            int rowNumber = getFirstAvailableRow(colNumber);
            if (!isWifi) {
                //if there is no row left to fill, return
                if (getFirstAvailableRow(colNumber) == -1 || !GameActivity.playerTurn) {
                    Toast.makeText(getApplicationContext(), "Cannot play in this column", Toast.LENGTH_SHORT).show();
                    return;
                }
                //play in given column
                play(player, rowNumber, colNumber, GameActivity.this.gameBoard);
                //if the game is done, do nothing (no need for the program to play)
                if(announceWinner(rowNumber,colNumber)) return;
                //Player's turn is done -> now program's turn
                GameActivity.playerTurn = false;
                //launch program's thinking thread (thread is used to avoid blocking the UI while doing calculations)
                new ComputerTurn().execute();
            } else {
                // This else is for multiplayer users. This will first save entry to firebase db and do further calculation in setGameId function
                if (getFirstAvailableRow(colNumber) == -1 || !GameActivity.playerTurn) {
                    Toast.makeText(getApplicationContext(), "Cannot play in this column or Not your turn", Toast.LENGTH_SHORT).show();
                    return;
                }
                FirebaseDatabase.getInstance().getReference().child("games")
                        .child(gameId)
                        .child(rowNumber + "_" + colNumber)
                        .setValue(GameActivity.multiplayer);
            }

        }
    }


//-------------------------- class listener ends here -----------------------------------------------------------------------------//

///---------------------- Thread for comoputer's turn starts here--------------------------------------------------------------------- ///

private class ComputerTurn extends AsyncTask<Void,Void,Integer>{

        //parameter to determine depth of recursion in minimax algorith , also determine dfficulty
        int maxDepth;

        //loops over each column and selects the one move out of all best possible move;
        private int minimax(){
            List<Integer> result = new ArrayList<>();  // list to hold all possible optimal moves
            int max = -10000 , m;

            int computer = Constants.COMPUTER;
            for(int col=0;col<6 ; col++){
                int row = getFirstAvailableRow(col);
                if(row==-1) continue;;
                gameBoard[col][row] = computer;
                m = minimax_recursion(PLAYER,0,row,col);
                gameBoard[col][row] = 0;
                if(m > max || max == -1000000){
                    max = m;
                    result.clear();
                    result.add(col);
                }else if (m ==  max){
                    result.add(col);
                }
            }

            //pick a random column from the list (to make the behavior unpredictable)
            return result.get(new Random().nextInt(result.size()));
        }

        private int minimax_recursion(int currPlayer, int depth,int currRow, int currCol){
            if(isTerminal(currRow,currCol)||depth==maxDepth)
                return checkWinner(currRow,currCol,depth);

            int min = 1000000, max = -1000000, m;
            int cmax = 0, cmin = 0, c = 0;

            //current player will loop over all the column to find optimal position
            for(int col=0;col<6;col++){
                int row = getFirstAvailableRow(col);
                if(row==-1) continue;
                gameBoard[col][row] = currPlayer;
                m = minimax_recursion(currPlayer^3 , depth+1,row,col);
                c++;
                gameBoard[col][row] = 0;

                if (m == max && max > 0) {
                    cmax++;
                } else {
                    cmax = 0;
                }
                if (m == min && min < 0) {
                    cmin++;
                } else {
                    cmin = 0;
                }

                max = Math.max(m, max);
                min = Math.min(m, min);

            }

            if(maxDepth > 4){   //to adjust difficulty
                if(c == cmax){  //if any play from here leads to winning, give greater maximum value (force playing here)
                    max = 200;
                }
                if(c == cmin){  //if any play from here leads to losing, give very low -ve value (force playing away)
                    min = -200;
                }
            }

            if(currPlayer == PLAYER){
                return min;
            }else{
                return max;
            }

        }


    //get the value of the current board state (the testing board this thread has, not the real board)
    //+ve number ==> good for the Computer (Computer wins), -ve number ==> bad for the Computer (computer loses)
    //0 ==> game not done or tie (nuetral state)
         private int checkWinner(int currRow,int currCol,int depth) {
             int state = getWinner(currCol,currRow);	//get the state
             //if computer loses returns -ve number that is lower as the computer loses sooner
             if(state == PLAYER){
                 return -maxDepth - 1 + depth;

                 //if the computer wins, returns +ve number that is greater as the computer wins sooner
             }else if(state == Constants.COMPUTER){
                 return maxDepth + 1 - depth;
             }

             //in case no one wins or tie
             return 0;

         }

    //checks the board and tells if the game is done or not
         private boolean isTerminal(int currRow,int currCol){
        //if there is a winner or a tie, then the game is done
            return (getWinner(currCol,currRow) != 0);
    }


    @Override
        protected Integer doInBackground(Void... params) {

                maxDepth = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getInt(Constants.DIFFICULTY,Constants.MODE_MEDIUM);
                return minimax();
        }

    @Override
    protected void onPostExecute(Integer col) {
        int row = getFirstAvailableRow(col);
        play(Constants.COMPUTER, row,col,  GameActivity.this.gameBoard);

        //if the game is done (Computer won or tie) return, no need to give the player access to the board
        if (announceWinner(row,col)) return;

        //player's turn, the listener will now play in the place he chooses
        GameActivity.playerTurn = true;
    }
}

}




