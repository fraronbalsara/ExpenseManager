package com.example.expensemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import io.opencensus.tags.Tags;

public class register extends AppCompatActivity {
    private static final int RESULT_LOAD_IMAGE = 1;

    private static final String TAG ="register";

    //New Variable Declarations
    private static String fname,lname,email,password,rpassword,mobile,income,currency;
    private static Uri selectedImage;

    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore userDatabase;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //EditTexts
        final EditText e1 = (EditText)findViewById(R.id.e1);
        final EditText e2 = (EditText)findViewById(R.id.e2);
        final EditText e3 = (EditText)findViewById(R.id.e3);
        final EditText e4 = (EditText)findViewById(R.id.e4);
        final EditText e5 = (EditText)findViewById(R.id.e5);
        final EditText e6 = (EditText)findViewById(R.id.e6);
        final EditText e7 = (EditText)findViewById(R.id.e7);

        //Spinner
        final Spinner sp = (Spinner)findViewById(R.id.spinner);

        //Buttons
        Button b1 = (Button)findViewById(R.id.b1);

        //CheckBoxes
        final CheckBox c1 = (CheckBox)findViewById(R.id.c1);
        final CheckBox c2 = (CheckBox)findViewById(R.id.c2);

        //ImageView
        final ImageView profile_pic = (ImageView)findViewById(R.id.profile_pic);

        //Progress Dialog
        final ProgressDialog pd = new ProgressDialog(register.this);

        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        userDatabase = FirebaseFirestore.getInstance();

        //Show/Hide Password Checkbox
        c1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!c1.isChecked()){
                    e6.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                else{
                    e6.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

        //Show/Hide Re-enter Password Checkbox
        c2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!c2.isChecked()){
                    e7.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                else{
                    e7.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

        //Select Profile Picture from external storage
        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent,RESULT_LOAD_IMAGE);
            }
        });

        //Register Button
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Retrieve values from EditTexts
                fname = e1.getText().toString().trim();
                lname = e2.getText().toString().trim();
                income = e3.getText().toString().trim();
                mobile = e4.getText().toString().trim();
                email = e5.getText().toString().trim();
                password = e6.getText().toString().trim();
                rpassword = e7.getText().toString().trim();
                currency = sp.getSelectedItem().toString().substring(4,5);

                int a = 0, b = 0, c = 0, d = 0, e = 0, f = 0,g = 0,h = 0;

                if (fname.isEmpty()) {
                    e1.requestFocus();
                    e1.setError("CANNOT BE LEFT EMPTY");
                } else if (!fname.matches("^[A-Z]+[a-zA-Z]*$")) {
                    e1.requestFocus();
                    e1.setError("ENTER VALID NAME WITH ONLY FIRST ALPHABET CAPITAL AND NO SPACES, NUMBERS OR SPECIAL CHARACTERS");
                } else {
                    a = 1;
                }

                if (lname.isEmpty()) {
                    e2.requestFocus();
                    e2.setError("CANNOT BE LEFT EMPTY");
                } else if (!lname.matches("^[A-Z]+[a-zA-Z]*$")) {
                    e2.requestFocus();
                    e2.setError("ENTER VALID NAME WITH ONLY FIRST ALPHABET CAPITAL AND NO SPACES, NUMBERS OR SPECIAL CHARACTERS");
                } else {
                    b = 1;
                }

                if(income.isEmpty()){
                    e3.requestFocus();
                    e3.setError("CANNOT BE LEFT EMPTY");
                }
                else if (income.equals("0")) {
                    e2.requestFocus();
                    e3.setError("CANNOT BE 0");
                }
                else{
                    c=1;
                }

                if(mobile.isEmpty()) {
                    e4.requestFocus();
                    e4.setError("CANNOT BE LEFT EMPTY");
                }
                else if(!mobile.matches("^(\\+91[\\-\\s]?)?[0]?(91)?[789]\\d{9}$")) {
                    e4.requestFocus();
                    e4.setError("ENTER VALID MOBILE NUMBER");
                }
                else {
                    d = 1;
                }

                if (email.isEmpty()) {
                    e5.requestFocus();
                    e5.setError("CANNOT BE LEFT EMPTY");
                }
                else if (!email.matches("^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$")) {
                    e5.requestFocus();
                    e5.setError("ENTER VALID EMAIL");
                }
                else {
                    e = 1;
                }

                if (password.length() < 6) {
                    e6.requestFocus();
                    e6.setError("PASSWORD MUST BE AT LEAST 6 CHARACTERS");
                }
                else {
                    f = 1;
                }

                if (password.equals(rpassword)) {
                    g = 1;
                }
                else {
                    e7.requestFocus();
                    e7.setError("PASSWORD DOES NOT MATCH");
                }

                if(selectedImage == null){
                    AlertDialog.Builder ad = new AlertDialog.Builder(register.this);
                    ad.setMessage("Add a profile picture.");
                    ad.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    ad.show();
                }
                else{
                    h = 1;
                }

                if(a==1 && b==1 && c==1 && d==1 && e==1 && f==1 && g==1 && h==1){

                    pd.setMessage("Please wait, storing data on server.");
                    pd.show();

                    firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(register.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                FirebaseUser user = firebaseAuth.getCurrentUser();

                                final StorageReference childStorage = storageReference.child("User_Profile").child(user.getUid()).child(selectedImage.getLastPathSegment());
                                childStorage.putFile(selectedImage)
                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                childStorage.getDownloadUrl()
                                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri uri) {
                                                                String downloadImage = uri.toString();
                                                                uploadData(fname, lname, currency, mobile, email, income, downloadImage);
                                                                pd.dismiss();
                                                                Intent i = new Intent(register.this, EmailVerification.class);
                                                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                startActivity(i);
                                                                finish();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                pd.dismiss();
                                                                Log.d(TAG,"Failed to get download url for profile picture");
                                                            }
                                                        });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                pd.dismiss();
                                                Log.d(TAG,"Failed to upload profile picture to storage");
                                            }
                                        });
                            }
                            else{
                                pd.dismiss();
                                String mes;
                                try{
                                    throw task.getException();
                                }
                                catch(FirebaseAuthUserCollisionException existEmail){
                                    Log.d(TAG,"Error: " + existEmail.toString());
                                    mes = "Email already exists.";
                                }
                                catch (Exception e){
                                    Log.d(TAG,"Error: " + e.toString());
                                    mes = "Error.. Something went wrong.";
                                }
                                AlertDialog.Builder ad = new AlertDialog.Builder(register.this);
                                ad.setMessage(mes);
                                ad.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                                ad.show();
                            }
                        }
                    });

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
        userDatabase.collection("users").document(id).set(user)
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
            selectedImage = data.getData();
            profile_pic.setImageURI(selectedImage);
        }
    }
}