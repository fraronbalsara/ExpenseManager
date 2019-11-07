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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class delete_account extends AppCompatActivity {

    private static final String TAG ="delete_account";

    //View Declarations
    EditText e1;
    CheckBox c1;
    Button b1;

    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore userDatabase;
    private StorageReference storageReference;

    //ProgressDialog
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        //View Initialization
        e1 = findViewById(R.id.e1);
        c1 = findViewById(R.id.c1);
        b1 = findViewById(R.id.b1);

        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        userDatabase = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        final String id = firebaseAuth.getCurrentUser().getUid();

        //ProgressDialog
        pd = new ProgressDialog(delete_account.this);

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

        //Show/Hide Password Checkbox
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

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String password = e1.getText().toString().trim();

                int a=0;

                if (password.isEmpty()) {
                    e1.requestFocus();
                    e1.setError("CANNOT BE LEFT EMPTY");
                }
                else{
                    a = 1;
                }

                if(a == 1) {
                    //Alert Dialog for warning before deleting account
                    AlertDialog.Builder ad = new AlertDialog.Builder(delete_account.this);
                    ad.setMessage("Last Chance. \nAre you sure you want to delete your account?");
                    ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            pd.setMessage("Please wait, authenticating");
                            pd.show();

                            firebaseAuth.signInWithEmailAndPassword(email[0], password).addOnCompleteListener(delete_account.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        pd.setMessage("Deleting Account.");
                                        firebaseAuth.getCurrentUser().delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "Account deleted successfully.");

                                                        //Deleting user document
                                                        userDatabase.collection("users").document(id).delete()
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Log.d(TAG, "Successfully deleted user document.");
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        pd.dismiss();
                                                                        Log.d(TAG, "Failed to delete user document.");
                                                                    }
                                                                });

                                                        //Deleting profile picture from storage
                                                        final StorageReference childStorage = storageReference.child("User_Profile").child(id);
                                                        childStorage.delete()
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Log.d(TAG, "Successfully deleted profile picture data.");
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Log.d(TAG, "Failed to delete profile picture data. --- " + e.toString());
                                                                    }
                                                                });

                                                        pd.dismiss();
                                                        AlertDialog.Builder ad = new AlertDialog.Builder(delete_account.this);
                                                        ad.setMessage("Account deleted successfully.");
                                                        ad.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                firebaseAuth.signOut();
                                                                Intent i = new Intent(delete_account.this, login.class);
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
                                                        AlertDialog.Builder ad = new AlertDialog.Builder(delete_account.this);
                                                        ad.setMessage("Oops! An error occurred, please try again later.");
                                                        ad.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {

                                                            }
                                                        });
                                                        ad.show();
                                                        Log.d(TAG, "Failed to delete account. --- " + e.toString());
                                                    }
                                                });
                                    } else {
                                        //Alert Dialog for email-password mismatch
                                        pd.dismiss();
                                        AlertDialog.Builder ad = new AlertDialog.Builder(delete_account.this);
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
                    });
                    ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });
                    ad.show();
                }
            }
        });
    }
}
