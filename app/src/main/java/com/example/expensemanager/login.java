package com.example.expensemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {

    private static final String TAG ="login";

    private String email,password;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //EditTexts
        final EditText e1 = (EditText)findViewById(R.id.e1);
        final EditText e2 = (EditText)findViewById(R.id.e2);

        //Buttons
        Button b1 = (Button)findViewById(R.id.b1);

        //TextViews
        final TextView t1 = (TextView)findViewById(R.id.t1);
        final TextView t2 = (TextView)findViewById(R.id.t2);

        //CheckBoxes
        final CheckBox c1 = (CheckBox)findViewById(R.id.c1);

        //ProgressDialog
        final ProgressDialog pd = new ProgressDialog(login.this);

        //Firebase
        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();

        //Used to send user to MainActivity if authenticated
        if(firebaseAuth.getCurrentUser() != null){
            Intent i = new Intent(login.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }

        //Login Button
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = e1.getText().toString().trim();
                password = e2.getText().toString().trim();
                int a=0,b=0;

                //Email validation
                if(!email.matches("^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$")){
                    e1.requestFocus();
                    e1.setError("ENTER VALID EMAIL");
                }
                else{a=1;}

                //Password validation
                if(password.length() < 6){
                    e2.requestFocus();
                    e2.setError("PASSWORD MUST BE AT LEAST 6 CHARACTERS");
                }
                else{b=1;}

                //True if both are validated
                if(a==1 && b==1){
                    //Progress Dialog
                    pd.setMessage("Please wait, authenticating.");
                    pd.show();

                    //Firebase sign in
                    firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(login.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Intent i = new Intent(login.this,MainActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                finish();

                            }
                            else{
                                //Alert Dialog for email-password mismatch
                                AlertDialog.Builder ad = new AlertDialog.Builder(login.this);
                                ad.setMessage("Email and Password not found.");
                                ad.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                                ad.show();
                            }
                            //Dismiss Progress Dialog
                            pd.dismiss();
                        }
                    });

                }

            }
        });

        //Register Button(TextView)
        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(login.this,register.class);
                startActivity(i);
            }
        });

        //Show/Hide Password Checkbox
        c1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!c1.isChecked()){
                    e2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                else{
                    e2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

        //Forgotten Password Button(TextView)
        t2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = e1.getText().toString();
                int a= 0;

                //Email validation
                if(!email.matches("^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$")){
                    e1.requestFocus();
                    e1.setError("ENTER VALID EMAIL");
                }
                else{a=1;}

                //True if email is validated
                if(a==1){
                    //Alert Dialog for password recovery email
                    AlertDialog.Builder ad = new AlertDialog.Builder(login.this);
                    ad.setMessage("Do you want to be sent a password recovery email?");
                    ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            firebaseAuth.sendPasswordResetEmail(email)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                AlertDialog.Builder ad1 = new AlertDialog.Builder(login.this);
                                                ad1.setMessage("Password recovery email sent.");
                                                ad1.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                });
                                                ad1.show();
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG,"Failed to send password recovery email");
                                        }
                                    });
                        }
                    });
                    ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
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