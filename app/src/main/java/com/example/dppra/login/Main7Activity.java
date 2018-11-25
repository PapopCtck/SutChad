package com.example.dppra.login;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Main7Activity extends AppCompatActivity {
    TableLayout tableLayout;
    TableRow row;
    TextView text1,text2;
    FirebaseAuth auth;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main7);
        tableLayout = (TableLayout) findViewById(R.id.simpleTableLayout);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Stats");

        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#59CB7F")));
        View decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        Intent in = getIntent();
        String getname = in.getStringExtra("itemname");


        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("datas");
        DatabaseReference ref = mDatabase.child(auth.getUid());
        DatabaseReference rec = ref.child(getname);
        rec.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    String name = ds.getKey();
                    String value = ds.getValue().toString();
                    long l = Long.parseLong(name);
                    System.out.println(name);
                    System.out.println(value);
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    Date resultdate = new Date(l);
                    String date = sdf.format(resultdate);
                    row = new TableRow(getApplicationContext());
                    text1 = new TextView(getApplicationContext());
                    text2 = new TextView(getApplicationContext());
                    text1.setGravity(Gravity.CENTER);
                    text1.setTextColor(Color.parseColor("#00B1FF"));
                    text2.setGravity(Gravity.CENTER);
                    text2.setTextColor(Color.parseColor("#000000"));
                    text1.setText(date);
                    text2.setText(value);
                    row.setPadding(0,50,0,50);
                    row.addView(text1);
                    row.addView(text2);
                    tableLayout.addView(row);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
