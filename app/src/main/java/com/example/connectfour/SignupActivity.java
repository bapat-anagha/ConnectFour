package com.example.connectfour;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
<<<<<<< Updated upstream
=======
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
>>>>>>> Stashed changes
import com.google.firebase.iid.FirebaseInstanceId;

import static com.example.connectfour.Util.savePushToken;

//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {
    Button btn_login;
    Button btn_signup;
    EditText ed_username;
    EditText ed_password;
    EditText ed_confirm;
    EditText ed_email;
    private FirebaseAuth mAuth;
  //  FirebaseDatabase rootNode;
   // DatabaseReference reference;
   // DatabaseReference reference1;
    User user1;
    Score score;
<<<<<<< Updated upstream
=======

    /* 29 may code update starts*/
    private DocumentReference mDocRef;
    static boolean repeat_user = false;
    //ArrayList<User> doctorsList=new ArrayList<>();
    //ArrayList<User> docs = new ArrayList<User>();
    //ArrayAdapter docsAdapter;
    /* 29 may code update starts*/
>>>>>>> Stashed changes
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        btn_login=findViewById(R.id.btn_login);
        btn_signup=findViewById(R.id.btn_signup);
        ed_username=findViewById(R.id.ed_username);
        ed_password=findViewById(R.id.ed_password);
        ed_confirm=findViewById(R.id.ed_confirm);
        ed_email=findViewById(R.id.ed_email);
        //rootNode=FirebaseDatabase.getInstance();
        user1=new User();
        score=new Score();
       // reference=rootNode.getReference().child("User");
       // reference1=rootNode.getReference().child("Score");

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!validateform()) {
                    Toast.makeText(SignupActivity.this, "Error in the input values!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                String username = ed_username.getText().toString().trim();
                String pass = ed_password.getText().toString().trim();
                String email = ed_email.getText().toString().trim();
                //user1.setUsername(username);
                //user1.setPassword(pass);
                mAuth = FirebaseAuth.getInstance();

                mAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("test", "loginWithEmail: ");
                                String uid = mAuth.getCurrentUser().getUid();

                                User user = new User(username,pass,email);
                                FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                                        .setValue(user);

                                /*score.setWon("0");
                                score.setLost("0");
                                score.setRanking("0");
                                score.setUsername(username);
                                FirebaseDatabase.getInstance().getReference().child("Score").child(uid).setValue(score);*/

                                String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                                savePushToken(refreshedToken, uid);

<<<<<<< Updated upstream
                                Toast.makeText(getApplicationContext(), "Sign up is successful!", Toast.LENGTH_SHORT).show();
                                //Intent intent = new Intent(getApplicationContext(), userHomeActivity.class);
                                //intent.putExtra("username", ed_username.getText().toString());
                                //intent.putExtra("password", ed_password1.getText().toString());
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
=======
                                /*changes for 29 may starts*/
                                mDocRef = FirebaseFirestore.getInstance().document("Scores/" + uid);
                                HashMap<String, String> userDetails = new HashMap<>();
                                userDetails.put("won", "0");
                                userDetails.put("lost", "0");
                                userDetails.put("ranking", "0");
                                userDetails.put("username", username);
                                mDocRef.set(userDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(SignupActivity.this, "successfully added in Firestore!",
                                                Toast.LENGTH_SHORT).show();
                                        Log.d("SignunActivity: ","successfully initialized scores in Firestore!");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SignupActivity.this, "Failure to add in Firestore!",
                                                Toast.LENGTH_SHORT).show();
                                        Log.d("SignunActivity: ","Not able to initialize scores in Firestore!");
                                    }
                                });
                                /*changes for 29 may ends*/

                                Toast.makeText(SignupActivity.this, "Sign up is successful!", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
>>>>>>> Stashed changes
                                startActivity(intent);

                            }
                            else
                            {
                                Toast.makeText(SignupActivity.this, "User already exists! Please Login!", Toast.LENGTH_LONG).show();
                            }
                        });
            }
                        });
                btn_login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(this,"password is "+ ed_password.getText().toString(), Toast.LENGTH_SHORT);
                        Intent intent = new Intent(SignupActivity.this, loginActivity.class);
                        startActivity(intent);

                    }
                });


            }

            private boolean validateform() {

                String email = ed_email.getText().toString().trim();
                String username = ed_username.getText().toString().trim();
                String pswd = ed_password.getText().toString().trim();
                String confirm_pswd = ed_confirm.getText().toString().trim();
                String passwordCheck = "^" + "(?=.*[a-zA-Z0-9])" + "(?=.*[@#$%^&+=])" + ".{6,}" + "$";
                //String noWhiteSpace="\\A\\w{4,20}\\z";
                String noWhiteSpace = ".*\\s.*";
                if (email.isEmpty()) {
                    ed_email.setError("Email is required!");
                    ed_email.requestFocus();
                    return false;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    ed_email.setError("Enter a correct email ID!");
                    ed_email.requestFocus();
                    return false;
                } else if (username.isEmpty()) {
                    ed_username.setError("Username is required!");
                    ed_username.requestFocus();
                    return false;
                } else if ((username.length() < 4) || (username.length() >= 11)) {
                    ed_username.setError("Username should be between 4 to 10 characters long");
                    ed_username.requestFocus();
                    return false;
                } else if (username.matches(noWhiteSpace)) {
                    ed_username.setError("Spaces are not allowed in username");
                    ed_username.requestFocus();
                    return false;
                }  else if (pswd.isEmpty()) {
                    ed_password.setError("Password is required!");
                    ed_password.requestFocus();
                    return false;
                } else if (pswd.matches(noWhiteSpace)) {
                    ed_password.setError("Spaces are not allowed in password");
                    ed_password.requestFocus();
                    return false;
                } else if (!pswd.matches(passwordCheck)) {
                    ed_password.setError("Password is weak! Include atleast 1 special character.");
                    ed_password.requestFocus();
                    return false;
                } else if (pswd.length() < 6 || pswd.length() > 15) {
                    ed_password.setError("Password must be between 6 to 15 characters long!");
                    ed_password.requestFocus();
                    return false;
                } else if (confirm_pswd.isEmpty()) {
                    ed_confirm.setError("Re-entry of the Password is required!");
                    ed_confirm.requestFocus();
                    return false;
                } else if (!pswd.equals(confirm_pswd)) {
                    ed_confirm.setError("Password mismatch!");
                    ed_confirm.requestFocus();
                    return false;
                } else {

                    //String uid = mAuth.getCurrentUser().getUid();
                   /* DatabaseReference ref2=FirebaseDatabase.getInstance().getReference().child("users");
                    ref2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot ds : dataSnapshot.getChildren())
                            {
                               String chk_user=dataSnapshot.child("username").getValue().toString();
                               if(username.equals(chk_user))
                               {
                                  repeat_user=true;
                               }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    if(repeat_user==true)
                    {
                        ed_username.setError("This username exists! Please choose another username.");
                        ed_username.requestFocus();
                        return false;
                    }*/
                    return true;
                }

            }
        }
