package com.example.dppra.login;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Main2Activity extends AppCompatActivity {
    FirebaseAuth auth;
    Button logout,userbtn;
    TextView username;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        username = (TextView) findViewById(R.id.username);



        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(Main2Activity.this, MainActivity.class));
            finish();
        } else {
            final String UID = auth.getCurrentUser().getUid();
            DatabaseReference ref = mDatabase.child(UID);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String USN = dataSnapshot.getValue().toString();
                    username.setText("Welcome : " + USN);
                    username.setVisibility(View.VISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });





        }


        logout = (Button) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.equals(username.getText().toString(),"Welcome : Guest")){
                    String UID = auth.getCurrentUser().getUid();
                    System.out.println(UID);
                    mDatabase.child(UID).removeValue();
                    Toast.makeText(Main2Activity.this, "Success!", Toast.LENGTH_SHORT).show();
                    auth.signOut();
                    startActivity(new Intent(Main2Activity.this, MainActivity.class));
                }else {
                    auth.signOut();
                    startActivity(new Intent(Main2Activity.this, MainActivity.class));
                }


//                finish();

            }
        });
        userbtn = (Button) findViewById(R.id.user);
        userbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Main2Activity.this,Main5Activity.class));
            }
        });

    }
}
