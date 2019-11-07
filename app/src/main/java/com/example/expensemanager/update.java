package com.example.expensemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class update extends AppCompatActivity {

    private static String TAG = "update";

    String date, cost, description, sub_category, main_category, uuid;

    //Firebase
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore userDatabase = FirebaseFirestore.getInstance();
    String id = firebaseAuth.getCurrentUser().getUid();

    //Progress Dialog
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        //Declaring Views
        final EditText EditText1 = findViewById(R.id.EditText1);
        final Button update = findViewById(R.id.update);
        final EditText EditText2 = findViewById(R.id.EditText2);
        final Spinner sp = findViewById(R.id.spinner);
        final Spinner sp2 = findViewById(R.id.spinner2);
        DatePicker datePicker = findViewById(R.id.datepicker_home);

        //Progress Dialog
        pd = new ProgressDialog(this);

        Bundle bundle = getIntent().getExtras();
        date = bundle.getString("date");
        cost = bundle.getString("cost");
        description = bundle.getString("description");
        sub_category = bundle.getString("sub");
        main_category = bundle.getString("main");
        uuid = bundle.getString("uuid");

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date d = simpleDateFormat.parse(date);
            long minDate = d.getTime();
            long maxDate = d.getTime();
            datePicker.setMaxDate(maxDate);
            datePicker.setMinDate(minDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        List<String> list = new ArrayList<>();
        list.add(main_category);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,list);
        sp.setAdapter(adapter);

        List<String> list2 = new ArrayList<>();
        list2.add(sub_category);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,list2);
        sp2.setAdapter(adapter2);

        EditText1.setText(cost);
        EditText2.setText(description);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String new_cost = EditText1.getText().toString().trim();
                String new_description = EditText2.getText().toString().trim();

                int a=0,b=0,c=0;
                if(new_cost.isEmpty()){
                    EditText1.requestFocus();
                    EditText1.setError("Field cannot be left empty");
                }
                else if(new_cost.contains(".")){
                    EditText1.requestFocus();
                    EditText1.setError("Invalid character  ' . '  found");
                }
                else if(Integer.parseInt(new_cost) == 0){
                    EditText1.requestFocus();
                    EditText1.setError("Set value higher than 0");
                }
                else if(new_cost.startsWith("0")){
                    EditText1.requestFocus();
                    EditText1.setError("Cannot begin with 0");
                }
                else{a=1;}

                if(new_description.isEmpty()){
                    EditText2.requestFocus();
                    EditText2.setError("Field cannot be left empty");
                }
                else if(new_description.contains("_")){
                    EditText2.requestFocus();
                    EditText2.setError("Invalid character  ' _ '  found");
                }
                else if(new_description.contains(".")){
                    EditText2.requestFocus();
                    EditText2.setError("Invalid character  ' . '  found");
                }
                else{b=1;}

                if(new_cost.equals(cost) && new_description.equals(description)){
                    AlertDialog.Builder ad = new AlertDialog.Builder(update.this);
                    ad.setMessage("No changes were made to data.");
                    ad.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    ad.show();
                }
                else{c=1;}

                if(a==1 && b==1 && c==1){

                    updateData(new_cost,new_description);

                }

            }
        });

    }

    public void updateData(String new_cost, String new_description){

        pd.setTitle("Updating... Please wait.");
        pd.show();

        final String cost_update = String.valueOf( Integer.parseInt(new_cost) - Integer.parseInt(cost) );

        //Retrieving old daily total values if they exist
        userDatabase.collection("users").document(id).collection(main_category).document(date).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                          @Override
                                          public void onSuccess(DocumentSnapshot documentSnapshot) {

                                              String daily_value = documentSnapshot.getString("total");
                                              String daily_category_value = documentSnapshot.getString("total-" + sub_category);

                                              String new_value = String.valueOf(Integer.parseInt(daily_value) + Integer.parseInt(cost_update));
                                              String new_value2 = String.valueOf(Integer.parseInt(daily_category_value) + Integer.parseInt(cost_update));

                                              //Map for daily total values to be set
                                              final Map<String, Object> user = new HashMap<>();
                                              user.put("total", new_value);
                                              user.put("total-" + sub_category, new_value2);

                                              //Update date-wise total data
                                              userDatabase.collection("users").document(id).collection(main_category).document(date).update(user)
                                                      .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                          @Override
                                                          public void onSuccess(Void aVoid) {
                                                              Log.d(TAG, "Successfully updated date-wise total data to database.");
                                                          }
                                                      })
                                                      .addOnFailureListener(new OnFailureListener() {
                                                          @Override
                                                          public void onFailure(@NonNull Exception e) {
                                                              Log.d(TAG, "Failed to update date-wise total data to database.");
                                                          }
                                                      });
                                          }
                                      })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"Failed to retrieve date-wise total data. ---- " + e.toString());
                    }
                });

        //Set Individual data values
        Map<String, Object> individual = new HashMap<>();
        individual.put("date", date);
        individual.put("cost", new_cost);
        individual.put("description", new_description);
        individual.put("sub-category", sub_category);
        individual.put("main-category", main_category);
        individual.put("id", uuid);

        //storing individual data individually according to date
        userDatabase.collection("users").document(id).collection(main_category).document(date).collection("Items").document(uuid).update(individual)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,"Successfully updated individual data sorted according to date.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"Failed to update individual data sorted according to date.");
                    }
                });

        //storing individual data collectively in IndividualValues collection
        userDatabase.collection("users").document(id).collection("IndividualValues").document(uuid).set(individual)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,"Successfully updated individual data in IndividualValues collection.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"Failed to update individual data in IndividualValues collection.");
                    }
                });

        //Total data
        final String[] month_value = new String[1];
        final String[] month_category_value = new String[1];
        final String[] year_value = new String[1];
        final String[] year_category_value = new String[1];

        String[] date_split = date.split("-");
        final String month = date_split[1] + "-" + date_split[2];
        final String year = date_split[2];

        userDatabase.collection("users").document(id).collection(main_category).document("Total").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        month_value[0] = documentSnapshot.getString("total-" + month);
                        month_category_value[0] = documentSnapshot.getString("total-" + sub_category + "-" + month);
                        year_value[0] = documentSnapshot.getString("total-" + year);
                        year_category_value[0] = documentSnapshot.getString("total-" + sub_category + "-" + year);

                        String new_value = String.valueOf(Integer.parseInt(month_value[0]) + Integer.parseInt(cost_update));
                        String new_value2 = String.valueOf(Integer.parseInt(month_category_value[0]) + Integer.parseInt(cost_update));
                        String new_value3 = String.valueOf(Integer.parseInt(year_value[0]) + Integer.parseInt(cost_update));
                        String new_value4 = String.valueOf(Integer.parseInt(year_category_value[0]) + Integer.parseInt(cost_update));

                        final Map<String, Object> user2 = new HashMap<>();
                        user2.put("total-" + month, new_value);
                        user2.put("total-" + sub_category + "-" + month, new_value2);
                        user2.put("total-" + year, new_value3);
                        user2.put("total-" + sub_category + "-" + year, new_value4);
                        userDatabase.collection("users").document(id).collection(main_category).document("Total").update(user2)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "Successfully updated total data to database.");

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "Failed to update total data to database.");
                                    }
                                });
                        pd.dismiss();
                        AlertDialog.Builder ad = new AlertDialog.Builder(update.this);
                        ad.setMessage("Successfully updated data.");
                        ad.setCancelable(false);
                        ad.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Failed to retrieve total data from database." + "\n" + e.toString());
                        pd.dismiss();
                        AlertDialog.Builder ad = new AlertDialog.Builder(update.this);
                        ad.setMessage("Failed to update data.");
                        ad.setCancelable(false);
                        ad.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                    }
                });

    }
}
