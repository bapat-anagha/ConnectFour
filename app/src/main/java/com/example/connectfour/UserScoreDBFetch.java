package com.example.connectfour;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class UserScoreDBFetch {
    String uid;
    private FirebaseAuth mAuth1;
    private FirebaseFirestore fstore1;

    public UserScoreDBFetch(String uid) {
        this.uid = uid;
    }

    public Score fetchDetails() {
        mAuth1 = FirebaseAuth.getInstance();
        fstore1=FirebaseFirestore.getInstance();
        Score score=new Score();
        DocumentReference documentReference = fstore1.collection("Scores").document(uid);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        score.setLost("1");
                        score.setWon("2");
                        score.setUsername("aa");
                        score.setRanking("2");
                    }
                }
           }

    });
   return score;
    }
}

