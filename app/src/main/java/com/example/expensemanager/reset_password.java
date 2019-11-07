package com.example.expensemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class reset_password extends AppCompatActivity {

    private static final String TAG ="reset_password";

    //View Declarations
    EditText e1, e2, e3;
    CheckBox c1, c2, c3;
    Button b1;

    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore userDatabase;

    //ProgressDialog
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        //View Initialization
        e1 = findViewById(R.id.e1);
        e2 = findViewById(R.id.e2);
        e3 = findViewById(R.id.e3);
        c1 = findViewById(R.id.c1);
        c2 = findViewById(R.id.c2);
        c3 = findViewById(R.id.c3);
        b1 = findViewById(R.id.b1);

        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        userDatabase = FirebaseFirestore.getInstance();
        String id = firebaseAuth.getCurrentUser().getUid();

        //ProgressDialog
        pd = new ProgressDialog(reset_password.this);

        final String[] email = new String[1];

        //Retrieve email
        userDatabase.collection("users").document(id).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        email[0] = documentSnapshot.getString("email");
                        Log.d(TAG,"Successfully retrieved email from database.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"Failed to retrieve email from database.");
                    }
                });

        //Show/Hide Enter Old Password Checkbox
        c1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!c1.isChecked()){
                    e1.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                else{
                    e1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

        //Show/Hide Enter New Password Checkbox
        c2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!c2.isChecked()){
                    e2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                else{
                    e2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

        //Show/Hide Re-enter New Password Checkbox
        c3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!c3.isChecked()){
                    e3.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                else{
                    e3.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = e1.getText().toString().trim();
                final String newPassword1 = e2.getText().toString().trim();
                String newPassword2 = e3.getText().toString().trim();
                int a=0,b=0,c=0;

                if (oldPassword.isEmpty()) {
                    e1.requestFocus();
                    e1.setError("CANNOT BE LEFT EMPTY");
                }
                else{
                    a = 1;
                }

                if (newPassword1.length() < 6) {
                    e2.requestFocus();
                    e2.setError("PASSWORD MUST BE AT LEAST 6 CHARACTERS");
                }
                else {
                    b = 1;
                }

                if (newPassword1.equals(newPassword2)) {
                    c = 1;
                }
                else {
                    e3.requestFocus();
                    e3.setError("PASSWORD DOES NOT MATCH");
                }

                if(a == 1 && b == 1 && c == 1) {

                    pd.setMessage("Please wait, authenticating");
                    pd.show();

                    firebaseAuth.signInWithEmailAndPassword(email[0], oldPassword).addOnCompleteListener(reset_password.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                pd.setMessage("Password change in progress");
                                firebaseAuth.getCurrentUser().updatePassword(newPassword1)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                pd.dismiss();
                                                Log.d(TAG,"Password changed successfully.");
                                                AlertDialog.Builder ad = new AlertDialog.Builder(reset_password.this);
                                                ad.setMessage("Password changed successfully.");
                                                ad.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        firebaseAuth.signOut();
                                                        Intent i = new Intent(reset_password.this,login.class);
                                                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        startActivity(i);
                                                        finish();
                                                    }
                                                });
                                                ad.show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                pd.dismiss();
                                                AlertDialog.Builder ad = new AlertDialog.Builder(reset_password.this);
                                                ad.setMessage("Oops! An error occurred, please try again later.");
                                                ad.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                });
                                                ad.show();
                                                Log.d(TAG,"Failed to change password. --- " + e.toString());
                                            }
                                        });
                            }
                            else {
                                pd.dismiss();
                                //Alert Dialog for email-password mismatch
                                AlertDialog.Builder ad = new AlertDialog.Builder(reset_password.this);
                                ad.setMessage("Email and Password not found.");
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
}
