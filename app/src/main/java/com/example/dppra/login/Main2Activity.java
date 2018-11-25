package com.example.dppra.login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Main2Activity extends AppCompatActivity {
    FirebaseAuth auth;
    Button logout, userbtn, button;
    TextView username, textView;
    ImageView imageView;
    public String fileToFirebase;
    public static final int REQUEST_IMAGE = 100;
    public static final int REQUEST_PERMISSION = 200;
    private String imageFilePath = "";
    public FirebaseVisionFaceDetectorOptions options =
            new FirebaseVisionFaceDetectorOptions.Builder()
                    .setPerformanceMode(FirebaseVisionFaceDetectorOptions.FAST)
                    .setContourMode(FirebaseVisionFaceDetectorOptions.NO_CONTOURS)
                    .build();
    public File takePhoto;


    private DatabaseReference mDatabase,aDatabase,UID,TS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        aDatabase = FirebaseDatabase.getInstance().getReference("datas");
        UID = aDatabase.child(auth.getUid());


        username = (TextView) findViewById(R.id.username);
        button = findViewById(R.id.button);
        imageView = findViewById(R.id.image);
        textView = findViewById(R.id.textView);


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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        }


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCameraIntent();
            }
        });


        logout = (Button) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.equals(username.getText().toString(), "Welcome : Guest")) {
                    String UID = auth.getCurrentUser().getUid();
                    System.out.println(UID);
                    mDatabase.child(UID).removeValue();
                    Toast.makeText(Main2Activity.this, "Success!", Toast.LENGTH_SHORT).show();
                    auth.signOut();
                    startActivity(new Intent(Main2Activity.this, MainActivity.class));
                } else {
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
                startActivity(new Intent(Main2Activity.this, Main5Activity.class));
            }
        });

    }

    private void openCameraIntent() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getPackageManager()) != null) {

            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            Uri photoUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile);
            takePhoto = photoFile;
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

            startActivityForResult(pictureIntent, REQUEST_IMAGE);
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Thanks for granting Permission", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageView.setImageURI(Uri.parse(imageFilePath));
        textView.setText("Loading");
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
//                imageView.setImageURI(Uri.parse(imageFilePath));
                FirebaseVisionFaceDetector detector = FirebaseVision.getInstance()
                        .getVisionFaceDetector(options);
                FirebaseVisionImage image = null;
                try {
                    Uri photoUri = FileProvider.getUriForFile(this, getPackageName() +".provider", takePhoto);

                    image = FirebaseVisionImage.fromFilePath(this, photoUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Task<List<FirebaseVisionFace>> result =
                        detector.detectInImage(image)
                                .addOnSuccessListener(
                                        new OnSuccessListener<List<FirebaseVisionFace>>() {
                                            @Override
                                            public void onSuccess(List<FirebaseVisionFace> faces) {
                                                // Task completed successfully
                                                int a = 0;
                                                for (FirebaseVisionFace face : faces) {
                                                    a++;
                                                    Rect bounds = face.getBoundingBox();
                                                    System.out.println(a);
                                                }
                                                textView.setText("Total face is " + a);
                                                System.out.println("total face is " + a);
//                                                System.out.println(faces.size());
                                                System.out.println("pass");
                                                String timeStamp = new SimpleDateFormat("dd_MM_yy", Locale.getDefault()).format(new Date());
                                                long yourmilliseconds = System.currentTimeMillis();
                                                String sec = yourmilliseconds+"";
                                                UID.child(timeStamp).child(sec).setValue(a);
                                            }
                                        })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Task failed with an exception
                                                // ...
                                                System.out.println("boom");
                                            }
                                        });
            }
            else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "You cancelled the operation", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private File createImageFile() throws IOException{

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        imageFilePath = image.getAbsolutePath();

        return image;
    }
}
