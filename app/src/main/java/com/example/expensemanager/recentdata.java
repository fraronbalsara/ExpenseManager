package com.example.expensemanager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link recentdata.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link recentdata#newInstance} factory method to
 * create an instance of this fragment.
 */
public class recentdata extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String TAG ="recentdata";

    //no data text view
    TextView noData;

    //date string
    String date;

    //Declaring views
    List<Model> modelList = new ArrayList<>();
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager layoutManager;

    //Firebase
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore userDatabase = FirebaseFirestore.getInstance();
    private String id = firebaseAuth.getCurrentUser().getUid();

    //Progress Dialog
    private ProgressDialog pd;

    //adapter
    CustomAdapter adapter;

    private OnFragmentInteractionListener mListener;

    public recentdata() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment recentdata.
     */
    // TODO: Rename and change types and number of parameters
    public static recentdata newInstance(String param1, String param2) {
        recentdata fragment = new recentdata();
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
            setHasOptionsMenu(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_recentdata, container, false);

        //Progress Dialog
        pd = new ProgressDialog(this.getActivity());

        //no data text view
        noData = v.findViewById(R.id.noData);

        //initialize views
        mRecyclerView = v.findViewById(R.id.recycler_view);

        //set recycler view properties
        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        Date d = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        date = simpleDateFormat.format(d);

        //search view
        final SearchView sv = v.findViewById(R.id.search_badge);

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                noData.setText("No Data Found");
                searchData(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() < 1){
                    modelList.clear();
                    showData(date);
                }
                searchData(newText);
                return false;
            }
        });

        return v;
    }

    private void showData(String date) {

        pd.setTitle("Loading Data...");
        pd.show();

        userDatabase.collection("users").document(id).collection("IndividualValues").whereEqualTo("date",date).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        pd.dismiss();
                        for (DocumentSnapshot doc : task.getResult()) {
                            Model model = new Model(doc.getString("date"), doc.getString("cost"), doc.getString("description"), doc.getString("sub-category"), doc.getString("main-category"), doc.getString("id"));
                            modelList.add(model);
                            if (model != null){noData.setText("");}
                        }
                        //adapter
                        adapter = new CustomAdapter(getActivity(), modelList, getContext());
                        mRecyclerView.setAdapter(adapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Log.d(TAG, "Failed to retrieve data. --- "+ e.toString());
                    }
                });
    }

    public void searchData(String s) {

        String[] split_s = s.split(":");

        if(split_s.length == 2) {
            userDatabase.collection("users").document(id).collection("IndividualValues").whereEqualTo(split_s[0], split_s[1]).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            modelList.clear();
                            for (DocumentSnapshot doc : task.getResult()) {
                                Model model = new Model(doc.getString("date"), doc.getString("cost"), doc.getString("description"), doc.getString("sub-category"), doc.getString("main-category"), doc.getString("id"));
                                modelList.add(model);
                                if (model != null){noData.setText("");}
                            }
                            //adapter
                            adapter = new CustomAdapter(getActivity(), modelList, getContext());
                            mRecyclerView.setAdapter(adapter);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Failed to retrieve data. --- " + e.toString());
                        }
                    });
        }
    }

    public void deleteData(final String d_date, final String cost, String description, final String main_category, final String sub_category, String uuid, final FragmentActivity fragmentActivity){

        pd = new ProgressDialog(fragmentActivity);
        pd.setTitle("Deleting... Please wait.");
        pd.show();

        //Retrieving old daily total values if they exist
        userDatabase.collection("users").document(id).collection(main_category).document(d_date).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        String daily_value = documentSnapshot.getString("total");
                        String daily_category_value = documentSnapshot.getString("total-" + sub_category);

                        String new_value = String.valueOf(Integer.parseInt(daily_value) - Integer.parseInt(cost));
                        String new_value2 = String.valueOf(Integer.parseInt(daily_category_value) - Integer.parseInt(cost));

                        //Map for daily total values to be set
                        final Map<String, Object> user = new HashMap<>();
                        user.put("total", new_value);
                        user.put("total-" + sub_category, new_value2);

                        //Update date-wise total data
                        userDatabase.collection("users").document(id).collection(main_category).document(d_date).update(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "Successfully deleted date-wise total data to database.");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "Failed to delete date-wise total data to database.");
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

        //deleting individual data individually according to date
        userDatabase.collection("users").document(id).collection(main_category).document(d_date).collection("Items").document(uuid).delete()
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

        //deleting individual data collectively in IndividualValues collection
        userDatabase.collection("users").document(id).collection("IndividualValues").document(uuid).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,"Successfully deleted individual data in IndividualValues collection.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"Failed to delete individual data in IndividualValues collection.");
                    }
                });

        //Total data
        final String[] month_value = new String[1];
        final String[] month_category_value = new String[1];
        final String[] year_value = new String[1];
        final String[] year_category_value = new String[1];

        String[] d_date_split = d_date.split("-");
        final String month = d_date_split[1] + "-" + d_date_split[2];
        final String year = d_date_split[2];

        userDatabase.collection("users").document(id).collection(main_category).document("Total").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        month_value[0] = documentSnapshot.getString("total-" + month);
                        month_category_value[0] = documentSnapshot.getString("total-" + sub_category + "-" + month);
                        year_value[0] = documentSnapshot.getString("total-" + year);
                        year_category_value[0] = documentSnapshot.getString("total-" + sub_category + "-" + year);

                        String new_value = String.valueOf(Integer.parseInt(month_value[0]) - Integer.parseInt(cost));
                        String new_value2 = String.valueOf(Integer.parseInt(month_category_value[0]) - Integer.parseInt(cost));
                        String new_value3 = String.valueOf(Integer.parseInt(year_value[0]) - Integer.parseInt(cost));
                        String new_value4 = String.valueOf(Integer.parseInt(year_category_value[0]) - Integer.parseInt(cost));

                        final Map<String, Object> user2 = new HashMap<>();
                        user2.put("total-" + month, new_value);
                        user2.put("total-" + sub_category + "-" + month, new_value2);
                        user2.put("total-" + year, new_value3);
                        user2.put("total-" + sub_category + "-" + year, new_value4);
                        userDatabase.collection("users").document(id).collection(main_category).document("Total").update(user2)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "Successfully deleted total data to database.");

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "Failed to delete total data to database.");
                                    }
                                });
                        pd.dismiss();
                        AlertDialog.Builder ad = new AlertDialog.Builder(fragmentActivity);
                        ad.setMessage("Successfully deleted data.");
                        ad.setCancelable(false);
                        ad.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        ad.show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Failed to retrieve total data from database." + "\n" + e.toString());
                        pd.dismiss();
                        AlertDialog.Builder ad = new AlertDialog.Builder(fragmentActivity);
                        ad.setMessage("Failed to delete data.");
                        ad.setCancelable(false);
                        ad.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        ad.show();
                    }
                });

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

    @Override
    public void onResume() {
        super.onResume();
        modelList.clear();
        showData(date);
    }
}
