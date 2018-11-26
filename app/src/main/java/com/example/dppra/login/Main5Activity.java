package com.example.dppra.login;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Main5Activity extends AppCompatActivity {
    ImageButton exitbtn;
    FirebaseAuth auth;
    private DatabaseReference mDatabase;
    TextView username;
    ConstraintLayout Stats,AccSett,Logout;
    String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);
        View decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        exitbtn = (ImageButton) findViewById(R.id.exit);

        exitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Main5Activity.this, Main2Activity.class));
            }
        });
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        username = (TextView) findViewById(R.id.oldpassword);
        username.setText("Loading....");
        username.setVisibility(View.VISIBLE);


        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(Main5Activity.this, MainActivity.class));
            finish();
        } else {
            final String UID = auth.getCurrentUser().getUid();
            DatabaseReference ref = mDatabase.child(UID);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String USN = dataSnapshot.getValue().toString();
                    username.setText(USN);
                    user = USN;

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }
        Stats = (ConstraintLayout) findViewById(R.id.constraintLayout2);
        Stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String USN = username.getText().toString();
                Intent i = new Intent(Main5Activity.this,Main8Activity.class);
                i.putExtra("username",USN);
                i.putExtra("UID",auth.getUid());
                startActivity(i);
            }
        });
        AccSett = (ConstraintLayout) findViewById(R.id.constraintLayout3);
        AccSett.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.equals(user,"Guest")){
                    Toast.makeText(Main5Activity.this,"Guest cannot edit account!",Toast.LENGTH_SHORT).show();
                }
                else{
//                    System.out.println(user);
                    startActivity(new Intent(Main5Activity.this,Main6Activity.class));
                }

            }
        });
        Logout = (ConstraintLayout) findViewById(R.id.constraintLayout4);
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.equals(username.getText().toString(),"Guest")){
                    String UID = auth.getCurrentUser().getUid();
//                    System.out.println(UID);
                    mDatabase.child(UID).removeValue();
                    Toast.makeText(Main5Activity.this, "Success!", Toast.LENGTH_SHORT).show();
                    auth.signOut();
                    startActivity(new Intent(Main5Activity.this, MainActivity.class));
                }else {
                    auth.signOut();
                    startActivity(new Intent(Main5Activity.this, MainActivity.class));
                }
            }
        });
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}

