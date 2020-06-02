package com.example.connectfour.wifi;

import android.util.Log;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.connectfour.User;
import com.example.connectfour.databinding.UserListItemBinding;

import java.io.IOException;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.connectfour.Constants.FIREBASE_CLOUD_FUNCTIONS_BASE;
import static com.example.connectfour.Util.getCurrentUserId;

public class Holder extends RecyclerView.ViewHolder {
    public UserListItemBinding binding;

    public Holder(View itemView) {
        super(itemView);
        binding = DataBindingUtil.bind(itemView);
        binding.invite.setOnClickListener(v -> {
            DatabaseReference db = FirebaseDatabase.getInstance().getReference();
            db.child("users")
                    .child(getCurrentUserId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d("sending notification-->", String.valueOf(dataSnapshot));
                            User me = dataSnapshot.getValue(User.class);

                            OkHttpClient client = new OkHttpClient();


                            String to = binding.getUser().getPushId();

                            Request request = new Request.Builder()
                                    .url(String
                                            .format("%s/sendNotification?to=%s&fromPushId=%s&fromId=%s&fromName=%s&type=%s",
                                                    FIREBASE_CLOUD_FUNCTIONS_BASE,
                                                    to,
                                                    me.getPushId(),
                                                    getCurrentUserId(),
                                                    me.getUsername(),
                                                    "invite"))
                                    .build();
                            Log.d("sending request-->", String.valueOf(request));
                            Log.d("to", to);
                            Log.d("fromId", getCurrentUserId());
                            Log.d("fromName", me.getUsername());
                            Log.d("", "");
                            client.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Log.d("errorrrr--->","");
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    Log.d("success--->","");
                                    response.body().close();
                                }
                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        });
    }
}
