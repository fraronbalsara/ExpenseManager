package com.example.expensemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class report_a_bug extends AppCompatActivity {

    private static final String TAG ="repprt_a_bug";

    private static final int RESULT_LOAD_IMAGE = 1;
    private Uri selectedImage;

    private TextView image_link;

    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore userDatabase;
    private StorageReference storageReference;
    String id;

    //Progress Dialog
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_a_bug);

        //Views
        final EditText sub = findViewById(R.id.sub);
        final EditText des = findViewById(R.id.des);
        image_link = findViewById(R.id.image_link);
        Button add_ss = findViewById(R.id.add_ss);
        Button submit = findViewById(R.id.submit);

        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        userDatabase = FirebaseFirestore.getInstance();
        final String id = firebaseAuth.getCurrentUser().getUid();

        //Progress Dialog
        pd = new ProgressDialog(report_a_bug.this);

        //To select new image from external storage
        add_ss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent,RESULT_LOAD_IMAGE);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Retrieve values from EditTexts
                final String subject = sub.getText().toString().trim();
                final String description = des.getText().toString().trim();

                int a=0,b=0;

                if (subject.isEmpty()) {
                    sub.requestFocus();
                    sub.setError("CANNOT BE LEFT EMPTY");
                }
                else{a=1;}
                if (description.isEmpty()) {
                    des.requestFocus();
                    des.setError("CANNOT BE LEFT EMPTY");
                }
                else{b=1;}

                if(a == 1 && b == 1 && selectedImage != null){

                    pd.setTitle("Sending bug report...");
                    pd.show();

                    final String report_id = UUID.randomUUID().toString();

                    final StorageReference childStorage = storageReference.child("report_screenshots").child(report_id).child(selectedImage.getLastPathSegment());
                    childStorage.putFile(selectedImage)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    childStorage.getDownloadUrl()
                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String downloadImage = uri.toString();
                                                    uploadData(subject, description, downloadImage, id, report_id);
                                                    pd.dismiss();
                                                    Log.d(TAG, "Successfully sent bug report.");
                                                    AlertDialog.Builder ad = new AlertDialog.Builder(report_a_bug.this);
                                                    ad.setMessage("Report sent successfully.");
                                                    ad.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            Intent intent = getIntent();
                                                            finish();
                                                            startActivity(intent);
                                                        }
                                                    });
                                                    ad.show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    pd.dismiss();
                                                    Log.d(TAG, "Failed to retrieve download url for screenshot.");
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Log.d(TAG, "Failed to upload screenshot.");
                                }
                            });
                }
                else if(a ==1 && b == 1 && selectedImage == null){

                    final String report_id = UUID.randomUUID().toString();

                    uploadData(subject, description, "null", id, report_id);
                    pd.dismiss();
                    Log.d(TAG, "Successfully sent bug report.");
                    AlertDialog.Builder ad = new AlertDialog.Builder(report_a_bug.this);
                    ad.setMessage("Report sent successfully.");
                    ad.setCancelable(false);
                    ad.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        }
                    });
                    ad.show();
                }

            }
        });

    }

    public void uploadData(String subject, String description, String downloadImage, String user_id, String report_id){
        Map<String, Object> report = new HashMap<>();
        report.put("subject",subject);
        report.put("description",description);
        report.put("screenshot",downloadImage);
        report.put("user-id",user_id);
        report.put("report-id",report_id);
        userDatabase.collection("reports").document(report_id).set(report)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,"Successfully stored data to database.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"Failed to store data to database.");
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final ImageView profile_pic = (ImageView)findViewById(R.id.profile_pic);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data){
            image_link.setText("Screenshot attached");
            selectedImage = data.getData();
        }
    }

}
