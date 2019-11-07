package com.example.expensemanager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link home.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class home extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG ="home";

    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore userDatabase;
    private StorageReference storageReference;

    //Progress Dialog
    ProgressDialog pd;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment home.
     */
    // TODO: Rename and change types and number of parameters
    public static home newInstance(String param1, String param2) {
        home fragment = new home();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_home, container, false);

        //Declaring Views
        final EditText EditText1 = (EditText)v.findViewById(R.id.EditText1);
        Button insert = (Button)v.findViewById(R.id.insert);
        final EditText EditText2 = (EditText)v.findViewById(R.id.EditText2);
        final Spinner sp = (Spinner)v.findViewById(R.id.spinner);
        final Spinner sp2 = (Spinner)v.findViewById(R.id.spinner2);
        final DatePicker datePicker = (DatePicker)v.findViewById(R.id.datepicker_home);

        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        userDatabase = FirebaseFirestore.getInstance();

        //Progress Dialog
        pd = new ProgressDialog(getActivity());

        Date d = new Date();
        datePicker.setMaxDate(d.getTime());
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date d2 = simpleDateFormat.parse("01-01-2015");
            long minDate = d2.getTime();
            datePicker.setMinDate(minDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(sp.getSelectedItem().toString().equals("Expense")) {
                    String[] array1 = new String[]{"Food", "Bills", "Transportation", "Home", "Car", "Entertainment", "Clothing", "Insurance", "Tax", "Health", "Sport", "Kids", "Pet", "Beauty", "Electronics", "Gift", "Social", "Travel", "Education", "Office", "Other"};
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, array1);
                    sp2.setAdapter(adapter);
                }
                else if(sp.getSelectedItem().toString().equals("Income")) {
                    String[] array2 = new String[]{"Salary","Awards","Grants","Rental","Refunds","Lottery","Dividends","Investments","Other"};
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, array2);
                    sp2.setAdapter(adapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_entered_data = EditText2.getText().toString().trim();
                String user_entered_item = EditText1.getText().toString().trim();
                String mode = sp.getSelectedItem().toString();
                String category = sp2.getSelectedItem().toString();

                int a=0,b=0;
                if(user_entered_item.isEmpty()){
                    EditText1.requestFocus();
                    EditText1.setError("Field cannot be left empty");
                }
                else if(user_entered_item.contains("_")){
                    EditText1.requestFocus();
                    EditText1.setError("Invalid character  ' _ '  found");
                }
                else if(user_entered_item.contains(".")){
                    EditText1.requestFocus();
                    EditText1.setError("Invalid character  ' . '  found");
                }
                else{a=1;}
                if(user_entered_data.isEmpty()){
                    EditText2.requestFocus();
                    EditText2.setError("Field cannot be left empty");
                }
                else if(user_entered_data.contains(".")){
                    EditText2.requestFocus();
                    EditText2.setError("Invalid character  ' . '  found");
                }
                else if(Integer.parseInt(user_entered_data) == 0){
                    EditText2.requestFocus();
                    EditText2.setError("Set value higher than 0");
                }
                else if(user_entered_data.startsWith("0")){
                    EditText2.requestFocus();
                    EditText2.setError("Cannot begin with 0");
                }
                else{b=1;}

                if(a==1 && b==1) {
                    pd.setMessage("Uploading..");
                    pd.show();

                    int dayint = datePicker.getDayOfMonth();
                    int monthint = datePicker.getMonth()+1;
                    int yearint = datePicker.getYear();
                    String date = String.valueOf(dayint);
                    if(date.length() == 1){date = "0" + date;}
                    String month_hold = String.valueOf(monthint);
                    if(month_hold.length() == 1){month_hold = "0" + month_hold;}
                    String year = String.valueOf(yearint);
                    String day = date + "-" + month_hold + "-" + year;
                    String month = month_hold + "-" + year;

                    uploadData(user_entered_data, user_entered_item, mode, category, day, month, year, v);
                }
            }
        });

        return v;
    }

    private void uploadData(final String user_entered_data, final String user_entered_item, final String mode, final String category, final String day, final String month, final String year, final View v) {
        final String id = firebaseAuth.getCurrentUser().getUid();

        //Date-wise individual values
        final String[] new_value = new String[1];
        final String[] new_value2 = new String[1];
        final String[] daily_value = new String[1];
        final String[] daily_category_value = new String[1];

        final String uuid = UUID.randomUUID().toString();

        userDatabase.collection("users").document(id).collection(mode).document(day).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        //Retrieving old daily total values if they exist
                        daily_value[0] = documentSnapshot.getString("total");
                        daily_category_value[0] = documentSnapshot.getString("total-" + category);
                        if (daily_value[0] == null) {
                            daily_value[0] = "0";
                        }
                        if (daily_category_value[0] == null) {
                            daily_category_value[0] = "0";
                        }
                        new_value[0] = String.valueOf(Integer.parseInt(daily_value[0]) + Integer.parseInt(user_entered_data));
                        new_value2[0] = String.valueOf(Integer.parseInt(daily_category_value[0]) + Integer.parseInt(user_entered_data));

                        //Map for daily total values to be set
                        final Map<String, Object> user = new HashMap<>();
                        user.put("total", new_value[0]);
                        user.put("total-" + category, new_value2[0]);

                        //Update date-wise total data if data already exists for the date
                        userDatabase.collection("users").document(id).collection(mode).document(day).update(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "Successfully stored date-wise total data to database.");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //If update fails then add data for today
                                        if (e.toString().equals("com.google.firebase.firestore.FirebaseFirestoreException: NOT_FOUND: No document to update: projects/expense-manager-cda68/databases/(default)/documents/users/" + id + "/" + mode + "/" + day)) {
                                            userDatabase.collection("users").document(id).collection(mode).document(day).set(user)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d(TAG, "Successfully created new document and stored date-wise data.");
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.d(TAG, "Failed to create new document and store date-wise data." + "\n" + e.toString());
                                                        }
                                                    });
                                        }
                                        else{
                                            Log.d(TAG, "Failed to store date-wise total data to database." + "\n" + e.toString());
                                        }
                                    }
                                });

                        //Set Individual data values
                        Map<String, Object> individual = new HashMap<>();
                        individual.put("date", day);
                        individual.put("cost", user_entered_data);
                        individual.put("description", user_entered_item);
                        individual.put("sub-category", category);
                        individual.put("main-category", mode);
                        individual.put("id", uuid);

                        //storing individual data individually according to date
                        userDatabase.collection("users").document(id).collection(mode).document(day).collection("Items").document(uuid).set(individual)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG,"Successfully created new document and stored individual data sorted according to date.");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG,"Failed to create new document and store individual data sorted according to date.");
                                    }
                                });

                        //storing individual data collectively in IndividualValues collection
                        userDatabase.collection("users").document(id).collection("IndividualValues").document(uuid).set(individual)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG,"Successfully created new document and stored individual data in IndividualValues collection.");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG,"Failed to create new document and store individual data in IndividualValues collection.");
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"Failed to retrieve date-wise total data. --- " + e.toString());
                    }
                });

        //Total data
        final String[] month_value = new String[1];
        final String[] month_category_value = new String[1];
        final String[] year_value = new String[1];
        final String[] year_category_value = new String[1];
        userDatabase.collection("users").document(id).collection(mode).document("Total").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        month_value[0] = documentSnapshot.getString("total-" + month);
                        if (month_value[0] == null) {
                            month_value[0] = "0";
                        }

                        month_category_value[0] = documentSnapshot.getString("total-" + category + "-" + month);
                        if (month_category_value[0] == null) {
                            month_category_value[0] = "0";
                        }

                        year_value[0] = documentSnapshot.getString("total-" + year);
                        if (year_value[0] == null) {
                            year_value[0] = "0";
                        }

                        year_category_value[0] = documentSnapshot.getString("total-" + category + "-" + year);
                        if (year_category_value[0] == null) {
                            year_category_value[0] = "0";
                        }

                        String new_value = String.valueOf(Integer.parseInt(month_value[0]) + Integer.parseInt(user_entered_data));
                        String new_value2 = String.valueOf(Integer.parseInt(month_category_value[0]) + Integer.parseInt(user_entered_data));
                        String new_value3 = String.valueOf(Integer.parseInt(year_value[0]) + Integer.parseInt(user_entered_data));
                        String new_value4 = String.valueOf(Integer.parseInt(year_category_value[0]) + Integer.parseInt(user_entered_data));

                        final Map<String, Object> user2 = new HashMap<>();
                        user2.put("total-" + month, new_value);
                        user2.put("total-" + category + "-" + month, new_value2);
                        user2.put("total-" + year, new_value3);
                        user2.put("total-" + category + "-" + year, new_value4);
                        userDatabase.collection("users").document(id).collection(mode).document("Total").update(user2)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "Successfully stored total data to database.");
                                        pd.dismiss();
                                        Snackbar.make(v,"Data inserted successfully.",Snackbar.LENGTH_LONG)
                                                .setAction("UNDO", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        deleteData(day,month,year,mode,category,user_entered_data,uuid,daily_value[0],daily_category_value[0],month_value[0],month_category_value[0],year_value[0],year_category_value[0]);
                                                    }
                                                })
                                                .show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        if (e.toString().equals("com.google.firebase.firestore.FirebaseFirestoreException: NOT_FOUND: No document to update: projects/expense-manager-cda68/databases/(default)/documents/users/" + id + "/" + mode + "/Total")) {
                                            userDatabase.collection("users").document(id).collection(mode).document("Total").set(user2)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d(TAG, "Successfully created new document and stored total data.");
                                                            pd.dismiss();
                                                            Snackbar.make(v,"Data inserted successfully.",Snackbar.LENGTH_LONG)
                                                                    .setAction("UNDO", new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View v) {
                                                                            deleteData(day,month,year,mode,category,user_entered_data,uuid,daily_value[0],daily_category_value[0],month_value[0],month_category_value[0],year_value[0],year_category_value[0]);
                                                                        }
                                                                    })
                                                                    .show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.d(TAG, "Failed to create new document and store total data." + "\n" + e.toString());
                                                            Snackbar.make(v,"Data insertion failed.",Snackbar.LENGTH_LONG).show();
                                                        }
                                                    });
                                        }
                                        else {
                                            Log.d(TAG, "Failed to store total data to database." + "\n" + e.toString());
                                            Snackbar.make(v,"Data insertion failed.",Snackbar.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Failed to retrieve total data from database." + "\n" + e.toString());
                    }
                });
    }

    public void deleteData( String day, String month, String year, String mode, String category, String user_entered_data, String uuid, String daily_value, String daily_category_value, String month_value, String month_category_value, String year_value, String year_category_value) {

        pd.setMessage("Deleting..");
        pd.show();

        final String id = firebaseAuth.getCurrentUser().getUid();

        final Map<String, Object> new_values = new HashMap<>();
        new_values.put("total", daily_value);
        new_values.put("total-" + category, daily_category_value);

        userDatabase.collection("users").document(id).collection(mode).document(day).update(new_values)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Successfully reverted date-wise data changes.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Failed to revert date-wise data changes.");
                    }
                });

        //Deleting/Reverting individual data
        userDatabase.collection("users").document(id).collection(mode).document(day).collection("Items").document(uuid).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,"Successfully deleted individual data sorted according to date.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"Failed to delete individual data sorted according to date.");
                    }
                });

        //Deleting/Reverting individual data stored collectively in IndividualValues collection
        userDatabase.collection("users").document(id).collection("IndividualValues").document(uuid).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,"Successfully deleted individual data from IndividualValues collection.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"Failed to delete individual data from IndividualValues collection.");
                    }
                });

        //Reverting Total data
        final Map<String, Object> new_values2 = new HashMap<>();
        new_values2.put("total-" + month, month_value);
        new_values2.put("total-" + category + "-" + month, month_category_value);
        new_values2.put("total-" + year, year_value);
        new_values2.put("total-" + category + "-" + year, year_category_value);

        userDatabase.collection("users").document(id).collection(mode).document("Total").update(new_values2)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Successfully reverted total data changes.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Failed to revert total data changes.");
                    }
                });
        pd.dismiss();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
