package com.example.admin.testingv1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserInfo extends AppCompatActivity implements View.OnClickListener {
    private String email;
    private Button requestBtn;
    private TextView unfriendBtn;
    private FirebaseAuth firebaseAuth;
    private String userEmail;
    private String Uid;
    private DatabaseReference myRef;
    private TextView email_;
    private boolean friend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        Intent incomingIntent = getIntent();
        email = incomingIntent.getStringExtra("Email");

        email_ = (TextView)findViewById(R.id.userEmail);
        email_.setText(email);
        requestBtn = (Button) findViewById(R.id.requestBtn);
        unfriendBtn = (TextView) findViewById(R.id.unFriendBtn);
        userEmail = firebaseAuth.getCurrentUser().getEmail();
        Uid = firebaseAuth.getCurrentUser().getUid();
        firebaseAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference().child("Users");

        if(isFriend()){
            requestBtn.setText("Send request to view calendar");
            unfriendBtn.setText("Unfriend");
            unfriendBtn.setOnClickListener(this);
        } else {
            requestBtn.setText("Send friend request");
        }

        requestBtn.setOnClickListener(this);

    }

    private boolean isFriend (){
        myRef.child(userEmail).child(Uid).child("Friends").child(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    friend = true;
                } else {
                    //user does not exist
                    friend = false;
                }
            }

            @Override
            public void onCancelled(FirebaseError arg0) {
                Toast.makeText(UserInfo.this, arg0.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return friend;
    }

    @Override
    public void onClick(View view) {
        if(view == requestBtn){
            if (requestBtn.getText().equals("Send friend request")){
                String requestId = myRef.child(email).child("Request").push().getKey();
                Request request = new Request (userEmail, "friendRequest", requestId);
                myRef.child(email).child("Request").child(requestId).setValue(request);
            } else {
                String requestId = myRef.child(email).child("Request").push().getKey();
                Request request = new Request (userEmail, "ViewCalendarRequest", requestId);
                myRef.child(email).child("Request").child(requestId).setValue(request);
            }
            Toast.makeText(UserInfo.this, "Request sent!", Toast.LENGTH_SHORT).show();
        }

        if(view == unfriendBtn){
            myRef.child(userEmail).child("Friends").child(email).removeValue();
            myRef.child(email).child("Friends").child(userEmail).removeValue();
            Toast.makeText(UserInfo.this, "Unfriended.", Toast.LENGTH_SHORT).show();
        }
    }
}