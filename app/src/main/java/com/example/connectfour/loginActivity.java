package com.example.connectfour;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import static com.example.connectfour.Util.getCurrentUserId;
import static com.example.connectfour.Util.savePushToken;

public class loginActivity extends AppCompatActivity {
    Button btn_login1;
    Button btn_signup1;
    EditText ed_username1;
    EditText ed_password1;
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    private FirebaseAuth mAuth;

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finishAffinity();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        btn_login1=findViewById(R.id.btn_login1);
        btn_signup1=findViewById(R.id.btn_signup1);
        ed_username1=findViewById(R.id.ed_username1);
        ed_password1=findViewById(R.id.ed_password1);
        mAuth = FirebaseAuth.getInstance();

        btn_signup1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),SignupActivity.class);
                startActivity(intent);
            }
        });


        btn_login1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate_username() && validate_password())
                {

                    mAuth.signInWithEmailAndPassword(ed_username1.getText().toString().trim() , ed_password1.getText().toString().trim())
                            .addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Log.d("test", "login page");
                                    //if (!isAnonymous()) {
                                    String refreshedToken = FirebaseInstanceId.getInstance().getToken();

                                    savePushToken(refreshedToken, getCurrentUserId());

                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(),"Please enter the correct Email ID/password.",Toast.LENGTH_LONG).show();
                                }


                            });
                }
            }
        });

    }

    private boolean validate_username()
    {
        String username=ed_username1.getText().toString().trim();
        String noWhiteSpace = ".*\\s.*";
        if(username.isEmpty())
        {
            ed_username1.setError("Email ID cannot be empty!");
            ed_username1.requestFocus();
            return false;
        }else if (username.matches(noWhiteSpace)) {
            ed_username1.setError("Spaces are not allowed in username");
            ed_username1.requestFocus();
            return false;
        } else
        {
            ed_username1.setError(null);
            return true;
        }
    }

    private boolean validate_password()
    {
        String password=ed_password1.getText().toString().trim();
        String noWhiteSpace = ".*\\s.*";
        if(password.isEmpty())
        {
            ed_password1.setError("Password cannot be empty!");
            ed_password1.requestFocus();
            return false;
        } else
        {
            ed_password1.setError(null);
            return true;
        }
    }

    public void loginUser(View view)
    {
        if(validate_username() && validate_password())
        {
            Toast.makeText(this,"Username/pass are correct",Toast.LENGTH_LONG).show();

        }
        else
        {
            Toast.makeText(this,"Username/pass not correct",Toast.LENGTH_LONG).show();
        }

    }


}
