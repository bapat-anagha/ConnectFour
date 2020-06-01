package com.example.connectfour;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class userHomeActivity extends AppCompatActivity {
TextView tv_user;
TextView tv_won;
TextView tv_lost;
TextView tv_rank;
Button btn_play;
Button btn_logout;
int won=0;
int lost=0;
private FirebaseAuth mAuth;

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        FirebaseAuth.getInstance().signOut();
        finish();
        Intent intent = new Intent(userHomeActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_userhome);
        tv_user=findViewById(R.id.tv_user);
        tv_lost=findViewById(R.id.tv_lost);
        tv_won=findViewById(R.id.tv_won);
        tv_rank=findViewById(R.id.tv_rank);
        btn_play=findViewById(R.id.btn_play);
        btn_logout=findViewById(R.id.btn_logout);
        mAuth = FirebaseAuth.getInstance();
        showUserData();

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                finishAffinity();
                Intent intent = new Intent(userHomeActivity.this, loginActivity.class);
                startActivity(intent);
            }
        });

        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(userHomeActivity.this," Button play",Toast.LENGTH_SHORT).show();
                //updateScores();
                startActivity(new Intent(userHomeActivity.this, MainActivity.class));
            }
        });
    }

       private void showUserData() {
        //Intent intent=getIntent();
        //String username=intent.getStringExtra("username");
        //String pass=intent.getStringExtra("password");

=======
       /*private void showUserData2() {
>>>>>>> Stashed changes
        String uid = mAuth.getCurrentUser().getUid();
        DatabaseReference reference2= FirebaseDatabase.getInstance().getReference().child("Score").child(uid);

        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    //String lost=dataSnapshot.child("lost").getValue().toString();
                    tv_lost.setText(dataSnapshot.child("lost").getValue().toString());
                    tv_rank.setText(dataSnapshot.child("ranking").getValue().toString());
                    tv_user.setText(dataSnapshot.child("username").getValue().toString());
                    tv_won.setText(dataSnapshot.child("won").getValue().toString());
<<<<<<< Updated upstream

                    //Toast.makeText(getApplicationContext(),"lost count "+lost,Toast.LENGTH_SHORT).show();

=======
                    updateRanking();
>>>>>>> Stashed changes
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }*/

<<<<<<< Updated upstream
    private void updateScores() {
=======


    private void showUserData() {
            String uid = mAuth.getCurrentUser().getUid();
            DocumentReference documentReference = fstore.collection("Scores").document(uid);
            documentReference.addSnapshotListener(userHomeActivity.this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    tv_lost.setText(documentSnapshot.getString("lost"));
                    tv_user.setText(documentSnapshot.getString("username"));
                    tv_won.setText(documentSnapshot.getString("won"));

                    updateRanking();
                }
            });
    }


    /*private void updateScores() {
>>>>>>> Stashed changes
        //Toast.makeText(this,"in button play",Toast.LENGTH_SHORT).show();
        String uid = mAuth.getCurrentUser().getUid();
        DatabaseReference reference2= FirebaseDatabase.getInstance().getReference().child("Score").child(uid);

        won=Integer.parseInt(tv_won.getText().toString());
        lost=Integer.parseInt(tv_lost.getText().toString());

        won=won+1;
        reference2.child("won").setValue(String.valueOf(won));
        tv_won.setText(String.valueOf(won));
<<<<<<< Updated upstream

=======
    }*/

    private void updateRanking()
    {
        FirebaseFirestore.getInstance()
                .collection("Scores")
                .orderBy("won", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snapshotList=queryDocumentSnapshots.getDocuments();
                        for(DocumentSnapshot snapshot: snapshotList)
                        {
                            count_rank++;
                           // if(snapshot.getString("username").equals(tv_user.getText().toString()))
                               // tv_rank.setText(count_rank);
                           // break;
                            String rank=tv_user.getText().toString();
                            if(rank.equals(snapshot.getString("username")))
                            {
                                tv_rank.setText(String.valueOf(count_rank));
                            }
                            Log.d("userHomeActivity: ","on success of "+rank+" data :"+snapshot.getString("username"));
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(userHomeActivity.this,"fetching ranking failed ",Toast.LENGTH_SHORT).show();
                        Log.d("userHomeActivity: ","Fetching and calculating ranking failed");
                    }
                });
>>>>>>> Stashed changes

    }
}
