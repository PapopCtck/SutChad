package com.example.dppra.login;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Main6Activity extends AppCompatActivity {
    FirebaseAuth auth;
    private DatabaseReference mDatabase;
    EditText pw,usn;
    Button submit;
    ImageButton backbtn;
    String oldusn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main6);
        usn = findViewById(R.id.username);
        pw = findViewById(R.id.password);
        backbtn = findViewById(R.id.backbtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Main6Activity.this,Main5Activity.class));
            }
        });

        submit = findViewById(R.id.submit);

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        final String UID = auth.getCurrentUser().getUid();
        DatabaseReference ref = mDatabase.child(UID);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String USN = dataSnapshot.getValue().toString();
                usn.setText(USN);
                oldusn = USN;


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newpassword = pw.getText().toString();
                String newusn = usn.getText().toString();
                if(newpassword != "" && !newpassword.isEmpty()){
                    auth.getCurrentUser().updatePassword(newpassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"Changed password",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Main6Activity.this,Main5Activity.class));
                            }
                        }
                    });
                }
                if (oldusn == newusn){
                    startActivity(new Intent(Main6Activity.this,Main5Activity.class));
                }
                else{
                    mDatabase.child(UID).setValue(newusn).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getApplicationContext(),"Changed username",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Main6Activity.this,Main5Activity.class));
                        }
                    });
                }

            }
        });



        View decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(

                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
