package com.example.expensemanager;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.SearchView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link daily.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link daily#newInstance} factory method to
 * create an instance of this fragment.
 */
public class daily extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "daily";
    private String Food, Bills, Transportation, Home, Car, Entertainment, Clothing, Insurance, Tax, Health, Sport, Kids, Pet, Beauty, Electronics, Gift, Social, Travel, Education, Office, Other, Total, Salary, Awards, Grants, Rental, Refunds, Lottery, Dividends, Investments, Other2, Total2;
    private Float a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,total,divisor;
    private Float a2,b2,c2,d2,e2,f2,g2,h2,i2,divisor2,total2;
    private ArrayList<String> xData = new ArrayList<>();
    private ArrayList<Float> yData = new ArrayList<>();
    private ArrayList<String> zData = new ArrayList<>();
    private PieChart pieChart, pieChart2;
    private SearchView searchView;
    private TextView textView, textView2;

    private String currency, current_date;

    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore userDatabase;
    private StorageReference storageReference;
    String id;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public daily() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment daily.
     */
    // TODO: Rename and change types and number of parameters
    public static daily newInstance(String param1, String param2) {
        daily fragment = new daily();
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

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_daily, container, false);

        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        userDatabase = FirebaseFirestore.getInstance();
        id = firebaseAuth.getCurrentUser().getUid();

        //Views
        searchView = v.findViewById(R.id.searchView);
        pieChart = v.findViewById(R.id.daily_pie);
        pieChart2 = v.findViewById(R.id.daily_pie2);
        textView = v.findViewById(R.id.pieChartData);
        textView2 = v.findViewById(R.id.pieChartData2);

        //Retrieving currency value
        userDatabase.collection("users").document(id).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        currency = documentSnapshot.getString("currency");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"Failed to retrieve currency");
                    }
                });

        //date retrieval
        Date d = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        current_date = simpleDateFormat.format(d);

        displayPieChart(current_date);
        displayPieChart2(current_date);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                displayPieChart(query);
                displayPieChart2(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() < 1){
                    displayPieChart(current_date);
                    displayPieChart2(current_date);
                }
                return false;
            }
        });

        return v;
    }

    private void displayPieChart(String date){
        userDatabase.collection("users").document(id).collection("Expense").document(date).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Food = documentSnapshot.getString("total-Food");
                        Bills = documentSnapshot.getString("total-Bills");
                        Transportation = documentSnapshot.getString("total-Transportation");
                        Home = documentSnapshot.getString("total-Home");
                        Car = documentSnapshot.getString("total-Car");
                        Entertainment = documentSnapshot.getString("total-Entertainment");
                        Clothing = documentSnapshot.getString("total-Clothing");
                        Insurance = documentSnapshot.getString("total-Insurance");
                        Tax = documentSnapshot.getString("total-Tax");
                        Health = documentSnapshot.getString("total-Health");
                        Sport = documentSnapshot.getString("total-Sport");
                        Kids = documentSnapshot.getString("total-Kids");
                        Pet = documentSnapshot.getString("total-Pet");
                        Beauty = documentSnapshot.getString("total-Beauty");
                        Electronics = documentSnapshot.getString("total-Electronics");
                        Gift = documentSnapshot.getString("total-Gift");
                        Social = documentSnapshot.getString("total-Social");
                        Travel = documentSnapshot.getString("total-Travel");
                        Education = documentSnapshot.getString("total-Education");
                        Office = documentSnapshot.getString("total-Office");
                        Other = documentSnapshot.getString("total-Other");
                        Total = documentSnapshot.getString("total");

                        xData.clear();
                        yData.clear();
                        zData.clear();

                        if(Total != null){total = Float.parseFloat(Total); divisor = total / 100;}
                        if(Food != null){a = Float.parseFloat(Food) / divisor; xData.add("Food"); yData.add(a); zData.add(Food);}
                        if(Bills != null){b = Float.parseFloat(Bills) / divisor; xData.add("Bills"); yData.add(b); zData.add(Bills);}
                        if(Transportation != null){c = Float.parseFloat(Transportation) / divisor; xData.add("Transportation"); yData.add(c); zData.add(Transportation);}
                        if(Home != null){d = Float.parseFloat(Home) / divisor; xData.add("Home"); yData.add(d); zData.add(Home);}
                        if(Car != null){e = Float.parseFloat(Car) / divisor; xData.add("Car"); yData.add(e); zData.add(Car);}
                        if(Entertainment != null){f = Float.parseFloat(Entertainment) / divisor; xData.add("Entertainment"); yData.add(f); zData.add(Entertainment);}
                        if(Clothing != null){g = Float.parseFloat(Clothing) / divisor; xData.add("Clothing"); yData.add(g); zData.add(Clothing);}
                        if(Insurance != null){h = Float.parseFloat(Insurance) / divisor; xData.add("Insurance"); yData.add(h); zData.add(Insurance);}
                        if(Tax != null){i = Float.parseFloat(Tax) / divisor; xData.add("Tax"); yData.add(i); zData.add(Tax);}
                        if(Health != null){j = Float.parseFloat(Health) / divisor; xData.add("Health"); yData.add(j); zData.add(Health);}
                        if(Sport != null){k = Float.parseFloat(Sport) / divisor; xData.add("Sport"); yData.add(k); zData.add(Sport);}
                        if(Kids != null){l = Float.parseFloat(Kids) / divisor; xData.add("Kids"); yData.add(l); zData.add(Kids);}
                        if(Pet != null){m = Float.parseFloat(Pet) / divisor; xData.add("Pet"); yData.add(m); zData.add(Pet);}
                        if(Beauty != null){n = Float.parseFloat(Beauty) / divisor; xData.add("Beauty"); yData.add(n); zData.add(Beauty);}
                        if(Electronics != null){o = Float.parseFloat(Electronics) / divisor; xData.add("Electronics"); yData.add(o); zData.add(Electronics);}
                        if(Gift != null){p = Float.parseFloat(Gift) / divisor; xData.add("Gift"); yData.add(p); zData.add(Gift);}
                        if(Social != null){q = Float.parseFloat(Social) / divisor; xData.add("Social"); yData.add(q); zData.add(Social);}
                        if(Travel != null){r = Float.parseFloat(Travel) / divisor; xData.add("Travel"); yData.add(r); zData.add(Travel);}
                        if(Education != null){s = Float.parseFloat(Education) / divisor; xData.add("Education"); yData.add(s); zData.add(Education);}
                        if(Office != null){t = Float.parseFloat(Office) / divisor; xData.add("Office"); yData.add(t); zData.add(Office);}
                        if(Other != null){u = Float.parseFloat(Other) / divisor; xData.add("Other"); yData.add(u); zData.add(Other);}

                        pieChart.setContentDescription("Daily Expense");
                        pieChart.setRotationEnabled(true);
                        pieChart.setHoleRadius(0f);
                        pieChart.setTransparentCircleAlpha(0);
                        addDataSet(pieChart);

                        String textData = "";
                        for(int i=0;i<xData.size();i++){
                            textData = textData + xData.get(i) + ": " + currency + zData.get(i) + "\n";
                        }
                        textData = textData + "Total: " + currency + Total;
                        textView.setText(textData);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"Failed to display daily pie chart. " + e.toString());
                    }
                });
    }

    private void displayPieChart2(String date){
        userDatabase.collection("users").document(id).collection("Income").document(date).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Salary = documentSnapshot.getString("total-Salary");
                        Awards = documentSnapshot.getString("total-Awards");
                        Grants = documentSnapshot.getString("total-Grants");
                        Rental = documentSnapshot.getString("total-Rental");
                        Refunds = documentSnapshot.getString("total-Refunds");
                        Lottery = documentSnapshot.getString("total-Lottery");
                        Dividends = documentSnapshot.getString("total-Dividends");
                        Investments = documentSnapshot.getString("total-Investments");
                        Other2 = documentSnapshot.getString("total-Other");
                        Total2 = documentSnapshot.getString("total");

                        xData.clear();
                        yData.clear();
                        zData.clear();

                        if(Total2 != null){total2 = Float.parseFloat(Total2); divisor2 = total2 / 100;}
                        if(Salary != null){a2 = Float.parseFloat(Salary) / divisor2; xData.add("Salary"); yData.add(a2); zData.add(Salary);}
                        if(Awards != null){b2 = Float.parseFloat(Awards) / divisor2; xData.add("Awards"); yData.add(b2); zData.add(Awards);}
                        if(Grants != null){c2 = Float.parseFloat(Grants) / divisor2; xData.add("Grants"); yData.add(c2); zData.add(Grants);}
                        if(Rental != null){d2 = Float.parseFloat(Rental) / divisor2; xData.add("Rental"); yData.add(d2); zData.add(Rental);}
                        if(Refunds != null){e2 = Float.parseFloat(Refunds) / divisor2; xData.add("Refunds"); yData.add(e2); zData.add(Refunds);}
                        if(Lottery != null){f2 = Float.parseFloat(Lottery) / divisor2; xData.add("Lottery"); yData.add(f2); zData.add(Lottery);}
                        if(Dividends != null){g2 = Float.parseFloat(Dividends) / divisor2; xData.add("Dividends"); yData.add(g2); zData.add(Dividends);}
                        if(Investments != null){h2 = Float.parseFloat(Investments) / divisor2; xData.add("Investments"); yData.add(h2); zData.add(Investments);}
                        if(Other2 != null){i2 = Float.parseFloat(Other2) / divisor2; xData.add("Other"); yData.add(i2); zData.add(Other2);}

                        pieChart2.setContentDescription("Daily Income");
                        pieChart2.setRotationEnabled(true);
                        pieChart2.setHoleRadius(0f);
                        pieChart2.setTransparentCircleAlpha(0);
                        addDataSet(pieChart2);

                        String textData = "";
                        for(int i=0;i<xData.size();i++){
                            textData = textData + xData.get(i) + ": " + currency + zData.get(i) + "\n";
                        }
                        textData = textData + "Total: " + currency + Total2;
                        textView2.setText(textData);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"Failed to display daily pie chart2. " + e.toString());
                    }
                });
    }

    private void addDataSet(PieChart pieChart){
        ArrayList<PieEntry> yEntry = new ArrayList<>();

        for(int i=0;i<yData.size();i++){
            yEntry.add(new PieEntry(yData.get(i), xData.get(i)));
        }

        PieDataSet pieDataSet = new PieDataSet(yEntry,"");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.BLUE);
        colors.add(Color.RED);
        colors.add(Color.GREEN);
        colors.add(Color.GRAY);
        colors.add(Color.MAGENTA);
        colors.add(Color.CYAN);
        colors.add(R.color.white);
        colors.add(R.color.lime);
        colors.add(R.color.olive);
        colors.add(R.color.purple);
        colors.add(R.color.aqua);
        colors.add(R.color.silver);
        colors.add(R.color.teal);
        colors.add(R.color.navy);
        colors.add(R.color.fuchsia);
        colors.add(R.color.maroon);
        colors.add(R.color.colorPreviousPrimary);
        colors.add(R.color.colorPreviousPrimaryDark);
        colors.add(R.color.colorAccent);
        colors.add(R.color.colorPrimary);
        colors.add(R.color.colorPrimaryDark);
        colors.add(Color.YELLOW);
        pieDataSet.setColors(colors);

        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
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
