package com.example.expensemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class edit_profile extends AppCompatActivity {

    private static final String TAG ="edit_profile";

    private static final int RESULT_LOAD_IMAGE = 1;

    //View Declarations
    EditText e1, e2, e3, e4, e5;
    Spinner sp;
    Button b1;
    ImageView imageView;
    ProgressDialog pd;

    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore userDatabase;
    private StorageReference storageReference;

    //Variable Declarations
    private String fname, lname, income, mobile, email, currency, edit_fname, edit_lname, edit_income, edit_mobile, edit_email, edit_currency, downloadImage;
    Uri profile_pic, selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        userDatabase = FirebaseFirestore.getInstance();
        final String id = firebaseAuth.getCurrentUser().getUid();

        //EditTexts
        e1 = (EditText)findViewById(R.id.e1);
        e2 = (EditText)findViewById(R.id.e2);
        e3 = (EditText)findViewById(R.id.e3);
        e4 = (EditText)findViewById(R.id.e4);
        e5 = (EditText)findViewById(R.id.e5);

        //Spinner
        sp = (Spinner)findViewById(R.id.spinner);

        //Buttons
        b1 = (Button)findViewById(R.id.b1);

        //ImageView
        imageView = (ImageView)findViewById(R.id.profile_pic);

        //Progress Dialog
        pd = new ProgressDialog(edit_profile.this);

        pd.setTitle("Loading...");
        pd.show();

        //Retrieving data to display
        userDatabase.collection("users").document(id).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        fname = documentSnapshot.getString("fname");
                        lname = documentSnapshot.getString("lname");
                        income = documentSnapshot.getString("income");
                        mobile = documentSnapshot.getString("mobile");
                        email = documentSnapshot.getString("email");
                        e1.setText(fname);
                        e2.setText(lname);
                        e3.setText(income);
                        e4.setText(mobile);
                        e5.setText(email);
                        currency = documentSnapshot.getString("currency");
                        if(currency.equals("₹")){sp.setSelection(0);}
                        else if(currency.equals("$")){sp.setSelection(1);}
                        else if(currency.equals("£")){sp.setSelection(2);}
                        else if(currency.equals("€")){sp.setSelection(3);}
                        downloadImage = documentSnapshot.getString("downloadImage");
                        profile_pic = Uri.parse(downloadImage);
                        Picasso.with(edit_profile.this).load(profile_pic).into(imageView);
                        Log.d(TAG,"Successfully retrieved data from database.");
                        pd.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"Failed to retrieve data from database.");
                        pd.dismiss();
                    }
                });

        //To select new image from external storage
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent,RESULT_LOAD_IMAGE);
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Retrieve values from EditTexts
                edit_fname = e1.getText().toString().trim();
                edit_lname = e2.getText().toString().trim();
                edit_income = e3.getText().toString().trim();
                edit_mobile = e4.getText().toString().trim();
                edit_email = e5.getText().toString().trim();
                edit_currency = sp.getSelectedItem().toString().substring(4,5);

                int a = 0, b = 0, c = 0, d = 0, e = 0;

                if (edit_fname.isEmpty()) {
                    e1.requestFocus();
                    e1.setError("CANNOT BE LEFT EMPTY");
                } else if (!edit_fname.matches("^[A-Z]+[a-zA-Z]*$")) {
                    e1.requestFocus();
                    e1.setError("ENTER VALID NAME WITH ONLY FIRST ALPHABET CAPITAL AND NO SPACES, NUMBERS OR SPECIAL CHARACTERS");
                } else {
                    a = 1;
                }

                if (edit_lname.isEmpty()) {
                    e2.requestFocus();
                    e2.setError("CANNOT BE LEFT EMPTY");
                } else if (!edit_lname.matches("^[A-Z]+[a-zA-Z]*$")) {
                    e2.requestFocus();
                    e2.setError("ENTER VALID NAME WITH ONLY FIRST ALPHABET CAPITAL AND NO SPACES, NUMBERS OR SPECIAL CHARACTERS");
                } else {
                    b = 1;
                }

                if(edit_income.isEmpty()){
                    e3.requestFocus();
                    e3.setError("CANNOT BE LEFT EMPTY");
                }
                else if (edit_income.equals("0")) {
                    e2.requestFocus();
                    e3.setError("CANNOT BE 0");
                }
                else{
                    c=1;
                }

                if(edit_mobile.isEmpty()) {
                    e4.requestFocus();
                    e4.setError("CANNOT BE LEFT EMPTY");
                }
                else if(!edit_mobile.matches("^(\\+91[\\-\\s]?)?[0]?(91)?[789]\\d{9}$")) {
                    e4.requestFocus();
                    e4.setError("ENTER VALID MOBILE NUMBER");
                }
                else {
                    d = 1;
                }

                if(imageView.getDrawable() == null){
                    AlertDialog.Builder ad = new AlertDialog.Builder(edit_profile.this);
                    ad.setMessage("Add a profile picture.");
                    ad.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    ad.show();
                }
                else{
                    e = 1;
                }

                if(a==1 && b==1 && c==1 && d==1 && e==1){

                    pd.setMessage("Please wait, storing data on server.");
                    pd.show();

                    if(selectedImage != null) {
                        final StorageReference childStorage = storageReference.child("User_Profile").child(id).child(selectedImage.getLastPathSegment());
                        childStorage.putFile(selectedImage)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        childStorage.getDownloadUrl()
                                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        String downloadImage = uri.toString();
                                                        uploadData(edit_fname, edit_lname, edit_currency, edit_mobile, edit_email, edit_income, downloadImage);
                                                        pd.dismiss();
                                                        Log.d(TAG, "Successfully updated user data.");
                                                        AlertDialog.Builder ad = new AlertDialog.Builder(edit_profile.this);
                                                        ad.setMessage("Profile updated successfully.");
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
                                                        Log.d(TAG, "Failed to get download url for profile picture");
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd.dismiss();
                                        Log.d(TAG, "Failed to upload profile picture to storage");
                                    }
                                });
                    }
                    else{
                        uploadData(edit_fname, edit_lname, edit_currency, edit_mobile, edit_email, edit_income, downloadImage );
                        pd.dismiss();
                        Log.d(TAG, "Successfully updated user data.");
                        AlertDialog.Builder ad = new AlertDialog.Builder(edit_profile.this);
                        ad.setMessage("Profile updated successfully.");
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
            }
        });

    }

    private void uploadData(String fname, String lname, String currency, String mobile, String email, String income, String downloadImage) {
        String id = firebaseAuth.getCurrentUser().getUid();

        Map<String, Object> user = new HashMap<>();
        user.put("fname",fname);
        user.put("lname",lname);
        user.put("currency",currency);
        user.put("mobile",mobile);
        user.put("email",email);
        user.put("income", income);
        user.put("downloadImage",downloadImage);
        userDatabase.collection("users").document(id).update(user)
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
    public void onBackPressed(){
        AlertDialog.Builder ad = new AlertDialog.Builder(edit_profile.this);
        ad.setMessage("There may be unsaved changes. Are you sure you want to quit?");
        ad.setCancelable(false);
        ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                edit_profile.super.finish();
            }
        });
        ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        ad.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final ImageView profile_pic = (ImageView)findViewById(R.id.profile_pic);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data){
            selectedImage = data.getData();
            imageView.setImageURI(selectedImage);
        }
    }
}
